package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.dataframe.Axis;
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.DataType;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.BooleanArrayList;
import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataframe.utils.IntArrayList;
import net.mahdilamb.dataviz.graphics.ChartCanvas;
import net.mahdilamb.dataviz.graphics.FillMode;
import net.mahdilamb.dataviz.graphics.MarkerShape;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.plots.DataFrameOnlyOperationException;
import net.mahdilamb.dataviz.plots.Scatter;
import net.mahdilamb.dataviz.plots.ScatterMode;
import net.mahdilamb.dataviz.utils.StringUtils;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.statistics.ArrayUtils;
import net.mahdilamb.statistics.StatUtils;
import net.mahdilamb.utils.tuples.GenericTuple;
import net.mahdilamb.utils.tuples.Tuple;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;

public abstract class PlotData<O extends PlotData<O>> implements FigureComponent<O> {


    protected enum Attribute {
        COLOR,
        OPACITY,
        SHAPE,
        SIZE,
        GROUP
    }


    /**
     * Data structure for relational data
     *
     * @param <O> the concrete type of the XY data
     */
    public abstract static class XYData<O extends XYData<O>> extends PlotData<O> {

        protected final DoubleArrayList x, y;
        protected double xMin, xMax, yMin, yMax;

        protected Stroke lineStroke;
        protected Color lineColor;
        protected double lineWidth = 2;
        protected double[] lineDashes;

        protected FillMode fillMode = FillMode.NONE;
        protected Color fillColor;

        protected String xLab = EMPTY_STRING, yLab = EMPTY_STRING;
        protected String[] xLabels;

        protected ScatterMode markerMode = ScatterMode.MARKER_ONLY;


        protected XYData(double[] x, double[] y) {
            this.x = new DoubleArrayList(x);
            this.y = new DoubleArrayList(y);
            if (this.x.size() != this.y.size()) {
                throw new IllegalArgumentException("X and Y must be the same size");
            }
            setRange(x, y);
            setUnlabelledHover();
        }

        protected XYData(double[] x, DoubleUnaryOperator y) {
            this(x, ArrayUtils.map(x, y));
        }

        protected XYData(double[] y) {
            this(ArrayUtils.range(y.length), y);
        }

        protected XYData(String[] x, double[] y) {
            final GroupBy<String> xAxis = new GroupBy<>(x);
            this.x = new DoubleArrayList(xAxis.toMeltedArray(new double[x.length]));
            this.y = new DoubleArrayList(y);
            if (this.x.size() != this.y.size()) {
                throw new IllegalArgumentException("X and Y must be the same size");
            }

            this.xLabels = xAxis.getGroups(new String[xAxis.numGroups()]);
            xMin = 0;
            xMax = xLabels.length - 1;
            yMin = StatUtils.min(y);
            yMax = StatUtils.max(y);
            setStringYHover();
        }

        protected XYData(final DataFrame dataFrame, final String x, final String y) {
            //todo if x is string series
            double[] valX = toArray(dataFrame, x);
            double[] valY = toArray(dataFrame, y);
            this.x = new DoubleArrayList(valX);
            this.y = new DoubleArrayList(valY);
            setRange(valX, valY);
            setLabelledHover();
            this.dataFrame = dataFrame;
            this.xLab = x;
            this.yLab = y;
        }

        void setRange(double[] x, double[] y) {
            xMin = StatUtils.min(x);
            xMax = StatUtils.max(x);
            yMin = StatUtils.min(y);
            yMax = StatUtils.max(y);
        }

        @SuppressWarnings("unchecked")
        void setUnlabelledHover() {
            final Map<String, IntFunction<?>> formatters = new HashMap<>(2);
            formatters.put("x", this.x::get);
            formatters.put("y", this.y::get);
            hoverFormatter = new HoverText<>((O) this, formatters);
            hoverFormatter.add("(%{x:s}, %{y:s})");
        }

        @SuppressWarnings("unchecked")
        void setStringYHover() {
            final Map<String, IntFunction<?>> formatters = new HashMap<>(2);
            formatters.put("x", this::getXLabel);
            formatters.put("y", this.y::get);
            hoverFormatter = new HoverText<>((O) this, formatters);
            hoverFormatter.add("(%{x:s}, %{y:s})");
        }

