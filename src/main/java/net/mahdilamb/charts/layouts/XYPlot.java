package net.mahdilamb.charts.layouts;

import net.mahdilamb.charts.Axis;

/**
 * A rectangular plot containing an x and y axis
 */
public interface XYPlot<S > extends PlotLayout<S> {
    /**
     * @return the X axis
     */
    Axis getXAxis();

    /**
     * @return the y axis
     */
    Axis getYAxis();
}
