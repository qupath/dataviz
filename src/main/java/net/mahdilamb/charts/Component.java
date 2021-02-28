package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.colormap.Color;

/**
 * A component in a chart
 */
public abstract class Component {
    /**
     * The root of the component
     */
    Renderer<?> renderer;
    /**
     * The direct parent of the component
     */
    protected Node parent;
    /**
     * The layout fields
     */
    double posX = 0, posY = 0, sizeX, sizeY;
    /**
     * Whether to lay the component as a block or online
     */
    boolean inline = true;
    /**
     * Whether the the layout needs to be refreshed on the next pass
     */
    private boolean layoutNeedsRefresh = true;
    /**
     * If the component needs to be redrawn on the next pass
     */
    private boolean drawNeedsRefresh = true;

    Component() {

    }

    /**
     * Calculate the size of the object (i.e updates sizeX and sizeY)
     *
     * @param source the source of the request
     * @param minX   the requested minX
     * @param minY   the requested minY
     * @param maxX   the requested maxX
     * @param maxY   the requested maxY
     */
    protected abstract void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY);

    /**
     * Draw the elements
     *
     * @param source the source of the draw request
     * @param canvas the canvas to draw on
     */
    protected abstract void drawComponent(Renderer<?> source, ChartCanvas<?> canvas);

    /**
     * Layout the components if they need to be laid out
     *
     * @param source the source of the request
     * @param minX   the requested minX
     * @param minY   the requested minY
     * @param maxX   the requested maxX
     * @param maxY   the requested maxY
     */
    protected final void layout(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        if (!layoutNeedsRefresh) {
            return;
        }
        layoutComponent(source, minX, minY, maxX, maxY);
        layoutNeedsRefresh = false;
    }

    /**
     * Draw the component if it needs drawn
     *
     * @param source the source of the draw request
     * @param canvas the canvas to draw on
     */
    protected final void draw(Renderer<?> source, ChartCanvas<?> canvas) {
        if (!drawNeedsRefresh) {
            return;
        }
        drawComponent(source, canvas);
        drawNeedsRefresh = false;
    }

    /**
     * Redraw the figure
     */
    protected Object refresh() {
        if (renderer != null) {
            renderer.refresh();
        }
        return this;
    }

    /**
     * Redraw a sub region of the figure
     */
    protected Object redrawLocal() {
        if (parent != null) {
            parent.refresh();
            return this;
        }
        return refresh();
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

    protected void assign(final Renderer<?> chart) {
        this.renderer = chart;
    }

    /**
     * Helper tool for making sure the bounds have been set correctly
     *
     * @param canvas the canvas to draw on
     */
    void drawBounds(final ChartCanvas<?> canvas) {
        drawBounds(canvas, this);
    }

    /**
     * Draw a component in the given canvas
     *
     * @param canvas    the canvas
     * @param component the component
     */
    static void drawBounds(final ChartCanvas<?> canvas, final Component component) {
        drawBounds(canvas, component, Color.black, Color.violet);
    }

    static void drawBounds(final ChartCanvas<?> canvas, final Component component, Color stroke, Color fill) {
        canvas.setFill(fill);
        canvas.fillRect(component.posX, component.posY, component.sizeX, component.sizeY);
        canvas.setStroke(stroke);
        canvas.strokeRect(component.posX, component.posY, component.sizeX, component.sizeY);
    }

    /**
     * Mark the layout as old
     */
    protected void markLayoutAsOld() {
        layoutNeedsRefresh = true;
        drawNeedsRefresh = true;
        refresh();
    }

    /**
     * Mark the draw as needing refresh
     */
    protected void markDrawAsOld() {
        drawNeedsRefresh = true;
        refresh();
    }

}
