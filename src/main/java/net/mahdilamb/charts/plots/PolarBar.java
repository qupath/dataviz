package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.ChartCanvas;

public final class PolarBar extends PlotSeries.Categorical<PolarBar> implements CircularPlot{
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

    @Override
    protected void drawSeries(Figure<?, ?> source, ChartCanvas<?> canvas, Plot<? extends PolarBar> plot) {

    }
}
