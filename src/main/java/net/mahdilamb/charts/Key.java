package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Side;
import net.mahdilamb.charts.graphics.Stroke;

/**
 * A class representing meta information for the chart (e.g. Legend and Colorbar)
 */
abstract class Key {
    final Chart<?, ?> chart;
    Title title;
    boolean isFloating = false, visible = true;
    double borderPadding = 0;
    Side side = Side.LEFT;
    Stroke border = Stroke.BLACK_STROKE;

    Key(Chart<?, ?> chart) {
        this.chart = chart;

    }

    /**
     * @return the border of the legend
     */
    public Stroke getBorder() {
        return border;
    }

    /**
     * @return the padding between the content and the border
     */
    public double getBorderPadding() {
        return borderPadding;
    }

    /**
     * @return the title of the legend. May be {@code null}
     */
    public Title getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.setTitle(title);
        chart.layout();
    }

    /**
     * @return the side of the chart this legend is on.
     */
    public Side getSide() {
        return side;
    }

    /**
     * @return whether the legend takes up space in the layout or appears over the plot are
     */
    public boolean isFloating() {
        return isFloating;
    }

    public boolean isVisible() {
        return visible;
    }


    protected abstract void layout(ChartCanvas<?> canvas, double x, double y, double width, double height);

    static abstract class KeyItem {
        /**
         * Lays out the key item and returns the height of the item
         *
         *
         * @param chart the source chart
         * @param canvas the canvas to draw on
         * @param x      the x position
         * @param y      the y position
         * @param width  the width to draw
         * @param height the height to draw
         * @return the height of the item
         */
        protected abstract double layout(Chart<?, ?> chart, ChartCanvas<?> canvas, double x, double y, double width, double height);

        protected abstract double getItemWidth(final Chart<?, ?> chart);

    }
}
