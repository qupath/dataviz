package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.dataframe.utils.DoubleArrayList;
import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.MarkerShape;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.statistics.StatUtils;
import net.mahdilamb.charts.utils.ArrayUtils;
import net.mahdilamb.charts.utils.IntersectsPoint;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.geom2d.trees.PointNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;

import static net.mahdilamb.charts.Figure.DEFAULT_QUALITATIVE_COLORMAP;
import static net.mahdilamb.charts.plots.Scatter.DEFAULT_MARKER_SIZE;
import static net.mahdilamb.charts.utils.ArrayUtils.rescale;

abstract class AbstractScatter<S extends AbstractScatter<S>> extends PlotSeries<S> implements RectangularPlot {

    protected static final class ScatterPoint<S extends AbstractScatter<S>> extends PointNode<Runnable> implements IntersectsPoint {

        public int i;
        S series;
        Color color = null;

        public ScatterPoint(final S series, double x, double y, Runnable callback) {
            super(x, y, (callback == null ? EMPTY_RUNNABLE : callback));
            this.series = series;
        }

        @Override
        public boolean intersects(double x, double y) {
            //TODO
            return false;
        }

        public void clear() {
            color = null;
        }

        public Color getColor() {
            if (color == null) {
                color = series.color == null ? DEFAULT_QUALITATIVE_COLORMAP.get(0) : series.color;
                final TraceGroup<?> colors = series.getAttribute(AttributeType.COLOR);
                if (colors != null) {
                    if (colors.source.getClass() == QualitativeColorAttribute.class) {
                        final Colormap colormap = ((QualitativeColorAttribute) colors.source).colormap == null ? DEFAULT_QUALITATIVE_COLORMAP : ((QualitativeColorAttribute) colors.source).colormap;
                        color = ((QualitativeColorAttribute) colors.source).get(colormap,i);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
                final TraceGroup<?> opacities = series.getAttribute(AttributeType.OPACITY);
                //TODO apply opacity
                if(series.opacity !=1){
                    color = new Color(color.red(),color.green(),color.blue(),series.opacity);
                }
            }
            return color;
        }
        public double getSize(){
            final TraceGroup<?> sizes = series.getAttribute(AttributeType.SIZE);
            if (sizes != null){
                return ((DimensionMap)sizes.source).get(i);
            }
            return series.size;
        }

    }


    Stroke lineStyle;
    /**
     * Default attributes
     */
    protected Color color = null;
    double opacity = 1;
    double size = DEFAULT_MARKER_SIZE;
    MarkerShape shape = MarkerShape.POINT;
    protected DoubleArrayList x, y;
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
        this.x = new DoubleArrayList(x);
        this.y = new DoubleArrayList(y);
        minX = StatUtils.min(x);
        maxX = StatUtils.max(x);
        minY = StatUtils.min(y);
        maxY = StatUtils.max(y);

        showLegend(false);
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

    /**
     * Set the opacities for each data element. If the length of the iterable is shorter than the number of points,
     * the opacity will be the default for the series. The opacities are assumed to be between 0 and 1.0.
     *
     * @param opacities the opacity values
     * @return this series
     * @see #setOpacities(Iterable, double, double) if the values need to be scaled before use
     */
    public S setOpacities(Iterable<Double> opacities) {
        final TraceGroup<DoubleArrayList> group = TraceGroup.createForDoubleArrayList(new DoubleArrayList(ArrayUtils.fill(new double[size()], opacities, Double.NaN)));
        addAttribute(AttributeType.OPACITY, group);
        return redraw();
    }

    /**
     * Set the opacities for each data element, scaled between the requested min and max
     *
     * @param opacity    the opacities
     * @param minOpacity the min opacity to scale to
     * @param maxOpacity the max opacity to scale to
     * @return this series
     */
    public S setOpacities(Iterable<Double> opacity, double minOpacity, double maxOpacity) {
        minOpacity = !Double.isFinite(minOpacity) ? 0 : minOpacity;
        maxOpacity = !Double.isFinite(maxOpacity) ? 1 : maxOpacity;
        addAttribute(AttributeType.OPACITY, TraceGroup.createForDoubleArrayList(
                new DoubleArrayList(
                        rescale(ArrayUtils.fill(new double[size()], opacity, Double.NaN), minOpacity, maxOpacity)))
        );
        return redraw();
    }


    /**
     * Set the line style, if lines are shown
     *
     * @param style the stroke
     * @return this series
     */
    public S setLineStyle(final Stroke style) {
        this.lineStyle = style;
        return redraw();
    }

    /**
     * Set the label for the x data
     *
     * @param name the label for the x data
     * @return this series
     */
    public S setXLabel(String name) {
        return orApplyToChart(
                (series, val) -> series.xLabel = val,
                (chart, val) -> chart.getPlot().getXAxis().setTitle(val),
                name
        );
    }

    /**
     * Set the label for the y data
     *
     * @param name the label for the y data
     * @return this series
     */
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
    public S showLegend(boolean showInLegend) {
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
    static double[] map(double[] x, DoubleUnaryOperator toYFunction) {
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
        return String.format("Scatter {name: %s, n: %d, %sx%s: %.2f to %.2f, %sy%s: %.2f to %.2f}", name, size(), sep, formatName(xLabel), minX, maxX, sep, formatName(yLabel), minY, maxY);
    }


    @Override
    protected <T> TraceGroup<T> addAttribute(AttributeType type, TraceGroup<T> attribute) {
        if (attribute.traces !=null && attribute.traces.length > 1){
            opacity = 0.8;
        }
        return super.addAttribute(type, attribute);
    }
}
