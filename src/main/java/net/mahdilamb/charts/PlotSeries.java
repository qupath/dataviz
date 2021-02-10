package net.mahdilamb.charts;

import net.mahdilamb.charts.dataframe.*;
import net.mahdilamb.charts.dataframe.utils.GroupBy;
import net.mahdilamb.charts.graphics.SelectedStyle;
import net.mahdilamb.charts.graphics.UnselectedStyle;
import net.mahdilamb.charts.plots.MarginalMode;
import net.mahdilamb.charts.plots.RectangularPlot;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ObjDoubleConsumer;

import static net.mahdilamb.charts.statistics.ArrayUtils.intRange;
import static net.mahdilamb.charts.statistics.StatUtils.max;
import static net.mahdilamb.charts.statistics.StatUtils.min;
import static net.mahdilamb.charts.utils.StringUtils.EMPTY_STRING;

/**
 * A series of data elements that can be added to a plot area
 *
 * @param <S> the type of the series
 */
public abstract class PlotSeries<S extends PlotSeries<S>> {

    Figure<?, ?> figure;

    private Map<AttributeType, Attribute<?>> attributes;
    protected DataFrame data;

    /*
    Layout suggestions
     */
    FactorizedDataAttribute<String> facetCol;
    FactorizedDataAttribute<String> facetRow;
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

    /**
     * Abstract attribute for a data point
     */
    protected static abstract class Attribute<T> {
        /**
         * The name of the attribute - this is also used for linking attributes together
         */
        public String name;
        /**
         * The type of the attribute
         */
        AttributeType type;
        /**
         * The next attribute in the link
         */
        public Attribute<?> next;

        Attribute(final String name) {
            this.name = name;
        }

        /**
         * @return the attribute type
         */
        public AttributeType getType() {
            return type;
        }

