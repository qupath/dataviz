package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.LegendItem;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.Axis;
import net.mahdilamb.charts.axes.NumericAxis;

abstract class AbstractCircular<R extends NumericAxis, A extends Axis> extends Plot {
    R radialAxis;
    A angularAxis;

    public R getRadialAxis() {
        return radialAxis;
    }

    public A getAngularAxis() {
        return angularAxis;
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
        layoutAxis(radialAxis,null);
        layoutAxis(angularAxis,null);
    }
}
