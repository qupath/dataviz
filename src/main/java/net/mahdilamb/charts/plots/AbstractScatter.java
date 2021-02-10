package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.MarkerShape;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.statistics.StatUtils;
import net.mahdilamb.charts.utils.ArrayUtils;
import net.mahdilamb.charts.utils.IntersectsPoint;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.geom2d.trees.PointNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;

import static net.mahdilamb.charts.plots.Scatter.DEFAULT_MARKER_SIZE;

abstract class AbstractScatter<S extends AbstractScatter<S>> extends PlotSeries<S> implements RectangularPlot {

    protected static final class ScatterPoint extends PointNode<Runnable> implements IntersectsPoint {

        int i = -1;

        public ScatterPoint(double x, double y, Runnable callback) {
            super(x, y, (callback == null ? EMPTY_RUNNABLE : callback));
        }

        @Override
        public boolean intersects(double x, double y) {
            //TODO
            return false;
        }
    }


    Stroke lineStyle;
    final List<ScatterPoint> points;
    /**
     * Global attributes
     */
    protected Color color;
    double opacity = 1;
    double size = DEFAULT_MARKER_SIZE;
    MarkerShape shape = MarkerShape.POINT;

    /**
     * The name of the x data
     */
    String xLabel = "x";
    /**
     * The name of the y data
     */
    String yLabel = "y";
    /**
     * Stores the min and max of each axis
     */
    double minX, maxX, minY, maxY;

    protected AbstractScatter(String name, double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException();
        }
        minX = StatUtils.min(x);
        maxX = StatUtils.max(x);
        minY = StatUtils.min(y);
        maxY = StatUtils.max(y);
        points = new ArrayList<>(x.length);
        for (int i = 0; i < x.length; ) {
            final ScatterPoint sp = new ScatterPoint(x[i], y[i], EMPTY_RUNNABLE);
            sp.i = i++;
            points.add(sp);
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
    }

    protected AbstractScatter(double[] x, DoubleUnaryOperator toYFunction) {
        this(x, map(x, toYFunction));
    }

    public S setOpacities(final String opacities, Iterable<Double> alphas) {
        //TODO
        addAttribute(AttributeType.OPACITY,new ScaledDoubleDataAttribute(opacities, ArrayUtils.fill(new double[points.size()], alphas, opacity)));
        return redraw();
    }

    public S setLineStyle(final Stroke style) {
        this.lineStyle = style;
        return redraw();
    }

    public S setXLabel(String name) {
        return orApplyToChart(
                (series, val) -> series.xLabel = val,
                (chart, val) -> chart.getPlot().getXAxis().setTitle(val),
                name
        );
    }

    public S setYLabel(String name) {
        return orApplyToChart(
                (series, val) -> series.yLabel = val,
                (chart, val) -> chart.getPlot().getYAxis().setTitle(val),
                name
        );
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
     * Set whether the series should be present in the legend
     *
     * @param showInLegend whether the series should be present in the legend
     * @return this XY series
     */
    public S showInLegend(boolean showInLegend) {
        this.showInLegend = showInLegend;
        return redraw();
    }

    /**
     * Set whether the series should be show in the color (only applicable if there is an associated colormap)
     *
     * @param showColorBar whether to show the series as a color bar
     * @return this series
     */
    public S showColorBar(boolean showColorBar) {
        this.showInColorBars = showColorBar;
        return redraw();
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
    protected void assign(Figure<?, ?> chart) {
        super.assign(chart);
        this.xLabel = null;
        this.yLabel = null;
    }

    @Override
    public String toString() {
        final String sep = "\n         ";
        return String.format("Scatter {name: %s, n: %d, %sx%s: %.2f to %.2f, %sy%s: %.2f to %.2f}", name, points.size(), sep, formatName(xLabel), minX, maxX, sep, formatName(yLabel), minY, maxY);
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
}
