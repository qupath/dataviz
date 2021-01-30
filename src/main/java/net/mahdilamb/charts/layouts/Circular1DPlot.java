package net.mahdilamb.charts.layouts;

import net.mahdilamb.charts.Axis;

/**
 * A circular plot that only contains 1 axis. E.g. pie plot
 */
public interface Circular1DPlot<S> extends PlotLayout<S> {
    /**
     * @return the axis of the plot
     */
    Axis getAxis();
}
