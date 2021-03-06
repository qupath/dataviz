package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataframe.utils.Sorts;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.graphics.shapes.Marker;
import net.mahdilamb.dataviz.utils.rtree.Node2D;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.dataviz.utils.rtree.RectangularNode;

import java.util.*;

/**
 * Layouts for plot areas
 */
public abstract class PlotLayout extends Component implements Themeable<PlotLayout> {
    Figure figure;
    String title;
    final List<PlotData<?>> data = new LinkedList<>();
    protected Map<PlotData<?>, RTree<Runnable>> polygons = Collections.emptyMap();
    protected Map<PlotData<?>, RTree<Runnable>> lines = Collections.emptyMap();
    protected Map<PlotData<?>, RTree<Runnable>> markers = Collections.emptyMap();
    protected Map<PlotData<?>, RTree<Runnable>> rectangles = Collections.emptyMap();
    Color background = Color.lightgray;
    Style selectedStyle = SelectedStyle.DEFAULT_SELECTED_STYLE;
    Style unselectedStyle = UnselectedStyle.DEFAULT_UNSELECTED_STYLE;
    Renderer.Tooltip tooltip = new Renderer.Tooltip();

    protected static class XYLayout extends PlotLayout {
        final Axis x, y;

        public XYLayout() {
            this(new Axis.XAxis(), new Axis.YAxis());
        }

        public XYLayout(Axis x, Axis y) {
            this.x = x;
            this.y = y;

        }

        @Override
        protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
            setBoundsFromExtent(minX, minY, maxX, maxY);
            if (title != null) {
                //TODO deal with title
            }
            y.layout(source, this, minX, minY, maxX, maxY);
            x.layout(source, this, minX, minY, maxX, maxY);
            sizeY = y.sizeY = maxY - minY - x.sizeY;
            x.posX = minX + y.sizeX;
            sizeX = x.sizeX = maxX - x.posX;
            posX = y.posX + y.sizeX;
            posY = y.posY;
            x.updateScale();
            y.updateScale();
        }

