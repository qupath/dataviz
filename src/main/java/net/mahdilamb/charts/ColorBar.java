package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.colormap.Colormap;

/**
 * The display configuration of the color bars
 */
public final class ColorBar extends Key {
    ColorBar(Chart<?, ?> chart) {
        super(chart);
    }

    @Override
    protected void layout(ChartCanvas<?> canvas, double x, double y, double width, double height) {
        //todo
    }

    static final class ColorBarItem {
        double scaleMin, scaleMax;
        Colormap colormap;
        boolean showText;
    }

    double barWidth, textSize;




}
