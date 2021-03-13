package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;
import net.mahdilamb.dataviz.utils.Kernels;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.stats.BinWidthEstimator;
import net.mahdilamb.stats.StatUtils;

import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.stats.ArrayUtils.linearlySpaced;

/**
 * KDE plot
 */
public final class KDE extends PlotData.DistributionData<KDE> {
    double[] binEdges;
    DoubleUnaryOperator kernel = Kernels::gaussian;

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
        net.mahdilamb.stats.Histogram histogram;
        if (binEdges == null) {
            histogram = StatUtils.histogram(BinWidthEstimator.NUMPY_AUTO, values);
        } else {
            histogram = StatUtils.histogram(binEdges, values);
        }

        double stDev = StatUtils.NaNStandardDeviation(values);
        double max = getMax() + stDev;
        double min = getMin() - stDev;
        final double[] xs = linearlySpaced(min, max, 1000);
        final double[] ys = new double[xs.length];
        double n = StatUtils.NaNCount(values);
        double bandwidth = 1.05 * stDev * Math.pow(n, (-.2));

        for (int i = 0; i < xs.length; ++i) {
            ys[i] = kde(xs[i], values, bandwidth, kernel);
        }
        final double binWidth = (histogram.getBinEdges()[histogram.getCount().length] - histogram.getBinEdges()[0]) / histogram.getCount().length;
        final double denom;
        if (plotLayout.getYAxis().getTitle() != DENSITY_LABEL) {
            denom = 1. / binWidth / n;
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
    /*
    public KDE setKernel(final String name) {
        switch (name.toLowerCase()) {
            case "gauss":
            case "gaussian":
            case "normal":
                kernel = KDE::gaussian;
                break;
            case "tophat":
            case "box":
                kernel = KDE::box;
                break;
            case "cos":
            case "cosine":
                kernel = KDE::trigonometric;
                break;
            case "epanechnikov":
                kernel = KDE::epanechnikov;
                break;
            case "triangular":
                kernel = KDE::triangular;
                break;
            default:
                throw new UnsupportedOperationException("Could not find a kernel called " + name);
        }
        return this;
    }*/


}
