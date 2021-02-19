package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Orientation;

public class Strip extends PlotSeries.Distribution<Strip>  {

    Orientation orientation = Orientation.VERTICAL;

    String xLabel, yLabel;


    protected double jitter = 0, pointPos = 0;

    public Strip(double[] values) {
        super(values);
    }


    public Strip setOrientation(final Orientation orientation) {
        this.orientation = orientation;
        return redraw();
    }

    @Override
    public String getXLabel() {
        return xLabel;
    }

    @Override
    public String getYLabel() {
        return yLabel;
    }


    @Override
    protected void drawSeries(Figure<?, ?> source, ChartCanvas<?> canvas, Plot<? extends Strip> plot) {

    }
}
