package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;

public final class PolarScatter extends PlotSeries.XY<PolarScatter> {
    Scatter.Mode markerMode = Scatter.Mode.MARKER_ONLY;
    double startAngle = 0;
    boolean clockWise = true, linesClosed = true;

    public PolarScatter(double[] r, double[] theta) {
        super(r, theta);
    }

    public PolarScatter setMode(Scatter.Mode mode) {
        this.markerMode = mode;
        return requestLayout();
    }

    protected PolarScatter setRadialLabel(String name) {
        return super.setXLabel(name);
    }

    protected PolarScatter setAngularLabel(String name) {
        return super.setYLabel(name);
    }

    public PolarScatter setStartAngle(double startAngle) {
        this.startAngle = startAngle;
        return requestDataUpdate();
    }

    public PolarScatter setClockwise() {
        this.clockWise = true;
        return requestDataUpdate();
    }

    public PolarScatter setCounterClockwise() {
        this.clockWise = true;
        return requestDataUpdate();
    }

    public PolarScatter setLinesClosed(final boolean linesClosed) {
        this.linesClosed = linesClosed;
        return requestLayout();
    }

}
