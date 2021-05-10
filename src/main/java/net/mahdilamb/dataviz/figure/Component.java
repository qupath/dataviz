package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.GraphicsContext;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.ui.IconStore;
import net.mahdilamb.dataviz.utils.ColorUtils;
import net.mahdilamb.dataviz.utils.functions.BiBooleanBiDoubleConsumer;
import net.mahdilamb.dataviz.utils.functions.BiBooleanIntConsumer;
import net.mahdilamb.dataviz.utils.functions.BiDoubleConsumer;

import java.awt.*;
import java.io.InputStream;
import java.util.Objects;

public abstract class Component extends AbstractComponent {

    private static final BufferingStrategy<? extends Component, ?> DEFAULT_BUFFERING_STRATEGY = BufferingStrategy.BASIC_BUFFERING;
    protected static final Color FOCUS_COLOR = new Color(.8f, 0f, .8f, .2f);

    /* Interaction fields */
    boolean hasFocus = false;
    boolean enabled = true;
    BiBooleanBiDoubleConsumer onMouseEnter = BiBooleanBiDoubleConsumer.IDENTITY;
    BiBooleanBiDoubleConsumer onMouseExit = BiBooleanBiDoubleConsumer.IDENTITY;
    BiBooleanBiDoubleConsumer onMouseUp = BiBooleanBiDoubleConsumer.IDENTITY;
    BiBooleanBiDoubleConsumer onMouseDown = BiBooleanBiDoubleConsumer.IDENTITY;
    BiBooleanBiDoubleConsumer onMouseClick = BiBooleanBiDoubleConsumer.IDENTITY;
    BiBooleanIntConsumer onKeyPress = BiBooleanIntConsumer.IDENTITY;
    BiBooleanIntConsumer onKeyRelease = BiBooleanIntConsumer.IDENTITY;
    BiBooleanIntConsumer onKeyType = BiBooleanIntConsumer.IDENTITY;

    /* Buffer fields */
    boolean bufferNeedsRefresh = true;
    boolean drawDirect = false;
    private final BufferingStrategy<? extends Component, ?> bufferingStrategy;
    Object bufferStore;
    int overflowTop = 5, overflowLeft = 5, overflowBottom = 5, overflowRight = 5;

    /* Others */
    Component parent;
    protected Tooltip tooltip;

    /**
     * Create a component that is double buffered
     */
    protected Component() {
        this.bufferingStrategy = DEFAULT_BUFFERING_STRATEGY;
    }

    /**
     * Create a component that has its own buffering strategy
     *
     * @param bufferingStrategy the buffering strategy
     */
    protected Component(BufferingStrategy<? extends Component, ?> bufferingStrategy) {
        this.bufferingStrategy = (bufferingStrategy == null ? BufferingStrategy.NO_BUFFERING : bufferingStrategy);
    }

    /**
     * Create a component that has its own buffering strategy with the given overflow
     *
     * @param bufferingStrategy the buffering strategy
     * @param overflowTop       the amount of overflow on the top
     * @param overflowLeft      the amount of overflow on the left
     * @param overflowBottom    the amount of overflow on the bottom
     * @param overflowRight     the amount of overflow on the right
     */
    protected Component(BufferingStrategy<? extends Component, ?> bufferingStrategy, int overflowTop, int overflowLeft, int overflowBottom, int overflowRight) {
        this.bufferingStrategy = bufferingStrategy;
        setOverflow(overflowTop, overflowLeft, overflowBottom, overflowRight);
    }

    /**
     * Create a component is doubly buffered with the given overflow
     *
     * @param overflowTop    the amount of overflow on the top
     * @param overflowLeft   the amount of overflow on the left
     * @param overflowBottom the amount of overflow on the bottom
     * @param overflowRight  the amount of overflow on the right
     */
    protected Component(int overflowTop, int overflowLeft, int overflowBottom, int overflowRight) {
        this.bufferingStrategy = DEFAULT_BUFFERING_STRATEGY;
        setOverflow(overflowTop, overflowLeft, overflowBottom, overflowRight);
    }

