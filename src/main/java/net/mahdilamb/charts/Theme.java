package net.mahdilamb.charts;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

/**
 * Default implementation of a theme
 *
 * @apiNote modification of the theme is done by the {@link Chart} class
 *
 */
//TODO as builder. Also synch with chart if it exists
public class Theme {
    Colormap colormap;
    Color background, plotBackground, borderColor;
    double plotPaddingX, plotPaddingY, borderSize = 1, borderPadding = 10, titleSize = 18, axisLabelSize = 10, majorTickSize = 5, minorTickSize = 2;
    private Chart<?,?> chart;

    /**
     * Create a theme from a few attributes
     *
     * @param background     background color
     * @param plotBackground plot background color
     * @param borderColor    border color
     * @param plotPaddingX   plot padding size x
     * @param plotPaddingY   plot padding size y
     */
    Theme(Colormap colormap, Color background, Color plotBackground, Color borderColor, double plotPaddingX, double plotPaddingY) {
        this.background = background;
        this.plotBackground = plotBackground;
        this.borderColor = borderColor;
        this.plotPaddingX = plotPaddingX;
        this.plotPaddingY = plotPaddingY;
        this.colormap = colormap;
    }

    protected Color getBackgroundColor() {
        return background;
    }

    protected Color getPlotBackgroundColor() {
        return plotBackground;
    }

    protected Color getBorderColor() {
        return borderColor;
    }

    protected double getBorderSize() {
        return borderSize;
    }

    protected double getBorderPadding() {
        return borderPadding;
    }

    protected double getTitleSize() {
        return titleSize;
    }

    protected double getAxisLabelSize() {
        return axisLabelSize;
    }

    protected double getMajorTickSize() {
        return majorTickSize;
    }

    protected double getMinorTickSize() {
        return minorTickSize;
    }

    protected double getPlotPaddingY() {
        return plotPaddingY;
    }

    protected double getPlotPaddingX() {
        return plotPaddingX;
    }

    protected Colormap getDefaultColormap() {
        return colormap;
    }

    void apply(final Chart<?, ?> chart) {
        //TODO
        this.chart = chart;
    }


}
