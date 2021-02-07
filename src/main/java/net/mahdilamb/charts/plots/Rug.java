package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;

final class Rug extends PlotSeries.Distribution<Rug>implements RectangularPlot {
    Rug(double[] values) {
        super(values);
    }
}