        @SuppressWarnings("unchecked")
        void setLabelledHover() {
            final Map<String, IntFunction<?>> formatters = new HashMap<>(2);
            formatters.put("x", this.x::get);
            formatters.put("y", this.y::get);
            hoverFormatter = new HoverText<>((O) this, formatters);
            hoverFormatter.add("%s=%{x:s}", this::getXLabel);
            hoverFormatter.add("%s=%{y:s}", this::getYLabel);
        }

        protected double getX(int i) {
            return x.get(i);
        }

        protected double getY(int i) {
            return y.get(i);
        }

        @Override
        public int size() {
            return x.size();
        }

        public final String getXLabel() {
            if (layout != null) {
                return layout.getXAxis().getTitle();
            }
            return xLab;
        }

        public final String getYLabel() {
            if (layout != null) {
                return layout.getYAxis().getTitle();
            }
            return yLab;
        }

        final String getXLabel(int i) {
            if (layout != null) {
                return layout.getXAxis().labels[i];
            }
            return xLabels[i];
        }

        /**
         * Set the label of the x data
         *
         * @param label the label
         * @return this plot data
         */
        @SuppressWarnings("unchecked")
        public final O setXLabel(final String label) {
            if (layout != null) {
                layout.getXAxis().setTitle(label);
            } else {
                this.xLab = label;
            }
            return (O) this;
        }

        /**
         * Set the label of the y data
         *
         * @param label the label
         * @return this plot data
         */
        @SuppressWarnings("unchecked")
        public final O setYLabel(final String label) {
            if (layout != null) {
                layout.getYAxis().setTitle(label);
            } else {
                this.yLab = label;
            }
            return (O) this;
        }

        /**
         * Set the fill mode of the xy data
         *
         * @param mode the fill mode
         * @return this plot data
         */
        @SuppressWarnings("unchecked")
        public O setFill(final FillMode mode) {
            this.fillMode = mode;
            clear();
            return (O) this;
        }

        /**
         * Set the line width of the data
         *
         * @param width the line width
         * @return this plot data
         */
        @SuppressWarnings("unchecked")
        public O setLineWidth(double width) {
            this.lineWidth = width;
            lineStroke = null;
            return (O) this;
        }

        @SuppressWarnings("unchecked")
        public O setLineColor(Color color) {
            if (this.fillColor == null) {
                fillColor = color;
            }
            this.lineColor = color;
            return (O) this;
        }

        public O setLineColor(String color) {
            return setLineColor(StringUtils.convertToColor(color));
        }

        Stroke getLineStroke() {
            if (lineStroke == null) {
                lineStroke = new Stroke(lineWidth, lineDashes);
            }
            return lineStroke;
        }

        @SuppressWarnings("unchecked")
        public O setFillColor(Color color) {
            this.fillColor = color;
            return (O) this;
        }

        public O setFillColor(String color) {
            return setFillColor(StringUtils.convertToColor(color));
        }

        protected final RTree<Runnable> createMarkers(PlotLayout plotLayout) {
            RTree<Runnable> markers = new RTree<>();
            final PlotShape.PlotMarker[] m;
            if (facets != null) {
                final IntArrayList ids = facets.key.get(plotLayout);
                m = new PlotShape.PlotMarker[ids.size()];
                int j = 0;
                for (int i : ids) {
                    PlotShape.PlotMarker marker = new PlotShape.PlotMarker(this, i, x.get(i), y.get(i), getSize(i));
                    m[j++] = marker;
                }
            } else {
                m = new PlotShape.PlotMarker[size()];
                for (int i = 0; i < size(); ++i) {
                    PlotShape.PlotMarker marker = new PlotShape.PlotMarker(this, i, x.get(i), y.get(i), getSize(i));
                    m[i] = marker;
                }
            }
            markers.putAll(m);

            return markers;
        }

