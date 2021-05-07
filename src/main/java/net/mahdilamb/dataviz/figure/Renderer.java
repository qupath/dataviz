package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.GraphicsContext;
import net.mahdilamb.dataviz.io.FigureExporter;
import net.mahdilamb.dataviz.ui.IconStore;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A figure renderer
 *
 * @param <IMG> the type of the image used by the renderer
 */
public abstract class Renderer<IMG> {

    private FigureBase<?> figure;
    protected boolean overlayEnabled = true;
    private Map<InputStream, IconStore<IMG>> icons;
    private Overlay overlay;

    Component lastFocus;
    Component lastHover;

    /**
     * Create a renderer
     */
    protected Renderer() {

    }

    /**
     * Method called when the renderer is initialized
     *
     * @param figure the figure to generate the renderer around
     */
    protected final void init(final FigureBase<?> figure) {
        this.figure = figure;
        figure.setContext(this.getFigureContext());
        overlay = new Overlay(figure);
        overlay.setContext(this.getOverlayContext());
        refresh();
    }

    final boolean drawDirect(Component component) {
        return (component.context == getFigureContext() && getFigure().drawDirect) || component.drawDirect;
    }

    /**
     * Refresh the figure onto the external canvas
     *
     * @param canvas the external canvas
     */
    protected void refresh(GraphicsContext<IMG> canvas) {
        figure.update(canvas, true);
    }

    /**
     * Refresh the figure locally, allowing buffered draws
     */
    protected void refresh() {
        figure.update(getFigureContext(), false);
    }

    /* Canvas helper methods */

    /**
     * Get the baseline offset of a font
     *
     * @param font the font
     * @return the baseline offset
     */
    protected abstract double getTextBaselineOffset(final Font font);

    /**
     * @param font the font
     * @param text the text
     * @return the width of the text with the given font
     */
    protected abstract double getTextWidth(final Font font, String text);

    /**
     * @param font      the font
     * @param character the character
     * @return the width of a character
     */
    protected abstract double getCharWidth(final Font font, char character);

    /**
     * @param font the font
     * @return the height of the text with the given font
     */
    protected abstract double getTextLineHeight(final Font font);

    /**
     * Load an image given a file
     *
     * @param file the file to load
     * @return the image
     */
    protected abstract IMG loadImage(final InputStream file);

    /**
     * Create a new buffer
     *
     * @param width          the width of the buffer
     * @param height         the height of the buffer
     * @param translateX     the x translation
     * @param translateY     the y translation
     * @param overflowTop    the amount of overflow space on the top
     * @param overflowLeft   the amount of overflow space on the left
     * @param overflowBottom the amount of overflow space on the bottom
     * @param overflowRight  the amount of overflow space on the right
     * @return the new buffer
     */
    protected abstract GraphicsBuffer<IMG> createBuffer(double width, double height, double translateX, double translateY, int overflowTop, int overflowLeft, int overflowBottom, int overflowRight);

    /**
     * Draw a buffer
     *
     * @param context the context to draw to
     * @param buffer  the buffer to draw from
     * @param x       the x position
     * @param y       the y position
     */
    protected abstract void drawBuffer(final GraphicsBuffer<IMG> context, GraphicsBuffer<IMG> buffer, double x, double y);

    protected abstract boolean bufferSizeChanged(GraphicsBuffer<IMG> buffer, double x, double y, double width, double height);

    protected abstract String getFromClipboard();

    protected abstract void addToClipboard(final String text);

    /**
     * @return the icon store
     */
    protected IconStore<IMG> getIcons(final InputStream source, final Class<? extends Enum<?>> keys) {
        if (icons == null) {
            icons = new HashMap<>();
        }
        final IconStore<IMG> icons = this.icons.get(source);
        if (icons == null) {
            final IMG img = loadImage(source);
            int width = (int) getImageWidth(img);
            int height = (int) getImageHeight(img);
            int cols = width / (height / 2);
            IconStore<IMG> i = new IconStore<>(keys, img, cols, width, height, this::cropImage);
            this.icons.put(source, i);
            return i;
        }
        return icons;
    }