        @Override
        protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {

            canvas.setFill(background);
            canvas.fillRect(x.posX, y.posY, x.sizeX, y.sizeY);
            x.drawGrid(source, canvas, y);
            y.drawGrid(source, canvas, x);
            x.draw(source, canvas, y);
            y.draw(source, canvas, x);
            canvas.setClip(ClipShape.RECTANGLE, x.posX, y.posY, x.sizeX, y.sizeY);
          /*  if (title != null) {
                System.out.println(">> Title <<");
                System.out.println(title);
            }

            */

            for (final PlotData<?> trace : data) {
                PlotTrace.Numeric size = (PlotTrace.Numeric) trace.getAttribute(PlotData.Attribute.SIZE);
                double b = size == null ? trace.getSize(-1) : size.scaleMax;
                double bx = b / x.scale,
                        by = b / y.scale;
                double minX = x.lower - bx,
                        minY = y.lower - by,
                        maxX = x.upper + bx,
                        maxY = y.upper + by;

                if (trace instanceof PlotData.RelationalData) {
                    if (((PlotData.RelationalData<?>) trace).fillMode != FillMode.NONE) {
                        canvas.setFill(((PlotData.RelationalData<?>) trace).fillColor);
                        @SuppressWarnings("unchecked") final Set<PlotShape.PlotPolygon> foundPolygons = (Set<PlotShape.PlotPolygon>) polygons.getOrDefault(trace, RTree.emptyTree()).search(new TreeSet<>(PlotShape.ORDER_COMPARATOR), minX, minY, maxX, maxY);
                        for (final PlotShape.PlotPolygon n : foundPolygons) {
                            drawPolygon(canvas, n);
                        }
                    }
                    //canvas.setStroke(((PlotData.XYData<?>) trace).getLineStroke(), ((PlotData.XYData<?>) trace).lineColor);
                }
                @SuppressWarnings("unchecked") final Collection<PlotShape.PlotPolyLine> foundLines = (Collection<PlotShape.PlotPolyLine>) lines.getOrDefault(trace, RTree.emptyTree()).search(minX, minY, maxX, maxY);
                for (final PlotShape.PlotPolyLine n : foundLines) {
                    canvas.setStroke(n.getColor());
                    drawLine(canvas, n);
                }
                if (trace.anySelected) {
                    for (final Node2D<Runnable> n : markers.getOrDefault(trace, RTree.emptyTree()).search(minX, minY, maxX, maxY)) {
                        final PlotShape p = (PlotShape) n;
                        if (!p.isVisible()) {
                            continue;
                        }
                        if (p instanceof PlotShape.PlotMarker) {
                            final Color color = p.getColor() == null ? p.parent.getColor(-1) : p.getColor();
                            boolean selected = trace.selected.get(p.i);
                            canvas.setFill(new Color(color.red(), color.green(), color.blue(), selected ? .8 : 0.2));
                            Marker.MARKER.x = ((PlotData.RelationalData<?>) p.parent).getX(p.i);
                            Marker.MARKER.y = ((PlotData.RelationalData<?>) p.parent).getY(p.i);
                            Marker.MARKER.shape = p.getShape();
                            Marker.MARKER.size = p.getSize();
                            transformMarker(Marker.MARKER);
                            Marker.MARKER.fill(canvas);
                            if (selected && trace.showEdge()) {
                                canvas.setStroke(Stroke.SOLID, Color.white);
                                Marker.MARKER.stroke(canvas);
                            }
                        }
                    }
                } else {
                    @SuppressWarnings("unchecked") final Collection<PlotShape.PlotRectangle> foundRectangles = (Collection<PlotShape.PlotRectangle>) rectangles.getOrDefault(trace, RTree.emptyTree()).search(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                    for (final PlotShape.PlotRectangle rectangle : foundRectangles) {
                        if (!rectangle.isVisible()) {
                            continue;
                        }
                        canvas.setFill(rectangle.getColor());
                        drawRectangle(canvas, rectangle);
                    }
                    @SuppressWarnings("unchecked") final Set<PlotShape.PlotMarker> foundMarkers = ((Set<PlotShape.PlotMarker>) markers.getOrDefault(trace, RTree.emptyTree()).search(new TreeSet<>(PlotShape.PlotMarker.ORDER_COMPARATOR), minX, minY, maxX, maxY));
                    for (final PlotShape.PlotMarker n : foundMarkers) {
                        if (!n.isVisible()) {
                            continue;
                        }
                        canvas.setFill(n.getColor() == null ? (n).parent.getColor(-1) : n.getColor());
                        Marker.MARKER.x = ((PlotData.RelationalData<?>) (n).parent).getX(n.i);
                        Marker.MARKER.y = ((PlotData.RelationalData<?>) (n).parent).getY(n.i);
                        Marker.MARKER.shape = n.getShape();
                        Marker.MARKER.size = n.getSize();
                        transformMarker(Marker.MARKER);
                        Marker.MARKER.fill(canvas);
                        if (trace.showEdge()) {
                            canvas.setStroke(trace.getEdgeStroke(), trace.getEdgeColor());
                            Marker.MARKER.stroke(canvas);
                        }
                    }
                }
            }
            canvas.clearClip();
            x.drawAxis(canvas);
            y.drawAxis(canvas);

        }


        @Override
        protected void markLayoutAsOld() {
            x.markLayoutAsOld();
            y.markLayoutAsOld();
            super.markLayoutAsOld();
        }

        @Override
        protected void markDrawAsOld() {
            x.markDrawAsOld();
            y.markDrawAsOld();
            super.markDrawAsOld();
        }

        @Override
        public Axis getXAxis() throws NullPointerException {
            return x;
        }

        @Override
        public Axis getYAxis() throws NullPointerException {
            return y;
        }

        @Override
        public PlotLayout setRange(double minX, double maxX, double minY, double maxY) {
            if (minX == maxX) {
                maxX += Math.ulp(1);
            }
            if (minY == maxY) {
                maxY += Math.ulp(1);
            }
            x.lower = Math.min(minX, maxX);
            x.upper = Math.max(minX, maxX);
            y.lower = Math.min(minY, maxY);
            y.upper = Math.max(minY, maxY);

            if (figure != null) {
                figure.markLayoutAsOld();
                figure.refresh();
            }
            return this;
        }

        @Override
        public String toString() {
            return String.format("RectangularPlot {%s}", title);
        }

        @Override
        public PlotLayout apply(Theme theme) {
            x.apply(theme);
            y.apply(theme);
            return super.apply(theme);
        }

        boolean intersectMarker(PlotShape.PlotMarker marker, double x, double y) {
            double w = marker.size * .5 / getXAxis().scale;
            double h = marker.size * .5 / getYAxis().scale;
            return RectangularNode.intersects(x, y, x, y, marker.getMinX() - w, marker.getMinY() - h, marker.getMinX() + w, marker.getMinY() + h);
        }

        /**
         * Add padding to the intersection search of internal nodes in marker Rtree
         *
         * @param node the node
         * @param x    the x of the point
         * @param y    the y of the point
         * @param w    the width padding
         * @param h    the height padding
         * @return whether the extended node intersects the point
         */
        private boolean pointIntersectsPaddedNode(Node2D<?> node, double x, double y, double w, double h) {
            return RectangularNode.intersects(x, y, x, y, node.getMinX() - w, node.getMinY() - h, node.getMaxX() + w, node.getMaxY() + h);
        }
        private boolean markerContainsPoint(PlotShape.PlotMarker marker, double x, double y){
            double w = marker.size * .5 / getXAxis().scale;
            double h = marker.size * .5 / getYAxis().scale;
            return RectangularNode.intersects(x, y, x, y, marker.xMin - w, marker.yMin - h, marker.xMax + w, marker.yMax + h);
        }
        @Override
        public Renderer.Tooltip getHoverText(double x, double y) {
            double xMin = this.x.getValueFromPosition(x);
            double yMin = this.y.getValueFromPosition(y);
            final SortedSet<Node2D<Runnable>> found = new TreeSet<>(PlotShape.ORDER_COMPARATOR);

            for (final PlotData<?> trace : data) {
                PlotTrace.Numeric size = (PlotTrace.Numeric) trace.getAttribute(PlotData.Attribute.SIZE);
                double b = size == null ? trace.getSize(-1) : size.scaleMax;
                double w = b / getXAxis().scale,
                        h = b / getYAxis().scale;
                markers.getOrDefault(trace, RTree.emptyTree()).findAll(found,
                        node -> pointIntersectsPaddedNode(node, xMin, yMin, w, h),
                        node -> markerContainsPoint((PlotShape.PlotMarker) node,xMin, yMin)
                );
                if (!found.isEmpty()) {
                    return tooltip.set(this.x.getPositionFromValue(found.last().getMinX()), this.y.getPositionFromValue(found.last().getMaxY()), ((PlotShape) found.last()).getColor(), ((PlotShape) found.last()).getHoverText());
                }
                rectangles.getOrDefault(trace, RTree.emptyTree()).search(found, xMin, yMin, xMin, yMin);
                if (!found.isEmpty()) {
                    return tooltip.set(this.x.getPositionFromValue(found.last().getMinX()), this.y.getPositionFromValue(found.last().getMaxY()), ((PlotShape) found.last()).getColor(), ((PlotShape) found.last()).getHoverText());
                }
            }

            return null;
        }

        @Override
        boolean canAdd(PlotData<?> data) {
            if (data instanceof PlotData.RelationalData) {
                return Objects.equals(x.getTitle(), ((PlotData.RelationalData<?>) data).getXLabel()) && Objects.equals(y.getTitle(), ((PlotData.RelationalData<?>) data).getYLabel()) && Objects.equals(data.title, title) && Arrays.equals(((PlotData.RelationalData<?>) data).xLabels, x.labels);
            } else if (data instanceof PlotData.CategoricalData) {
                return Objects.equals(x.getTitle(), ((PlotData.CategoricalData<?>) data).categoryLabel) && Objects.equals(y.getTitle(), ((PlotData.CategoricalData<?>) data).valueLabel) && Objects.equals(data.title, title);//&& Arrays.equals(((PlotData.CategoricalData<?>) trace).xLabels, x.labels);

            }
            return false;
        }
    }

