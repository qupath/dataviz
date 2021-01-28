package net.mahdilamb.charts.series;

public interface PlotWithColorBar<S extends PlotSeries<S>> {

    /**
     * Set the color bar as visible (if one is used)
     *
     * @param showColorBar whether to show the color bar
     * @return this scatter series
     */
    S showColorBar(boolean showColorBar);

}
