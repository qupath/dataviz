package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.ChartCanvas;

public class Histogram2D extends PlotSeries.Distribution2D<Histogram2D> implements RectangularPlot{
    double nBinsX = Double.NaN, nBinsY = Double.NaN;

    public Histogram2D(double[] x, double[] y) {
        super(x, y);
    }

    public Histogram2D setBinsX(double nBins) {
        this.nBinsX = nBins;
        return requestDataUpdate();
    }

    public Histogram2D setBinsY(double nBins) {
        this.nBinsY = nBins;
        return requestDataUpdate();
    }

    @Override
    protected void drawSeries(Figure<?, ?> source, ChartCanvas<?> canvas, Plot<? extends Histogram2D> plot) {

    }
}