    static final class FacetGrid extends XYLayout {

        public FacetGrid(Figure figure, PlotData<?> trace) {
            trace.figure = figure;
            this.figure = figure;
            this.data.add(trace);
            trace.facets.plots = new XYLayout[trace.facets.cols == null ? 1 : trace.facets.cols.numGroups()][trace.facets.rows == null ? 1 : trace.facets.rows.numGroups()];
            trace.facets.key = new HashMap<>(trace.facets.plots.length * trace.facets.plots[0].length);
            if (trace.facets.cols != null && trace.facets.rows != null) {
                for (final GroupBy.Group<?> col : trace.facets.cols) {
                    trace.facets.plots[col.getID()] = new XYLayout[trace.facets.rows.numGroups()];
                    for (final GroupBy.Group<?> row : trace.facets.rows) {
                        final XYLayout plot = new XYLayout(x, y);
                        trace.facets.plots[col.getID()][row.getID()] = plot;
                        plot.title = String.format("%s%s%s", String.format(trace.facets.formatTitle, trace.facets.colName, col.get()), trace.facets.divider, String.format(trace.facets.formatTitle, trace.facets.rowName, row.get()));
                        plot.data.add(trace);
                        plot.figure = figure;
                        trace.facets.key.put(plot, Sorts.and(col.getIndices(), row.getIndices()));
                        trace.init(plot);
                    }
                }

            } else if (trace.facets.rows != null) {
                throw new UnsupportedOperationException("rows only");

            } else if (trace.facets.cols != null) {
                for (final GroupBy.Group<?> group : trace.facets.cols) {
                    trace.facets.plots[group.getID()] = new XYLayout[]{new XYLayout(x, y)};
                    trace.facets.plots[group.getID()][0].title = String.format(trace.facets.formatTitle, trace.facets.colName, group.get());
                    trace.facets.plots[group.getID()][0].data.add(trace);
                    trace.facets.plots[group.getID()][0].figure = figure;
                    trace.facets.key.put(trace.facets.plots[group.getID()][0], group.getIndices());
                    trace.init(trace.facets.plots[group.getID()][0]);
                }
            }
            x.title.setText(((PlotData.RelationalData<?>) trace).xLab);
            x.labels = ((PlotData.RelationalData<?>) trace).xLabels;
            x.figure = figure;
            y.title.setText(((PlotData.RelationalData<?>) trace).yLab);
            y.figure = figure;
        }

