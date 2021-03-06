package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.PlotData;

public final class Histogram2D extends PlotData.Distribution2D<Histogram2D> {
    public Histogram2D(DataFrame dataFrame, String x, String y) {
        super(dataFrame, x, y);
    }
    public Histogram2D setXBins(int bins){
        this.xBins = bins;
        return this;
    }
    public Histogram2D setYBins(int bins){
        this.yBins = bins;
        return this;
    }
}
