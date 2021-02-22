package net.mahdilamb.charts;

import net.mahdilamb.charts.dataframe.*;
import net.mahdilamb.charts.dataframe.utils.DoubleArrayList;
import net.mahdilamb.charts.dataframe.utils.GroupBy;
import net.mahdilamb.charts.functions.BiObjAndBiDoubleConsumer;
import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.SelectedStyle;
import net.mahdilamb.charts.graphics.UnselectedStyle;
import net.mahdilamb.charts.plots.MarginalMode;
import net.mahdilamb.charts.plots.RectangularPlot;
import net.mahdilamb.charts.plots.Scatter;
import net.mahdilamb.charts.statistics.BinWidthEstimator;
import net.mahdilamb.charts.statistics.Histogram;
import net.mahdilamb.charts.statistics.StatUtils;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;

import static net.mahdilamb.charts.Figure.DEFAULT_QUALITATIVE_COLORMAP;
import static net.mahdilamb.charts.statistics.StatUtils.max;
import static net.mahdilamb.charts.statistics.StatUtils.min;
import static net.mahdilamb.charts.utils.StringUtils.EMPTY_STRING;

/**
 * A series of data elements that can be added to a plot area.
 *
 * @param <S> the type of the series
 */
public abstract class PlotSeries<S extends PlotSeries<S>> {

    Figure<?, ?> figure;

    private Map<AttributeType, TraceGroup<?>> attributes;
    protected DataFrame data;

    /*
    Layout suggestions
     */
    //todo
    String facetCol;
    String facetRow;
    int facetColWrap = -1;


    /**
     * The attribute type
     */
    protected enum AttributeType {
        /**
         * The color attribute
         */
        COLOR,
        /**
         * The size attribute
         */
        SIZE,
        /**
         * The marker shape attribute
         */
        SHAPE,
        /**
         * Marker opacity attribute
         */
        OPACITY,
        /**
         * Error x attribute
         */
        ERROR_X,
        /**
         * Error y attribute
         */
        ERROR_Y;

        @Override
        public String toString() {
            return StringUtils.snakeToLowerCase(super.toString());
        }
    }


    protected static class TraceGroup<T> {
        public String name;
        public AttributeType type;
        public Trace[] traces;
        public final T source;
        public TraceGroup<?> next;

        private TraceGroup(String name, T source) {
            this.name = name;
            this.source = source;

        }

        @Override
        public String toString() {
            return String.format("TraceGroup {%s, type: %s, traces: %d}", name, type, traces == null ? 0 : traces.length);
        }

        public static TraceGroup<QualitativeColorAttribute> createForQualitativeColor(final String name, final QualitativeColorAttribute source) {
            final TraceGroup<QualitativeColorAttribute> traceGroup = new TraceGroup<>(name, source);
            traceGroup.traces = new Trace[source.factors.length];
            for (final GroupBy.Group<String> group : source.groups) {
                final Trace trace = new Trace(group);
                traceGroup.traces[trace.i] = trace;
            }
            return traceGroup;
        }

        public static TraceGroup<DimensionMap> createForDimension(String name, DimensionMap attributeMap, double scaleMin, double scaleMax, String[] names, double[] values) {
            final TraceGroup<DimensionMap> traceGroup = new TraceGroup<>(name, attributeMap);
            attributeMap.scaleMin = scaleMin;
            attributeMap.scaleMax = scaleMax;

            if (names != null) {
                traceGroup.traces = new Trace[names.length];
                for (int i = 0; i < traceGroup.traces.length; ++i) {
                    final Trace trace = new Trace();
                    trace.i = i;
                    trace.name = names[i];
                    //todo use val to make glyph
                    traceGroup.traces[i] = trace;
                }
            } else {
                final Histogram hist = StatUtils.histogram(Math.min(4, StatUtils.histogramBinEdges(BinWidthEstimator.NUMPY_AUTO, attributeMap.values).length - 1), attributeMap.values);
                int count = 0;
                for (int i = 0; i < hist.numBins(); ++i) {
                    if (hist.getCount(i) != 0) {
                        ++count;
                    }
                }
                traceGroup.traces = new Trace[count];
                for (int i = hist.numBins() - 1, j = 0; i >= 0; --i) {
                    if (hist.getCount(i) == 0) {
                        continue;
                    }
                    final Trace trace = new Trace();
                    trace.name = String.format("%.3f", hist.getBinEdges()[i]);//TODO deal with formatting
                    trace.glyphSize = attributeMap.scale(hist.getBinEdges()[i]);
                    trace.i = j++;
                    traceGroup.traces[trace.i] = trace;
                }
            }

            return traceGroup;
        }

