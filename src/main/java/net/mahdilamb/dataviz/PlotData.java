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
import net.mahdilamb.dataviz.utils.rtree.Node2D;
import net.mahdilamb.dataviz.utils.rtree.PointNode;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.dataviz.utils.rtree.RectangularNode;
import net.mahdilamb.statistics.ArrayUtils;
import net.mahdilamb.statistics.StatUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static net.mahdilamb.dataviz.utils.Functions.EMPTY_RUNNABLE;
import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;

public abstract class PlotData<O extends PlotData<O>> implements FigureComponent<O> {


    protected enum Attribute {
        COLOR,
        OPACITY,
        SHAPE,
        SIZE,
        GROUP
    }

    interface PlotShape {
        Comparator<Node2D<Runnable>> ORDER_COMPARATOR = new Comparator<Node2D<Runnable>>() {
            @Override
            public int compare(Node2D<Runnable> o1, Node2D<Runnable> o2) {
                return Integer.compare(((PlotData.PlotShape) o1).i(), ((PlotData.PlotShape) o2).i());
            }
        };

        int i();

        PlotData<?> getSource();

        default Color getColor() {
            return getSource().getColor(i());
        }

        default double getSize() {
            return getSource().getSize(i());
        }

        default MarkerShape getShape() {
            return getSource().getShape(i());
        }

        default String getHoverText() {
            return getSource().getHoverText(i());
        }

        default boolean isVisible() {
            return getSource().isVisible(i());
        }

    }

    protected static final class PlotMarker extends PointNode<Runnable> implements PlotShape {

        public int i;
        public PlotData<?> parent;

        public PlotMarker(double x, double y, Runnable data) {
            super(x, y, data);
        }

        public PlotMarker(double x, double y) {
            this(x, y, EMPTY_RUNNABLE);
        }

        @Override
        public int i() {
            return i;
        }

        @Override
        public PlotData<?> getSource() {
            return parent;
        }

        @Override
        public boolean intersects(double minX, double minY, double maxX, double maxY) {
            if (minX == maxX && maxY == minY) {
                double a = getSize() * .5 / ((PlotLayout.Rectangular) parent.layout).x.scale;
                double b = getSize() * .5 / ((PlotLayout.Rectangular) parent.layout).y.scale;
                return RectangularNode.intersects(minX, minY, maxX, maxY, getMidX() - a, getMidY() - b, getMidX() + a, getMidY() + b);
            }
            return super.intersects(minX, minY, maxX, maxY);
        }

        @Override
        public String toString() {
            return String.format("PlotMarker {%s, %s}", ((XYData<?>) parent).getX(i), ((XYData<?>) parent).getY(i));
        }
    }

    protected static final class PlotPolyLine extends Node2D<Runnable> implements PlotShape {

        protected static final class Segment extends RectangularNode<Runnable> {
            PlotPolyLine line;
            double startX, startY, endX, endY;

            private Segment(double startX, double startY, double endX, double endY) {
                super(Math.min(startX, endX), Math.min(startY, endY), Math.max(startX, endX), Math.max(startY, endY));
                this.startX = startX;
                this.startY = startY;
                this.endX = endX;
                this.endY = endY;
            }
        }

        int i;
        public final XYData<?> parent;

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        IntArrayList ids;
        private Segment[] segs;

        public PlotPolyLine(XYData<?> parent, IntArrayList ids, Runnable data) {
            this(parent, ids.size() == 0 ? -1 : ids.get(0), ids, data);

        }

        public PlotPolyLine(XYData<?> parent, IntArrayList ids) {
            this(parent, ids.size() == 0 ? -1 : ids.get(0), ids, EMPTY_RUNNABLE);

        }

        public PlotPolyLine(XYData<?> parent, int i, IntArrayList ids) {
            this(parent, i, ids, EMPTY_RUNNABLE);

        }

