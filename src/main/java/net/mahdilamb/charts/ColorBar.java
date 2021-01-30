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


    double barWidth, textSize;

    static final class ColorBarItem  extends KeyItem{
        double scaleMin, scaleMax;
        Colormap colormap;
        boolean showText;

        @Override
        protected double layout(Chart<?, ?> chart, ChartCanvas<?> canvas, double x, double y, double width, double height) {
            //TODO
            return 0;
        }

        @Override
        protected double getItemWidth(Chart<?, ?> chart) {
            //TODO
            return 0;
        }
    }



}
