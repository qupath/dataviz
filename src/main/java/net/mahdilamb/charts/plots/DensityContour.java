package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;

public class DensityContour extends PlotSeries.Distribution2D<DensityContour>implements RectangularPlot {
    boolean showLabels = false, useFill = false, showOutline = true;

    public DensityContour(double[] x, double[] y) {
        super(x, y);
    }

    public DensityContour showLabels(boolean showLabels) {
        this.showLabels = showLabels;
        return requestDataUpdate();
    }

    public DensityContour setFilled(boolean filled) {
        this.useFill = filled;
        return redraw();
    }

    public DensityContour showOutlines(boolean showOutline) {
        this.showOutline = showOutline;
        return redraw();
    }
}
