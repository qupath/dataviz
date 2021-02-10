package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;

public final class PolarScatter extends PlotSeries<PolarScatter> implements CircularPlot{
    Scatter.Mode markerMode = Scatter.Mode.MARKER_ONLY;
    double startAngle = 0;
    boolean clockWise = true, linesClosed = true;

    String rLabel;
    String thetaLabel;
    protected double[] r;
    protected double[] theta;

    public PolarScatter(double[] r, double[] theta) {
        this.r = r;
        this.theta = theta;
        if (r.length != theta.length) {
            throw new IllegalArgumentException();
        }
    }

    public PolarScatter setMode(Scatter.Mode mode) {
        this.markerMode = mode;
        return redraw();
    }

    protected PolarScatter setRadialLabel(String name) {
        this.rLabel = name;
        return redraw();
    }

    protected PolarScatter setAngularLabel(String name) {
        this.thetaLabel = name;
        return redraw();
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
        return redraw();
    }

}
