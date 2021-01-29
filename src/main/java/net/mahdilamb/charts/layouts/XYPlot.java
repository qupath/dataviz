package net.mahdilamb.charts.layouts;

import net.mahdilamb.charts.Axis;
import net.mahdilamb.charts.plots.PlotSeries;

/**
 * A rectangular plot containing an x and y axis
 */
public interface XYPlot<S extends PlotSeries<S>> extends Plot<S> {
    /**
     * @return the X axis
     */
    Axis getXAxis();

    /**
     * @return the y axis
     */
    Axis getYAxis();
}
