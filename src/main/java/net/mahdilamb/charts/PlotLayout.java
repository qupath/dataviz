package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.shapes.Marker;
import net.mahdilamb.charts.rtree.Node2D;
import net.mahdilamb.charts.rtree.RTree;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataframe.utils.Sorts;

import java.util.*;

public abstract class PlotLayout extends Component {
    Figure figure;
    String title;
    final List<PlotData<?>> traces = new LinkedList<>();
    protected Map<PlotData<?>, RTree<Runnable>> polygons = Collections.emptyMap();
    protected Map<PlotData<?>, RTree<Runnable>> lines = Collections.emptyMap();
    protected Map<PlotData<?>, RTree<Runnable>> markers = Collections.emptyMap();
    Color background = Color.lightgray;

    static class Rectangular extends PlotLayout {
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
            y.sizeY = maxY - minY - x.sizeY;
            x.posX = minX + y.sizeX;
            x.sizeX -= y.sizeX;
            posX = y.posX + y.sizeX;
            posY = y.posY;
            sizeX = x.sizeX;
            sizeY = y.sizeY;
            x.updateXAxisScale();
            y.updateYAxisScale();
        }

        @Override
        protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
            canvas.setFill(background);
            canvas.fillRect(posX, posY, sizeX, sizeY);
            ((Axis.XAxis) x).drawXAxis(source, canvas, y);
            ((Axis.YAxis) y).drawYAxis(source, canvas, x);
            ((Axis.XAxis) x).drawXGrid(source, canvas, y);
            ((Axis.YAxis) y).drawYGrid(source, canvas, x);
          /*  if (title != null) {
                System.out.println(">> Title <<");
                System.out.println(title);
            }
            System.out.println(">> Axes <<");
            System.out.println(getXAxis());
            System.out.println(getYAxis());
*/

            Set<Node2D<Runnable>> visible = new TreeSet<>(PlotData.PlotShape.ORDER_COMPARATOR);
            for (final PlotData<?> trace : traces) {
                visible.addAll(markers.getOrDefault(trace, RTree.emptyTree()).search(x.lower, y.lower, x.upper, y.upper));
            }
            for (final Node2D<Runnable> n : visible) {
                final PlotData.PlotShape p = (PlotData.PlotShape) n;
                if (!p.isVisible()) {
                    continue;
                }
                if (p instanceof PlotData.PlotMarker) {
                    canvas.setFill(p.getColor() == null?p.getSource().getColor(-1):p.getColor());
                    Marker.MARKER.x = ((PlotData.XYData<?>) p.getSource()).getX(((PlotData.PlotMarker) p).i);
                    Marker.MARKER.y = ((PlotData.XYData<?>) p.getSource()).getY(((PlotData.PlotMarker) p).i);
                    Marker.MARKER.shape = p.getShape();
                    Marker.MARKER.size = p.getSize();
                    transformMarker(Marker.MARKER);
                    Marker.MARKER.fill(canvas);
                }
            }


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
            this.traces.add(trace);
            trace.facets.plots = new Rectangular[trace.facets.cols == null ? 1 : trace.facets.cols.numGroups()][trace.facets.rows == null ? 1 : trace.facets.rows.numGroups()];
            trace.facets.key = new HashMap<>(trace.facets.plots.length * trace.facets.plots[0].length);
            if (trace.facets.cols != null && trace.facets.rows != null) {
                for (final GroupBy.Group<?> col : trace.facets.cols) {
                    trace.facets.plots[col.getID()] = new Rectangular[trace.facets.rows.numGroups()];
                    for (final GroupBy.Group<?> row : trace.facets.rows) {
                        final Rectangular plot = new Rectangular(x, y);
                        trace.facets.plots[col.getID()][row.getID()] = plot;
                        plot.title = String.format("%s%s%s", String.format(trace.facets.formatTitle, trace.facets.colName, col.get()), trace.facets.divider, String.format(trace.facets.formatTitle, trace.facets.rowName, row.get()));
                        plot.traces.add(trace);
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
                    trace.facets.plots[group.getID()][0].traces.add(trace);
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
            for (final PlotLayout[] cols : traces.get(0).facets.plots) {
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
            super.layout(source, x, y, width, height);
        }
    }

    abstract boolean canAdd(PlotData<?> trace);

    public void setTitle(String title) {
        this.title = title;
    }

    final boolean add(final PlotData<?> trace) {
        if (canAdd(trace)) {
            traces.add(trace);
            if (trace.plot != null) {
                throw new UnsupportedOperationException();
            }
            trace.plot = this;
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
        if (traces.isEmpty()) {
            return null;
        }
        return traces.get(traces.size() - 1);

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
        marker.x = getXAxis().posX + ((marker.x - getXAxis().lower) * getXAxis().scale);
        marker.y = getYAxis().posY + (((getYAxis().upper - marker.y) / getYAxis().range) * getYAxis().sizeY);

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

}