    /**
     * Set the overflow amount
     *
     * @param overflowTop    the amount of overflow on the top
     * @param overflowLeft   the amount of overflow on the left
     * @param overflowBottom the amount of overflow on the bottom
     * @param overflowRight  the amount of overflow on the right
     */
    private void setOverflow(int overflowTop, int overflowLeft, int overflowBottom, int overflowRight) {
        this.overflowTop = overflowTop;
        this.overflowLeft = overflowLeft;
        this.overflowBottom = overflowBottom;
        this.overflowRight = overflowRight;
    }

    /**
     * @param <T> the type of the buffer
     * @return the buffering strategy
     */
    @SuppressWarnings("unchecked")
    protected <T> BufferingStrategy<Component, T> getBufferingStrategy() {
        return (BufferingStrategy<Component, T>) bufferingStrategy;
    }

    /**
     * Set the parent of this component
     *
     * @param component the parent component
     */
    protected void setParent(final Component component) {
        this.parent = component;
    }

    /**
     * @return the tooltip associated with this component
     */
    protected Tooltip getTooltip() {
        return tooltip;
    }

    /**
     * Set the tooltip for the component
     *
     * @param tooltip the tooltip
     */
    public final void setTooltip(final Tooltip tooltip) {
        if (tooltip == null) {
            if (context.getRenderer().lastHover == this) {
                context.getRenderer().getOverlay().clearTooltip();
                redraw();
            }
        } else {
            tooltip.component = this;
            tooltip.setContext(context);
            if (context != null) {
                if (context.getRenderer().lastHover == this) {
                    context.getRenderer().getOverlay().showTooltip(tooltip);
                    redraw();
                }
            }
        }
        this.tooltip = tooltip;


    }

