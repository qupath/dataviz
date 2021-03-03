package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataframe.utils.Sorts;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.graphics.shapes.Marker;
import net.mahdilamb.dataviz.utils.rtree.Node2D;
import net.mahdilamb.dataviz.utils.rtree.RTree;

import java.util.*;

/**
 * Layouts for plot areas
 */
public abstract class PlotLayout extends Component {
    Figure figure;
    String title;
    final List<PlotData<?>> data = new LinkedList<>();
    protected Map<PlotData<?>, RTree<Runnable>> polygons = Collections.emptyMap();
    protected Map<PlotData<?>, RTree<Runnable>> lines = Collections.emptyMap();
    protected Map<PlotData<?>, RTree<Runnable>> markers = Collections.emptyMap();
    Color background = Color.lightgray;
    Style selectedStyle = SelectedStyle.DEFAULT_SELECTED_STYLE;
    Style unselectedStyle = UnselectedStyle.DEFAULT_UNSELECTED_STYLE;

    protected static class Rectangular extends PlotLayout {
        final Axis x, y;

        public Rectangular() {
            this(new Axis.XAxis(), new Axis.YAxis());
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
                if (trace instanceof PlotData.XYData) {
                    canvas.setStroke(((PlotData.XYData<?>) trace).lineStroke, ((PlotData.XYData<?>) trace).lineColor);
                }
                @SuppressWarnings("unchecked") final Collection<PlotData.PlotPolyLine> foundLines = (Collection<PlotData.PlotPolyLine>) lines.getOrDefault(trace, RTree.emptyTree()).search(minX, minY, maxX, maxY);
                for (final PlotData.PlotPolyLine n : foundLines) {
                    canvas.setStroke(n.getColor());
                    drawLine(canvas, n);
                }
                if (trace.anySelected) {
                    for (final Node2D<Runnable> n : markers.getOrDefault(trace, RTree.emptyTree()).search(minX, minY, maxX, maxY)) {
                        final PlotData.PlotShape p = (PlotData.PlotShape) n;
                        if (!p.isVisible()) {
                            continue;
                        }
                        if (p instanceof PlotData.PlotMarker) {
                            final Color color = p.getColor() == null ? p.getSource().getColor(-1) : p.getColor();
                            boolean selected = trace.selected.get(p.i());
                            canvas.setFill(new Color(color.red(), color.green(), color.blue(), selected ? .8 : 0.2));
                            Marker.MARKER.x = ((PlotData.XYData<?>) p.getSource()).getX(p.i());
                            Marker.MARKER.y = ((PlotData.XYData<?>) p.getSource()).getY(p.i());
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
                    for (final Node2D<Runnable> n : markers.getOrDefault(trace, RTree.emptyTree()).search(minX, minY, maxX, maxY)) {
                        final PlotData.PlotShape p = (PlotData.PlotShape) n;
                        if (!p.isVisible()) {
                            continue;
                        }
                        if (p instanceof PlotData.PlotMarker) {
                            canvas.setFill(p.getColor() == null ? p.getSource().getColor(-1) : p.getColor());
                            Marker.MARKER.x = ((PlotData.XYData<?>) p.getSource()).getX(p.i());
                            Marker.MARKER.y = ((PlotData.XYData<?>) p.getSource()).getY(p.i());
                            Marker.MARKER.shape = p.getShape();
                            Marker.MARKER.size = p.getSize();
                            transformMarker(Marker.MARKER);
                            Marker.MARKER.fill(canvas);
                            if (trace.showEdge()) {
                                canvas.setStroke(trace.getEdgeStroke(),trace.getEdgeColor());
                                Marker.MARKER.stroke(canvas);
                            }
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

        public Rectangular(Axis x, Axis y) {
            this.x = x;
            this.y = y;

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
        boolean canAdd(PlotData<?> trace) {
            if (trace instanceof PlotData.XYData) {
                return Objects.equals(x.getTitle(), ((PlotData.XYData<?>) trace).getXLabel()) && Objects.equals(y.getTitle(), ((PlotData.XYData<?>) trace).getYLabel()) && Objects.equals(trace.title, title) && Arrays.equals(((PlotData.XYData<?>) trace).xLabels, x.labels);
            }
            return false;
        }
    }

    static final class RectangularFacetGrid extends Rectangular {

        public RectangularFacetGrid(Figure figure, PlotData<?> trace) {
            trace.figure = figure;
            this.figure = figure;
            this.data.add(trace);
            trace.facets.plots = new Rectangular[trace.facets.cols == null ? 1 : trace.facets.cols.numGroups()][trace.facets.rows == null ? 1 : trace.facets.rows.numGroups()];
            trace.facets.key = new HashMap<>(trace.facets.plots.length * trace.facets.plots[0].length);
            if (trace.facets.cols != null && trace.facets.rows != null) {
                for (final GroupBy.Group<?> col : trace.facets.cols) {
                    trace.facets.plots[col.getID()] = new Rectangular[trace.facets.rows.numGroups()];
                    for (final GroupBy.Group<?> row : trace.facets.rows) {
                        final Rectangular plot = new Rectangular(x, y);
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
                    trace.facets.plots[group.getID()] = new Rectangular[]{new Rectangular(x, y)};
                    trace.facets.plots[group.getID()][0].title = String.format(trace.facets.formatTitle, trace.facets.colName, group.get());
                    trace.facets.plots[group.getID()][0].data.add(trace);
                    trace.facets.plots[group.getID()][0].figure = figure;
                    trace.facets.key.put(trace.facets.plots[group.getID()][0], group.getIndices());
                    trace.init(trace.facets.plots[group.getID()][0]);
                }
            }
            x.title.setText(((PlotData.XYData<?>) trace).xLab);
            x.labels = ((PlotData.XYData<?>) trace).xLabels;
            x.figure = figure;
            y.title.setText(((PlotData.XYData<?>) trace).yLab);
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
        boolean canAdd(PlotData<?> trace) {
            return false;
        }

        @Override
        public void layoutComponent(Renderer<?> source, double x, double y, double width, double height) {
            super.layoutComponent(source, x, y, width, height);
        }
    }

    abstract boolean canAdd(PlotData<?> trace);

    public PlotLayout setTitle(String title) {
        if (figure != null){
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
            if (trace instanceof PlotData.XYData) {
                ((PlotData.XYData<?>) trace).xLab = null;
                ((PlotData.XYData<?>) trace).yLab = null;
                ((PlotData.XYData<?>) trace).xLabels = null;
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
    }

    void transformMarker(Marker marker) {
        marker.x = transformX(marker.x);
        marker.y = transformY(marker.y);

    }

    double transformX(double x) {
        return getXAxis().posX + ((x - getXAxis().lower) * getXAxis().scale);
    }

    double transformY(double y) {
        return getYAxis().posY + (((getYAxis().upper - y) / getYAxis().range) * getYAxis().sizeY);
    }

    void drawLine(ChartCanvas<?> canvas, PlotData.PlotPolyLine n) {
        for (final PlotData.PlotPolyLine.Segment s : n.getSegments()) {
            canvas.strokeLine(transformX(s.startX), transformY(s.startY), transformX(s.endX), transformY(s.endY));
        }
    }

    public PlotLayout setXRange(double min, double max) {
        if (getXAxis() == null) {
            System.err.println("No x axis");
            return this;
        }
        getXAxis().setRange(min, max);
        return this;
    }

    public PlotLayout setYRange(double min, double max) {
        if (getYAxis() == null) {
            System.err.println("No y axis");
            return this;
        }
        getYAxis().setRange(min, max);
        return this;
    }

    public abstract PlotLayout setRange(double minX, double maxX, double minY, double maxY);


}