        protected final RTree<Runnable> createLines(PlotLayout plotLayout) {
            if (group == null || colors == null) {
                RTree<Runnable> lines = new RTree<>();
                if (colors != null && colors.getClass() == PlotTrace.Categorical.class) {
                    final PlotShape.PlotPolyLine[] l = new PlotShape.PlotPolyLine[((PlotTrace.Categorical) colors).categories.length];
                    for (int i = 0; i < ((PlotTrace.Categorical) colors).categories.length; ++i) {
                        l[i] = new PlotShape.PlotPolyLine(this, -1, new IntArrayList());
                    }
                    for (int i = 0; i < ((PlotTrace.Categorical) colors).indices.length; ++i) {
                        l[((PlotTrace.Categorical) colors).indices[i]].ids.add(i);
                        if (l[((PlotTrace.Categorical) colors).indices[i]].i == -1) {
                            l[((PlotTrace.Categorical) colors).indices[i]].i = i;
                        }
                    }
                    lines.putAll(l);
                } else {
                    final PlotShape.PlotPolyLine line = new PlotShape.PlotPolyLine(this, 0, new IntArrayList(ArrayUtils.intRange(x.size())));
                    lines.put(line);
                }
                return lines;
            } else {
                //Create possible combinations
                @SuppressWarnings("unchecked") final GenericTuple<String>[][] keyCombinations = new GenericTuple[getCategories(group).length][getCategories((PlotTrace.Categorical) colors).length];
                for (int i = 0; i < getCategories(group).length; ++i) {
                    for (int j = 0; j < getCategories((PlotTrace.Categorical) colors).length; ++j) {
                        keyCombinations[i][j] = Tuple.of(getCategories((PlotTrace.Categorical) colors)[j], getCategories(group)[i]);
                    }
                }
                //Create group by
                @SuppressWarnings("unchecked") final GenericTuple<String>[] keys = new GenericTuple[size()];
                for (int i = 0; i < size(); ++i) {
                    keys[i] = keyCombinations[getRaw(group, i)][getRaw(((PlotTrace.Categorical) colors), i)];
                }
                final GroupBy<GenericTuple<String>> lines = new GroupBy<>(keys);
                //create lines
                PlotShape.PlotPolyLine[] pLines = new PlotShape.PlotPolyLine[lines.numGroups()];
                int i = 0;
                for (final GroupBy.Group<GenericTuple<String>> group : lines) {
                    pLines[i++] = new PlotShape.PlotPolyLine(this, group.getIndices().get(0), group.getIndices());
                }
                RTree<Runnable> l = new RTree<>();
                l.putAll(pLines);
                return l;
            }

        }

        protected final RTree<Runnable> createPolygons(PlotLayout plotLayout) {
            RTree<Runnable> polygons = new RTree<>();
            if (numAttributes() == 0) {
                final PlotShape.PlotPolygon polygon = new PlotShape.PlotPolygon(this, new IntArrayList(ArrayUtils.intRange(x.size())));
                polygon.i = 0;
                polygons.put(polygon);
            } else {
                if (colors != null && colors.getClass() == PlotTrace.Categorical.class) {
                    final PlotShape.PlotPolygon[] p = new PlotShape.PlotPolygon[((PlotTrace.Categorical) colors).categories.length];
                    for (int i = 0; i < ((PlotTrace.Categorical) colors).categories.length; ++i) {
                        p[i] = new PlotShape.PlotPolygon(this, new IntArrayList());
                    }
                    for (int i = 0; i < ((PlotTrace.Categorical) colors).indices.length; ++i) {
                        p[((PlotTrace.Categorical) colors).indices[i]].ids.add(i);
                        if (p[((PlotTrace.Categorical) colors).indices[i]].i == -1) {
                            p[((PlotTrace.Categorical) colors).indices[i]].i = i;
                        }
                    }
                    polygons.putAll(p);
                }

            }

            return polygons;
        }

        @Override
        protected void clear() {
            if (layout != null) {
                layout.clear(this);
                init(layout);
            }

        }

        protected static double[] toArray(final DataFrame dataFrame, final String seriesName) {
            if (!DataType.isNumeric(dataFrame.getType(seriesName))) {
                throw new UnsupportedOperationException("Series must be numeric");
            }
            return dataFrame.getDoubleSeries(seriesName).toArray(new double[dataFrame.size(Axis.INDEX)]);
        }
    }

    /**
     * Data structure for categorical data
     *
     * @param <O> the concrete type of the XY daata
     */
    public static abstract class CategoricalData<O extends CategoricalData<O>> extends PlotData<O> {

