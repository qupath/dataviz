package net.mahdilamb.charts;

import net.mahdilamb.colormap.Colormap;

/**
 * The display configuration of the color bars
 */
public final class ColorBar extends Key {
    ColorBar(Chart<?, ?> chart) {
        super(chart);
    }

    private static final class ColorBarItem {
        double scaleMin, scaleMax;
        Colormap colormap;
        boolean showText;
    }

    double barWidth, textSize;




}
