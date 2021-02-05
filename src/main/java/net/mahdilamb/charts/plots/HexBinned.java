package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;

public class HexBinned extends PlotSeries.Distribution2D<HexBinned> {
    int nBinsX = -1, nBinsY = 1;

    public HexBinned(double[] x, double[] y) {
        super(x, y);
    }

    public HexBinned setBinsX(int nBins) {
        this.nBinsX = nBins;
        return requestDataUpdate();
    }

    public HexBinned setBinsY(int nBins) {
        this.nBinsY = nBins;
        return requestDataUpdate();
    }
}