        public static <T> TraceGroup<FromGroupIdMap<T>> createForGroupID(String name, GroupBy<String> groups, IntFunction<T> setter) {
            final TraceGroup<FromGroupIdMap<T>> traceGroup = new TraceGroup<>(name, new FromGroupIdMap<>(groups, setter));
            traceGroup.traces = new Trace[groups.numGroups()];
            for (final GroupBy.Group<String> group : groups) {
                final Trace trace = new Trace(group);
                traceGroup.traces[trace.i] = trace;
            }
            return traceGroup;
        }

        public static <T> TraceGroup<FromGroupNameMap<T>> createForGroupVal(String name, GroupBy<String> groups, Function<String, T> setter) {
            final TraceGroup<FromGroupNameMap<T>> traceGroup = new TraceGroup<>(name, new FromGroupNameMap<>(groups, setter));
            traceGroup.traces = new Trace[groups.numGroups()];
            int j = 0;
            for (final GroupBy.Group<String> group : groups) {
                final Trace trace = new Trace(group);
                traceGroup.traces[j++] = trace;
            }
            return traceGroup;

        }

        public static TraceGroup<SequentialColorAttribute> createForSequentialColormap(String name, SequentialColorAttribute sequentialColorAttribute) {
            final TraceGroup<SequentialColorAttribute> traceGroup = new TraceGroup<>(name, sequentialColorAttribute);
            //TODO
            return traceGroup;
        }


        public static TraceGroup<DimensionMap> createForDimension(String name, DimensionMap attributeMap, double scaleMin, double scaleMax) {
            return createForDimension(name, attributeMap, scaleMin, scaleMax, null, null);
        }

        public static TraceGroup<DoubleArrayList> createForDoubleArrayList(final DoubleArrayList arrayList) {
            return new TraceGroup<>(null, arrayList);
        }

        public static <T> TraceGroup<List<T>> createForArrayList(final List<T> arrayList) {
            return new TraceGroup<>(null, arrayList);
        }
    }

    protected static class Trace {

        /* legend attributes */
        BiObjAndBiDoubleConsumer<ChartCanvas<?>, Trace> glyph;
        public double glyphSize = Scatter.DEFAULT_MARKER_SIZE;
        public Object glyphData;
        String name;
        int i;

        public Trace() {

        }

        public Trace(GroupBy.Group<String> group) {
            name = group.get();
            i = group.getID();

        }

        @Override
        public String toString() {
            return String.format("Trace {%s, id: %d}", name, i);
        }

        public int get() {
            return i;
        }

    }

    protected static class FactorMap {
        int[] values;
        public final GroupBy<String> groups;
        final int[] factors;
        final String[] labels;

        FactorMap(final GroupBy<String> groups) {
            this.groups = groups;
            values = new int[groups.size()];
            factors = new int[groups.numGroups()];
            labels = new String[groups.numGroups()];
            int j = 0;
            for (final GroupBy.Group<String> g : groups) {
                factors[j] = g.getID();
                labels[j] = g.get();
                for (int i : g) {
                    values[i] = g.getID();
                }
                ++j;
            }
        }
    }

    protected static class FromGroupIdMap<T> extends FactorMap {

        private final T[] data;
        private final int[] groups;

        @SuppressWarnings("unchecked")
        FromGroupIdMap(GroupBy<String> groups, IntFunction<T> converter) {
            super(groups);
            data = (T[]) new Object[groups.numGroups()];
            for (final GroupBy.Group<String> g : groups) {
                data[g.getID()] = converter.apply(g.getID());
            }
            this.groups = groups.toMeltedArray();
        }

        public T get(int index) {
            return data[groups[index]];
        }
    }

    protected static class FromGroupNameMap<T> extends FactorMap {

        private final T[] data;
        private final int[] groups;