        @Override
        public void drawComponent(Renderer<?> renderer, ChartCanvas<?> canvas) {
            for (final PlotLayout[] cols : data.get(0).facets.plots) {
                for (final PlotLayout cell : cols) {
                    cell.draw(renderer, canvas);
                }
            }
        }

        @Override
        boolean canAdd(PlotData<?> data) {
            return false;
        }

        @Override
        public void layoutComponent(Renderer<?> source, double x, double y, double width, double height) {
            super.layoutComponent(source, x, y, width, height);
        }
    }

    abstract boolean canAdd(PlotData<?> trace);

    public PlotLayout setTitle(String title) {
        if (figure != null) {
            figure.setTitle(title);
            return this;
        }
        this.title = title;
        return this;
    }

    final boolean add(final PlotData<?> trace) {
        if (canAdd(trace)) {
            data.add(trace);
            if (trace.layout != null) {
                throw new UnsupportedOperationException();
            }
            trace.layout = this;
            trace.figure = figure;
            trace.title = null;
            if (trace instanceof PlotData.RelationalData) {
                ((PlotData.RelationalData<?>) trace).xLab = null;
                ((PlotData.RelationalData<?>) trace).yLab = null;
                ((PlotData.RelationalData<?>) trace).xLabels = null;
            } else if (trace instanceof PlotData.CategoricalData) {
                ((PlotData.CategoricalData<?>) trace).categoryLabel = null;
                ((PlotData.CategoricalData<?>) trace).valueLabel = null;
            }
            trace.init(this);
            return true;
        }
        return false;
    }


    PlotData<?> getTrace() {
        if (data.isEmpty()) {
            return null;
        }
        return data.get(data.size() - 1);
    }

    public Axis getXAxis() throws NullPointerException {
        return null;
    }

    public Axis getYAxis() throws NullPointerException {
        return null;
    }

    void putPolygons(PlotData<?> trace, RTree<Runnable> polygons) {
        if (((Map<?, ?>) this.polygons) == Collections.emptyMap()) {
            this.polygons = new LinkedHashMap<>();
        }
        this.polygons.put(trace, polygons);
    }

