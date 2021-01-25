package net.mahdilamb.charts;

import net.mahdilamb.charts.styles.Orientation;
import net.mahdilamb.charts.styles.Text;

public abstract class Plot {

    protected double x, y, width, height;

    Chart<?> chart;

    /**
     * Store the last parameters for chart exporter
     *
     * @param x      the x position of the plot area
     * @param y      the y position of the plot area
     * @param width  the width of the plot area
     * @param height the height of the plot area
     */
    final void layout(double x, double y, double width, double height) {

        //TODO get axis dims and update
        layoutPlot(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected abstract void layoutPlot(double x, double y, double width, double height);

    protected abstract Iterable<LegendItem> getLegendItems();

    protected abstract void layoutAxes();

    protected static void layoutAxis(Axis axis, Orientation orientation) {
        axis.layout(orientation);
    }


    protected double getTextHeight(Text text){
        return chart.getTextHeight(text);
    }
    protected double getTextWidth(Text text){
        return chart.getTextWidth(text);
    }

}
