package net.mahdilamb.charts;

import net.mahdilamb.colormap.Colormap;

/**
 * The display configuration of the color bars
 */
public final class ColorBar extends Key {
    double barWidth, scaleMin, scaleMax, textSize;
    Colormap colormap;
    boolean showText;

    ColorBar(Chart<?, ?> chart) {
        super(chart);
    }




}
