package net.mahdilamb.charts.graphics;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

/**
 * A theme to be used in a chart
 */
//TODO builder
public interface Theme {

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
    double getBorderSize();

    /**
     * @return the padding between the border and the content
     */
    double getBorderPadding();

    /**
     * @return the size of the title
     */
    double getTitleSize();

    /**
     * @return the font size of the axis labels
     */
    double getAxisLabelSize();

    /**
     * @return the size of the major ticks
     */
    double getMajorTickSize();

    /**
     * @return the size of the minor ticks
     */
    double getMinorTickSize();

    /**
     * @return the vertical padding between plots
     */
    double getPlotPaddingY();

    /**
     * @return the horizontal padding between plots
     */
    double getPlotPaddingX();

    /**
     * @return the default colormap
     */
    Colormap getDefaultColormap();

    /**
     * @return a copy of this theme
     */
    Theme copy();


}
