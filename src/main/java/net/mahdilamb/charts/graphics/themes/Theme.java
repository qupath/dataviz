package net.mahdilamb.charts.graphics.themes;

import net.mahdilamb.colormap.Color;

/**
 * A theme to be used in a chart
 */
public interface Theme extends Cloneable {
    /**
     * @return the "page" background color
     */
    Color getBackgroundColor();

    /**
     * @return the plot background color
     */
    Color getPlotBackgroundColor();

    /**
     * @return the border color
     */
    Color getBorderColor();

    /**
     * @return the width of the border
     */
    default double getBorderSize() {
        return 1;
    }

    /**
     * @return the padding between the border and the content
     */
    default double getBorderPadding() {
        return 10;
    }

    /**
     * @return the size of the title
     */
    default double getTitleSize() {
        return 18;
    }

    /**
     * @return the font size of the axis labels
     */
    default double getAxisLabelSize() {
        return 10;
    }

    /**
     * @return the size of the major ticks
     */
    default double getMajorTickSize() {
        return 2;
    }

    /**
     * @return the size of the minor ticks
     */
    default double getMinorTickSize() {
        return 1;
    }

    /**
     * @return the vertical padding between plots
     */
    double getPlotVerticalPadding();

    /**
     * @return the horizontal padding between plots
     */
    double getPlotHorizontalPadding();

    /**
     * @return a copy of this theme
     */
    Theme copy();

    /**
     * @return a deep copy of this theme
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    default Theme clone() {
        return copy();
    }

}
