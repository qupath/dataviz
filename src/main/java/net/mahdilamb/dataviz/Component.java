package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.dataviz.graphics.ChartCanvas;
import net.mahdilamb.dataviz.graphics.Font;

/**
 * A component in a chart
 */
public abstract class Component {
    /**
     * The root of the component
     */
    Renderer<?> renderer;
    /**
     * The layout fields
     */
    double posX = 0, posY = 0, sizeX, sizeY;
    /**
     * Whether the the layout needs to be refreshed on the next pass
     */
    boolean layoutNeedsRefresh = true;
    /**
     * If the component needs to be redrawn on the next pass
     */
    boolean drawNeedsRefresh = true;

    protected Component() {

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

    /**
     * Helper tool for making sure the bounds have been set correctly
     *
     * @param canvas the canvas to draw on
     */
    void drawBounds(final ChartCanvas<?> canvas) {
        drawBounds(canvas, this);
    }

    static void printBounds(final String c, Component component) {
        System.out.printf("%s x:%s y:%s w:%s h:%s%n", c, component.posX, component.posY, component.sizeX, component.sizeY);
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

    protected static void print(Object... val) {
        for (final Object o : val) {
            System.out.print(o);
            System.out.print(' ');
        }
        System.out.println();
    }

    /**
     * Mark the layout as old
     */
    protected void markLayoutAsOld() {
        layoutNeedsRefresh = true;
        drawNeedsRefresh = true;
    }

    /**
     * Mark the draw as needing refresh
     */
    protected void markDrawAsOld() {
        drawNeedsRefresh = true;
    }

    /**
     * @return the x position
     */
    protected double getX() {
        return posX;
    }

    /**
     * @return the y position
     */
    protected double getY() {
        return posY;
    }


    /*
    "Friend" methods
     */

    /**
     * @param source the renderer
     * @param font   the font
     * @return the line height using a renderer
     */
    protected static double getTextLineHeight(Renderer<?> source, final Font font) {
        return source.getTextLineHeight(font);
    }

    /**
     * @param source the renderer
     * @param font   the font
     * @return the baseline offset using a renderer
     */
    protected static double getTextBaselineOffset(Renderer<?> source, final Font font) {
        return source.getTextBaselineOffset(font);
    }

    /**
     * @param source    the renderer
     * @param font      the font
     * @param character the character
     * @return the width of a character using a renderer
     */
    protected static double getTextCharWidth(Renderer<?> source, final Font font, char character) {
        return source.getCharWidth(font, character);
    }

}
