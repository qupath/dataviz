package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;
import net.mahdilamb.dataviz.utils.BandwidthEstimators;
import net.mahdilamb.dataviz.utils.Kernels;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.stats.StatUtils;

import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.stats.ArrayUtils.intRange;
import static net.mahdilamb.stats.ArrayUtils.linearlySpaced;

/**
 * KDE plot
 */
public final class KDE extends PlotData.DistributionData<KDE> {
    DoubleUnaryOperator kernel = Kernels::gaussian;
    double bandwidth = Double.NaN;
    int[] order;

    /**
     * Create a histogram series from a dataframe
     *
     * @param dataFrame the dataframe
     * @param y         the series containing values to create a histogram out of
     */
    public KDE(DataFrame dataFrame, String y) {
        super(dataFrame, y);
    }

    public KDE(double[] values) {
        super(values);
    }

    private static double kde(double x, double[] values, double bandwidth, DoubleUnaryOperator kernel) {
        if (values.length == 0) {
            return 0;
        }
        double res = 0;
        int j = 0;
        for (double value : values) {
            if (Double.isNaN(value)) {
                continue;
            }
            res += kernel.applyAsDouble((x - value) / bandwidth);
            ++j;
        }
        return res / (j * bandwidth);
    }

    @Override
    protected void init(PlotLayout plotLayout) {

        double stDev = StatUtils.NaNStandardDeviation(values::get, values.size());
        double max = getMax() + stDev;
        double min = getMin() - stDev;
        final double[] xs = linearlySpaced(min, max, 1000);
        final double[] ys = new double[xs.length];
        double n = StatUtils.NaNCount(values::get, values.size());
        if (Double.isNaN(bandwidth)) {
            bandwidth = BandwidthEstimators.scottsRule(values, getOrder());
        }
        for (int i = 0; i < xs.length; ++i) {
            ys[i] = kde(xs[i], values.toArray(), bandwidth, kernel);
        }
        final double denom;
        if (plotLayout.getYAxis().getTitle() != DENSITY_LABEL) {
            denom = 1. / bandwidth / n;
            for (int i = 0; i < xs.length; ++i) {
                ys[i] /= denom;
            }
        }
        final RTree<Runnable> lines = createLines(layout, xs, ys);
        putLines(layout, this, lines);

        updateXYBounds(
                plotLayout,
                getMin(),
                getMax(),
                0,
                lines.getMaxY(),
                false,
                true
        );
    }

    private int[] getOrder() {
        if (order == null) {
            order = intRange(values.size());
        }
        return order;
    }

    public KDE setKernel(final String name) {
        this.kernel = Kernels.getKernel(name);
        clear();
        return this;
    }

    /**
     * Set the bandwidth using a nonparametric estimator (e.g. "scott" or "silverman")
     *
     * @param name the band width estimator
     * @return this KDE plot
     */
    public KDE setBandwidth(final String name) {
        bandwidth = BandwidthEstimators.getEstimator(name).apply(values, getOrder());
        clear();
        return this;
    }

    public KDE setBandwidth(double value) {
        this.bandwidth = value;
        clear();
        return this;
    }


}
