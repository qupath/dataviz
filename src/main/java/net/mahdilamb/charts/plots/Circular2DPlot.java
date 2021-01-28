package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Axis;
import net.mahdilamb.charts.series.PlotSeries;

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
