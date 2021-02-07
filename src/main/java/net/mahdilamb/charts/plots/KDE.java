package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;

final class KDE extends PlotSeries.Distribution<KDE>implements RectangularPlot {
    KDE(double[] values) {
        super(values);
    }
}