        public PlotPolyLine(XYData<?> parent, int i, IntArrayList ids, Runnable data) {
            super(data);
            this.i = i;
            this.parent = parent;
            this.ids = ids;
        }

        @Override
        public int i() {
            return i;
        }

        @Override
        public PlotData<?> getSource() {
            return parent;
        }

        Segment[] getSegments() {
            if (segs == null) {
                minX = Double.POSITIVE_INFINITY;
                minY = Double.POSITIVE_INFINITY;
                maxX = Double.NEGATIVE_INFINITY;
                maxY = Double.NEGATIVE_INFINITY;
                if (ids.size() < 2) {
                    segs = new Segment[0];
                } else {
                    segs = new Segment[ids.size() - 1];
                    for (int i = 1, h = 0; i < ids.size(); h = i++) {
                        segs[h] = new Segment(parent.getX(ids.get(h)), parent.getY(ids.get(h)), parent.getX(ids.get(i)), parent.getY(ids.get(i)));
                        segs[h].line = this;
                        minX = Math.min(minX, segs[h].getMinX());
                        minY = Math.min(minY, segs[h].getMinY());
                        maxX = Math.max(maxX, segs[h].getMaxX());
                        maxY = Math.max(maxY, segs[h].getMaxY());
                    }
                }
            }
            return segs;
        }

        public double getStartX() {
            if (getSegments().length == 0) {
                return Double.NaN;
            }
            return getSegments()[0].startX;
        }

        public double getStartY() {
            if (getSegments().length == 0) {
                return Double.NaN;
            }
            return getSegments()[0].startY;
        }

        public double getEndX() {
            if (getSegments().length == 0) {
                return Double.NaN;
            }
            return getSegments()[getSegments().length - 1].endX;
        }

        public double getEndY() {
            if (getSegments().length == 0) {
                return Double.NaN;
            }
            return getSegments()[getSegments().length - 1].endY;
        }

        @Override
        public double getMinX() {
            getSegments();
            return minX;
        }

        @Override
        public double getMinY() {
            getSegments();
            return minY;
        }

        @Override
        public double getMaxX() {
            getSegments();
            return maxX;
        }

        @Override
        public double getMaxY() {
            getSegments();
            return maxY;
        }
    }

    protected static final class PlotPolygon extends Node2D<Runnable> implements PlotShape {
        protected static final class PlotPoint extends PointNode<Runnable> {

            public PlotPoint(double x, double y, Runnable data) {
                super(x, y, data);
            }

            public PlotPoint(double x, double y) {
                super(x, y, EMPTY_RUNNABLE);
            }

            @Override
            public String toString() {
                return String.format("Point {x: %s, y: %s}", getMidX(), getMidY());
            }
        }

        public int i;
        public XYData<?> parent;

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        IntArrayList ids;
        PlotPoint[] points;

        protected PlotPolygon(XYData<?> parent, IntArrayList ids, Runnable data) {
            super(data);
            this.parent = parent;
            this.ids = ids;
            this.i = ids.size() == 0 ? -1 : ids.get(0);
        }

        protected PlotPolygon(XYData<?> parent, IntArrayList ids) {
            this(parent, ids, EMPTY_RUNNABLE);
        }

