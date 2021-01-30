package net.mahdilamb.charts.layouts;

import net.mahdilamb.charts.Axis;

/**
 * A circular plot containing two axes
 */
public interface Circular2DPlot<S> extends PlotLayout<S> {
    /**
     * @return the "radial" or outside axis
     */
    Axis getRadialAxis();

    /**
     * @return the "angular" or inner axis
     */
    Axis getAngularAxis();
}
