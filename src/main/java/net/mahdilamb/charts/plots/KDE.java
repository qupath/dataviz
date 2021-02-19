package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.ChartCanvas;

final class KDE extends PlotSeries.Distribution<KDE>implements RectangularPlot {
    KDE(double[] values) {
        super(values);
    }

    @Override
    protected void drawSeries(Figure<?, ?> source, ChartCanvas<?> canvas, Plot<? extends KDE> plot) {

    }
}
