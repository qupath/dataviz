package net.mahdilamb.charts.layouts;


/**
 * An XY plot with potentially two marginals
 */
public interface XYMarginalPlot<S > extends XYPlot<S> {
    /**
     * @return the marginal associated with the x axis
     */
    PlotLayout<?> getXMarginal();

    /**
     * @return the marginal associated with the y axis
     */
    PlotLayout<?> getYMarginal();

    @Override
    default int numSeries() {
        return 1;
    }
}
