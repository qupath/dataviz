package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.colormap.Colormap;

import static net.mahdilamb.charts.utils.ArrayUtils.fill;

@PlotType(name = "Pie", compatibleSeries = {PlotType.DataType.STRING, PlotType.DataType.NUMERIC})
public final class Pie extends PlotSeries.Categorical<Pie> implements CircularPlot{
    double hole = 0;
    double[] pull;

    public Pie(String[] names, double[] values) {
        super(names, values);
       //todo grouping
    }

    public Pie setColorSequence(final Colormap colormap) {
        this.colormap = colormap;
        return redraw();
    }


    public Pie setHoleSize(double size) {
        this.hole = size;
        return redraw();
    }

    public Pie setPulls(final Iterable<Double> pulls) {
        this.pull = fill(new double[values.length], pulls, 0);
        return requestDataUpdate();
    }
}
