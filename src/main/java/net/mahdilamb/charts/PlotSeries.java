package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.plots.*;
import net.mahdilamb.charts.series.DataFrame;
import net.mahdilamb.charts.series.DoubleSeries;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.reference.sequential.Viridis;
import net.mahdilamb.geom2d.trees.PointNode;
import net.mahdilamb.geom2d.trees.RTree;

import java.util.*;
//TODO deal with grouping and colorscale clash - alpha by iterable

/**
 * Implementations of series
 *
 * @param <S> the type of the series
 */
//TODO hide constructor
public abstract class PlotSeries<S> extends ChartComponent implements PlotWithColorBar<S>, PlotWithLegend<S> {


    public static final Colormap DEFAULT_SEQUENTIAL_COLORMAP = new Viridis();

    @Override
    protected void calculateBounds(ChartCanvas<?> canvas, Chart<?, ?> source, double minX, double minY, double maxX, double maxY) {

    }


    private enum ColorMode {
        SINGLETON,
        ITERABLE,
        COLORMAP
    }

    protected boolean needsUpdating = true;

    /**
     * Series display
     */
    boolean showInLegend = true, showInColorBars = false;

    String name;

    /**
     * Edge options
     */
    Color edgeColor = Color.BLACK;
    boolean showEdges = false;
    double edgeSize = 1;

    /**
     * Face options
     */
    ColorMode colorMode = ColorMode.SINGLETON;

    Color faceColor;
    List<Color> faceColors;
    Colormap colormap;
    double[] faceColorArray;
    double minScale, maxScale;


    /**
     * Called before the series is added to a plot
     *
     * @param a the first axis
     * @param b the second axis (may be {@code null})
     * @return the series
     */
    protected abstract S prepare(Axis a, Axis b);

    /**
     * Called before the series is added to a plot. Shortcut for 1 axis plots
     *
     * @param a the axis
     * @return this series
     */
    protected S prepare(Axis a) {
        return prepare(a, null);
    }

    static void checkAxis(Axis axis) {
        if (axis.lowerBound > axis.upperBound) {
            double t = axis.upperBound;
            axis.upperBound = axis.lowerBound;
            axis.lowerBound = t;
        }
    }