        public abstract T get(int index);

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Attribute)) {
                return false;
            }
            final Attribute<?> other = (Attribute<?>) obj;
            return other.name.equals(name);
        }
    }

    /**
     * A data attribute
     *
     * @param <T> the type of the data
     */
    protected static class DataAttribute<T> extends Attribute<T> {

        T[] attributes;

        /**
         * Create a data attribute with the given data
         *
         * @param name       the name of the attribute
         * @param attributes the actual attributes
         */
        public DataAttribute(final String name, T[] attributes) {
            super(name);
            this.attributes = attributes;
        }

        /**
         * Get the attribute at the given index
         *
         * @param index the index
         * @return the attribute at the specified index
         */
        @Override
        public T get(int index) {
            return attributes[index];
        }
    }

    /**
     * A primitive double attribute
     */
    protected static class DoubleDataAttribute extends Attribute<Double> {
        double[] attributes;

        /**
         * Create a data attribute with the given data
         *
         * @param name       the name of the attribute
         * @param attributes the actual attributes
         */
        public DoubleDataAttribute(final String name, double[] attributes) {
            super(name);
            this.attributes = attributes;
        }

        /**
         * Get the attribute at the given index
         *
         * @param index the index
         * @return the attribute at the specified index
         */
        public double getDouble(int index) {
            return attributes[index];
        }

        @Override
        public Double get(int index) {
            return getDouble(index);
        }
    }

    protected static class PairedDoubleDataAttribute extends DoubleDataAttribute {
        public double[] right;

        public PairedDoubleDataAttribute(final String name, double[] attributes, double[] right) {
            super(name, attributes);
            this.right = right;
        }
    }

    protected static class ScaledDoubleDataAttribute extends DoubleDataAttribute {

        public double scaleNaN = 0, scaleMin = 0, scaleMax = 1;

        //the actual min and max values to use from the value range
        public double valueMin, valueMax;

        public ScaledDoubleDataAttribute(String name, double[] attributes) {
            super(name, attributes);
            valueMin = min(attributes);
            valueMax = max(attributes);
        }

        @Override
        public double getDouble(int index) {
            if (Double.isNaN(attributes[index])) {
                return scaleNaN;
            }
            return (((attributes[index] - valueMin) / (valueMax - valueMin)) * (scaleMax - scaleMin)) + scaleMin;
        }
    }


    protected static class IntDataAttribute extends Attribute<Integer> {
        public int[] attributes;

        public IntDataAttribute(final String name, int[] attributes) {
            super(name);
            this.attributes = attributes;
        }

        public int getInt(int index) {
            return attributes[index];
        }

        @Override
        public Integer get(int index) {
            return getInt(index);
        }
    }

    protected static class FactorizedDataAttribute<T> extends Attribute<T> {
        public int[] factors;
        public T[] data;
        public int[] attributes;
        public String[] names;

        public FactorizedDataAttribute(final String name, int[] attributes, int[] factors) {
            super(name);
            this.attributes = attributes;
            this.factors = factors;
        }

        public FactorizedDataAttribute(final String name, final GroupBy<String> groups) {
            this(name, new int[groups.size()], intRange(groups.numGroups()));
            names = new String[groups.numGroups()];
            int i = 0;
            for (final GroupBy.Group<String> group : groups) {
                for (final int j : group) {
                    attributes[j] = group.getID();
                }
                factors[i] = group.getID();
                names[i] = group.get();
                ++i;

            }
        }

        @SuppressWarnings("unchecked")
        public FactorizedDataAttribute<T> mapFactor(IntFunction<T> mapper, Class<T> toClass) {
            if (data == null) {
                data = (T[]) Array.newInstance(toClass, attributes.length);
            }
            for (int i = 0; i < attributes.length; i++) {
                data[i] = mapper.apply(factors[attributes[i]]);
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        public FactorizedDataAttribute<T> map(Function<String, T> mapper, Class<T> toClass) {
            if (data == null) {
                data = (T[]) Array.newInstance(toClass, attributes.length);
            }
            for (int i = 0; i < attributes.length; i++) {
                data[i] = mapper.apply(names[factors[attributes[i]]]);
            }
            return this;
        }

        @Override
        public T get(int index) {
            return data[index];
        }
    }

    protected static final class QualitativeColorAttribute extends FactorizedDataAttribute<Color> {
        public Colormap colormap = null;//null inherit from plot

        public QualitativeColorAttribute(String name, int[] attributes, int[] factors) {
            super(name, attributes, factors);
        }

        public QualitativeColorAttribute(final String name, final GroupBy<String> groups) {
            super(name, groups);
        }
    }

    protected static final class SequentialColorAttribute extends ScaledDoubleDataAttribute {
        public Colormap colormap = null;//null inherit from plot
        public boolean useLogarithmic = false;

        public SequentialColorAttribute(String name, double[] attributes) {
            super(name, attributes);
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

    @SuppressWarnings("unchecked")
    protected S ifSeriesCategorical(final String seriesName, BiConsumer<S, StringSeries> categorical, BiConsumer<S, DoubleSeries> qualitative) {
        if (data == null) {
            throw new UnsupportedOperationException("This method should only be used if working with a dataframe");
        }
        final DataSeries<?> series = data.get(seriesName);
        if (series.getType() == DataType.DOUBLE) {
            qualitative.accept((S) this, series.asDouble());
        } else {
            categorical.accept((S) this, series.asString());
        }
        return redraw();
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
     * @param <A>       the type of the attribute
     * @return the attribute
     */
    protected <A extends Attribute<T>, T> A addAttribute(final AttributeType type, final A attribute) {
        attribute.type = type;
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
        }
        removeAttribute(attribute);
        for (final Attribute<?> a : attributes()) {
            if (a == attribute) {
                continue;
            }
            if (attribute.equals(a)) {
                Attribute<?> b = a;
                while (b.next != null) {
                    b = b.next;
                }
                b.next = attribute;
                return attribute;
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
        attributes.remove(attrName);
    }

    protected Attribute<?> getAttribute(AttributeType name) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(name);
    }

    /**
     * @return an iterable over the ordered attributes
     */
    protected Iterable<Attribute<?>> attributes() {
        if (attributes == null) {
            return () -> new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Attribute<?> next() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return () -> attributes.values().iterator();
    }

    protected Iterable<Map.Entry<AttributeType, Attribute<?>>> attributeEntries() {
        if (attributes == null) {
            return () -> new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Map.Entry<AttributeType, Attribute<?>> next() {
                    return null;
                }
            };
        }
        return () -> attributes.entrySet().iterator();
    }

    private void removeAttribute(Attribute<?> attribute) {
        for (final Attribute<?> a : attributes()) {
            Attribute<?> candidate = a;
            boolean contains = false;

            do {
                if (candidate.type == attribute.type) {
                    contains = true;
                    break;
                }
                candidate = candidate.next;
            } while (candidate != null);

            if (contains) {
                if (candidate == a) {
                    //is also a key
                    attributes.remove(candidate.type);
                    if (candidate.next != null) {
                        //move to the beginning of the links
                        attributes.put(candidate.next.type, candidate.next);
                    }
                    candidate.next = null;

                } else {
                    //TODO deal with replacing and removing
                }
            }
        }
    }

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
        this.facetCol = new FactorizedDataAttribute<>(seriesName, new GroupBy<>(series, series.size()));
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
        this.facetRow = new FactorizedDataAttribute<>(seriesName, new GroupBy<>(series, series.size()));
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
}
