package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.graphics.shapes.Marker;
import net.mahdilamb.charts.rtree.Node2D;
import net.mahdilamb.charts.rtree.PointNode;
import net.mahdilamb.charts.rtree.RTree;
import net.mahdilamb.charts.rtree.RectangularNode;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.dataframe.Axis;
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.DataType;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataframe.utils.IntArrayList;
import net.mahdilamb.statistics.ArrayUtils;
import net.mahdilamb.statistics.StatUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static net.mahdilamb.charts.functions.Utils.EMPTY_RUNNABLE;
import static net.mahdilamb.charts.utils.StringUtils.EMPTY_STRING;

public abstract class PlotData<O extends PlotData<O>> {

    protected enum Attribute {
        COLOR,
        OPACITY,
        SHAPE,
        SIZE
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

        public int i;
        public XYData<?> parent;

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        IntArrayList ids;
        private Segment[] segs;

        protected PlotPolyLine(XYData<?> parent, IntArrayList ids, Runnable data) {
            super(data);
            this.parent = parent;
            this.ids = ids;
            this.i = ids.size() == 0 ? -1 : ids.get(0);
        }

        protected PlotPolyLine(XYData<?> parent, IntArrayList ids) {
            this(parent, ids, EMPTY_RUNNABLE);
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

    protected Figure figure;
    protected PlotLayout plot;
    protected String title = null;

    protected DataFrame dataFrame;
    protected String name;

    protected Trace.Categorical group;
    private Map<Attribute, Trace> attributes;
    protected Trace colors, opacities;

    protected Facets facets;

    protected HoverText<O> hoverFormatter;
    protected List<String> hoverText;

    boolean showScale = true;
    boolean showInLegend = true;

    Colormap colormap;

    protected abstract int size();

    protected boolean isVisible(int index) {
        return isVisible(colors, index) && isVisible(opacities, index);
    }

    protected static boolean isVisible(final Trace group, int index) {
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

    protected Color getColor(int i) {
        if (colors != null && i !=-1) {
            final Colormap colormap = this.colormap != null ? this.colormap : (colors instanceof Trace.Numeric) ? figure.sequentialColormap : figure.qualitativeColormap;
            return colors.get(colormap, i);
        }
        return figure.qualitativeColormap.get(0);
    }

    protected MarkerShape getShape(int i) {
        return MarkerShape.CIRCLE;
    }

    protected void addToHoverText(final Trace trace, String formatting, Supplier<?> supplier, String key, IntFunction<?> getter) {
        trace.defaultSeg = hoverFormatter.add(formatting, supplier);
        hoverFormatter.put(key, getter);

        boolean found = false;
        for (final Map.Entry<Attribute, Trace> t : attributes()) {
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

    protected void removeAttribute(final Attribute attribute) {
        if (attributes == null) {
            return;
        }
        final Trace tg = attributes.get(attribute);
        //TODO deal with removing linked attributes
        if (tg != null) {
            hoverFormatter.remove(tg.defaultSeg);
            attributes.remove(attribute);
        }
    }

    protected <T extends Trace> T addAttribute(final Attribute attribute, T traceGroup) {
        if (attributes == null) {
            attributes = new LinkedHashMap<>(Attribute.values().length);
        }
        removeAttribute(attribute);
        for (final Map.Entry<Attribute, Trace> a : attributes()) {

            Trace candidate = a.getValue();
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
        attributes.put(attribute, traceGroup);
        return traceGroup;
    }

    protected int numAttributes() {
        return attributes == null ? 0 : attributes.size();
    }

    protected Iterable<Map.Entry<Attribute, Trace>> attributes() {
        if (attributes == null) {
            return () -> new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Map.Entry<Attribute, Trace> next() {
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

    public Renderer<?> show() {
        if (figure == null) {
            figure = new Figure();
            figure.addTrace(this);
        }
        return figure.show();
    }

    public <T extends Renderer<?>> T show(Function<Figure, T> creator) {
        if (figure == null) {
            figure = new Figure();
            figure.addTrace(this);
        }
        return creator.apply(figure);
    }

    @SuppressWarnings("unchecked")
    public O setName(String name) {
        this.name = name;
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
        if (colors == null || colors instanceof Trace.Numeric) {
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
        if (plot != null) {
            if (plot instanceof List) {
                for (final PlotLayout p : ((List<PlotLayout>) plot)) {
                    p.setTitle(title);
                }
            } else {
                plot.setTitle(title);
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

    abstract void clear();

    abstract void init(PlotLayout plotLayout);

    public void layoutLegendItems(Legend legend, Renderer<?> source, double lineHeight) {
        if (!showInLegend) {
            return;
        }
        if (numAttributes() == 0) {
            if (name == null) {
                return;
            }
            legend.sizeY += lineHeight;
            legend.sizeX = Math.max(legend.sizeX, getSize(-1) + source.getTextWidth(legend.itemFont, name));

        } else {
            //TODO
            for (final Map.Entry<Attribute, Trace> attr : attributes()) {
                if (attr.getValue() != null && !attr.getValue().showInLegend) {
                    continue;
                }

                if (attr.getValue() instanceof Trace.Categorical) {
                    System.out.println(attr.getValue());
                } else if (attr.getValue() instanceof Trace.Numeric) {
                    System.out.println(attr.getValue());
                } else {
                    System.out.println(attr.getValue() == null ? null : attr.getValue().getClass());
                }
            }
        }
    }


    void drawLegendItems(Legend legend, Renderer<?> source, ChartCanvas<?> canvas, double lineHeight, double baseOffset) {
        if (!showInLegend) {
            return;
        }
        if (numAttributes() == 0) {
            if (name == null) {
                return;
            }
            if (legend.orientation == Orientation.VERTICAL) {
                double y = legend.posY;
                canvas.setFill(Color.BLACK);
                canvas.fillText(name, getSize(-1) + legend.posX, y + baseOffset);
                canvas.setFill(getColor(-1));
                Marker.MARKER.shape = getShape(-1);
                Marker.MARKER.size = getSize(-1);
                Marker.MARKER.x = legend.posX + Marker.MARKER.size * .5;
                Marker.MARKER.y = legend.posY + .5 * lineHeight;
                Marker.MARKER.fill(canvas);
            } else {
                throw new UnsupportedOperationException();
            }


        } else {
            if (legend.orientation == Orientation.VERTICAL) {
                if (name != null){
                    double y = legend.posY;
                    canvas.setFill(Color.BLACK);
                    canvas.fillText(name, getSize(-1) + legend.posX, y + baseOffset);
                    canvas.setFill(getColor(-1));
                    Marker.MARKER.shape = getShape(-1);
                    Marker.MARKER.size = getSize(-1);
                    Marker.MARKER.x = legend.posX + Marker.MARKER.size * .5;
                    Marker.MARKER.y = legend.posY + .5 * lineHeight;
                    Marker.MARKER.fill(canvas);
                }

            } else {
                throw new UnsupportedOperationException();
            }
            /*for (final Map.Entry<Attribute, Trace> attr : attributes()) {
                if (attr.getValue() != null && !attr.getValue().showInLegend) {
                    continue;
                }


                if (attr.getValue() instanceof Trace.Categorical) {
                    System.out.println(attr.getValue());
                } else if (attr.getValue() instanceof Trace.Numeric) {
                    System.out.println(attr.getValue());
                } else {
                    System.out.println(attr.getValue() == null ? null : attr.getValue().getClass());
                }
            }*/
        }
    }

    void drawColorScales() {
        if (showScale & colors != null & (colors instanceof Trace.Numeric)) {
            System.out.printf("%s {min: %s, max: %s}%n", colormap, ((Trace.Numeric) colors).valMin, ((Trace.Numeric) colors).valMax);
        }
    }

    @SuppressWarnings("unchecked")
    O updateTrace(final Trace trace, Consumer<Trace> apply) {
        apply.accept(trace);
        return (O) this;
    }

    @SuppressWarnings("unchecked")
    public O updateTrace(final String name, Consumer<Trace> fn) {
        for (final Map.Entry<Attribute, Trace> a : attributes()) {
            if (name.equals(a.getValue().name)) {
                return updateTrace(a.getValue(), fn);
            }
        }
        return (O) this;
    }

    public static final class HoverText<O extends PlotData<O>> {

        public static class Segment {
            String formatting;
            final Supplier<?>[] suppliers;

            Segment(final String formatting, final Supplier<?>[] suppliers) {
                this.formatting = formatting;
                this.suppliers = suppliers;
                int args = 0;
                int i = 0;
                //make sure arguments match
                while (i < formatting.length()) {
                    final char c = formatting.charAt(i);
                    if (c == '%') {
                        int j = i + 1;
                        if (i + 1 < formatting.length() && (formatting.charAt(j) != '%')) {
                            if (formatting.charAt(j) == '{') {
                                while (formatting.charAt(i) != '}') {
                                    ++i;
                                }
                                ++i;
                                continue;
                            } else {
                                ++args;
                            }
                        }
                    }
                    ++i;
                }
                if (args != suppliers.length) {
                    throw new IllegalArgumentException("Not enough arguments for formatting");
                }
            }

            @Override
            public String toString() {
                return String.format("HoverSegment [%s]", formatting);
            }
        }

        private static final String DEFAULT_JOIN = ";";

        private final List<Segment> segments = new LinkedList<>();
        private final Map<String, IntFunction<?>> formatters;
        private final StringBuilder sb = new StringBuilder();
        private final String join;
        private final O plotData;

        public HoverText(final O plotData, final Map<String, IntFunction<?>> formatters, String join) {
            this.formatters = formatters;
            this.plotData = plotData;
            this.join = join == null ? DEFAULT_JOIN : join;
        }

        public HoverText(final O plotData, final Map<String, IntFunction<?>> formatters) {
            this(plotData, formatters, ";");
        }

        void put(String key, IntFunction<?> getter) {
            formatters.put(key, getter);
        }

        public Segment add(final String formatting, Supplier<?>... suppliers) {
            final Segment segment = new Segment(formatting, suppliers);
            segments.add(segment);
            clear();
            return segment;
        }

        private void clear() {
            plotData.hoverText = null;
        }

        public void remove(Segment seg) {
            segments.remove(seg);
            clear();
        }

        void setFormatting(final String key, final String formatting) {
            for (final Segment segment : segments) {
                int i = 0;
                final int j = segment.formatting.length() - 1;
                while (i < j) {
                    final char c = segment.formatting.charAt(i);
                    if (c == '%') {
                        int k = i + 1;
                        int l = 0;
                        if (segment.formatting.charAt(k) == '{') {
                            boolean found = true;
                            while (l < key.length()) {
                                if (key.charAt(l) != segment.formatting.charAt(k + l + 1)) {
                                    found = false;
                                    break;
                                }
                                ++l;
                            }
                            if (found) {
                                int m = k + 2 + key.length();
                                int n = m;
                                while (segment.formatting.charAt(n) != '}') {
                                    ++n;
                                }
                                segment.formatting = String.format("%s%s%s", segment.formatting.substring(0, m), formatting.substring(1), segment.formatting.substring(n));
                                clear();
                                return;
                            }
                        }
                    }
                    ++i;
                }
            }
        }

        public String get(int i) {
            sb.setLength(0);
            for (final Segment segment : segments) {
                final int j = segment.formatting.length() - 1;
                int k = 0, o = 0;
                while (k < j) {
                    final char c = segment.formatting.charAt(k);
                    if (c != '%') {
                        sb.append(c);
                    } else {
                        int a = k + 1;
                        if (segment.formatting.charAt(a) != '%') {
                            int b = a;
                            if (segment.formatting.charAt(a) == '{') {
                                int w = b;
                                while (segment.formatting.charAt(b) != '}') {
                                    if (segment.formatting.charAt(b) == ':') {
                                        w = b;
                                    }
                                    ++b;
                                }
                                final String name = segment.formatting.substring(k + 2, w);
                                final String formatting = String.format("%%%s", segment.formatting.substring(w + 1, b));
                                final IntFunction<?> fn = formatters.get(name);
                                if (fn == null) {
                                    throw new UnsupportedOperationException("Could not find attribute with the name " + name);
                                }
                                sb.append(String.format(formatting, fn.apply(i)));

                            } else {
                                while (!StringUtils.isFormatSpecifier(segment.formatting.charAt(b))) {
                                    ++b;
                                }
                                sb.append(String.format(segment.formatting.substring(k, b + 1), segment.suppliers[o++].get()));
                            }
                            k = b + 1;
                            continue;
                        }
                    }
                    ++k;
                }
                if (k != segment.formatting.length()) {
                    sb.append(segment.formatting.charAt(j));
                }
                sb.append(join);
            }
            sb.setLength(sb.length() - join.length());
            return sb.toString();
        }
    }

    //TODO colwrap
    protected static final class Facets {
        private static final String DEFAULT_TITLE_FORMAT = "%s = %s";
        private static final String DEFAULT_JOIN = " | ";
        PlotLayout[][] plots;
        public Map<PlotLayout, IntArrayList> key = Collections.emptyMap();
        GroupBy<?> cols, rows;
        String colName, rowName;
        String formatTitle = DEFAULT_TITLE_FORMAT;
        String divider = DEFAULT_JOIN;

        public Facets() {

        }

        void setCols(final String name, GroupBy<?> group) {
            plots = null;
            key = Collections.emptyMap();
            this.colName = name;
            this.cols = group;
        }

        void setRows(final String name, GroupBy<?> group) {
            plots = null;
            key = Collections.emptyMap();
            this.rowName = name;
            this.rows = group;
        }

        @Override
        public String toString() {
            return String.format(
                    "Facets {%s%s}",
                    cols == null ? EMPTY_STRING : String.format("cols=%s", colName),
                    rows == null ? EMPTY_STRING : String.format(", rows=%s", rowName));
        }
    }

    public abstract static class XYData<O extends XYData<O>> extends PlotData<O> {

        private final DoubleArrayList x, y;
        double xMin, xMax, yMin, yMax;

        Stroke lineStroke;
        Color lineColor;
        double lineWidth = 1;
        double[] lineDashes;

        FillMode fillMode = FillMode.NONE;
        Color fillColor;

        protected String xLab, yLab;
        String[] xLabels;

        @SuppressWarnings("unchecked")
        public XYData(double[] x, double[] y) {
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

        @SuppressWarnings("unchecked")
        public XYData(String[] x, double[] y) {
            this.x = new DoubleArrayList(ArrayUtils.range(x.length));
            this.y = new DoubleArrayList(y);
            if (this.x.size() != this.y.size()) {
                throw new IllegalArgumentException("X and Y must be the same size");
            }
            this.xLabels = x;
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

        public XYData(final DataFrame dataFrame, final String x, final String y) {
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
            if (plot != null) {
                return plot.getXAxis().getTitle();
            }
            return xLab;
        }

        public final String getYLabel() {
            if (plot != null) {
                return plot.getYAxis().getTitle();
            }
            return yLab;
        }

        final String getXLabel(int i) {
            if (plot != null) {
                return plot.getXAxis().labels[i];
            }
            return xLabels[i];
        }

        @SuppressWarnings("unchecked")
        public final O setXLabel(final String label) {
            if (plot != null) {
                plot.getXAxis().setTitle(label);
            } else {
                this.xLab = label;
            }
            return (O) this;
        }

        @SuppressWarnings("unchecked")
        public final O setYLabel(final String label) {
            if (plot != null) {
                plot.getYAxis().setTitle(label);
            } else {
                this.yLab = label;
            }
            return (O) this;
        }

        @SuppressWarnings("unchecked")
        public O setFill(final FillMode mode) {
            this.fillMode = mode;
            clear();
            return (O) this;
        }

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
            if (numAttributes() == 0) {
                final PlotPolyLine line = new PlotPolyLine(this, new IntArrayList(ArrayUtils.intRange(x.size())));
                line.i = 0;
                lines.put(line);
            } else {
                if (colors != null && colors.getClass() == Trace.Categorical.class) {
                    final PlotPolyLine[] l = new PlotPolyLine[((Trace.Categorical) colors).categories.length];
                    for (int i = 0; i < ((Trace.Categorical) colors).categories.length; ++i) {
                        l[i] = new PlotPolyLine(this, new IntArrayList());
                    }
                    for (int i = 0; i < ((Trace.Categorical) colors).indices.length; ++i) {
                        l[((Trace.Categorical) colors).indices[i]].ids.add(i);
                        if (l[((Trace.Categorical) colors).indices[i]].i == -1) {
                            l[((Trace.Categorical) colors).indices[i]].i = i;
                        }
                    }
                    lines.putAll(l);
                }
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
                if (colors != null && colors.getClass() == Trace.Categorical.class) {
                    final PlotPolygon[] p = new PlotPolygon[((Trace.Categorical) colors).categories.length];
                    for (int i = 0; i < ((Trace.Categorical) colors).categories.length; ++i) {
                        p[i] = new PlotPolygon(this, new IntArrayList());
                    }
                    for (int i = 0; i < ((Trace.Categorical) colors).indices.length; ++i) {
                        p[((Trace.Categorical) colors).indices[i]].ids.add(i);
                        if (p[((Trace.Categorical) colors).indices[i]].i == -1) {
                            p[((Trace.Categorical) colors).indices[i]].i = i;
                        }
                    }
                    polygons.putAll(p);
                }

            }

            return polygons;
        }

        @Override
        protected void clear() {
            if (plot != null) {
                plot.clear(this);
            }
        }

        protected static double[] toArray(final DataFrame dataFrame, final String seriesName) {
            if (!DataType.isNumeric(dataFrame.getType(seriesName))) {
                throw new UnsupportedOperationException("Series must be numeric");
            }
            return dataFrame.getDoubleSeries(seriesName).toArray(new double[dataFrame.size(Axis.INDEX)]);
        }

    }

}
