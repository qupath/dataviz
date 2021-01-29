package net.mahdilamb.charts.layouts;

import net.mahdilamb.charts.Axis;
import net.mahdilamb.charts.plots.PlotSeries;

/**
 * A circular plot containing two axes
 */
public interface Circular2DPlot<S extends PlotSeries<S>> extends Plot<S> {
    /**
     * @return the "radial" or outside axis
     */
    Axis getRadialAxis();

    /**
     * @return the "angular" or inner axis
     */
    Axis getAngularAxis();
}
