package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;

public final class PolarBar extends PlotSeries.Categorical<PolarBar> {
    double startAngle = 0;
    boolean clockWise = true;

    public PolarBar(String[] names, double[] values) {
        super(names, values);
    }

    public PolarBar setStartAngle(double startAngle) {
        this.startAngle = startAngle;
        return requestDataUpdate();
    }

    public PolarBar setClockwise() {
        this.clockWise = true;
        return requestDataUpdate();
    }

    public PolarBar setCounterClockwise() {
        this.clockWise = true;
        return requestDataUpdate();
    }
}
