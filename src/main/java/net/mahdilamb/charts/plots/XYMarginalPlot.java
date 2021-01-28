package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.series.PlotSeries;

/**
 * An XY plot with potentially two marginals
 */
public interface XYMarginalPlot<S extends PlotSeries<S>> extends XYPlot<S> {
    /**
     * @return the marginal associated with the x axis
     */
    Plot<?> getXMarginal();

    /**
     * @return the marginal associated with the y axis
     */
    Plot<?> getYMarginal();

    @Override
    default int numSeries() {
        return 1;
    }
}
