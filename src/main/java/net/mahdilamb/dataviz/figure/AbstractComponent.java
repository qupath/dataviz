package net.mahdilamb.dataviz.figure;

import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.GraphicsContext;

import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * A component in a chart
 */
public abstract class AbstractComponent {
    /**
     * The root of the component
     */
    GraphicsContext<?> context;

    /**
     * The layout fields
     */
    double posX = 0, posY = 0,
            sizeX, sizeY;
    /**
     * Whether the the layout needs to be refreshed on the next pass
     */
    boolean layoutNeedsRefresh = true;

    AbstractComponent() {

    }

    /**
     * Draw the component if it needs drawn
     *
     * @param <T>      the type of the image in the renderer
     * @param renderer the source of the draw request
     * @param context  the canvas to draw on
     */
    protected abstract <T> void draw(Renderer<T> renderer, GraphicsBuffer<T> context);

    protected abstract void markLayoutAsOldQuietly();

    protected abstract <T> void markBufferAsOldQuietly();

    @SuppressWarnings("unchecked")
    protected <T> GraphicsContext<T> getContext() {
        return (GraphicsContext<T>) context;
    }

    /**
     * Mark the layout as old
     */
    protected final void relayout() {
        markLayoutAsOldQuietly();
        if (context != null) {
            layout(context.getRenderer(), posX, posY, posX + sizeX, posY + sizeY);
        }
        redraw();
    }

    protected final <T> void redraw() {
        markBufferAsOldQuietly();
        if (context == null) {
            return;
        }
        final GraphicsContext<T> context = getContext();
        if (context == context.getRenderer().getFigureContext()) {
            context.getRenderer().getFigure().update();
        }
        context.getRenderer().getOverlay().draw(context.getRenderer());

    }

    /**
     * Calculate the size of this component (i.e updates sizes and positions)
     *
     * @param renderer the source of the request
     * @param minX     the requested minX
     * @param minY     the requested minY
     * @param maxX     the requested maxX
     * @param maxY     the requested maxY
     */
    protected abstract <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY);

    /**
     * Draw the elements
     *
     * @param <T>      the type of the image in the renderer
     * @param renderer the source of the draw request
     * @param canvas   the canvas to draw on
     */
    protected abstract <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas);

    abstract boolean hasChildren();

    /**
     * Layout the components if they need to be laid out
     *
     * @param renderer the source of the request
     * @param minX     the requested minX
     * @param minY     the requested minY
     * @param maxX     the requested maxX
     * @param maxY     the requested maxY
     */
    protected final <T> void layout(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
        if (context == null) {
            return;
        }
        if (layoutNeedsRefresh || hasChildren()) {
            layoutComponent(renderer, minX, minY, maxX, maxY);
        }
        layoutNeedsRefresh = false;
    }

    <T> void setContext(GraphicsContext<T> context) {
        this.context = context;

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
    void drawBounds(final GraphicsContext<?> canvas) {
        drawBounds(canvas, this);
    }

    public static void printBounds(final String c, AbstractComponent component) {
        System.out.printf("%s x:%s y:%s w:%s h:%s%n", c, component.posX, component.posY, component.sizeX, component.sizeY);
    }

    public static void printBounds(AbstractComponent component) {
        System.out.printf("%s x:%s y:%s w:%s h:%s%n", component.toString(), component.posX, component.posY, component.sizeX, component.sizeY);
    }


    /**
     * Draw a component in the given canvas
     *
     * @param canvas    the canvas
     * @param component the component
     */
    static void drawBounds(final GraphicsContext<?> canvas, final AbstractComponent component) {
        drawBounds(canvas, component, Color.black, Colors.violet);
    }

    static void drawBounds(final GraphicsContext<?> canvas, final AbstractComponent component, Color stroke, Color fill) {
        canvas.setFill(fill);
        canvas.fillRect(component.posX, component.posY, component.sizeX, component.sizeY);
        canvas.setStroke(stroke);
        canvas.strokeRect(component.posX, component.posY, component.sizeX, component.sizeY);
    }

    public static void print(Object... val) {
        for (final Object o : val) {
            System.out.print(o);
            System.out.print(' ');
        }
        System.out.println();
    }


    /**
     * @return the x position
     */
    public double getX() {
        return posX;
    }

    /**
     * @return the y position
     */
    public double getY() {
        return posY;
    }

    /**
     * @return the width of this component
     */
    public double getWidth() {
        return sizeX;
    }

    /**
     * @return the height of this component
     */
    public double getHeight() {
        return sizeY;
    }

    /**
     * @param x the x position
     * @param y the y position
     * @return whether this component contains the given point
     */
    public final boolean containsPoint(double x, double y) {
        return x >= posX && y >= posY
                && x <= (posX + sizeX) && y <= (posY + sizeY);
    }

    /**
     * @param x the x position
     * @param y the y position
     * @return the component at the given position
     */
    protected abstract Component getComponentAt(double x, double y);

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

    protected static double getTextWidth(Renderer<?> source, final Font font, String text) {
        return source.getTextWidth(font, text);
    }

    /**
     * Layout a component using the given renderer on a canvas
     *
     * @param component the component
     * @param renderer  the renderer
     * @param minX      the requested minX
     * @param minY      the requested minY
     * @param maxX      the requested maxX
     * @param maxY      the requested maxY
     */
    protected static <T> void layoutComponent(AbstractComponent component, Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
        component.layoutComponent(renderer, minX, minY, maxX, maxY);
    }

    /**
     * Draw a component using the given renderer and canvas
     *
     * @param component the component
     * @param renderer  the renderer
     * @param canvas    the canvas
     * @param <T>       the type of the image in the renderer
     */
    protected static <T> void drawComponent(AbstractComponent component, Renderer<T> renderer, GraphicsContext<T> canvas) {
        component.drawComponent(renderer, canvas);
    }

    /**
     * Layout a component using the given renderer on a canvas
     *
     * @param component the component
     * @param renderer  the renderer
     * @param minX      the requested minX
     * @param minY      the requested minY
     * @param maxX      the requested maxX
     * @param maxY      the requested maxY
     */
    protected static <T> void layout(AbstractComponent component, Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
        component.layout(renderer, minX, minY, maxX, maxY);
    }

    /**
     * Draw a component using the given renderer and canvas
     *
     * @param component the component
     * @param renderer  the renderer
     * @param canvas    the canvas
     * @param <T>       the type of the image in the renderer
     */
    protected static <T> void draw(AbstractComponent component, Renderer<T> renderer, GraphicsContext<T> canvas) {
        component.draw(renderer, canvas);
    }

    protected static double clipX(final Renderer<?> renderer, double x, double width) {
        return Math.min(Math.max(0, x), renderer.getFigure().width - width);

    }

    protected static double clipY(final Renderer<?> renderer, double y, double height) {
        return Math.min(Math.max(0, y), renderer.getFigure().height - height);

    }

    protected static File getOutputPath(final Renderer<?> renderer, List<String> fileTypes, String defaultExtension) {
        return renderer.getOutputPath(fileTypes, defaultExtension);
    }

}
