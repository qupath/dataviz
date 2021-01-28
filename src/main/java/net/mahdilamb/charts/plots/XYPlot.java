package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Axis;
import net.mahdilamb.charts.series.PlotSeries;

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
