package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Chart;
import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.MarkerShape;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.statistics.StatUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.geom2d.trees.PointNode;
import net.mahdilamb.geom2d.trees.RectangularNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;

import static net.mahdilamb.charts.plots.AbstractScatter.ScatterPoint.EMPTY_RUNNABLE;
import static net.mahdilamb.charts.plots.Scatter.DEFAULT_MARKER_SIZE;

abstract class AbstractScatter<S extends AbstractScatter<S>> extends PlotSeries<S> implements RectangularPlot {
    @FunctionalInterface
    private interface IntersectsPoint {
        boolean intersects(double x, double y);
    }


    protected static final class ScatterPoint extends PointNode<Runnable> implements IntersectsPoint {
        static final Runnable EMPTY_RUNNABLE = () -> {
        };
        public double size,
                opacity = 1.,
                bandLower = Double.NaN,
                bandUpper = Double.NaN,
                errorXLower = Double.NaN,
                errorXUpper = Double.NaN,
                errorYLower = Double.NaN,
                errorYUpper = Double.NaN;
        public MarkerShape markerShape;
        public Color markerColor;
        public GroupAttributes groupAttributes;

        public ScatterPoint(double x, double y, double size, Runnable callback) {
            super(x, y, (callback == null ? EMPTY_RUNNABLE : callback));
            this.size = size;
        }

        @Override
        public boolean intersects(double x, double y) {
            //TODO
            return false;
        }
    }

    protected static final class ScatterLine extends RectangularNode<Runnable> implements IntersectsPoint {
        public double thickness;
        ScatterPoint start, end;

        ScatterLine(ScatterPoint start, ScatterPoint end, double thickness, Runnable callback) {
            super(Math.min(start.getMidX(), end.getMidX()) - .5 * thickness, Math.min(start.getMidY(), end.getMidY()) - .5 * thickness, Math.max(start.getMidX(), end.getMidX()) + .5 * thickness, Math.max(start.getMidY(), end.getMidY()) + .5 * thickness, (callback == null ? EMPTY_RUNNABLE : callback));
            this.start = start;
            this.end = end;
            this.thickness = thickness;

        }

        @Override
        public boolean intersects(double x, double y) {
            //TODO
            return false;
        }
    }

    protected Color color;
    Stroke lineStyle;
    /**
     * The name of the x data
     */
    String xLabel;
    /**
     * The name of the y data
     */
    String yLabel;
    /**
     * Stores the min and max of each axis
     */
    double minX, maxX, minY, maxY;

    final List<ScatterPoint> points;
    List<ScatterLine> lines;


    protected AbstractScatter(String name, double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException();
        }
        minX = StatUtils.min(x);
        maxX = StatUtils.max(x);
        minY = StatUtils.min(y);
        maxY = StatUtils.max(y);
        points = new ArrayList<>(x.length);
        for (int i = 0; i < x.length; ++i) {
            points.add(new ScatterPoint(x[i], y[i], DEFAULT_MARKER_SIZE, EMPTY_RUNNABLE));
        }
        showInLegend(false);
        setName(name);
    }

    /**
     * Create an abstract XY series
     *
     * @param x the x data
     * @param y the y data
     * @throws IllegalArgumentException if the x and y series are not the same length
     */
    protected AbstractScatter(double[] x, double[] y) {
        this(null, x, y);
        showInLegend(false);
    }

    protected AbstractScatter(double[] x, DoubleUnaryOperator toYFunction) {
        this(x, map(x, toYFunction));
    }

    protected void fillDoubles(ObjDoubleConsumer<ScatterPoint> setter, Iterable<Double> values, double defaultValue) {
        final Iterator<ScatterPoint> iterator = points.iterator();
        final Iterator<Double> valueIterator = values.iterator();
        while (iterator.hasNext()) {
            setter.accept(iterator.next(), valueIterator.hasNext() ? valueIterator.next() : defaultValue);
        }
    }

    protected void fillDoubles(ObjDoubleConsumer<ScatterPoint> setter, Iterable<Double> values) {
        final Iterator<ScatterPoint> iterator = points.iterator();
        final Iterator<Double> valueIterator = values.iterator();
        while (iterator.hasNext() && valueIterator.hasNext()) {
            setter.accept(iterator.next(), valueIterator.next());
        }
    }

    protected <U> void fill(BiConsumer<ScatterPoint, U> setter, Iterable<U> values) {
        final Iterator<ScatterPoint> iterator = points.iterator();
        final Iterator<U> valueIterator = values.iterator();
        while (iterator.hasNext() && valueIterator.hasNext()) {
            setter.accept(iterator.next(), valueIterator.next());
        }
    }


    public S setOpacities(Iterable<Double> alphas) {
        fillDoubles((p, x) -> p.opacity = x, alphas, 1);
        return requestLayout();
    }

    public S setLineStyle(final Stroke style) {
        this.lineStyle = style;
        return requestLayout();
    }

    public S setLineWidth(double size) {
        return setLineStyle(new Stroke(lineStyle.getColor(), size));
    }

    public S setLineColor(Color color) {
        return setLineStyle(new Stroke(color, lineStyle.getWidth()));
    }

    //todo trendline, cluster

    @Override
    public S setColors(String name, Iterable<String> groups) {
        return super.setColors(name, groups);
    }

    public S setXLabel(String name) {
        ifAssigned(
                (chart, val) -> chart.getPlot().getXAxis().setTitle(val),
                (series, val) -> series.xLabel = val,
                name
        );
        return requestLayout();
    }

    public S setYLabel(String name) {
        ifAssigned(
                (chart, val) -> chart.getPlot().getYAxis().setTitle(val),
                (series, val) -> series.yLabel = val,
                name
        );
        return requestLayout();
    }

    /**
     * Set whether the series should be present in the legend
     *
     * @param showInLegend whether the series should be present in the legend
     * @return this XY series
     */
    public S showInLegend(boolean showInLegend) {
        this.showInLegend = showInLegend;
        return requestLayout();
    }

    /**
     * Set whether the series should be show in the color (only applicable if there is an associated colormap)
     *
     * @param showColorBar whether to show the series as a color bar
     * @return this series
     */
    public S showColorBar(boolean showColorBar) {
        this.showInColorBars = showColorBar;
        return requestLayout();
    }

    /**
     * Set the x and y labels at the same time
     *
     * @param xLabel the name of the x data
     * @param yLabel the name of the data
     * @return this series
     */
    public S setLabels(final String xLabel, final String yLabel) {
        return setXLabel(xLabel).setYLabel(yLabel);
    }

    /**
     * Create an array of y mapped from x
     *
     * @param x           the x data
     * @param toYFunction the map function
     * @return the mapped data
     */
    private static double[] map(double[] x, DoubleUnaryOperator toYFunction) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = toYFunction.applyAsDouble(x[i]);
        }
        return y;
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
        return minX;
    }

    @Override
    public double getMaxX() {
        return maxX;
    }

    @Override
    public double getMinY() {
        return minY;
    }

    @Override
    public double getMaxY() {
        return maxY;
    }

    @Override
    protected void assignToChart(Chart<?> chart) {
        super.assignToChart(chart);
        this.xLabel = null;
        this.yLabel = null;
    }
}