        @SuppressWarnings("unchecked")
        FromGroupNameMap(GroupBy<String> groups, Function<String, T> converter) {
            super(groups);
            data = (T[]) new Object[groups.numGroups()];
            for (final GroupBy.Group<String> g : groups) {
                data[g.getID()] = converter.apply(g.get());
            }
            this.groups = groups.toMeltedArray();
        }

        public T get(int index) {
            return data[groups[index]];
        }
    }

    protected static class DimensionMap {
        final double[] values;
        //the actual min and max values to use from the value range
        public final double valMin, valRange;
        //the minimum values to map to in the colormap
        public double scaleMin = 0, scaleMax = 1;
        public boolean useLogarithmic = false;

        public DimensionMap(double[] values) {
            this.values = values;
            this.valMin = StatUtils.min(values);
            double valMax = StatUtils.max(values);
            valRange = valMax - valMin;
        }

        public double get(int index) {
            return scale(values[index]);
        }

        public double scale(double value) {
            double v = (((value - valMin) / valRange) * (scaleMax - scaleMin)) + scaleMin;
            if (useLogarithmic) {
                //todo scale logarithmically
            }
            return v;
        }
    }

    protected static final class ErrorAttribute {
        DoubleArrayList upper;
        DoubleArrayList lower;

        public ErrorAttribute(DoubleArrayList data) {
            this.upper = data;
            this.lower = data;
        }

        public ErrorAttribute(DoubleArrayList lower, DoubleArrayList upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    protected static final class QualitativeColorAttribute extends FactorMap {
        public Colormap colormap = null;//null inherit from plot

        public QualitativeColorAttribute(final GroupBy<String> groups) {
            super(groups);
        }

        public Color get(final Colormap colormap, int index) {
            float c = ((float) values[index]) / colormap.size();
            return colormap.get(c - ((int) c));
        }

        public Color getI(final Colormap colormap, int index) {
            float c = ((float) index) / colormap.size();
            return colormap.get(c - ((int) c));
        }
    }

    protected static final class SequentialColorAttribute extends DimensionMap {
        public Colormap colormap = null;//null inherit from plot

        public SequentialColorAttribute(Colormap colormap, double[] attributes) {
            this(attributes);
            this.colormap = colormap;
        }

        public SequentialColorAttribute(double[] attributes) {
            super(attributes);
        }
    }

    protected static final Runnable EMPTY_RUNNABLE = () -> {
    };
    SelectedStyle selectedStyle = SelectedStyle.DEFAULT_SELECTED_STYLE;
    UnselectedStyle unselectedStyle = UnselectedStyle.DEFAULT_UNSELECTED_STYLE;

    protected String name;

    /**
     * Series display
     */
    protected boolean showInLegend = true, showInColorBars = false;

    /**
     * The colormap used for each group when a color hasn't been specified is populated using this colormap
     */
    protected Colormap colormap = null;//inherit

    protected void assign(Figure<?, ?> chart) {
        this.figure = chart;
    }

    protected S ifSeriesCategorical(final String seriesName, BiConsumer<S, StringSeries> categorical, BiConsumer<S, DoubleSeries> qualitative) {
        return ifSeriesCategorical(seriesName, categorical, qualitative, this::redraw);
    }

    @SuppressWarnings("unchecked")
    protected S ifSeriesCategorical(final String seriesName, BiConsumer<S, StringSeries> categorical, BiConsumer<S, DoubleSeries> qualitative, Supplier<S> andThen) {
        if (data == null) {
            throw new UnsupportedOperationException("This method should only be used if working with a dataframe");
        }
        final Series<?> series = data.get(seriesName);
        if (series.getType() == DataType.DOUBLE) {
            qualitative.accept((S) this, series.asDouble());
        } else {
            categorical.accept((S) this, series.asString());
        }
        return andThen.get();
    }

    /**
     * Utility method to use. Runs a function either on the series (if not assigned to the chart, or assigned to the chart)
     *
     * @param seriesSetter the function to run if not assigned to a chart
     * @param chartSetter  the function to run if assigned to a chart
     * @param val          the value to use
     */
    @SuppressWarnings("unchecked")
    protected S orApplyToChart(ObjDoubleConsumer<S> seriesSetter, ObjDoubleConsumer<Figure<?, ?>> chartSetter, double val) {
        //TODO update for multiplot
        if (this.figure == null) {
            seriesSetter.accept((S) this, val);
        } else {
            chartSetter.accept(this.figure, val);
            return (S) this;
        }
        return redraw();
    }

    /**
     * Convenience method to add an attribute and return it
     *
     * @param type      the name of the attribute
     * @param attribute the attribute
     * @return the attribute
     */
    protected <T> TraceGroup<T> addAttribute(final AttributeType type, final TraceGroup<T> attribute) {
        switch (type) {
            case SIZE:
                return addAttribute(type, attribute, (canvas, trace, x, y) -> {
                    canvas.setFill(Color.gray);
                    canvas.fillOval(x - trace.glyphSize * .5, y - trace.glyphSize * .5, trace.glyphSize, trace.glyphSize);
                });
            case SHAPE:
                return addAttribute(type, attribute, null);
            case COLOR:
                if (attribute.source.getClass() == QualitativeColorAttribute.class) {
                    return addAttribute(type, attribute, (canvas, trace, x, y) -> {
                        canvas.setFill(((QualitativeColorAttribute) attribute.source).getI(((QualitativeColorAttribute) attribute.source).colormap == null ? DEFAULT_QUALITATIVE_COLORMAP : ((QualitativeColorAttribute) attribute.source).colormap, trace.i));
                        canvas.fillOval(x - trace.glyphSize * .5, y - trace.glyphSize * .5, trace.glyphSize, trace.glyphSize);
                    });
                }
                return addAttribute(type, attribute, null);

            default:
                throw new UnsupportedOperationException();
        }


    }

    protected <T> TraceGroup<T> addAttribute(final AttributeType type, final TraceGroup<T> attribute, BiObjAndBiDoubleConsumer<ChartCanvas<?>, Trace> glyph) {
        attribute.type = type;
        for (final Trace t : attribute.traces) {
            if (t.glyph != null) {
                break;
            }
            t.glyph = glyph;
        }
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
        }
        removeAttribute(type);
        for (final Map.Entry<AttributeType, TraceGroup<?>> group : attributes.entrySet()) {
            if (group.getValue().name.equals(attribute.name)) {
                TraceGroup<?> candidate = group.getValue();
                while (candidate.next != null) {
                    candidate = candidate.next;
                }
                candidate.next = attribute;
                return attribute;
            } else if (group.getValue().next != null) {
                //todo traverse and link
            }
        }

        attributes.put(type, attribute);
        return attribute;
    }