        protected PlotPoint[] getPoints() {
            if (points == null) {
                minX = Double.POSITIVE_INFINITY;
                minY = Double.POSITIVE_INFINITY;
                maxX = Double.NEGATIVE_INFINITY;
                maxY = Double.NEGATIVE_INFINITY;

                switch (parent.fillMode) {
                    case NONE:
                        points = new PlotPoint[0];
                        break;
                    case TO_SELF:
                        points = new PlotPoint[ids.size() + 1];
                        for (int i : ids) {
                            points[i] = new PlotPoint(parent.getX(ids.get(i)), parent.getY(ids.get(i)));
                            minX = Math.min(minX, points[i].getMidX());
                            minY = Math.min(minY, points[i].getMidY());
                            maxX = Math.max(maxX, points[i].getMidX());
                            maxY = Math.max(maxY, points[i].getMidY());
                        }
                        points[ids.size()] = points[0];
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
            return points;
        }

        @Override
        public double getMinX() {
            getPoints();
            return minX;
        }

        @Override
        public double getMinY() {
            getPoints();
            return minY;
        }

        @Override
        public double getMaxX() {
            getPoints();
            return maxX;
        }

        @Override
        public double getMaxY() {
            getPoints();
            return maxY;
        }

        @Override
        public int i() {
            return i;
        }

        @Override
        public PlotData<?> getSource() {
            return parent;
        }

        @Override
        public String toString() {
            return String.format("Polygon {min=%s,%s, min=%s,%s, n=%s}", getMinX(), getMinY(), getMaxX(), getMaxY(), ids.size() + 1);
        }
    }

    public abstract static class XYData<O extends XYData<O>> extends PlotData<O> {

        protected final DoubleArrayList x, y;
        protected double xMin, xMax, yMin, yMax;

        protected Stroke lineStroke;
        protected Color lineColor = Color.white;
        protected double lineWidth = 2;
        protected double[] lineDashes;

        protected FillMode fillMode = FillMode.NONE;
        protected Color fillColor;

        protected String xLab = EMPTY_STRING, yLab = EMPTY_STRING;
        protected String[] xLabels;

        protected ScatterMode markerMode = ScatterMode.MARKER_ONLY;


        @SuppressWarnings("unchecked")
        protected XYData(double[] x, double[] y) {
            this.x = new DoubleArrayList(x);
            this.y = new DoubleArrayList(y);
            if (this.x.size() != this.y.size()) {
                throw new IllegalArgumentException("X and Y must be the same size");
            }
            xMin = StatUtils.min(x);
            xMax = StatUtils.max(x);
            yMin = StatUtils.min(y);
            yMax = StatUtils.max(y);
            final Map<String, IntFunction<?>> formatters = new HashMap<>(2);
            formatters.put("x", this.x::get);
            formatters.put("y", this.y::get);
            hoverFormatter = new HoverText<>((O) this, formatters);
            hoverFormatter.add("%s=%{x:s}", this::getXLabel);
            hoverFormatter.add("%s=%{y:s}", this::getYLabel);

        }

        protected XYData(double[] x, DoubleUnaryOperator y) {
            this(x, ArrayUtils.map(x, y));
        }

        protected XYData(double[] y) {
            this(ArrayUtils.range(y.length), y);
        }

        @SuppressWarnings("unchecked")
        protected XYData(String[] x, double[] y) {
            this.x = new DoubleArrayList(ArrayUtils.range(x.length));
            this.y = new DoubleArrayList(y);
            if (this.x.size() != this.y.size()) {
                throw new IllegalArgumentException("X and Y must be the same size");
            }
            this.xLabels = x;//TODO groupby and x should be indices
            xMin = 0;
            xMax = this.x.size() - 1;
            yMin = StatUtils.min(y);
            yMax = StatUtils.max(y);
            final Map<String, IntFunction<?>> formatters = new HashMap<>(2);
            formatters.put("x", this::getXLabel);
            formatters.put("y", this.y::get);
            hoverFormatter = new HoverText<>((O) this, formatters);
            hoverFormatter.add("%s=%{x:s}", this::getXLabel);
            hoverFormatter.add("%s=%{y:s}", this::getYLabel);
        }

        protected XYData(final DataFrame dataFrame, final String x, final String y) {
            //todo if x is string series
            this(toArray(dataFrame, x), toArray(dataFrame, y));
            this.dataFrame = dataFrame;
            this.xLab = x;
            this.yLab = y;
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
            return (O) this;
        }

        @SuppressWarnings("unchecked")
        public O setLineColor(Color color) {
            this.lineColor = color;
            return (O) this;
        }

        public O setLineColor(String color) {
            return setLineColor(StringUtils.convertToColor(color));
        }

        @SuppressWarnings("unchecked")
        public O setFillColor(Color color) {
            this.fillColor = color;
            return (O) this;
        }

        public O setFillColor(String color) {
            return setFillColor(StringUtils.convertToColor(color));
        }

        protected RTree<Runnable> createMarkers(PlotLayout plotLayout) {
            RTree<Runnable> markers = new RTree<>();
            final PlotMarker[] m;
            if (facets != null) {
                final IntArrayList ids = facets.key.get(plotLayout);
                m = new PlotMarker[ids.size()];
                int j = 0;
                for (int i : ids) {
                    PlotMarker marker = new PlotMarker(x.get(i), y.get(i));
                    marker.i = i;
                    marker.parent = this;
                    m[j++] = marker;
                }
            } else {
                m = new PlotMarker[size()];
                for (int i = 0; i < size(); ++i) {
                    PlotMarker marker = new PlotMarker(x.get(i), y.get(i));
                    marker.i = i;
                    marker.parent = this;
                    m[i] = marker;
                }
            }
            markers.putAll(m);

            return markers;
        }

        protected RTree<Runnable> createLines(PlotLayout plotLayout) {
            RTree<Runnable> lines = new RTree<>();
            if (colors != null && colors.getClass() == PlotTrace.Categorical.class) {
                final PlotPolyLine[] l = new PlotPolyLine[((PlotTrace.Categorical) colors).categories.length];
                for (int i = 0; i < ((PlotTrace.Categorical) colors).categories.length; ++i) {
                    l[i] = new PlotPolyLine(this, -1, new IntArrayList());
                }
                for (int i = 0; i < ((PlotTrace.Categorical) colors).indices.length; ++i) {
                    l[((PlotTrace.Categorical) colors).indices[i]].ids.add(i);
                    if (l[((PlotTrace.Categorical) colors).indices[i]].i == -1) {
                        l[((PlotTrace.Categorical) colors).indices[i]].i = i;
                    }
                }
                lines.putAll(l);
            } else {
                final PlotPolyLine line = new PlotPolyLine(this, 0, new IntArrayList(ArrayUtils.intRange(x.size())));
                lines.put(line);
            }
            return lines;
        }

        protected RTree<Runnable> createPolygons(PlotLayout plotLayout) {
            RTree<Runnable> polygons = new RTree<>();
            if (numAttributes() == 0) {
                final PlotPolygon polygon = new PlotPolygon(this, new IntArrayList(ArrayUtils.intRange(x.size())));
                polygon.i = 0;
                polygons.put(polygon);
            } else {
                if (colors != null && colors.getClass() == PlotTrace.Categorical.class) {
                    final PlotPolygon[] p = new PlotPolygon[((PlotTrace.Categorical) colors).categories.length];
                    for (int i = 0; i < ((PlotTrace.Categorical) colors).categories.length; ++i) {
                        p[i] = new PlotPolygon(this, new IntArrayList());
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

    /**
     * Update the bounds of an xy layout
     *
     * @param plotLayout the plot layout
     * @param xMin       the x min of the data
     * @param xMax       the x max of the data
     * @param yMin       the y min of the data
     * @param yMax       the y max of the data
     */
    protected static void updateXYBounds(final PlotLayout plotLayout, double xMin, double xMax, double yMin, double yMax) {
        plotLayout.getXAxis().dataLower = Math.min(plotLayout.getXAxis().dataLower, xMin);
        plotLayout.getYAxis().dataLower = Math.min(plotLayout.getYAxis().dataLower, yMin);
        plotLayout.getXAxis().dataUpper = Math.max(plotLayout.getXAxis().dataUpper, xMax);
        plotLayout.getYAxis().dataUpper = Math.max(plotLayout.getYAxis().dataUpper, yMax);
        plotLayout.getXAxis().reset(true);
        plotLayout.getYAxis().reset(true);
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
}
