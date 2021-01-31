package net.mahdilamb.charts.layouts;

import net.mahdilamb.charts.Axis;

public interface XYPlot<S> extends PlotLayout<S> {
    /**
     * @return the X axis
     */
    Axis getXAxis();

    /**
     * @return the y axis
     */
    Axis getYAxis();
}