    /**
     * Remove an attribute by name
     *
     * @param attrName the name of the attribute
     */
    protected void removeAttribute(final AttributeType attrName) {
        if (attributes == null) {
            return;
        }
        for (final Map.Entry<AttributeType, TraceGroup<?>> group : attributes.entrySet()) {
            if (group.getKey() == attrName) {
                if (group.getValue().next != null) {
                    //todo reel in
                } else {
                    //just remove key
                }
            } else if (group.getValue().next != null) {
                TraceGroup<?> candidate = group.getValue();
                while (candidate != null) {
                    if (candidate.type == attrName) {
                        //todo remove
                        break;
                    }
                    candidate = candidate.next;
                }
            }
        }
    }

    protected TraceGroup<?> getAttribute(AttributeType name) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(name);
    }

    int numAttributes() {
        return attributes == null ? 0 : attributes.size();
    }

    /**
     * @return an iterable over the ordered attributes
     */
    protected Iterable<TraceGroup<?>> attributes() {
        if (attributes == null) {
            return () -> new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public TraceGroup<?> next() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return () -> attributes.values().iterator();
    }

    protected Iterable<Map.Entry<AttributeType, TraceGroup<?>>> attributeEntries() {
        if (attributes == null) {
            return () -> new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Map.Entry<AttributeType, TraceGroup<?>> next() {
                    return null;
                }
            };
        }
        return () -> attributes.entrySet().iterator();
    }

    private void removeAttribute(TraceGroup<?> attribute) {
        if (attributes == null) {
            return;
        }
        attributes.remove(attribute.type);
    }

    protected abstract void drawSeries(Figure<?, ?> source, ChartCanvas<?> canvas, Plot<? extends S> plot);

    /**
     * @return this series after firing a redraw request if the chart is specified
     */
    @SuppressWarnings("unchecked")
    protected S redraw() {
        if (figure != null) {
            figure.redraw();
        }
        return (S) this;
    }

