package net.mahdilamb.dataviz.figure;

import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.GraphicsContext;
import net.mahdilamb.dataviz.swing.SwingRenderer;
import net.mahdilamb.dataviz.ui.Label;
import net.mahdilamb.dataviz.ui.Toolbar;
import net.mahdilamb.dataviz.ui.WrappedLabel;

import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;

/**
 * Content representing the part of the display that can be exported
 *
 * @param <C> the concrete type of the content
 */
public abstract class FigureBase<C extends FigureBase<C>> extends Group {
    /*
    Default dimensions
     */
    private static final double DEFAULT_WIDTH = 800;
    private static final double DEFAULT_HEIGHT = 640;
    /**
     * The title of the content - defaults to an empty string
     */
    protected final WrappedLabel title = new WrappedLabel(EMPTY_STRING, new Font(Font.Family.SANS_SERIF, 24));
    /**
     * Content background color
     */
    Color backgroundColor = Color.white;
    /**
     * The dimensions of the content
     */
    double width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;
    /**
     * Whether to draw with a buffer. The default should be false as there will be no buffer
     */
    boolean drawDirect = false;

    /**
     * Create content
     */
    protected FigureBase() {

    }

    protected FigureBase(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        for (final AbstractComponent c : getChildren()) {
            layoutComponent(c, renderer, minX, minY, maxX, maxY);
        }
    }

    /**
     * @return the toolbar to be used in the overlay
     */
    protected Toolbar createToolbar() {
        return null;
    }

    /**
     * @return the title of the figure
     */
    public String getTitle() {
        return title.getText();
    }

    /**
     * Set the title of the figure
     *
     * @param title the title
     * @return this figure
     */
    @SuppressWarnings("unchecked")
    public final C setTitle(final String title) {
        this.title.setText(title);
        update();
        return (C) this;
    }

    /**
     * Update the title
     *
     * @param fn the function to apply to the title
     * @return this figure
     */
    @SuppressWarnings("unchecked")
    public final C updateTitle(final Consumer<Label> fn) {
        fn.accept(title);
        update();
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C setTitleVisibility(boolean visibility) {
        title.setVisible(visibility);
        update();
        return (C) this;
    }

    /**
     * Show this figure in Swing
     *
     * @implNote This method is terminal because this will spawn a new thread and no longer be in sync with the calling
     * thread
     */
    public void show() {
        FigureViewer.getDefault().show(this);
    }

    /**
     * Show this figure in a non-Swing renderer
     *
     * @param creator the function that renders a figure
     */
    public void show(FigureViewer creator) {
        creator.show(this);
    }

    /**
     * Save this figure
     *
     * @param file the path
     * @implNote as with showing, this *can* lead to asyncrhony (except for non-bitmap output, hence this is also a
     * terminal operation
     */
    public void saveAs(final File file) {
        (context = (context != null ? context : ((Renderer) new SwingRenderer(this, true)).getFigureContext())).getRenderer().saveAs(file);
    }

    /**
     * @return the background color of the figure
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Set the background color
     *
     * @param color the color
     * @return this figure
     */
    @SuppressWarnings("unchecked")
    public final C setBackgroundColor(Color color) {
        this.backgroundColor = (color == null ? Colors.TRANSPARENT : color);
        update();
        return (C) this;
    }

    /**
     * @return the width of the figure
     */
    @Override
    public double getWidth() {
        return width;
    }

    /**
     * @return the height of the figure
     */
    @Override
    public double getHeight() {
        return height;
    }

    /**
     * Update the figure locally
     */
    protected final void update() {
        if (context == null) {
            //not ready yet
            return;
        }
        update(context, false);
    }

    final void update(final GraphicsContext canvas, boolean drawDirect) {
        final GraphicsContext context = getContext();
        if (context != null) {
            final boolean oldDrawDirect = this.drawDirect;
            this.drawDirect = drawDirect;
            canvas.reset();
            layout(context.getRenderer(), 0, 0, width, height);
            draw(context.getRenderer(), canvas);
            canvas.done();
            context.getRenderer().done();
            this.drawDirect = oldDrawDirect;
            return;
        }
        System.err.println("No renderer");
    }

    protected static void markComponentLayoutAsOld(final AbstractComponent component) {
        component.markLayoutAsOldQuietly();
    }

}
