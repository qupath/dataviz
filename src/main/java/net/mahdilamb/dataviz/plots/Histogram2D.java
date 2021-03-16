package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.stats.ArrayUtils;

public final class Histogram2D extends PlotData.DistributionData2D<Histogram2D> {
    public Histogram2D(DataFrame dataFrame, String x, String y) {
        super(dataFrame, x, y);
    }

    public Histogram2D setXBins(int bins) {
        this.xEdges = ArrayUtils.linearlySpaced(xMin, xMax, bins + 1);
        return clear();
    }

    public Histogram2D setYBins(int bins) {
        this.yEdges = ArrayUtils.linearlySpaced(yMin, yMax, bins + 1);
        return clear();
    }

    public Histogram2D setXBins(double start, double end, int bins) {
        this.xEdges = ArrayUtils.linearlySpaced(Math.min(start, end), Math.max(start, end), bins);
        return clear();

    }

    public Histogram2D setYBins(double start, double end, int bins) {
        this.yEdges = ArrayUtils.linearlySpaced(Math.min(start, end), Math.max(start, end), bins);
        return clear();
    }

    @Override
    protected void init(double[] x, double[] y) {
        super.init(x, y);
        addCountToHoverText();
    }

    @Override
    protected void init(PlotLayout plotLayout) {
        final RTree<Runnable> rectangles = createRectangles(this);
        putRectangles(plotLayout, this, rectangles);
        updateXYBounds(plotLayout, rectangles, false, false);
    }


}
