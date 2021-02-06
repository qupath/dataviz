package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Stroke;

public abstract class ChartComponent {
    Chart<?> chart;
    double boundsX, boundsY, boundsWidth, boundsHeight;

    ChartComponent() {

    }

    /**
     * Request a layout to the chart, if one has been assigned
     */
    protected Object requestLayout() {
        if (chart != null) {
            chart.requestLayout();
        }
        return this;
    }

    /**
     * Set the bounds from a rectangle
     *
     * @param boundsX      the min x
     * @param boundsY      the min y
     * @param boundsWidth  the width
     * @param boundsHeight the height
     */
    protected void setBoundsFromRect(double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
        this.boundsX = boundsX;
        this.boundsY = boundsY;
        this.boundsWidth = boundsWidth;
        this.boundsHeight = boundsHeight;
    }

    /**
     * Set the bounds using the min and max
     *
     * @param minX the min X
     * @param minY the min Y
     * @param maxX the max X
     * @param maxY the max Y
     */
    protected void setBoundsFromExtent(double minX, double minY, double maxX, double maxY) {
        this.boundsX = minX;
        this.boundsY = minY;
        this.boundsWidth = maxX - minX;
        this.boundsHeight = maxY - minY;
    }

    /**
     * Helper tool for making sure the bounds have been set correctly
     *
     * @param canvas the canvas to draw on
     */
    void drawBounds(final ChartCanvas<?> canvas) {
        drawBounds(canvas, this);
    }

    static <S> void drawBounds(final ChartCanvas<?> canvas, final ChartComponent component) {
        canvas.setStroke(Stroke.BLACK_STROKE);
        canvas.strokeRect(component.boundsX, component.boundsY, component.boundsWidth, component.boundsHeight);
    }

    /**
     * Calculate the minimum display area
     *
     * @param canvas the canvas that will eventually be drawn on
     * @param source the source of the request
     * @param minX   the requested minX
     * @param minY   the requested minY
     * @param maxX   the requested maxX
     * @param maxY   the requested maxY
     */
    protected abstract void calculateBounds(ChartCanvas<?> canvas, Chart<? extends Object> source, double minX, double minY, double maxX, double maxY);

    /**
     * Draw the elements
     *
     * @param canvas the canvas to draw on
     * @param source the source of the draw request
     * @param minX   the requested minX
     * @param minY   the requested minY
     * @param maxX   the requested maxX
     * @param maxY   the requested maxY
     */
    protected abstract void layout(ChartCanvas<?> canvas, Chart<? extends Object> source, double minX, double minY, double maxX, double maxY);

}