        List<String> categories;
        DoubleArrayList values;
        double valueMin, valueMax;
        String categoryLabel, valueLabel;
        protected String[] categoryLabels;

        protected CategoricalData(final String[] categories, final double[] values) {
            init(categories, values);
        }

        protected CategoricalData(final DataFrame dataFrame, final String category, final String values) {
            this.dataFrame = dataFrame;
            final GroupBy<String> group = new GroupBy<>(dataFrame.getStringSeries(category));
            init(group.getGroups(new String[group.numGroups()]), dataFrame.getDoubleSeries(values).toArray(new double[dataFrame.size(Axis.INDEX)]));
            categoryLabel = category;
            valueLabel = values;
        }

        @SuppressWarnings("unchecked")
        void init(String[] categories, double[] values) {
            valueMin = StatUtils.min(values);
            valueMax = StatUtils.max(values);
            this.categories = Arrays.asList(categories);
            this.values = new DoubleArrayList(values);
            final Map<String, IntFunction<?>> formatters = new HashMap<>(2);
            formatters.put("x", this::getCategory);
            formatters.put("y", this::getValue);
            hoverFormatter = new HoverText<>((O) this, formatters);
            hoverFormatter.add("(%{x:s}, %{y:s})");
        }

        String getCategory(int i) {
            return categories.get(i);
        }

        double getValue(int i) {
            return values.get(i);
        }

        @Override
        protected int size() {
            return categories.size();
        }

        @Override
        protected void clear() {

        }

        @Override
        protected void init(PlotLayout plotLayout) {
            updateXYBounds(plotLayout, 0, categories.size(), 0, valueMax, false);
            plotLayout.getXAxis().majorTickSpacing =1;
            plotLayout.getXAxis().minorTickSpacing =.2;
            putRectangles(layout, this, createRectangles(layout));

        }

        public String[] getCategoryLabels() {
            if (categoryLabels == null) {
                //TODO, deal with group
                final GroupBy<String> categories = new GroupBy<>(this.categories);
                categoryLabels = categories.getGroups(new String[categories.numGroups()]);
            }
            return categoryLabels;
        }

        @SuppressWarnings("unchecked")
        public O setColors(String series) throws DataFrameOnlyOperationException {
            final Series<?> s = getSeries(series);
            if (s.getType() == DataType.DOUBLE) {
                throw new UnsupportedOperationException("Series must not be double");
            }
            clear();
            colors = addAttribute(new PlotTrace.Categorical(this, Attribute.COLOR, s));
            addToHoverText(colors, "%s=%{color:s}", () -> colors.getName(), "color", ((PlotTrace.Categorical) colors)::get);
            return (O) this;
        }

        protected final RTree<Runnable> createRectangles(PlotLayout layout) {
            final RTree<Runnable> rectangles = new RTree<>();
            final PlotShape.PlotRectangle[] r = new PlotShape.PlotRectangle[size()];
            for (int i = 0; i < size(); ++i) {
                r[i] = new PlotShape.PlotRectangle(this, i, i + .1, 0, .8, values.get(i));
            }
            rectangles.putAll(r);
            return rectangles;
        }


    }

    protected Figure figure;
    protected PlotLayout layout;
    protected String title = null;

    protected DataFrame dataFrame;
    protected String name;

    protected PlotTrace.Categorical group;
    private Map<Attribute, PlotTrace> attributes;
    protected PlotTrace colors, opacities;
    protected double opacity = 1;

    protected Facets facets;

    protected HoverText<O> hoverFormatter;
    protected List<String> hoverText;

    boolean showScale = true;
    boolean showInLegend = true;
    boolean anySelected = false;
    BooleanArrayList selected = null;

    Colormap colormap;

    protected List<Color> defaultColor = Collections.emptyList();
    protected List<Color> selectedColor = Collections.emptyList();

    /**
     * Set the name of the data
     *
     * @param name the name
     * @return this data
     */
    @SuppressWarnings("unchecked")
    public O setName(String name) {
        this.name = name;
        return (O) this;
    }

    /**
     * @return the name of this data
     */
    public String getName() {
        return name;
    }

    public O addHoverData(String series) throws DataFrameOnlyOperationException {
        return addHoverData(series, "s");
    }

