package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Stroke;

public abstract class ChartComponent {
    Figure<?, ?> figure;
    ChartNode<ChartComponent> parentNode;
    double posX = 0, posY = 0, sizeX, sizeY;
    boolean inline = true;
    boolean layoutNeedsRefresh = true;

    ChartComponent() {

    }

    /**
     * Calculate the size of the object (i.e updates sizeX and sizeY)
     * @param source the source of the request
     * @param canvas the canvas that will eventually be drawn on
     * @param minX   the requested minX
     * @param minY   the requested minY
     * @param maxX   the requested maxX
     * @param maxY   the requested maxY
     */
    protected abstract void layout(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY);

    /**
     * Draw the elements
     * @param source the source of the draw request
     * @param canvas the canvas to draw on
     * @param minX   the requested minX
     * @param minY   the requested minY
     * @param maxX   the requested maxX
     * @param maxY   the requested maxY
     */
    protected abstract void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY);

    /**
     * Request a layout to the chart, if one has been assigned
     */
    protected Object redraw() {
        if (parentNode != null) {
            parentNode.redraw();
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
        this.posX = boundsX;
        this.posY = boundsY;
        this.sizeX = boundsWidth;
        this.sizeY = boundsHeight;
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
        this.posX = minX;
        this.posY = minY;
        this.sizeX = maxX - minX;
        this.sizeY = maxY - minY;
    }

    protected void assign(final Figure<?, ?> chart) {
        this.figure = chart;
    }

    /**
     * Helper tool for making sure the bounds have been set correctly
     *
     * @param canvas the canvas to draw on
     */
    void drawBounds(final ChartCanvas<?> canvas) {
        drawBounds(canvas, this);
    }

    static void drawBounds(final ChartCanvas<?> canvas, final ChartComponent component) {
        canvas.setStroke(Stroke.BLACK_STROKE);
        canvas.strokeRect(component.posX, component.posY, component.sizeX, component.sizeY);
    }

}
