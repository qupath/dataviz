package net.mahdilamb.charts.plots;

public interface PlotWithColorBar<S> {

    /**
     * Set the color bar as visible (if one is used)
     *
     * @param showColorBar whether to show the color bar
     * @return this scatter series
     */
    S showColorBar(boolean showColorBar);

}