    @SuppressWarnings("unchecked")
    public O addHoverData(String series, final String formatting) throws DataFrameOnlyOperationException {
        final Series<?> s = getSeries(series);
        hoverFormatter.put(series, s::get);
        hoverFormatter.add(String.format("%s=%%{%s:%s}", series, series, formatting.charAt(0) == '%' ? formatting.substring(1) : formatting));
        return (O) this;
    }

    public O setColormap(String colormap) {
        return setColormap(Colormaps.get(colormap));
    }

    @SuppressWarnings("unchecked")
    public O setColormap(Colormap colormap) {
        this.colormap = colormap;
        return (O) this;
    }

    public O setColormap(String... colormap) {
        if (colors == null || colors instanceof PlotTrace.Numeric) {
            return setColormap(StringUtils.convertToSequentialColormap(colormap));
        } else {
            return setColormap(StringUtils.convertToQualitativeColormap(colormap));
        }
    }

    @SuppressWarnings("unchecked")
    public O showScale(boolean show) {
        this.showScale = show;
        return (O) this;
    }

    @SuppressWarnings("unchecked")
    public O setTitle(String title) {
        if (layout != null) {
            if (layout instanceof List) {
                for (final PlotLayout p : ((List<PlotLayout>) layout)) {
                    p.setTitle(title);
                }
            } else {
                layout.setTitle(title);
            }
        } else {
            this.title = title;
        }
        return (O) this;
    }

    @SuppressWarnings("unchecked")
    public O showInLegend(boolean show) {
        this.showInLegend = show;
        return (O) this;
    }

    @SuppressWarnings("unchecked")
    public O setColumns(final String name) throws DataFrameOnlyOperationException {
        if (dataFrame == null) {
            throw new DataFrameOnlyOperationException();
        }
        if (facets == null) {
            facets = new Facets();
        }
        facets.setCols(name, new GroupBy<>(dataFrame.getStringSeries(name)));
        clear();
        return (O) this;
    }

    @SuppressWarnings("unchecked")
    public O setRows(final String name) throws DataFrameOnlyOperationException {
        if (dataFrame == null) {
            throw new DataFrameOnlyOperationException();
        }
        if (facets == null) {
            facets = new Facets();
        }
        facets.setRows(name, new GroupBy<>(dataFrame.getStringSeries(name)));
        clear();
        return (O) this;
    }

    /**
     * @return the number of elements in the data
     */
    protected abstract int size();

    /**
     * @param index the index of interest
     * @return whether the element at the index is visible
     */
    protected boolean isVisible(int index) {
        return isVisible(colors, index) && isVisible(opacities, index);
    }

    protected static boolean isVisible(final PlotTrace group, int index) {
        return group == null || group.isVisible(index);
    }

    protected final String getHoverText(int i) {
        if (hoverText == null) {
            hoverText = new ArrayList<>(size());
            for (int j = 0; j < size(); ) {
                hoverText.add(hoverFormatter.get(j++));
            }
        }
        if (hoverText.isEmpty()) {
            return EMPTY_STRING;
        }
        return hoverText.get(i);
    }

    Color getSelectedColor(int i) {
        if (i < 0) {
            return figure.qualitativeColormap.get(0);
        }
        if (((List<?>) selectedColor) == Collections.emptyList()) {
            selectedColor = new ArrayList<>(size());
            for (int j = 0; j < size(); ++j) {
                final Color original = defaultColor.get(j);
                selectedColor.add(new Color(original.red(), original.green(), original.blue(), opacity * (selected.get(j) ? layout.selectedStyle.opacity : layout.unselectedStyle.opacity)));
            }
        }
        return selectedColor.get(i);


    }

    Colormap getColormap() {
        return this.colormap != null ? this.colormap : (colors instanceof PlotTrace.Numeric) ? figure.sequentialColormap : figure.qualitativeColormap;
    }

    protected Color getDefaultColor(int i) {
        if (colors == null) {
            return getDefaultColor();
        }
        if (((List<?>) defaultColor) == Collections.emptyList()) {
            defaultColor = new ArrayList<>(size());
            final Colormap colormap = getColormap();
            for (int j = 0; j < size(); ++j) {
                defaultColor.add(opacity == 1 ? colors.get(colormap, j) : new Color(colors.get(colormap, j).red(), colors.get(colormap, j).green(), colors.get(colormap, j).blue(), opacity));
            }
        }
        return defaultColor.get(i);
    }

