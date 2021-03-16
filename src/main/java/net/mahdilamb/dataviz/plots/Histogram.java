package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.stats.BinWidthEstimator;
import net.mahdilamb.stats.StatUtils;

import static net.mahdilamb.stats.ArrayUtils.linearlySpaced;

/**
 * Histogram series
 */
public final class Histogram extends PlotData.DistributionData<Histogram> {
    double[] binEdges;
    boolean useDensity = false;

    /**
     * Create a histogram series from a dataframe
     *
     * @param dataFrame the dataframe
     * @param y         the series containing values to create a histogram out of
     */
    public Histogram(DataFrame dataFrame, String y) {
        super(dataFrame, y);
    }

    public Histogram(double[] values) {
        super(values);
    }

    /**
     * Set the number of bins in the histogram
     *
     * @param numBins the number of bins
     * @return this histogram plot
     */
    public Histogram setNumBins(int numBins) {
        this.binEdges = linearlySpaced(getMin(), getMax(), numBins);
        clear();
        return this;
    }

    public Histogram useDensity(final boolean useDensity) {
        this.useDensity = useDensity;
        yLabel = useDensity ? DENSITY_LABEL : COUNT_LABEL;
        clear();
        return this;
    }

    @Override
    protected void init(PlotLayout plotLayout) {
        net.mahdilamb.stats.Histogram histogram;
        //TODO new histogram method that is function
        if (binEdges == null) {
            histogram = StatUtils.histogram(BinWidthEstimator.NUMPY_AUTO, values.toArray());
        } else {
            histogram = StatUtils.histogram(binEdges, values.toArray());
        }


        final RTree<Runnable> rectangles = createRectangles(layout, histogram, useDensity);
        putRectangles(layout, this, rectangles);
        updateXYBounds(
                plotLayout,
                rectangles.getMinX(),
                histogram.getBin(histogram.numBins() - 1).get(0) + 1,
                0,
                rectangles.getMaxY(),
                true,
                true
        );
    }

}
