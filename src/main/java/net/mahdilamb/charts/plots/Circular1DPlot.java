package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Axis;
import net.mahdilamb.charts.series.PlotSeries;

/**
 * A circular plot that only contains 1 axis. E.g. pie plot
 */
public interface Circular1DPlot<S extends PlotSeries<S>> extends Plot<S> {
    /**
     * @return the axis of the plot
     */
    Axis getAxis();
}