    protected Color getDefaultColor() {
        return figure.getDefaultColor(this);
    }

    protected Color getColor(int i) {
        if (i < 0) {
            return getDefaultColor();
        }
        if (anySelected) {
            return getSelectedColor(i);
        }
        return getDefaultColor(i);
    }

    protected boolean showEdge() {
        return true;
    }

    protected Color getEdgeColor() {
        return Color.white;
    }

    protected Stroke getEdgeStroke() {
        return Stroke.SOLID;
    }


    protected MarkerShape getShape(int i) {
        return MarkerShape.CIRCLE;
    }

    protected void addToHoverText(final PlotTrace trace, String formatting, Supplier<?> supplier, String key, IntFunction<?> getter) {
        trace.defaultSeg = hoverFormatter.add(formatting, supplier);
        hoverFormatter.put(key, getter);

        boolean found = false;
        for (final Map.Entry<Attribute, PlotTrace> t : attributes()) {
            if (t.getValue() == trace) {
                found = true;
                break;
            }
        }
        if (found) {
            hoverFormatter.remove(trace.defaultSeg);
        }
    }

    protected double getSize(int i) {
        return Scatter.DEFAULT_MARKER_SIZE;
    }

    protected PlotTrace getAttribute(Attribute attr) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(attr);
    }

    protected void removeAttribute(final Attribute attribute) {
        if (attributes == null) {
            return;
        }
        final PlotTrace tg = attributes.get(attribute);
        //TODO deal with removing linked attributes
        if (tg != null) {
            hoverFormatter.remove(tg.defaultSeg);
            attributes.remove(attribute);
        }
    }

    protected <T extends PlotTrace> T addAttribute(T traceGroup) {
        if (attributes == null) {
            attributes = new LinkedHashMap<>(Attribute.values().length);
        }
        removeAttribute(traceGroup.attribute);
        for (final Map.Entry<Attribute, PlotTrace> a : attributes()) {

            PlotTrace candidate = a.getValue();
            if (candidate.series == null) {
                continue;
            }
            if (candidate.series.getName().equals(traceGroup.series.getName())) {
                while (candidate.next != null) {
                    candidate = candidate.next;
                }
                candidate.next = traceGroup;
                return traceGroup;
            }

        }
        attributes.put(traceGroup.attribute, traceGroup);
        if (traceGroup.attribute == Attribute.SIZE && traceGroup instanceof PlotTrace.Numeric && ((PlotTrace.Numeric) traceGroup).scaleMin < 0) {
            ((PlotTrace.Numeric) traceGroup).scaleMin = Scatter.DEFAULT_MARKER_SIZE;
            ((PlotTrace.Numeric) traceGroup).scaleMin = Scatter.DEFAULT_MAX_MARKER_SIZE;
        }
        return traceGroup;
    }

    protected int numAttributes() {
        return attributes == null ? 0 : attributes.size();
    }

    protected Iterable<Map.Entry<Attribute, PlotTrace>> attributes() {
        if (attributes == null) {
            return () -> new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Map.Entry<Attribute, PlotTrace> next() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return attributes.entrySet();
    }

    protected Series<?> getSeries(String series) throws DataFrameOnlyOperationException {
        if (dataFrame == null) {
            throw new DataFrameOnlyOperationException();
        }
        final Series<?> s = dataFrame.get(series);
        if (s == null) {
            throw new UnsupportedOperationException("No such series " + series);
        }
        return s;
    }

    protected static <S> S ifSeriesCategorical(final DataFrame dataFrame, final String seriesName, final Consumer<Series<?>> categorical, final Consumer<Series<?>> scalar, final Supplier<S> ret) throws DataFrameOnlyOperationException {
        if (dataFrame == null) {
            throw new DataFrameOnlyOperationException();
        }
        final Series<?> s = dataFrame.get(seriesName);
        if (s == null) {
            throw new IllegalArgumentException("Could not find series called " + seriesName);
        }
        if (s.getType() != DataType.DOUBLE) {
            categorical.accept(s.asString());
        } else {
            scalar.accept(s.asDouble());
        }
        return ret.get();
    }


    @Override
    public Figure getFigure() {
        if (figure == null) {
            figure = new Figure();
            figure.addTrace(this);
        }
        return figure;
    }


    /**
     * Clear the currently associated shapes
     */
    protected abstract void clear();

    /**
     * Performed actions when the data is added to a layout
     *
     * @param plotLayout the layout
     */
    protected abstract void init(PlotLayout plotLayout);

    final boolean showsColorBar() {
        return showScale & colors != null & (colors instanceof PlotTrace.Numeric);
    }

    void layoutColorBar(Renderer<?> source, ColorScales scales) {
        ((PlotTrace.Numeric) colors).layoutColorBar(source, scales);

    }

    void drawColorBar(Renderer<?> source, ChartCanvas<?> canvas, ColorScales scales) {
        ((PlotTrace.Numeric) colors).drawColorBar(source, canvas, scales);

    }


    @SuppressWarnings("unchecked")
    O updateTrace(final PlotTrace trace, Consumer<PlotTrace> apply) {
        apply.accept(trace);
        return (O) this;
    }

    @SuppressWarnings("unchecked")
    public O updateTrace(final String name, Consumer<PlotTrace> fn) {
        for (final Map.Entry<Attribute, PlotTrace> a : attributes()) {
            if (name.equals(a.getValue().getName())) {
                return updateTrace(a.getValue(), fn);
            }
        }
        return (O) this;
    }

    protected static void putMarkers(PlotLayout plotLayout, PlotData<?> data, RTree<Runnable> markers) {
        plotLayout.putMarkers(data, markers);
    }

    protected static void putLines(PlotLayout plotLayout, PlotData<?> data, RTree<Runnable> lines) {
        plotLayout.putLines(data, lines);
    }

    protected static void putPolygons(PlotLayout plotLayout, PlotData<?> data, RTree<Runnable> polygons) {
        plotLayout.putPolygons(data, polygons);
    }

    protected static void putRectangles(PlotLayout plotLayout, PlotData<?> data, RTree<Runnable> rectangles) {
        plotLayout.putRectangles(data, rectangles);
    }

    /**
     * Update the bounds of an xy layout
     *
     * @param plotLayout the plot layout
     * @param xMin       the x min of the data
     * @param xMax       the x max of the data
     * @param yMin       the y min of the data
     * @param yMax       the y max of the data
     */
    protected static void updateXYBounds(final PlotLayout plotLayout, double xMin, double xMax, double yMin, double yMax, boolean useNice) {
        plotLayout.getXAxis().dataLower = Math.min(plotLayout.getXAxis().dataLower, xMin);
        plotLayout.getYAxis().dataLower = Math.min(plotLayout.getYAxis().dataLower, yMin);
        plotLayout.getXAxis().dataUpper = Math.max(plotLayout.getXAxis().dataUpper, xMax);
        plotLayout.getYAxis().dataUpper = Math.max(plotLayout.getYAxis().dataUpper, yMax);
        plotLayout.getXAxis().reset(useNice);
        plotLayout.getYAxis().reset(useNice);


    }

    /**
     * @param trace the trace
     * @return the categories from a trace
     */
    protected static String[] getCategories(PlotTrace.Categorical trace) {
        return trace.categories;
    }

    /**
     * @param trace the trace
     * @param i     the index of the value
     * @return the source value from a trace
     */
    protected static int getRaw(PlotTrace.Categorical trace, int i) {
        return trace.getRaw(i);
    }

    /**
     * @param trace the trace
     * @param i     the index of the value
     * @return the source value from a trace
     */
    protected static double getRaw(PlotTrace.Numeric trace, int i) {
        return trace.getRaw(i);
    }

    protected static Color getBackgroundColor(final PlotLayout layout) {
        return layout.background;
    }

    protected static int numXLabels(net.mahdilamb.dataviz.Axis axis) {
        return axis.labels == null ? -1 : axis.labels.length;
    }

    protected static void setTicks(final net.mahdilamb.dataviz.Axis axis, double major, double minor) {
        axis.majorTickSpacing = major;
        axis.minorTickSpacing = minor;
    }

    protected static void setTicks(final net.mahdilamb.dataviz.Axis axis, double major) {
        axis.majorTickSpacing = major;
        axis.minorTickSpacing = major * .2;
    }
}
