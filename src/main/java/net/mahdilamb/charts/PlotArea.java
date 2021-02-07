package net.mahdilamb.charts;

import net.mahdilamb.colormap.Color;

/**
 * Plot area
 *
 * @param <S> the type of the series contained in the plot
 */
public interface PlotArea<S extends PlotSeries<S>> {

    /**
     * @return the x axis or {@code null} if the plot doesn't have an x axis
     */
    Axis getXAxis();

    /**
     * @return the y axis or {@code null} if the plot doesn't have an x axis
     */
    Axis getYAxis();

    /**
     * @return the radial axis or {@code null} if the plot doesn't have an x axis
     */
    Axis getRadialAxis();

    /**
     * @return the angular axis or {@code null} if the plot doesn't have an x axis
     */
    Axis getAngularAxis();

    default boolean hasXAxis() {
        return getXAxis() != null;
    }

    default boolean hasYAxis() {
        return getYAxis() != null;
    }

    default boolean hasRadialAxis() {
        return getRadialAxis() != null;
    }

    default boolean hasAngularAxis() {
        return getAngularAxis() != null;
    }

    /**
     * @return the number of series in the chart
     */
    int numSeries();

    /**
     * Get the series by index
     *
     * @param index the index
     * @return the series at the specified index
     */
    S getSeries(int index);

    void setBackground(final Color color);
}
