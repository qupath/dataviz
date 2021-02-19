package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Orientation;
import net.mahdilamb.charts.graphics.Stroke;

import static net.mahdilamb.charts.utils.ArrayUtils.fill;

public final class Bar extends PlotSeries.Categorical<Bar> implements RectangularPlot {


    @Override
    protected void drawSeries(Figure<?, ?> source, ChartCanvas<?> canvas, Plot<? extends Bar> plot) {

    }

    public enum Mode {
        STACKED,
        GROUPED,
        OVERLAY
    }

    static final double DEFAULT_WIDTH = 0.8;
    static final double DEFAULT_BASE = 0;
    /**
     * The default offset on the categorical axis
     */
    static final double DEFAULT_OFFSET = 0;

    Orientation orientation = Orientation.VERTICAL;

    String xLabel, yLabel;

    double groupGap = 0;
    double gap = 0;
    Mode mode = Mode.GROUPED;
    double[] widths;
    double[] bases;

    double[] errorLower;
    double[] errorUpper;
    Stroke errorUpperStroke;
    Stroke errorLowerStroke;

    double[] colorScale;

    double offset = DEFAULT_OFFSET;

    public Bar(String[] names, double[] values) {
        super(names, values);
        //todo grouping
    }

    public Bar setWidths(Iterable<Double> widths) {
        this.widths = fill(new double[values.length], widths, DEFAULT_WIDTH);
        return redraw();
    }

    public Bar setBases(Iterable<Double> bases) {
        this.bases = fill(new double[values.length], bases, 0);
        return redraw();
    }

    public Bar setErrorUpper(Iterable<Double> error) {
        this.errorUpper = fill(new double[values.length], error, Double.NaN);
        return redraw();
    }

    public Bar setErrorLower(Iterable<Double> error) {
        this.errorLower = fill(new double[values.length], error, Double.NaN);
        return redraw();
    }

    public Bar setBarGap(double gap) {
        this.gap = gap;
        return redraw();
    }

    public Bar setOffset(double offset) {
        this.offset = offset;
        return redraw();
    }

    public Bar setMode(Mode mode) {
        this.mode = mode;
        return redraw();
    }

    public Bar setGroupGap(double gap) {
        this.groupGap = gap;
        return redraw();
    }

    public Bar setColors(final Iterable<Double> colors) {
        this.colorScale = fill(new double[values.length], colors, Double.NaN);
        return redraw();
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
        return super.getMinX();
    }

    @Override
    public double getMaxX() {
        return super.getMaxX();
    }

    @Override
    public double getMinY() {
        return super.getMinY();
    }

    @Override
    public double getMaxY() {
        return super.getMaxY();
    }

}
