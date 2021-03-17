package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataframe.utils.IntroSort;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;
import net.mahdilamb.dataviz.PlotTrace;
import net.mahdilamb.dataviz.utils.Kernels;
import net.mahdilamb.stats.ArrayUtils;
import net.mahdilamb.stats.StatUtils;

import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;

import static net.mahdilamb.dataviz.utils.Interpolations.lerp;

//TODO
final class Contour extends PlotData.DistributionData2D<Contour> {
    int xBins = 500;
    int yBins = 500;
    double[][] densities;
    DoubleUnaryOperator kernel = Kernels::gaussian;

    public Contour(DataFrame dataFrame, String x, String y) {
        super(dataFrame, x, y);
    }

    @Override
    protected void init(double[] x, double[] y) {
        super.init(x, y);
        putFormatter("density", i -> getRaw(colors.asB(), i));
        hoverFormatter.add("(%{density:s})");
    }

    @Override
    protected void init(PlotLayout plotLayout) {
        int[] xOrder = ArrayUtils.intRange(x.size());
        IntroSort.argSort(
                xOrder,
                (IntToDoubleFunction) i -> x.get(i),
                true
        );

        densities = new double[yBins][xBins];
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
        double minDensity = Double.POSITIVE_INFINITY;
        double maxDensity = Double.NEGATIVE_INFINITY;
        double[] kdes = new double[yBins * xBins];
        //TODO remove densities
        int j = 0;
        for (int y = 0; y < yBins; ++y) {
            double xCen = xMin + cellWidth * .5;
            for (int x = 0; x < xBins; ++x) {
                double finalXCen = xCen;
                double finalYCen = yCen;
                double kde = StatUtils.mean(i -> kernel.applyAsDouble(euclideanDistance(this.x.get(i), this.y.get(i), finalXCen, finalYCen) / h), this.x.size()) / h2;
                kdes[j++] = kde;
                densities[y][x] = kde;
                maxDensity = Math.max(maxDensity, kde);
                minDensity = Math.min(minDensity, kde);
                xCen += cellWidth;
            }
            yCen += cellHeight;
        }
        setColors(new PlotTrace.Numeric(this, Attribute.COLOR, "Density", kdes, 0, maxDensity));
        putPolygons(layout, this, createContours(this, kdes, minDensity, maxDensity, xBins, xMin, yMin, cellWidth, cellHeight));
        updateXYBounds(plotLayout, this.xMin, this.xMax, this.yMin, this.yMax, false, false);
    }

    private static double euclideanDistance(double v1x, double v1y, double v2x, double v2y) {
        return Math.sqrt((v1x - v2x) * (v1x - v2x) + (v1y - v2y) * (v1y - v2y));
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

    @Override
    protected int size() {
        return xBins * yBins;
    }
}
