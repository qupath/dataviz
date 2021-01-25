package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.LegendItem;
import net.mahdilamb.charts.Axis;
import net.mahdilamb.charts.Plot;

/**
 * A plot containing multiple rectangular plots
 */
abstract class AbstractGridPlot<X extends Axis, Y extends Axis> extends Plot {
    private X xAxis;
    private Y yAxis;

    public final X getXAxis() {
        return xAxis;
    }

    public final Y getYAxis() {
        return yAxis;
    }


    @Override
    protected void layoutPlot(double x, double y, double width, double height) {
        //TODO
    }

    @Override
    protected Iterable<LegendItem> getLegendItems() {
        //TODO
        return null;
    }

    @Override
    protected void layoutAxes() {
        //TODO
    }
}
