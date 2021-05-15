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
    GraphicsContext context;

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
     * @param renderer the source of the draw request
     * @param context  the canvas to draw on
     */
    protected abstract void draw(Renderer renderer, GraphicsBuffer context);

    protected abstract void markLayoutAsOldQuietly();

    protected abstract void markDrawAsOldQuietly();

    protected GraphicsContext getContext() {
        return  context;
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

    protected final void redraw() {
        markDrawAsOldQuietly();
        if (context == null) {
            return;
        }
        final GraphicsContext context = getContext();
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
    protected abstract void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY);

    /**
     * Draw the elements
     *
     * @param renderer the source of the draw request
     * @param canvas   the canvas to draw on
     */
    protected abstract void drawComponent(Renderer renderer, GraphicsBuffer canvas);

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
    protected final void layout(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        if (context == null) {
            return;
        }
        if (layoutNeedsRefresh || hasChildren()) {
            layoutComponent(renderer, minX, minY, maxX, maxY);
        }
        layoutNeedsRefresh = false;
    }

    void setContext(GraphicsContext context) {
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
    void drawBounds(final GraphicsContext canvas) {
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
    static void drawBounds(final GraphicsContext canvas, final AbstractComponent component) {
        drawBounds(canvas, component, Color.black, Colors.violet);
    }

    static void drawBounds(final GraphicsContext canvas, final AbstractComponent component, Color stroke, Color fill) {
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
    public boolean containsPoint(double x, double y) {
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
     * @param renderer the renderer
     * @param font     the font
     * @param text     the text
     * @return the line height using a renderer
     */
    protected static double getTextLineHeight(Renderer renderer, final Font font, final String text) {
        return renderer.getTextLineHeight(font, text);
    }

    /**
     * @param renderer the renderer
     * @param font     the font
     * @return the baseline offset using a renderer
     */
    protected static double getTextBaselineOffset(Renderer renderer, final Font font) {
        return renderer.getTextBaselineOffset(font);
    }

    /**
     * @param renderer  the renderer
     * @param font      the font
     * @param character the character
     * @return the width of a character using a renderer
     */
    protected static double getTextCharWidth(Renderer renderer, final Font font, char character) {
        return renderer.getCharWidth(font, character);
    }

    protected static double getTextWidth(Renderer renderer, final Font font, String text) {
        return renderer.getTextWidth(font, text);
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
    protected static void layoutComponent(AbstractComponent component, Renderer renderer, double minX, double minY, double maxX, double maxY) {
        component.layoutComponent(renderer, minX, minY, maxX, maxY);
    }

    /**
     * Draw a component using the given renderer and canvas
     *
     * @param component the component
     * @param renderer  the renderer
     * @param canvas    the canvas
     */
    protected static void drawComponent(AbstractComponent component, Renderer renderer, GraphicsBuffer canvas) {
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
    protected static void layout(AbstractComponent component, Renderer renderer, double minX, double minY, double maxX, double maxY) {
        component.layout(renderer, minX, minY, maxX, maxY);
    }

    /**
     * Draw a component using the given renderer and canvas
     *
     * @param component the component
     * @param renderer  the renderer
     * @param canvas    the canvas
     */
    protected static void draw(AbstractComponent component, Renderer renderer, GraphicsContext canvas) {
        component.draw(renderer, canvas);
    }

    protected static String getFromClipboard(final Renderer renderer) {
        return renderer.getFromClipboard();
    }
    protected static void addToClipboard(final Renderer renderer, final String content) {
         renderer.addToClipboard(content);
    }
    protected static double clipX(final Renderer renderer, double x, double width) {
        return Math.min(Math.max(0, x), renderer.getFigure().width - width);

    }

    protected static double clipY(final Renderer renderer, double y, double height) {
        return Math.min(Math.max(0, y), renderer.getFigure().height - height);

    }

    protected static File getOutputPath(final Renderer renderer, List<String> fileTypes, String defaultExtension) {
        return renderer.getOutputPath(fileTypes, defaultExtension);
    }

    protected static void markLayoutAsOld(final AbstractComponent component) {
        component.markLayoutAsOldQuietly();
    }

    protected static void markDrawAsOld(final AbstractComponent component) {
        component.markDrawAsOldQuietly();
    }

    protected static  void addToOverlay(final AbstractComponent component) {
        final GraphicsContext context;
        if ((context = component.getContext()) == null) {
            return;
        }
        context.getRenderer().addToOverlay(component);

    }

    protected static void removeFromOverlay(final AbstractComponent component) {
        final GraphicsContext context;
        if ((context = component.getContext()) == null) {
            return;
        }
        context.getRenderer().removeFromOverlay(component);
    }
}
