package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.Orientation;
import net.mahdilamb.charts.statistics.BinWidthEstimator;
import net.mahdilamb.charts.statistics.StatUtils;

public final class Histogram extends PlotSeries.Distribution<Histogram> {

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
        return requestLayout();
    }

    public Histogram setMarginalMode(final MarginalMode mode) {
        this.mode = mode;
        return requestDataUpdate();
    }

    @Override
    public Histogram setColors(String name, Iterable<String> groups) {
        return super.setColors(name, groups);
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
}
