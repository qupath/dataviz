package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;
import net.mahdilamb.dataviz.PlotTrace;
import net.mahdilamb.dataviz.utils.BandwidthEstimators;
import net.mahdilamb.dataviz.utils.DistanceMetrics;
import net.mahdilamb.dataviz.utils.Functions;
import net.mahdilamb.dataviz.utils.Kernels;
import net.mahdilamb.stats.ArrayUtils;
import net.mahdilamb.stats.StatUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleUnaryOperator;

public final class Density2D extends PlotData.DistributionData2D<Density2D> {
    int xBins = 20;
    int yBins = 20;
    DoubleUnaryOperator kernel = Kernels::gaussian;
    Functions.DoubleQuaternaryFunction distanceMetric = DistanceMetrics::euclidean;
    double bandwidth = Double.NaN;

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
        int[] yOrder = ArrayUtils.intRange(y.size());

        double xMin = this.xMin;
        double xMax = this.xMax;
        double yMin = this.yMin;
        double yMax = this.yMax;
        if (Double.isNaN(bandwidth)) {
            bandwidth = Math.sqrt(BandwidthEstimators.silvermansRule(x, xOrder) * BandwidthEstimators.silvermansRule(y, yOrder));
        }
        double h2 = bandwidth * bandwidth;

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
                        () -> kdes[l] = StatUtils.mean(i -> kernel.applyAsDouble(distanceMetric.apply(Density2D.this.x.get(i), Density2D.this.y.get(i), finalXCen, finalYCen) / bandwidth), Density2D.this.x.size()) / h2
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
        this.distanceMetric = DistanceMetrics.getDistanceMetric2D(distanceMetric);
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

    public Density2D setXBins(int bins) {
        this.xBins = bins;
        clear();
        return this;
    }

    public Density2D setYBins(int bins) {
        this.yBins = bins;
        clear();
        return this;
    }

    @Override
    protected int size() {
        return xBins * yBins;
    }

}
