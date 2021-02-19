package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Orientation;
import net.mahdilamb.charts.statistics.BinWidthEstimator;
import net.mahdilamb.charts.statistics.StatUtils;

public final class Histogram extends PlotSeries.Distribution<Histogram>implements RectangularPlot {

    net.mahdilamb.charts.statistics.Histogram histogram;
    Orientation orientation = Orientation.VERTICAL;
    MarginalMode mode = MarginalMode.NONE;
    Bar.Mode barMode = Bar.Mode.STACKED;
    boolean useNormalized = false, useCumulative = false;

    public Histogram(double[] values, BinWidthEstimator estimator) {
        super(values);
        this.histogram = StatUtils.histogram(estimator, values);
    }

    public Histogram(double[] values) {
        this(values, BinWidthEstimator.NUMPY_AUTO);
    }

    public Histogram(double[] values, int numBins) {
        super(values);
        this.histogram = StatUtils.histogram(numBins, values);
    }


    public Histogram setOrientation(final Orientation orientation) {
        this.orientation = orientation;
        return redraw();
    }

    public Histogram setMarginalMode(final MarginalMode mode) {
        this.mode = mode;
        return requestDataUpdate();
    }

    public Histogram setNormalized(boolean normalized) {
        this.useNormalized = normalized;
        return requestDataUpdate();
    }

    public Histogram setBarMode(final Bar.Mode mode) {
        this.barMode = mode;
        return requestDataUpdate();
    }

    public Histogram setCumulative(final boolean cumulative) {
        this.useCumulative = cumulative;
        return requestDataUpdate();
    }

    @Override
    protected void drawSeries(Figure<?, ?> source, ChartCanvas<?> canvas, Plot<? extends Histogram> plot) {

    }
}
