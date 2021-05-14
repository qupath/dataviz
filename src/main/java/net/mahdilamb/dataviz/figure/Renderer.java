package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.GraphicsContext;
import net.mahdilamb.dataviz.io.FigureExporter;
import net.mahdilamb.dataviz.ui.IconStore;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.mahdilamb.dataviz.figure.AbstractComponent.print;

/**
 * A figure renderer
 */
public abstract class Renderer {

    private FigureBase<?> figure;
    protected boolean overlayEnabled = true;
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

    /**
     * @param component the component to draw
     * @return whether this component should be drawn unbuffered or not
     */
    final boolean drawDirect(Component component) {
        return (component.context == getFigureContext() && getFigure().drawDirect) || component.drawDirect;
    }

    /**
     * Refresh the figure onto the external canvas
     *
     * @param canvas the external canvas
     */
    protected void refresh(GraphicsContext canvas) {
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
     * @param text the text
     * @return the height of the text with the given font
     */
    protected abstract double getTextLineHeight(final Font font, final String text);

    /**
     * @return the text from the clipboard or {@code null} if no text
     */
    protected abstract String getFromClipboard();

    /**
     * Add text to the clipboard
     *
     * @param text the text to add
     */
    protected abstract void addToClipboard(final String text);

    /**
     * This method is called by a figure when a rendering pass has finished
     */
    protected abstract void done();

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
    protected abstract GraphicsContext getFigureContext();

    /**
     * Save a file based on its extension (svg, jpg, bmp, png are supported)
     *
     * @param file the file to save as
     */
    public final void saveAs(File file) {
        FigureExporter.exportFigure(file, this);
    }

    /**
     * @param renderer the renderer
     * @param context  the context
     * @return whether the context is the figure context
     */
    public static boolean isFigureContext(final Renderer renderer, final GraphicsContext context) {
        return context == renderer.getFigureContext();
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
    protected abstract GraphicsContext getOverlayContext();

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

    /* Input events */

    /**
     * @param x the x position in the figure
     * @param y the y position in the figure
     * @return the component at the x, y position
     */
    protected Component getComponentAt(double x, double y) {
        Component component;
        if (!overlayEnabled || (component = getOverlay().getComponentAt(x, y)) == null) {
            component = getFigure().getComponentAt(x, y);
        }
        return component;
    }

    /**
     * Method for when the mouse is moved
     *
     * @param x the x position of the cursor
     * @param y the y position of the cursor
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
            if (lastHover != null) {
                lastHover.onMouseExit(ctrlDown, shiftDown, x, y);
            }
            clearTooltip();
            if (hover != null) {
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
        getOverlay().setVisible(true);
        getOverlay().draw(this);

    }

    /**
     * Method for when the mouse is pressed
     *
     * @param x the x position of the cursor
     * @param y the y position of the cursor
     */
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

    /**
     * Method for when the mouse is released
     *
     * @param x the x position of the cursor
     * @param y the y position of the cursor
     */
    protected void mouseReleased(boolean ctrlDown, boolean shiftDown, double x, double y) {
        if (lastFocus != null && lastFocus.isEnabled()) {
            lastFocus.onMouseUp(ctrlDown, shiftDown, x, y);
        }
    }


    /**
     * Method for when the mouse is clicked
     *
     * @param x the x position of the cursor
     * @param y the y position of the cursor
     */
    protected void mouseClicked(boolean ctrlDown, boolean shiftDown, double x, double y) {
        final Component c = getComponentAt(x, y);
        final Component lastFocus = this.lastFocus;
        if (c != null && c.isEnabled()) {
            c.setFocused(true);
            c.onMouseClick(ctrlDown, shiftDown, x, y);
            this.lastFocus = c;
        }
    }

    /**
     * Method for when the mouse is double-clicked
     *
     * @param x the x position of the cursor
     * @param y the y position of the cursor
     */
    protected void mouseDoubleClicked(boolean ctrlDown, boolean shiftDown, double x, double y) {
        final Component c = getComponentAt(x, y);
        if (c != null && c.isEnabled()) {
            c.onMouseDoubleClick(ctrlDown, shiftDown, x, y);
        }
    }

    /**
     * Method called when the mouse goes out of the figure area
     */
    protected void mouseExited() {
        if (lastHover != null) {
            lastHover.onMouseExit(false, false, -1, -1);
            lastHover = null;
        }
        getOverlay().clearTooltip();
        getOverlay().draw(this);
    }

    /**
     * Method for when the mouse wheel is scrolled
     *
     * @param x the x position of the cursor
     * @param y the y position of the cursor
     */
    protected void mouseScrolled(boolean controlDown, boolean shiftDown, double x, double y, double rotation) {
        if (lastHover != null && lastHover.isEnabled()) {
            lastHover.onMouseScroll(controlDown, shiftDown, x, y, rotation);
        }
    }

    /**
     * Perform key pressed event
     *
     * @param ctrlDown  whether the control key is down
     * @param shiftDown whether the shift key is down
     * @param keyCode   the key code
     */
    protected void keyPressed(boolean ctrlDown, boolean shiftDown, int keyCode, char character) {
        if (lastFocus == null) {
            return;
        }
        lastFocus.onKeyPress(ctrlDown, shiftDown, keyCode, character);
    }

    /**
     * Perform key released event
     *
     * @param ctrlDown  whether the control key is down
     * @param shiftDown whether the shift key is down
     * @param keyCode   the key code
     */
    protected void keyReleased(boolean ctrlDown, boolean shiftDown, int keyCode, char character) {
        if (lastFocus == null) {
            return;
        }
        lastFocus.onKeyRelease(ctrlDown, shiftDown, keyCode, character);
    }

    /**
     * Perform key typed event
     *
     * @param ctrlDown  whether the control key is down
     * @param shiftDown whether the shift key is down
     * @param keyCode   the key code
     */
    protected void keyTyped(boolean ctrlDown, boolean shiftDown, int keyCode, char character) {
        if (lastFocus == null) {
            return;
        }
        lastFocus.onKeyType(ctrlDown, shiftDown, keyCode, character);
    }

}
