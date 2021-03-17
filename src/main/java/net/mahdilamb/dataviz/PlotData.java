package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.dataframe.Axis;
import net.mahdilamb.dataframe.*;
import net.mahdilamb.dataframe.utils.BooleanArrayList;
import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataframe.utils.IntArrayList;
import net.mahdilamb.dataviz.graphics.ChartCanvas;
import net.mahdilamb.dataviz.graphics.FillMode;
import net.mahdilamb.dataviz.graphics.shapes.MarkerShape;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.plots.DataFrameOnlyOperationException;
import net.mahdilamb.dataviz.plots.Scatter;
import net.mahdilamb.dataviz.plots.ScatterMode;
import net.mahdilamb.dataviz.utils.MarchingSquares;
import net.mahdilamb.dataviz.utils.StringUtils;
import net.mahdilamb.dataviz.utils.SubVariant;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.stats.ArrayUtils;
import net.mahdilamb.stats.BinWidthEstimator;
import net.mahdilamb.stats.Histogram;
import net.mahdilamb.stats.StatUtils;
import net.mahdilamb.utils.tuples.GenericTuple;
import net.mahdilamb.utils.tuples.Tuple;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;
import static net.mahdilamb.stats.ArrayUtils.linearlySpaced;
import static net.mahdilamb.stats.StatUtils.sum;

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
    public abstract static class RelationalData<O extends RelationalData<O>> extends PlotData<O> {

        protected final DoubleArrayList x, y;
        protected double xMin, xMax, yMin, yMax;

        protected FillMode fillMode = FillMode.NONE;

        protected String xLab = EMPTY_STRING, yLab = EMPTY_STRING;
        protected String[] xLabels;

        protected ScatterMode markerMode = ScatterMode.MARKER_ONLY;


        protected RelationalData(double[] x, double[] y) {
            this.x = new DoubleArrayList(x);
            this.y = new DoubleArrayList(y);
            if (this.x.size() != this.y.size()) {
                throw new IllegalArgumentException("X and Y must be the same size");
            }
            setRange(x, y);
            setUnlabelledHover();
        }

        protected RelationalData(double[] x, DoubleUnaryOperator y) {
            this(x, ArrayUtils.map(x, y));
        }

        protected RelationalData(double[] y) {
            this(ArrayUtils.range(y.length), y);
        }

        protected RelationalData(String[] x, double[] y) {
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

        protected RelationalData(final DataFrame dataFrame, final String x, final String y) {
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

        /**
         * @param i the index
         * @return the x value at the given index
         */
        public double getX(int i) {
            return x.get(i);
        }

        /**
         * @param i the index
         * @return the y value at the given index
         */
        public double getY(int i) {
            return y.get(i);
        }

        @Override
        public int size() {
            return x.size();
        }

        /**
         * @return the label of the x series
         */
        public final String getXLabel() {
            if (layout != null) {
                return layout.getXAxis().getTitle();
            }
            return xLab;
        }

        /**
         * @return the label of the y series
         */
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

        @SuppressWarnings("unchecked")
        public O setFillColor(Color color) {
            if (fillMode == FillMode.NONE) {
                fillMode = FillMode.TO_SELF;
            }
            this.fillColor = color;
            return (O) this;
        }

        public O setFillColor(String color) {
            return setFillColor(StringUtils.convertToColor(color));
        }

        protected final RTree<Runnable> createMarkers(PlotLayout plotLayout) {
            RTree<Runnable> markers = new RTree<>();
            final PlotShape.Marker[] m;
            if (facets != null) {
                final IntArrayList ids = facets.key.get(plotLayout);
                m = new PlotShape.Marker[ids.size()];
                int j = 0;
                for (int i : ids) {
                    PlotShape.Marker marker = new PlotShape.Marker(this, i, x.get(i), y.get(i), getSize(i));
                    m[j++] = marker;
                }
            } else {
                m = new PlotShape.Marker[size()];
                for (int i = 0; i < size(); ++i) {
                    PlotShape.Marker marker = new PlotShape.Marker(this, i, x.get(i), y.get(i), getSize(i));
                    m[i] = marker;
                }
            }
            markers.putAll(m);

            return markers;
        }

        protected final RTree<Runnable> createLines(PlotLayout plotLayout) {
            if (group == null || colors == null) {
                RTree<Runnable> lines = new RTree<>();
                if (colors != null && colors.get() != null && colors.isA()) {
                    final PlotShape.PolyLine[] l = new PlotShape.PolyLine[colors.asA().categories.length];
                    for (int i = 0; i < colors.asA().categories.length; ++i) {
                        l[i] = new PlotShape.PolyLine(this, -1, new IntArrayList());
                    }
                    for (int i = 0; i < colors.asA().indices.length; ++i) {
                        l[colors.asA().indices[i]].ids.add(i);
                        if (l[colors.asA().indices[i]].i == -1) {
                            l[colors.asA().indices[i]].i = i;
                        }
                    }
                    lines.putAll(l);
                } else {
                    final PlotShape.PolyLine line = new PlotShape.PolyLine(this, 0, new IntArrayList(ArrayUtils.intRange(x.size())));
                    lines.put(line);
                }
                return lines;
            } else {
                //Create possible combinations
                @SuppressWarnings("unchecked") final GenericTuple<String>[][] keyCombinations = new GenericTuple[getCategories(group).length][getCategories(colors.asA()).length];
                for (int i = 0; i < getCategories(group).length; ++i) {
                    for (int j = 0; j < getCategories(colors.asA()).length; ++j) {
                        keyCombinations[i][j] = Tuple.of(getCategories(colors.asA())[j], getCategories(group)[i]);
                    }
                }
                //Create group by
                @SuppressWarnings("unchecked") final GenericTuple<String>[] keys = new GenericTuple[size()];
                for (int i = 0; i < size(); ++i) {
                    keys[i] = keyCombinations[getRaw(group, i)][getRaw((colors.asA()), i)];
                }
                final GroupBy<GenericTuple<String>> lines = new GroupBy<>(keys);
                //create lines
                PlotShape.PolyLine[] pLines = new PlotShape.PolyLine[lines.numGroups()];
                int i = 0;
                for (final GroupBy.Group<GenericTuple<String>> group : lines) {
                    pLines[i++] = new PlotShape.PolyLine(this, group.getIndices().get(0), group.getIndices());
                }
                RTree<Runnable> l = new RTree<>();
                l.putAll(pLines);
                return l;
            }

        }

        protected final RTree<Runnable> createPolygons(PlotLayout plotLayout) {
            RTree<Runnable> polygons = new RTree<>();
            if (numAttributes() == 0) {
                final PlotShape.Polygon polygon = new PlotShape.Polygon(this, new IntArrayList(ArrayUtils.intRange(x.size())));
                polygon.i = 0;
                polygons.put(polygon);
            } else {
                if (colors != null && colors.get() != null && colors.isA()) {
                    final PlotShape.Polygon[] p = new PlotShape.Polygon[colors.asA().categories.length];
                    for (int i = 0; i < colors.asA().categories.length; ++i) {
                        p[i] = new PlotShape.Polygon(this, new IntArrayList());
                    }
                    for (int i = 0; i < colors.asA().indices.length; ++i) {
                        p[colors.asA().indices[i]].ids.add(i);
                        if (p[colors.asA().indices[i]].i == -1) {
                            p[colors.asA().indices[i]].i = i;
                        }
                    }
                    polygons.putAll(p);
                }

            }

            return polygons;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected O clear() {
            if (layout != null) {
                layout.clear(this);
                init(layout);
            }
            return (O )this;
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
     * @param <O> the concrete type of the XY data
     */
    public static abstract class CategoricalData<O extends CategoricalData<O>> extends PlotData<O> {

        IntArrayList categories;
        DoubleArrayList values;
        double valueMin, valueMax;
        String categoryLabel, valueLabel;
        protected String[] categoryLabels;

        protected CategoricalData(final String[] categories, final double[] values) {
            init(categories, values);
        }

        protected CategoricalData(final DataFrame dataFrame, final String category, final String values) {
            Objects.requireNonNull(this.dataFrame = dataFrame);
            GroupBy<String> xyGrouping = new GroupBy<>(dataFrame.getStringSeries(category));
            init(xyGrouping.getGroups(new String[xyGrouping.numGroups()]), dataFrame.getDoubleSeries(values).toArray(new double[dataFrame.size(Axis.INDEX)]));
            categories = new IntArrayList(xyGrouping.toMeltedArray(new int[dataFrame.size(Axis.INDEX)]));
            categoryLabel = category;
            valueLabel = values;
        }

        protected CategoricalData(DataFrame dataFrame, String x, String[] y) {
            String[] categoryLabels = dataFrame.get(x).asString().toArray(new String[dataFrame.size(Axis.INDEX)]);
            this.dataFrame = dataFrame;
            int count = categoryLabels.length * y.length;
            this.categories = new IntArrayList(count);
            double[] values = new double[count];
            int[] colors = new int[count];
            int k = 0;
            for (int c = 0; c < y.length; ++c) {
                final DoubleSeries series = dataFrame.get(y[c]).asDouble();
                for (int row = 0; row < categoryLabels.length; ++row) {
                    values[k] = series.get(row);
                    this.categories.add(row);
                    colors[k++] = c;
                }
            }
            init(categoryLabels, values);
            setColors(new PlotTrace.Categorical(this, Attribute.COLOR, categoryLabels, colors));
            this.colors.asA().name = "variable";


        }

        @SuppressWarnings("unchecked")
        void init(String[] categories, double[] values) {
            valueMin = 0;
            valueMax = StatUtils.max(values);
            this.categoryLabels = categories;
            this.values = new DoubleArrayList(values);
            final Map<String, IntFunction<?>> formatters = new HashMap<>(2);
            formatters.put("x", this::getCategory);
            formatters.put("y", this::getValue);
            hoverFormatter = new HoverText<>((O) this, formatters);
            hoverFormatter.add("(%{x:s}, %{y:s})");
        }

        String getCategory(int i) {
            return categoryLabels[categories.get(i)];
        }

        double getValue(int i) {
            return values.get(i);
        }

        @Override
        protected int size() {
            return categories.size();
        }

        @Override
        protected void init(PlotLayout plotLayout) {
            putRectangles(layout, this, createRectangles(layout));

            updateXYBounds(plotLayout, 0, categoryLabels.length, valueMin, valueMax, true, true);
            plotLayout.getXAxis().showZeroLine = false;
            plotLayout.getYAxis().showZeroLine = false;

        }

        public String[] getCategoryLabels() {
            return categoryLabels;
        }

        @SuppressWarnings("unchecked")
        public O setColors(String series) throws DataFrameOnlyOperationException {
            final Series<?> s = getSeries(series);
            clear();
            if (s.getType() == DataType.DOUBLE) {
                setColors(new PlotTrace.Numeric(this, Attribute.COLOR, s));
                return (O) this;
            }
            setColors(new PlotTrace.Categorical(this, Attribute.COLOR, s));
            return (O) this;
        }

        protected final RTree<Runnable> createRectangles(PlotLayout layout) {


            //TODO other barmodes
            final RTree<Runnable> rectangles = new RTree<>();
            final PlotShape.Rectangle[] r = new PlotShape.Rectangle[size()];
            /* todo
            if (colors != null && colors.isA() && colors.asA().categories.length > 1) {
                double[] posOffset = new double[categoryLabels.length];
                double[] negOffset = new double[posOffset.length];
                for (int i = 0; i < size(); ++i) {
                    double v = values.get(i);
                    if (v < 0) {
                        negOffset[categories.get(i) ] -= v;
                    } else {
                        posOffset[categories.get(i) ] += v;
                    }
                }
                System.out.println(Arrays.toString(posOffset));


                double[][] posHeight = new double[colors.asA().categories.length][categoryLabels.length];
                double[][] negHeight = new double[colors.asA().categories.length][categoryLabels.length];
                for (int i = 0; i < size(); ++i) {
                    double v = values.get(i);
                    if (v < 0) {
                        r[i] = new PlotShape.Rectangle(this, i, categories.get(i) + .1, negOffset[colors.asA().getRaw(i)] + negHeight[colors.asA().getRaw(i)][categories.get(i)], .8, values.get(i));
                        negHeight[colors.asA().getRaw(i)][categories.get(i)] -= v;
                    } else {
                        r[i] = new PlotShape.Rectangle(this, i, categories.get(i) + .1, posOffset[colors.asA().getRaw(i)] + posHeight[colors.asA().getRaw(i)][categories.get(i)], .8, values.get(i));
                        posHeight[colors.asA().getRaw(i)][categories.get(i)] += v;
                    }
                }

                valueMin = StatUtils.min(negHeight[0]);
                valueMax = StatUtils.max(posHeight[0]);
            } else {*/
            double[] posHeight = new double[categoryLabels.length];
            double[] negHeight = new double[categoryLabels.length];
            for (int i = 0; i < size(); ++i) {
                double v = values.get(i);
                if (v < 0) {
                    r[i] = new PlotShape.Rectangle(this, i, categories.get(i) + .1, negHeight[categories.get(i)], .8, values.get(i));
                    negHeight[categories.get(i)] -= v;
                } else {
                    r[i] = new PlotShape.Rectangle(this, i, categories.get(i) + .1, posHeight[categories.get(i)], .8, values.get(i));
                    posHeight[categories.get(i)] += v;
                }
            }
            valueMin = StatUtils.min(negHeight);
            valueMax = StatUtils.max(posHeight);
            /*}*/
            rectangles.putAll(r);

            return rectangles;


        }


    }

    public static abstract class TabularData<O extends TabularData<O>> extends PlotData<O> {

        protected TabularData(DataFrame dataFrame) {
            this.dataFrame = dataFrame;
        }

        @Override
        protected int size() {
            return dataFrame.size();
        }

        public int numColumns() {
            return dataFrame.size(Axis.COLUMN);
        }

        public int numRows() {
            return dataFrame.size(Axis.INDEX);
        }

        public String getHeader(int col) {
            return dataFrame.get(col).getName();
        }
    }

    protected Figure figure;
    protected PlotLayout layout;
    protected String title = null;

    protected DataFrame dataFrame;
    protected String name;

    protected PlotTrace.Categorical group;
    private Map<Attribute, PlotTrace> attributes;
    protected PlotTrace opacities;
    protected SubVariant<PlotTrace.Categorical, PlotTrace.Numeric, PlotTrace> colors;
    protected double opacity = 1;

    protected Facets facets;

    protected HoverText<O> hoverFormatter;
    protected List<String> hoverText;

    boolean showScale = true;
    boolean showInLegend = true;
    boolean anySelected = false;
    BooleanArrayList selected = null;

    Colormap colormap;

    protected Color fillColor;


    protected Stroke lineStroke;
    protected Color lineColor;
    protected double lineWidth = 2;
    protected double[] lineDashes;

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
        if (colors == null || colors.get() == null || colors.isB()) {
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
        return (colors == null || isVisible(colors.get(), index)) && isVisible(opacities, index);
    }

    protected static boolean isVisible(final PlotTrace group, int index) {
        return group == null || group.isVisible(index);
    }

    protected final String getHoverText(int i) {
        if (hoverText == null && hoverFormatter != null) {
            hoverText = new ArrayList<>(size());
            for (int j = 0; j < size(); ++j) {
                hoverText.add(hoverFormatter.get(j));
            }
        }
        if (hoverText == null || hoverText.isEmpty()) {
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

    protected Colormap getSequentialColormap() {
        return this.colormap != null ? this.colormap : figure.sequentialColormap;

    }

    protected Colormap getColormap() {
        return this.colormap != null ? this.colormap : (colors != null && colors.isB()) ? figure.sequentialColormap : figure.qualitativeColormap;
    }

    protected Color getDefaultColor(int i) {
        if (colors == null) {
            return getDefaultColor();
        }
        if (((List<?>) defaultColor) == Collections.emptyList()) {
            defaultColor = new ArrayList<>(size());
            final Colormap colormap = getColormap();
            for (int j = 0; j < size(); ++j) {
                defaultColor.add(opacity == 1 ? colors.get().get(colormap, j) : new Color(colors.get().get(colormap, j).red(), colors.get().get(colormap, j).green(), colors.get().get(colormap, j).blue(), opacity));
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

    protected void setColors(final PlotTrace.Categorical trace) {
        if (colors == null) {
            this.colors = SubVariant.ofA(addAttribute(trace));
        } else {
            this.colors.setToA(addAttribute(trace));
        }
        addToHoverText(this.colors.asA(), "%s=%{color:s}", () -> this.colors.get().getName(), "color", this.colors.asA()::get);
    }

    protected void setColors(final PlotTrace.Numeric trace) {
        if (colors == null) {
            this.colors = SubVariant.ofB(addAttribute(trace));
        } else {
            colors.setToB(addAttribute(trace));
        }
        addToHoverText(colors.asB(), "%s=%{color:.s}", () -> colors.get().getName(), "color", this.colors.asB()::getRaw);
        trace.scaleMin = 0;
        trace.scaleMax = 1;
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
    @SuppressWarnings("unchecked")
    protected O clear(){
        return (O) this;
    }

    /**
     * Performed actions when the data is added to a layout
     *
     * @param plotLayout the layout
     */
    protected abstract void init(PlotLayout plotLayout);

    final boolean showsColorBar() {
        return showScale & colors != null && (colors.isB());
    }

    void layoutColorBar(Renderer<?> source, ColorScales scales) {
        colors.asB().layoutColorBar(source, scales);

    }

    void drawColorBar(Renderer<?> source, ChartCanvas<?> canvas, ColorScales scales) {
        colors.asB().drawColorBar(source, canvas, scales);

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
    protected static void updateXYBounds(final PlotLayout plotLayout, double xMin, double xMax, double yMin, double yMax, boolean xUseNice, boolean yUseNice) {
        plotLayout.getXAxis().dataLower = Math.min(plotLayout.getXAxis().dataLower, xMin);
        plotLayout.getYAxis().dataLower = Math.min(plotLayout.getYAxis().dataLower, yMin);
        plotLayout.getXAxis().dataUpper = Math.max(plotLayout.getXAxis().dataUpper, xMax);
        plotLayout.getYAxis().dataUpper = Math.max(plotLayout.getYAxis().dataUpper, yMax);
        plotLayout.getXAxis().reset(xUseNice);
        plotLayout.getYAxis().reset(yUseNice);


    }

    protected static void updateXYBounds(PlotLayout plotLayout, RTree<Runnable> rectangles, boolean xUseNice, boolean yUseNice) {
        plotLayout.getXAxis().dataLower = Math.min(plotLayout.getXAxis().dataLower, rectangles.getMinX());
        plotLayout.getYAxis().dataLower = Math.min(plotLayout.getYAxis().dataLower, rectangles.getMinY());
        plotLayout.getXAxis().dataUpper = Math.max(plotLayout.getXAxis().dataUpper, rectangles.getMaxX());
        plotLayout.getYAxis().dataUpper = Math.max(plotLayout.getYAxis().dataUpper, rectangles.getMaxY());
        plotLayout.getXAxis().reset(xUseNice);
        plotLayout.getYAxis().reset(yUseNice);
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

    public static abstract class DistributionData<O extends DistributionData<O>> extends PlotData<O> {
        protected DoubleArrayList values;
        protected double valMin, valMax;
        protected String yLabel = COUNT_LABEL;
        String xLabel;
        protected static final String COUNT_LABEL = "Count";
        protected static final String DENSITY_LABEL = "Density";

        protected DistributionData(final DataFrame dataFrame, final String y) {
            this.dataFrame = dataFrame;
            init(dataFrame.getDoubleSeries(y).toArray(new double[dataFrame.size(Axis.INDEX)]));
            xLabel = y;
        }

        protected DistributionData(final double[] values) {
            init(values);
        }

        protected void init(double[] values) {
            this.values = new DoubleArrayList(values);
            valMin = StatUtils.NaNMin(values);
            valMax = StatUtils.NaNMax(values);
        }

        @Override
        protected int size() {
            return values.size();
        }

        public double getMin() {
            return valMin;
        }

        public double getMax() {
            return valMax;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected O clear() {
return (O)this;
        }


        protected final RTree<Runnable> createRectangles(PlotLayout layout, Histogram histogram, boolean useDensity) {
            final double binWidth = (histogram.getBinEdges()[histogram.getCount().length] - histogram.getBinEdges()[0]) / histogram.getCount().length;
            final long n = sum(histogram.getCount());
            final double denom;
            if (useDensity) {
                denom = 1 / binWidth / n;
            } else {
                denom = 1;
            }
            final RTree<Runnable> rectangles = new RTree<>();
            final PlotShape.Rectangle[] r = new PlotShape.Rectangle[histogram.numBins()];
            double width = histogram.getBinEdges()[1] - histogram.getBinEdges()[0];
            for (int i = 0; i < histogram.numBins(); ++i) {
                r[i] = new PlotShape.Rectangle(this, i, histogram.getBinEdges()[i], 0, width, histogram.getCount(i) * denom);
            }
            rectangles.putAll(r);

            return rectangles;


        }

        protected final RTree<Runnable> createLines(PlotLayout layout, double[] xs, double[] ys) {
            final RTree<Runnable> lines = new RTree<>();
            final PlotShape.PolyLine line = new PlotShape.PolyLine(this, xs, ys);
            lines.put(line);
            return lines;
        }
    }

    public static abstract class DistributionData2D<O extends DistributionData2D<O>> extends PlotData<O> {
        protected DoubleArrayList x, y;
        double[] count;
        protected double[] xEdges, yEdges;

        String xLabel, yLabel;
        protected double xMin, xMax, yMin, yMax;

        public DistributionData2D(DataFrame dataFrame, String x, String y) {
            Objects.requireNonNull(this.dataFrame = dataFrame);
            init(dataFrame.getDoubleSeries(x).toArray(new double[dataFrame.size(Axis.INDEX)]), dataFrame.getDoubleSeries(y).toArray(new double[dataFrame.size(Axis.INDEX)]));
            xLabel = x;
            yLabel = y;
        }

        @SuppressWarnings("unchecked")
        protected void init(double[] x, double[] y) {
            xMin = StatUtils.min(x);
            xMax = StatUtils.max(x);
            yMin = StatUtils.min(y);
            yMax = StatUtils.max(y);
            this.x = new DoubleArrayList(x);
            this.y = new DoubleArrayList(y);
            final Map<String, IntFunction<?>> formatters = new HashMap<>(2);
            //formatters.put("ybin", i->String.format("%s",getYEdges()[i%(getXEdges().length)]));
            //formatters.put("y", this.y::get);
            formatters.put("count", i -> (int) this.getCounts()[i]);
            hoverFormatter = new HoverText<>((O) this, formatters);

            //hoverFormatter.add("(%{x:s}, %{y:s})");
        }

        protected void addCountToHoverText() {
            hoverFormatter.add("(count=%{count:d})");
        }

        @Override
        protected int size() {
            return getCounts().length;
        }

        protected double[] getXEdges() {
            if (xEdges == null) {
                double[] xs = x.toArray();
                xEdges = StatUtils.histogramBinEdges(BinWidthEstimator.NUMPY_AUTO, xs);
            }
            return xEdges;
        }

        protected double[] getYEdges() {
            if (yEdges == null) {
                double[] ys = y.toArray();
                yEdges = StatUtils.histogramBinEdges(BinWidthEstimator.NUMPY_AUTO, ys);
            }
            return yEdges;
        }

        double[] getCounts() {
            if (count == null) {
                int xBins = getXEdges().length;
                int yBins = getYEdges().length;
                double xMin = xEdges[0],
                        xMax = xEdges[xEdges.length - 1],
                        yMin = yEdges[0],
                        yMax = yEdges[yEdges.length - 1];
                double dx = (xMax - xMin) / (xBins - 1),
                        dy = (yMax - yMin) / (yBins - 1);
                count = new double[xBins * yBins];
                for (int i = 0; i < x.size(); ++i) {
                    int _x = (int) ((x.get(i) - xMin) / dx);
                    int _y = (int) ((y.get(i) - yMin) / dy);
                    ++count[_x * yBins + _y];
                }
            }
            return count;
        }

        protected static RTree<Runnable> createRectangles(DistributionData2D<?> data) {
            final RTree<Runnable> rectangles = new RTree<>();
            PlotShape.Rectangle[] rs = new PlotShape.Rectangle[data.getCounts().length];
            double width = data.getXEdges()[1] - data.getXEdges()[0];
            double height = data.getYEdges()[1] - data.getYEdges()[0];
            for (int i = 0; i < data.getXEdges().length; ++i) {
                for (int j = 0, k = i * data.getYEdges().length; j < data.getYEdges().length; ++j, ++k) {
                    rs[k] = new PlotShape.Rectangle(data, k, data.getXEdges()[i], data.getYEdges()[j], width, height);
                }
            }
            data.setColors(new PlotTrace.Numeric(data, Attribute.COLOR, "count", data.count));
            rectangles.putAll(rs);
            return rectangles;
        }

        protected static RTree<Runnable> createRectangles(DistributionData2D<?> density2D, double[] densities, int width, double xMin, double yMin, double w, double h) {
            final RTree<Runnable> rectangles = new RTree<>();
            int height = densities.length / width;
            PlotShape.Rectangle[] rs = new PlotShape.Rectangle[densities.length];
            for (int y = 0, i = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    rs[i] = new PlotShape.Rectangle(density2D, i, xMin + x * w, yMin + y * h, w, h);
                    ++i;
                }
            }
            rectangles.putAll(rs);

            return rectangles;
        }

        protected static RTree<Runnable> createContours(DistributionData2D<?> density2D, double[] kdes, double minDensity, double maxDensity, int xBins, double xMin, double yMin, double cellWidth, double cellHeight) {
            final RTree<Runnable> polygons = new RTree<>();
            final double[] isolines = linearlySpaced(minDensity * .1, maxDensity * 1.1, 100);
            for (double threshold : isolines) {
                final List<PlotShape.Polygon.PlotPoint> points = new LinkedList<>();
                MarchingSquares.marchingSquares(kdes, xBins, threshold, (x, y) -> points.add(new PlotShape.Polygon.PlotPoint(xMin + x * cellWidth, yMin + y * cellHeight)));
                PlotShape.Polygon polygon = new PlotShape.Polygon(density2D, -1, points.toArray(new PlotShape.Polygon.PlotPoint[0]));
                polygons.put(polygon);
            }
            return polygons;
        }


    }

    Stroke getLineStroke() {
        if (lineStroke == null) {
            lineStroke = new Stroke(lineWidth, lineDashes);
        }
        return lineStroke;
    }

    protected void putFormatter(String key, IntFunction<?> getter) {
        hoverFormatter.put(key, getter);
    }
}