    /**
     * Request a data update
     *
     * @return this series
     */
    protected S requestDataUpdate() {
        return redraw();
    }

    /**
     * Set the name of this series
     *
     * @param name the name to set
     * @return this series
     */
    public S setName(String name) {
        andApplyToChart(
                (series, val) -> series.name = val,
                (chart, val) -> {
                }, name
        );
        return redraw();
    }

    @SuppressWarnings("unchecked")
    public S setCols(final String seriesName) throws UnsupportedOperationException {
        if (data == null) {
            throw new UnsupportedOperationException("This method should only be used if a dataframe is attached");
        }
        if (figure != null) {
            throw new UnsupportedOperationException("Cannot set number of columns after chart created");
        }
        final StringSeries series = data.getStringSeries(seriesName);
        this.facetCol = seriesName;//TODO
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setRows(final String seriesName) throws UnsupportedOperationException {
        if (data == null) {
            throw new UnsupportedOperationException("This method should only be used if a dataframe is attached");
        }
        if (figure != null) {
            throw new UnsupportedOperationException("Cannot set number of rows after chart created");
        }
        final StringSeries series = data.getStringSeries(seriesName);
        this.facetRow = seriesName; // todo
        this.facetColWrap = -1;
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setColWrap(final int colWrap) throws UnsupportedOperationException {
        if (data == null) {
            throw new UnsupportedOperationException("This method should only be used if a dataframe is attached");
        }
        if (figure != null) {
            throw new UnsupportedOperationException("Cannot set column wrap after chart created");
        }
        this.facetColWrap = colWrap;
        this.facetRow = null;
        return (S) this;
    }

    /**
     * A one-dimensional plot series with distribution data
     *
     * @param <S> the concrete type of this series
     */
    public abstract static class Distribution<S extends Distribution<S>> extends PlotSeries<S> implements RectangularPlot {
        protected final double[] values;
        String xLabel, yLabel;
        protected double valueMin, valueMax;

        /**
         * Create a distribution series from the given series
         *
         * @param values the values to create the distribution series of
         */
        protected Distribution(double[] values) {
            this.values = values;
            valueMin = min(values);
            valueMax = max(values);
        }

        @Override
        public String getXLabel() {
            return xLabel;
        }

        @Override
        public String getYLabel() {
            return yLabel;
        }

        @Override
        public double getMinX() {
            return 0;
        }

        @Override
        public double getMaxX() {
            return 1;
        }

        @Override
        public double getMinY() {
            return valueMin;
        }

        @Override
        public int size() {
            //todo
            return values.length;
        }

        @Override
        public double getMaxY() {
            return valueMax;
        }
    }

    /**
     * An abstract 2D distribution series
     *
     * @param <S> the concrete type of the series
     */
    public static abstract class Distribution2D<S extends Distribution2D<S>> extends PlotSeries<S> implements RectangularPlot {
        private final double[] x;
        private final double[] y;
        MarginalMode marginalModeX = MarginalMode.NONE;
        MarginalMode marginalModeY = MarginalMode.NONE;
        String xLabel, yLabel;
        protected final double xMin, xMax, yMin, yMax;

        /**
         * Create a 2D distribution series
         *
         * @param x the x data
         * @param y the y data
         */
        protected Distribution2D(double[] x, double[] y) {
            this.x = x;
            this.y = y;
            xMin = min(x);
            xMax = max(x);
            yMin = min(y);
            yMax = max(y);

        }

        @Override
        public int size() {
            //TODO
            return x.length * y.length;
        }

        /**
         * Set the marginal mode of the x data
         *
         * @param mode the mode
         * @return this series
         */
        public S setMarginalX(MarginalMode mode) {
            this.marginalModeX = mode;
            return requestDataUpdate();
        }

        /**
         * Set the marginal mode of the y data
         *
         * @param mode the mode
         * @return this series
         */
        public S setMarginalY(MarginalMode mode) {
            this.marginalModeY = mode;
            return requestDataUpdate();
        }

        @Override
        public String getXLabel() {
            return xLabel;
        }

        @Override
        public double getMinX() {
            return xMin;
        }

        @Override
        public double getMaxX() {
            return xMax;
        }

        @Override
        public double getMinY() {
            return yMin;
        }

        @Override
        public double getMaxY() {
            return yMax;
        }

        @Override
        public String getYLabel() {
            return yLabel;
        }
    }

    public static abstract class Categorical<S extends Categorical<S>> extends PlotSeries<S> {
        protected final String[] names;


        protected final double[] values;
        protected final double valueMin, valueMax;

        public Categorical(String[] names, double[] values) {
            if (names.length != values.length) {
                throw new IllegalArgumentException();
            }
            this.names = names;
            this.values = values;
            valueMin = min(values);
            valueMax = max(values);
        }

        protected double getMinX() {
            return 0;
        }

        protected double getMaxX() {
            return names.length;
        }

        protected double getMinY() {
            return valueMin;
        }

        protected double getMaxY() {
            return valueMax;
        }

        @Override
        public int size() {
            return names.length;
        }

    }

    public static abstract class Matrix<S extends Matrix<S>> extends PlotSeries<S> implements RectangularPlot {
        String xLabel, yLabel;
        protected final double[][] data;
        final int rowMajorWidth, rowMajorHeight;

        protected Matrix(double[][] data) {
            this.data = data;
            this.rowMajorHeight = data.length;
            this.rowMajorWidth = calculateWidth(data);
        }

        @Override
        public double getMinX() {
            return -.5;
        }

        @Override
        public double getMaxX() {
            return rowMajorWidth - .5;
        }

        @Override
        public double getMinY() {
            return -.5;
        }

        @Override
        public double getMaxY() {
            return rowMajorHeight - .5;
        }

        static int calculateWidth(double[][] data) {
            int width = -1;
            for (final double[] d : data) {
                width = Math.max(width, d.length);
            }
            return width;
        }

        @Override
        public String getXLabel() {
            return xLabel;
        }

        @Override
        public String getYLabel() {
            return yLabel;
        }

        @Override
        public int size() {
            return rowMajorHeight * rowMajorWidth;
        }
    }

    /**
     * Utility method to use. Runs a function either on the series (if not assigned to the chart, or assigned to the chart)
     *
     * @param chartSetter  the function to run if assigned to a chart
     * @param seriesSetter the function to run if not assigned to a chart
     * @param val          the value to use
     */
    @SuppressWarnings("unchecked")
    protected <T> S orApplyToChart(BiConsumer<S, T> seriesSetter, BiConsumer<Figure<?, ?>, T> chartSetter, T val) {
        //TODO update for multiplot
        if (this.figure == null) {
            seriesSetter.accept((S) this, val);
        } else {
            chartSetter.accept(this.figure, val);
            return (S) this;
        }
        return redraw();
    }

    @SuppressWarnings("unchecked")
    protected void andApplyToChart(ObjDoubleConsumer<S> seriesSetter, ObjDoubleConsumer<Figure<?, ?>> chartSetter, double val) {
        //TODO update for multiplot
        if (this.figure != null) {
            chartSetter.accept(this.figure, val);
        }
        seriesSetter.accept((S) this, val);
    }

    @SuppressWarnings("unchecked")
    protected <T> void andApplyToChart(BiConsumer<S, T> seriesSetter, BiConsumer<Figure<?, ?>, T> chartSetter, T val) {
        //TODO update for multiplot
        if (this.figure != null) {
            chartSetter.accept(this.figure, val);
        }
        seriesSetter.accept((S) this, val);
    }

    protected static String formatName(Object name) {
        return name == null ? EMPTY_STRING : String.format(" ('%s')", name);
    }

    protected static <T> String formatWord(T word, Function<T, String> nonNull) {
        return word == null ? EMPTY_STRING : nonNull.apply(word);
    }

    public abstract int size();

    protected static <S extends PlotSeries<S>> double convertXToPosition(Plot<? extends S> plot, double x) {
        x -= plot.getXAxis().currentLowerBound;
        x *= plot.getXAxis().scale;
        x += plot.getXAxis().posX;
        return x;
    }

    protected static <S extends PlotSeries<S>> double convertYToPosition(Plot<? extends S> plot, double y) {
        y -= plot.getYAxis().currentLowerBound;
        y *= plot.getYAxis().scale;
        y = plot.getYAxis().sizeY - y + plot.getYAxis().posY;
        return y;
    }

}
