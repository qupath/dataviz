package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.Theme;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

/**
 * Default implementation of a theme
 *
 * @apiNote modification of the theme is done by the {@link Chart} class
 */
class ThemeImpl implements Theme {
    Colormap colormap;
    Color background, plotBackground, borderColor;
    double plotPaddingX, plotPaddingY, borderSize = 1, borderPadding = 10, titleSize = 18, axisLabelSize = 10, majorTickSize = 5, minorTickSize = 2;

    /**
     * Create a theme from a few attributes
     *
     * @param background     background color
     * @param plotBackground plot background color
     * @param borderColor    border color
     * @param plotPaddingX   plot padding size x
     * @param plotPaddingY   plot padding size y
     */
    ThemeImpl(Colormap colormap, Color background, Color plotBackground, Color borderColor, double plotPaddingX, double plotPaddingY) {
        this.background = background;
        this.plotBackground = plotBackground;
        this.borderColor = borderColor;
        this.plotPaddingX = plotPaddingX;
        this.plotPaddingY = plotPaddingY;
        this.colormap = colormap;
    }

    /**
     * Copy constructor
     *
     * @param other the source theme
     */
    ThemeImpl(Theme other) {
        this.background = other.getBackgroundColor();
        this.plotBackground = other.getPlotBackgroundColor();
        this.borderColor = other.getBorderColor();
        this.plotPaddingX = other.getPlotPaddingX();
        this.plotPaddingY = other.getPlotPaddingY();
        this.borderSize = other.getBorderSize();
        this.borderPadding = other.getBorderPadding();
        this.titleSize = other.getTitleSize();
        this.axisLabelSize = other.getAxisLabelSize();
        this.majorTickSize = other.getMajorTickSize();
        this.minorTickSize = other.getMinorTickSize();
        this.colormap = other.getDefaultColormap();
    }

    @Override
    public Color getBackgroundColor() {
        return background;
    }

    @Override
    public Color getPlotBackgroundColor() {
        return plotBackground;
    }

    @Override
    public Color getBorderColor() {
        return borderColor;
    }

    @Override
    public double getBorderSize() {
        return borderSize;
    }

    @Override
    public double getBorderPadding() {
        return borderPadding;
    }

    @Override
    public double getTitleSize() {
        return titleSize;
    }

    @Override
    public double getAxisLabelSize() {
        return axisLabelSize;
    }

    @Override
    public double getMajorTickSize() {
        return majorTickSize;
    }

    @Override
    public double getMinorTickSize() {
        return minorTickSize;
    }

    @Override
    public double getPlotPaddingY() {
        return plotPaddingY;
    }

    @Override
    public double getPlotPaddingX() {
        return plotPaddingX;
    }

    @Override
    public Colormap getDefaultColormap() {
        return colormap;
    }

    @Override
    public Theme copy() {
        return new ThemeImpl(this);
    }

}
