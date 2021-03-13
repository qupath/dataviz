package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataframe.utils.IntroSort;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;
import net.mahdilamb.dataviz.PlotTrace;
import net.mahdilamb.dataviz.utils.DistanceMetrics;
import net.mahdilamb.dataviz.utils.Functions;
import net.mahdilamb.dataviz.utils.Kernels;
import net.mahdilamb.stats.ArrayUtils;
import net.mahdilamb.stats.StatUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;

import static net.mahdilamb.dataviz.utils.Interpolations.lerp;

public final class Density2D extends PlotData.DistributionData2D<Density2D> {
    int xBins = 50;
    int yBins = 50;
    DoubleUnaryOperator kernel = Kernels::gaussian;
    Functions.DoubleQuaternaryFunction distanceMetric = DistanceMetrics::euclidean;

    public Density2D(DataFrame dataFrame, String x, String y) {
        super(dataFrame, x, y);
    }

    @Override
    protected void init(double[] x, double[] y) {
        super.init(x, y);
        putFormatter("density", i -> getRaw(colors.asB(), i));
        hoverFormatter.add("(%{density:.7f})");
    }

    @Override
    protected void init(PlotLayout plotLayout) {
        int[] xOrder = ArrayUtils.intRange(x.size());
        IntroSort.argSort(
                xOrder,
                x.size(),
                (IntToDoubleFunction) i -> x.get(i),
                true
        );

        double xSD = StatUtils.standardDeviation(x::get, x.size());
        double ySD = StatUtils.standardDeviation(y::get, y.size());
        double xMin = this.xMin - xSD;
        double xMax = this.xMax + xSD;
        double yMin = this.yMin - ySD;
        double yMax = this.yMax + ySD;
        double h = bandwidthNRD(x, xOrder);
        double h2 = h * h;
        double cellWidth = (xMax - xMin) / xBins;
        double cellHeight = (yMax - yMin) / yBins;
        double yCen = yMin + cellHeight * .5;

        final double[] kdes = new double[yBins * xBins];
        final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int j = 0;
        for (int y = 0; y < yBins; ++y) {
            double xCen = xMin + cellWidth * .5;
            for (int x = 0; x < xBins; ++x) {
                double finalXCen = xCen;
                double finalYCen = yCen;
                final int l = j;
                pool.submit(
                        () -> kdes[l] = StatUtils.mean(i -> kernel.applyAsDouble(distanceMetric.apply(Density2D.this.x.get(i), Density2D.this.y.get(i), finalXCen, finalYCen) / h), Density2D.this.x.size()) / h2
                );
                ++j;
                xCen += cellWidth;
            }
            yCen += cellHeight;
        }
        pool.shutdown();
        try {
            if (pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                setColors(new PlotTrace.Numeric(this, Attribute.COLOR, "Density", kdes, 0, StatUtils.max(kdes)));
                putRectangles(layout, this, createRectangles(this, kdes, xBins, xMin, yMin, cellWidth, cellHeight));
                updateXYBounds(plotLayout, this.xMin, this.xMax, this.yMin, this.yMax, false, false);
            } else {
                System.err.println("2D KDE took longer than the maximum available time");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the distance metric to be used for computing the density
     *
     * @param distanceMetric the name of the distance metric (e.g. "euclid", "manhattan", "chebyshev", "cosine",
     *                       "braycurtis"). The default is euclidean
     * @return this 2d density plot
     */
    public Density2D setDistanceMetric(final String distanceMetric) {
        this.distanceMetric = DistanceMetrics.getDistanceMetric(distanceMetric);
        clear();
        return this;
    }

    /**
     * Set the kernel for calculating the 2D distance
     *
     * @param kernel the name of the kernel (e.g. "gaussian", "tophat", "trigonometric", "epanechnikov", "triangular",
     *               "exponential"). The default is gaussian
     * @return this 2d density plot
     */
    public Density2D setKernel(final String kernel) {
        this.kernel = Kernels.getKernel(kernel);
        clear();
        return this;
    }

    @Override
    protected int size() {
        return xBins * yBins;
    }

    private static double quantile(DoubleArrayList values, int[] order, double quantile) {
        double pos = quantile * values.size();
        int p = (int) pos;
        if (pos != p) {
            return lerp(values.get(order[p]), values.get(order[p + 1]), pos - p);
        } else {
            return values.get(order[p]);
        }
    }

    /**
     * Adapted from https://stat.ethz.ch/R-manual/R-devel/library/MASS/html/bandwidth.nrd.html
     *
     * @param values the values to get the bandwidth of
     * @param order  the sort order of the values
     * @return the default bandwidth
     */
    static double bandwidthNRD(DoubleArrayList values, int[] order) {
        double h = (quantile(values, order, .75) - quantile(values, order, .25)) / 1.34;
        return Math.pow(4 * 1.06 * Math.min(Math.sqrt(StatUtils.variance(values::get, values.size())), h) * values.size(), -.2);
    }
}
