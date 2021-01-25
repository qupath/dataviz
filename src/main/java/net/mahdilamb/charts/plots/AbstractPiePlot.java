package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.LegendItem;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.axes.LabeledAxis;

abstract class AbstractPiePlot extends Plot {
    LabeledAxis axis;

    public LabeledAxis getAxis() {
        return axis;
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
        layoutAxis(axis,null);
    }
}