    @SuppressWarnings("unchecked")
    public S setName(String name) {
        this.name = name;
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setEdgeSize(double size) {
        if (edgeSize != size) {
            needsUpdating = true;
        }
        edgeSize = size;
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setEdgeColor(Color color) {
        if (!edgeColor.equals(color)) {
            needsUpdating = true;
        }
        edgeColor = color;
        if (!showEdges && color.alpha() != 0) {
            showEdges = true;
        }
        return (S) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S showLegend(boolean showLegend) {
        if (showLegend != showInLegend) {
            needsUpdating = true;
        }
        showInLegend = showLegend;
        return (S) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S showColorBar(boolean showColorBar) {
        if (showColorBar != showInColorBars) {
            needsUpdating = true;
        }
        showInColorBars = showColorBar;
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setColor(Color color) {
        if (!color.equals(faceColor) || colorMode != ColorMode.SINGLETON) {
            needsUpdating = true;
        }
        colorMode = ColorMode.SINGLETON;
        faceColor = color;
        return (S) this;
    }

    List<Color> prepareColorList(int size) {
        needsUpdating = true;

        if (faceColors == null) {
            faceColors = new ArrayList<>(size);
        }
        faceColors.clear();
        if (size > 0) {
            ((ArrayList<Color>) faceColors).ensureCapacity(size);
        }
        colorMode = ColorMode.ITERABLE;
        return faceColors;
    }


    @SuppressWarnings("unchecked")
    public S setColors(String... colorNames) {
        faceColorArray = null;
        final List<Color> colors = prepareColorList(colorNames.length);
        for (final String string : colorNames) {
            colors.add(Color.get(string));
        }
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setColors(Iterable<String> colorNames) {
        faceColorArray = null;
        final List<Color> colors = prepareColorList(-1);
        for (final String string : colorNames) {
            colors.add(Color.get(string));
        }
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setColors(Colormap colormap, Iterable<? extends Number> scalars) {
        needsUpdating = true;
        colorMode = ColorMode.COLORMAP;
        this.colormap = colormap;
        int size = 0;
        double min = Double.NaN, max = Double.NaN;
        for (final Number n : scalars) {
            if (n != null && (Double.isNaN(min) || n.doubleValue() < min)) {
                min = n.doubleValue();
            }
            if (n != null && (Double.isNaN(max) || n.doubleValue() > max)) {
                max = n.doubleValue();
            }
            ++size;
        }
        faceColors = null;
        faceColorArray = new double[size];
        this.minScale = min;
        this.maxScale = max;
        int i = 0;
        for (final Number n : scalars) {
            faceColorArray[i++] = n == null ? Double.NaN : n.doubleValue();
        }
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setColors(Colormap colormap, double... scalars) {
        needsUpdating = true;
        colorMode = ColorMode.COLORMAP;
        this.colormap = colormap;
        double min = Double.NaN, max = Double.NaN;
        for (final Number n : scalars) {
            if (n != null && (Double.isNaN(min) || n.doubleValue() < min)) {
                min = n.doubleValue();
            }
            if (n != null && (Double.isNaN(max) || n.doubleValue() > max)) {
                max = n.doubleValue();
            }
        }
        faceColors = null;
        faceColorArray = scalars;
        this.minScale = min;
        this.maxScale = max;
        return (S) this;
    }

    static class ScatterImpl extends PlotSeries<Scatter> implements Scatter {
        static final class ScatterPoint extends PointNode<MarkerImpl> {

            ScatterPoint(double x, double y, MarkerImpl data) {
                super(x, y, data);
            }
        }

        MarkerMode mode = MarkerMode.MARKER_ONLY;
        Iterable<String> groups;
        Set<String> groupNames;
        MarkerShape markerShape;
        double markerSize = 10;
        private final double[] x;
        private final double[] y;
        boolean prepared = false;

        private final RTree<MarkerImpl> points = new RTree<>();
        private ScatterPoint[] scatterPoints;

        ScatterImpl(double[] x, double[] y) {
            this.x = x;
            this.y = y;
        }

        ScatterImpl(Iterable<? extends Number> x, Iterable<? extends Number> y, int numPoints) {
            this.x = new double[numPoints];
            this.y = new double[numPoints];
            final Iterator<? extends Number> xIterator = x.iterator();
            final Iterator<? extends Number> yIterator = y.iterator();
            int i = 0;
            while (i < numPoints && xIterator.hasNext() && yIterator.hasNext()) {
                Number xi = xIterator.next();
                Number yi = yIterator.next();
                this.x[i] = xi == null ? Double.NaN : xi.doubleValue();
                this.y[i++] = yi == null ? Double.NaN : yi.doubleValue();
            }
        }

        ScatterImpl(final DataFrame dataset, final String x, final String y) {
            this.x = ((DoubleSeries) dataset.getDoubleSeries(x)).toArray(new double[dataset.get(0).size()]);
            this.y = ((DoubleSeries) dataset.getDoubleSeries(y)).toArray(new double[dataset.get(0).size()]);

        }

        @Override
        public Scatter setGroups(Iterable<String> groupings) {
            this.groups = groupings;
            return this;
        }



        @Override
        protected Scatter prepare(final Axis xAxis, final Axis yAxis) {

            return this;
        }

        @Override
        public Scatter setMarkerMode(MarkerMode mode) {
            if (mode != this.mode) {
                needsUpdating = true;
            }
            this.mode = mode;
            return this;
        }


        @Override
        public String toString() {
            //TODO
            return "Scatter series {" +
                    "marker: " + StringUtils.snakeToTitleCase(markerShape.name()) +
                    ", markerSize: " + markerSize +
                    '}';
        }

        @Override
        public Scatter setMarkerSize(double size) {
            if (size != markerSize) {
                needsUpdating = true;
            }
            markerSize = size;
            return this;
        }

        @Override
        public Scatter setMarker(MarkerShape marker) {
            if (marker != this.markerShape) {
                needsUpdating = true;
            }
            this.markerShape = Objects.requireNonNull(marker);
            return this;
        }

        @Override
        protected void layout(ChartCanvas<?> canvas,  Chart<?, ?>  source, double minX, double minY, double maxX, double maxY) {

        }
    }

    static abstract class BarImpl extends PlotSeries<Bar> {


    }

    static abstract class ViolinImpl extends PlotSeries<Violin> {


    }

    static abstract class RugImpl extends PlotSeries<Rug> {


    }

    static abstract class AreaImpl extends PlotSeries<Area> {


    }

    static abstract class BoxImpl extends PlotSeries<BoxAndWhisker> {


    }

    static abstract class ContourImpl extends PlotSeries<Contour> {


    }

    static abstract class DendrogramImpl extends PlotSeries<Dendrogram> {


    }

    static abstract class DensityImpl extends PlotSeries<Density> {


    }

    static abstract class KDEImpl extends PlotSeries<KDE> {

    }

    static abstract class DotImpl extends PlotSeries<Dot> {


    }

    static abstract class HeatmapImpl extends PlotSeries<Heatmap> {


    }

    static abstract class Histogram2DImpl extends PlotSeries<Histogram2D> {


    }

    static abstract class HistogramImpl extends PlotSeries<Histogram> {


    }

    static abstract class LineImpl extends PlotSeries<Line> {


    }

    static abstract class TableImpl extends PlotSeries<TableColumn> {


    }

    static abstract class PolarImpl extends PlotSeries<Polar> {


    }

    static abstract class PieImpl extends PlotSeries<net.mahdilamb.charts.plots.Pie> {


    }

    static final class MarkerImpl implements Marker {
        Fill face;
        Stroke edge;
        double size, edgeWidth;
        MarkerShape markerShape;

        MarkerImpl(MarkerShape markerShape, double size, Color face, double edgeWidth, Color edge) {
            this.edge = new Stroke(edge, edgeWidth);
            this.edgeWidth = edgeWidth;
            this.markerShape = markerShape;
            this.face = new Fill(face);
            this.size = size;
        }


        @Override
        public Fill getFill() {
            return face;
        }

        @Override
        public Stroke getStroke() {
            return edge;
        }

        @Override
        public double getSize() {
            return size;
        }

        @Override
        public MarkerShape getShape() {
            return markerShape;
        }
    }
}