    /**
     * Crop an image
     *
     * @param source the source image
     * @param x      the x position
     * @param y      the y position
     * @param width  the width of the image
     * @param height the height of the image
     * @return the original image cropped
     */
    protected abstract IMG cropImage(IMG source, int x, int y, int width, int height);

    /**
     * @param image the image to get the width of
     * @return the width of an image
     */
    protected abstract double getImageWidth(IMG image);

    /**
     * @param image the image to get the height
     * @return the height of an image
     */
    protected abstract double getImageHeight(IMG image);

    /**
     * @param image the image to get the bytes of
     * @return the image as a byte array of PNG encoding bytes
     */
    protected abstract byte[] bytesFromImage(IMG image);

    /**
     * Get a packed int from an image, laid out as alpha, red, green, blue
     *
     * @param image the image (native to its canvas)
     * @param x     the x position
     * @param y     the y position
     * @return the packed into from an image at a specific position
     */
    protected abstract int argbFromImage(IMG image, int x, int y);

    /**
     * @param fileTypes the supported filetypes
     * @return a file path
     */
    protected abstract File getOutputPath(List<String> fileTypes, final String defaultExtension);

    /*  Figure  */

    /**
     * @return the figure currently being used in the renderer
     */
    public FigureBase<?> getFigure() {
        return figure;
    }

    /**
     * @return the main canvas in the renderer
     */
    protected abstract GraphicsContext<IMG> getFigureContext();

    /**
     * Save a file based on its extension (svg, jpg, bmp, png are supported)
     *
     * @param file the file to save as
     */
    public final void saveAs(File file) {
        FigureExporter.exportFigure(file, this);
    }

    /* Overlay */

    /**
     * @return the interactive overlay (the part of the display that isn't exported)
     */
    protected Overlay getOverlay() {
        return overlay;
    }

    /**
     * @return the overlay canvas
     */
    protected abstract GraphicsContext<IMG> getOverlayContext();

    /**
     * Add content to the overlay
     *
     * @param component the component to add
     */
    protected <AC extends AbstractComponent> void addToOverlay(final AC component) {
        getOverlay().contentVisible = true;
        getOverlay().add(component);
    }

    /**
     * Remove content from the overlay
     *
     * @param component the component to remove
     */
    protected <AC extends AbstractComponent> void removeFromOverlay(final AC component) {
        getOverlay().remove(component);
    }

    /**
     * Clear the content in the overlay
     */
    protected void clearOverlay() {
        getOverlay().clear();
    }

    /**
     * Set the visibility of the toolbar
     *
     * @param visibility the visibility to test
     */
    public void setToolbarVisibility(boolean visibility) {
        getOverlay().toolbarHasChanges = getOverlay().toolbarAlwaysShown != visibility;
        getOverlay().toolbarAlwaysShown = visibility;
    }

    /**
     * Clear the tooltip display
     */
    protected void clearTooltip() {
        getOverlay().clearTooltip();
    }

    protected Component getComponentAt(double x, double y) {
        Component component;
        if (!overlayEnabled || (component = getOverlay().getComponentAt(x, y)) == null) {
            component = getFigure().getComponentAt(x, y);
        }
        return component;
    }

    /* Input events */

    /**
     * Method for when the mouse is moved
     *
     * @param x the x position of the cursor
     * @param y the y position of the cursor
     * @implNote As a general rule, the overlay will be drawn from scratch on each pass as it is likely
     * to be fairly light-weight
     */
    protected void mouseMoved(boolean ctrlDown, boolean shiftDown, double x, double y) {
        final Component hover = getComponentAt(x, y);
        if (hover != null && lastHover != null) {
            Component c = hover.parent;
            while (c != null) {
                if (c == lastHover) {
                    if (hover.isEnabled()) {
                        hover.onMouseEnter(ctrlDown, shiftDown, x, y);
                        getOverlay().showTooltip(hover.getTooltip());
                    }
                    lastHover = hover;
                    break;
                }
                c = c.parent;
            }
        }
        if (hover != lastHover) {
            boolean doDefaultAction = true;
            if (lastHover != null && hover != null) {
                Component c = lastHover.parent;
                while (c != null) {
                    if (c == hover) {
                        doDefaultAction = false;
                        break;
                    }
                    c = c.parent;
                }
            }
            if (doDefaultAction && lastHover != null) {
                lastHover.onMouseExit(ctrlDown, shiftDown, x, y);
            }
            clearTooltip();
            if (doDefaultAction && hover != null) {
                if (hover.isEnabled()) {
                    hover.onMouseEnter(ctrlDown, shiftDown, x, y);
                    getOverlay().showTooltip(hover.getTooltip());
                }
            }
            lastHover = hover;
        } else if (hover != null) {
            hover.onMouseMove(ctrlDown, shiftDown, x, y);
        }
        if (hover == null) {
            clearTooltip();
            setToolbarVisibility(false);
        }
        getOverlay().draw(this);
    }