    @Override
    protected void markLayoutAsOldQuietly() {
        layoutNeedsRefresh = true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final <T> void markDrawAsOldQuietly() {
        bufferNeedsRefresh = true;
        if (context != null && bufferStore != null) {
            final BufferingStrategy<Component, T> bufferingStrategy = getBufferingStrategy();
            bufferingStrategy.clearBuffer((Renderer<T>) context.getRenderer(), this);
        }
    }

    @Override
    protected final <T> void draw(Renderer<T> renderer, GraphicsBuffer<T> context) {
        final BufferingStrategy<Component, T> bufferingStrategy = getBufferingStrategy();
        bufferingStrategy.draw(this, renderer, context);
    }

    @Override
    final boolean hasChildren() {
        return false;
    }

    @Override
    <T> void setContext(GraphicsContext<T> context) {
        super.setContext(context);
        if (tooltip != null) {
            tooltip.setContext(context);
        }
    }

    @Override
    protected Component getComponentAt(double x, double y) {
        if (containsPoint(x, y)) {
            return this;
        }
        return null;
    }

    /**
     * Set the focus state of this component
     *
     * @param focused whether the item is focused
     */
    final void setFocused(final boolean focused) {
        if (this.hasFocus != (this.hasFocus = focused) && enabled) {
            if (focused) {
                onFocus();
            } else {
                onBlur();
            }
        }
    }

    /**
     * Set the enable state of this component
     *
     * @param enabled whether this component is enabled
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled != (this.enabled = enabled)) {
            redraw();
        }
    }

    /**
     * Draw a focus ring around this component
     *
     * @param canvas the canvas to draw to
     * @param <T>    the type of the image in the buffer
     */
    protected <T> void drawFocusRing(GraphicsBuffer<T> canvas) {
        if (!hasFocus) {
            return;
        }
        canvas.setStroke(FOCUS_COLOR);
        canvas.setStroke(Stroke.SOLID);
        canvas.strokeRoundRect(getX(), getY(), getWidth(), getHeight(), 4, 4);
    }

    /**
     * Set the click action
     *
     * @param consumer the click action
     */
    public final void setOnMouseClick(BiBooleanBiDoubleConsumer consumer) {
        Objects.requireNonNull(this.onMouseClick = consumer);
    }

    /**
     * Set the hover action
     *
     * @param consumer the hover action
     */
    public final void setOnMouseEnter(BiBooleanBiDoubleConsumer consumer) {
        Objects.requireNonNull(this.onMouseEnter = consumer);
    }

    /**
     * Set the hover action
     *
     * @param consumer the hover action
     */
    public final void setOnMouseExit(BiBooleanBiDoubleConsumer consumer) {
        Objects.requireNonNull(this.onMouseExit = consumer);
    }

    /**
     * Set the click action
     *
     * @param consumer the click action
     */
    public final void setOnMouseClick(BiDoubleConsumer consumer) {
        Objects.requireNonNull(consumer);
        this.onMouseClick = (ctrl, shirt, x, y) -> consumer.accept(x, y);
    }

    /**
     * Set the hover action
     *
     * @param consumer the hover action
     */
    public final void setOnMouseEnter(BiDoubleConsumer consumer) {
        Objects.requireNonNull(consumer);
        this.onMouseEnter = (ctrl, shirt, x, y) -> consumer.accept(x, y);
    }

    /**
     * Set the click action
     *
     * @param consumer the click action
     */
    public final void setOnMouseClick(Runnable consumer) {
        Objects.requireNonNull(consumer);
        this.onMouseClick = (ctrl, shirt, x, y) -> consumer.run();
    }

    /**
     * Set the hover action
     *
     * @param consumer the hover action
     */
    public final void setOnMouseEnter(Runnable consumer) {
        Objects.requireNonNull(consumer);
        this.onMouseEnter = (ctrl, shirt, x, y) -> consumer.run();
    }
    /**
     * Set the mouse out action
     *
     * @param consumer the hover action
     */
    public final void setOnMouseExit(Runnable consumer) {
        Objects.requireNonNull(consumer);
        this.onMouseExit = (ctrl, shirt, x, y) -> consumer.run();
    }
    /**
     * Method called on single click
     *
     * @param ctrlDown  whether the ctrl key is down
     * @param shiftDown whether the shift key is down
     * @param x         the absolution x position of the mouse in respect to the panel
     * @param y         the absolution y position of the mouse in respect to the panel
     */
    protected void onMouseClick(boolean ctrlDown, boolean shiftDown, double x, double y) {
        onMouseClick.accept(ctrlDown, shiftDown, x, y);
    }

    /**
     * Method called on double click
     *
     * @param ctrlDown  whether the ctrl key is down
     * @param shiftDown whether the shift key is down
     * @param x         the absolution x position of the mouse in respect to the panel
     * @param y         the absolution y position of the mouse in respect to the panel
     */
    protected void onMouseDoubleClick(boolean ctrlDown, boolean shiftDown, double x, double y) {

    }

    /**
     * Method called when a scroll action is performed
     *
     * @param ctrlDown  whether the ctrl key is down
     * @param shiftDown whether the shift key is down
     * @param x         the absolution x position of the mouse in respect to the panel
     * @param y         the absolution y position of the mouse in respect to the panel
     * @param rotation  the amount of rotation
     */
    protected void onMouseScroll(boolean ctrlDown, boolean shiftDown, double x, double y, double rotation) {

    }

    /**
     * Method called when the mouse enters this component
     *
     * @param ctrlDown  whether the ctrl key is down
     * @param shiftDown whether the shift key is down
     * @param x         the absolution x position of the mouse in respect to the panel
     * @param y         the absolution y position of the mouse in respect to the panel
     */
    protected void onMouseEnter(boolean ctrlDown, boolean shiftDown, double x, double y) {
        onMouseEnter.accept(ctrlDown, shiftDown, x, y);
    }

    /**
     * Method called when the mouse moves over this component
     *
     * @param ctrlDown  whether the ctrl key is down
     * @param shiftDown whether the shift key is down
     * @param x         the absolution x position of the mouse in respect to the panel
     * @param y         the absolution y position of the mouse in respect to the panel
     */
    protected void onMouseMove(boolean ctrlDown, boolean shiftDown, double x, double y) {

    }

    /**
     * Method called when the mouse leaves this component
     *
     * @param ctrlDown  whether the ctrl key is down
     * @param shiftDown whether the shift key is down
     * @param x         the absolution x position of the mouse in respect to the panel
     * @param y         the absolution y position of the mouse in respect to the panel
     */
    protected void onMouseExit(boolean ctrlDown, boolean shiftDown, double x, double y) {
        onMouseExit.accept(ctrlDown, shiftDown, x, y);
    }

    /**
     * Method called when this component receives focus
     */
    protected void onFocus() {
        redraw();
    }

    /**
     * Method called when this component loses focus
     */
    protected void onBlur() {
        redraw();
    }

    /**
     * Method called when the mouse is pressed on this component
     *
     * @param ctrlDown  whether the ctrl key is down
     * @param shiftDown whether the shift key is down
     * @param x         the absolution x position of the mouse in respect to the panel
     * @param y         the absolution y position of the mouse in respect to the panel
     */
    protected void onMouseDown(boolean ctrlDown, boolean shiftDown, double x, double y) {
        onMouseDown.accept(ctrlDown, shiftDown, x, y);
    }

    /**
     * Method called when the mouse is released after having been pressed on this component
     *
     * @param ctrlDown  whether the ctrl key is down
     * @param shiftDown whether the shift key is down
     * @param x         the absolution x position of the mouse in respect to the panel
     * @param y         the absolution y position of the mouse in respect to the panel
     */
    protected void onMouseUp(boolean ctrlDown, boolean shiftDown, double x, double y) {
        onMouseUp.accept(ctrlDown, shiftDown, x, y);
    }


    protected void onKeyPress(boolean ctrlDown, boolean shiftDown, int keyCode) {
        onKeyPress.accept(ctrlDown, shiftDown, keyCode);
    }

    protected void onKeyRelease(boolean ctrlDown, boolean shiftDown, int keyCode) {
        onKeyRelease.accept(ctrlDown, shiftDown, keyCode);
    }

    protected void onKeyType(boolean ctrlDown, boolean shiftDown, int keyCode) {
        onKeyType.accept(ctrlDown, shiftDown, keyCode);
    }

    /**
     * @return whether the component is enabled
     */
    protected final boolean isEnabled() {
        return enabled;
    }

    /**
     * @return whether the component is focused
     */
    protected final boolean isFocused() {
        return hasFocus;
    }


    protected static <T> T getIcon(final InputStream source, final Class<? extends Enum<?>> keys, Renderer<T> renderer, int key, final Color backgroundColor) {
        if (ColorUtils.getForegroundFromBackground((backgroundColor == null ? renderer.getFigure().getBackgroundColor() : backgroundColor)) == Color.BLACK) {
            return renderer.getIcons(source, keys).getDark(key);
        }
        return renderer.getIcons(source, keys).getLight(key);
    }

    public static <T> T getIcon(final InputStream source, final Class<? extends Enum<?>> keys, Renderer<T> renderer, int key) {
        return getIcon(source, keys, renderer, key, null);
    }

    protected static double getIconWidth(final InputStream source, final Class<? extends Enum<?>> keys, Renderer<?> renderer) {
        return renderer.getIcons(source, keys).getIconWidth();
    }

    protected static double getIconHeight(final InputStream source, final Class<? extends Enum<?>> keys, Renderer<?> renderer) {
        return renderer.getIcons(source, keys).getIconHeight();
    }

    public static <T> T getMaterialIcon(Renderer<T> renderer, IconStore.MaterialIconKey key, final Color backgroundColor) {
        return getIcon(IconStore.MATERIAL_ICONS, IconStore.MaterialIconKey.class, renderer, key.getIndex(), backgroundColor);
    }

    protected static double getMaterialIconWidth(Renderer<?> renderer) {
        return renderer.getIcons(IconStore.MATERIAL_ICONS, IconStore.MaterialIconKey.class).getIconWidth();
    }

    protected static double getMaterialIconHeight(Renderer<?> renderer) {
        return renderer.getIcons(IconStore.MATERIAL_ICONS, IconStore.MaterialIconKey.class).getIconHeight();
    }

    public static <T> T getDataVizIcon(Renderer<T> renderer, IconStore.DataVizIconKey key, final Color backgroundColor) {
        return getIcon(IconStore.DATAVIZ_ICONS, IconStore.DataVizIconKey.class, renderer, key.getIndex(), backgroundColor);
    }

    protected static double getDataVizIconWidth(Renderer<?> renderer) {
        return renderer.getIcons(IconStore.DATAVIZ_ICONS, IconStore.DataVizIconKey.class).getIconWidth();
    }

    protected static double getDataVizIconHeight(Renderer<?> renderer) {
        return renderer.getIcons(IconStore.DATAVIZ_ICONS, IconStore.DataVizIconKey.class).getIconHeight();
    }

    protected static void mirrorContext(final Component a, final Component b) {
        b.setContext(a.context);
    }
}