    void putMarkers(PlotData<?> trace, RTree<Runnable> polygons) {
        if (((Map<?, ?>) this.markers) == Collections.emptyMap()) {
            this.markers = new LinkedHashMap<>();
        }
        this.markers.put(trace, polygons);
    }

    void putLines(PlotData<?> trace, RTree<Runnable> polygons) {
        if (((Map<?, ?>) this.lines) == Collections.emptyMap()) {
            this.lines = new LinkedHashMap<>();
        }
        this.lines.put(trace, polygons);
    }

    void putRectangles(PlotData<?> trace, RTree<Runnable> rectangles) {
        if (((Map<?, ?>) this.rectangles) == Collections.emptyMap()) {
            this.rectangles = new LinkedHashMap<>();
        }
        this.rectangles.put(trace, rectangles);
    }

    void clear(PlotData<?> trace) {
        if (((Map<?, ?>) this.polygons) != Collections.emptyMap()) {
            this.polygons.remove(trace);
        }
        if (((Map<?, ?>) this.lines) != Collections.emptyMap()) {
            this.lines.remove(trace);
        }
        if (((Map<?, ?>) this.markers) != Collections.emptyMap()) {
            this.markers.remove(trace);
        }
        if (((Map<?, ?>) this.rectangles) != Collections.emptyMap()) {
            this.rectangles.remove(trace);
        }
    }

    final void transformMarker(Marker marker) {
        marker.x = transformX(marker.x);
        marker.y = transformY(marker.y);

    }

    double transformX(double x) {
        return getXAxis().posX + ((x - getXAxis().lower) * getXAxis().scale);
    }

    double transformY(double y) {
        return getYAxis().posY + (((getYAxis().upper - y) / getYAxis().range) * getYAxis().sizeY);
    }

    final void drawLine(ChartCanvas<?> canvas, PlotShape.PlotPolyLine n) {
        for (final PlotShape.PlotPolyLine.Segment s : n.getSegments()) {
            canvas.strokeLine(transformX(s.startX), transformY(s.startY), transformX(s.endX), transformY(s.endY));
        }
    }

    final void drawRectangle(ChartCanvas<?> canvas, PlotShape.PlotRectangle n) {
        canvas.fillRect(transformX(n.getMinX()), transformY(n.getMaxY()), ((XYLayout) n.parent.layout).x.scale * n.w, ((XYLayout) n.parent.layout).y.scale * n.h);

    }

    final void drawPolygon(ChartCanvas<?> canvas, PlotShape.PlotPolygon n) {
        canvas.beginPath();
        canvas.moveTo(transformX(n.getPoints()[0].getMidX()), transformY(n.getPoints()[0].getMidY()));
        for (int i = 1; i < n.getPoints().length; ++i) {
            canvas.lineTo(transformX(n.getPoints()[i].getMidX()), transformY(n.getPoints()[i].getMidY()));
        }
        canvas.closePath();
        canvas.fill();

    }

    /**
     * Set the range of the x axis
     *
     * @param min the new min value of the x axis
     * @param max the new max value of the x axis
     * @return this layout
     */
    public PlotLayout setXRange(double min, double max) {
        if (getXAxis() == null) {
            System.err.println("No x axis");
            return this;
        }
        getXAxis().setRange(min, max);
        return this;
    }

    /**
     * Set the range of the y axis
     *
     * @param min the new min value of the y axis
     * @param max the new max value of the y axis
     * @return this layout
     */
    public PlotLayout setYRange(double min, double max) {
        if (getYAxis() == null) {
            System.err.println("No y axis");
            return this;
        }
        getYAxis().setRange(min, max);
        return this;
    }

    /**
     * Set the range of the x and y axis
     *
     * @param minX the new min value of the x axis
     * @param maxX the new max value of the x axis
     * @param minY the new min value of the y axis
     * @param maxY the new max value of the y axis
     * @return this layout
     */
    public abstract PlotLayout setRange(double minX, double maxX, double minY, double maxY);

    @Override
    public PlotLayout apply(Theme theme) {
        theme.layout.accept(this);
        return this;
    }

    /**
     * Get a tooltip from the position
     *
     * @param x the x position of the cursor
     * @param y the y position of the cursor
     * @return a tooltip for the current position
     */
    abstract Renderer.Tooltip getHoverText(double x, double y);

}