    /**
     * Method called when the mouse goes out of the figure area
     */
    protected void mouseExited() {
        if (lastHover != null) {
            lastHover.onMouseExit(false, false, -1, -1);
            lastHover = null;
        }
        getOverlay().setVisible(false);
        getOverlay().clearTooltip();
        getOverlay().draw(this);
    }

    protected void mousePressed(boolean ctrlDown, boolean shiftDown, double x, double y) {
        final Component c = getComponentAt(x, y);
        if (c != lastFocus) {
            if (lastFocus != null) {
                lastFocus.setFocused(false);
                lastFocus = null;
            }
        }
        if (c != null && c.isEnabled()) {
            c.setFocused(true);
            c.onMouseDown(ctrlDown, shiftDown, x, y);
            lastFocus = c;
        }
    }

    protected void mouseReleased(boolean ctrlDown, boolean shiftDown, double x, double y) {
        if (lastFocus != null && lastFocus.isEnabled()) {
            lastFocus.onMouseUp(ctrlDown, shiftDown, x, y);
        }
    }

    protected void mouseClicked(boolean ctrlDown, boolean shiftDown, double x, double y) {
        final Component c;
        if ((c = getComponentAt(x, y)) != null && c.isEnabled()) {
            c.setFocused(true);
            c.onMouseClick(ctrlDown, shiftDown, x, y);
        }
    }

    protected void mouseDoubleClicked(boolean ctrlDown, boolean shiftDown, double x, double y) {
        final Component c = getComponentAt(x, y);
        if (c != null && c.isEnabled()) {
            c.onMouseDoubleClicked(ctrlDown, shiftDown, x, y);
        }
    }

    protected void mouseScrolled(boolean controlDown, boolean shiftDown, double x, double y, double rotation) {
        if (lastHover != null && lastHover.isEnabled()) {
            lastHover.onMouseScrolled(controlDown, shiftDown, x, y, rotation);
        }

    }

    /**
     * Perform key pressed event
     *
     * @param ctrlDown  whether the control key is down
     * @param shiftDown whether the shift key is down
     * @param keyCode   the key code
     */
    protected void keyPressed(boolean ctrlDown, boolean shiftDown, int keyCode) {
        //todo
        if (lastFocus == null) {
            return;
        }
        lastFocus.onKeyPress(ctrlDown, shiftDown, keyCode);
    }

    /**
     * Perform key released event
     *
     * @param ctrlDown  whether the control key is down
     * @param shiftDown whether the shift key is down
     * @param keyCode   the key code
     */
    protected void keyReleased(boolean ctrlDown, boolean shiftDown, int keyCode) {
        //todo
        if (lastFocus == null) {
            return;
        }
        lastFocus.onKeyRelease(ctrlDown, shiftDown, keyCode);
    }

    /**
     * Perform key typed event
     *
     * @param ctrlDown  whether the control key is down
     * @param shiftDown whether the shift key is down
     * @param keyCode   the key code
     */
    protected void keyTyped(boolean ctrlDown, boolean shiftDown, int keyCode) {
        //todo
        if (lastFocus == null) {
            return;
        }
        lastFocus.onKeyType(ctrlDown, shiftDown, keyCode);
    }

    protected abstract void done();

    public static <IMG> boolean isFigureContext(final Renderer<IMG> renderer, final GraphicsContext<IMG> context) {
        return context == renderer.getFigureContext();
    }
}
