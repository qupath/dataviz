package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Axis;
import net.mahdilamb.charts.series.PlotSeries;

/**
 * A plot that is a table
 *
 * @param <S> the type of the plot series in the plot
 */
public interface TabularPlot<S extends PlotSeries<S>> extends Plot<S> {
    /**
     * @return the column heading
     */
    Axis getColumnHeading();

    /**
     * @return the row heading
     */
    Axis getRowHeading();
}
