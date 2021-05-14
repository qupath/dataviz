package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.swing.BufferedImageExtended;
import net.mahdilamb.dataviz.swing.SwingPainter;

/**
 * Buffering strategies for drawing components
 */
public abstract class BufferingStrategy<C extends Component> {
    static final SwingPainter BUFFERING_PAINTER = new SwingPainter();
    /**
     * A "no buffering strategy" - the content is drawn directly on the canvas
     */
    public static final BufferingStrategy<Component> NO_BUFFERING = new NoBuffering();
    /**
     * Simple double buffering - the content is drawn to a buffer and the buffer is displayed
     */
    public static final BufferingStrategy<Component> BASIC_BUFFERING = new SimpleBuffering();

    BufferingStrategy() {

    }

    /**
     * Draw the component on the context
     *
     * @param component the component
     * @param renderer  the renderer
     * @param context   the context
     */
    abstract void draw(final AbstractComponent component, Renderer renderer, GraphicsBuffer context);

    protected abstract void clearBuffer(Renderer renderer, final C component);


    /**
     * Abstract buffered strategy
     *
     * @param <STORE> the type of the store
     */
    private static abstract class BufferedStrategyImpl<C extends Component, STORE> extends BufferingStrategy<C> {
        @SuppressWarnings("unchecked")
        protected STORE getBufferStore(final C component) {
            return (STORE) component.bufferStore;
        }

        protected STORE setBufferStore(final C component, final STORE store) {
            component.bufferStore = store;
            return store;
        }

        protected boolean needsRefresh(final C component) {
            return component.bufferNeedsRefresh;
        }

        @Override
        final void draw(AbstractComponent component, Renderer renderer, GraphicsBuffer context) {
            if (context == null) {
                return;
            }
            if (component.hasChildren()) {
                //is a group - doesn't need to draw
                component.drawComponent(renderer, context);
            } else {
                @SuppressWarnings("unchecked") final C c = (C) component;
                if (renderer.drawDirect((Component) component)) {
                    //this render is directly to the canvas - draw as such
                    drawUnbuffered(c, renderer, context);
                } else {
                    //draw using the buffer
                    drawBuffered(c, renderer, context);
                }
                c.bufferNeedsRefresh = false;
            }
        }

        protected final void drawUnbuffered(C component, Renderer renderer, GraphicsBuffer context) {
            component.drawComponent(renderer, context);
        }

        /**
         * Draw the component on the given buffer
         *
         * @param component the component
         * @param renderer  the renderer
         * @param context   the buffer
         */
        protected abstract void drawBuffered(C component, Renderer renderer, GraphicsBuffer context);
    }

    /**
     * Create a custom buffering strategy
     *
     * @param <C>     the type of the component
     * @param <STORE> the type of the store
     */
    public static abstract class CustomBufferedStrategy<C extends Component, STORE> extends BufferedStrategyImpl<C, STORE> {

        /**
         * Create a custom buffered strategy
         */
        protected CustomBufferedStrategy() {
        }

        @Override
        protected abstract void drawBuffered(final C component, Renderer renderer, GraphicsBuffer context);


    }

    /**
     * A "none" buffering strategy
     */
    private static final class NoBuffering extends BufferingStrategy<Component> {

        @Override
        void draw(AbstractComponent component, Renderer renderer, GraphicsBuffer context) {
            if (context == null) {
                return;
            }
            component.drawComponent(renderer, context);

        }

        @Override
        protected void clearBuffer(Renderer renderer, Component component) {
            //ignored
        }
    }

    /**
     * A double-buffer whereby the component is drawn to a buffered image and the buffered image is drawn until the
     * component has changed
     */
    private static final class SimpleBuffering extends BufferedStrategyImpl<Component, GraphicsBuffer> {

        @Override
        protected void drawBuffered(Component component, Renderer renderer, GraphicsBuffer context) {
            GraphicsBuffer buffer = getBufferStore(component);
            if (buffer == null) {
                buffer = createBuffer(
                        component.getWidth(), component.getHeight(),
                        component.getX(), component.getY(),
                        component.overflowTop, component.overflowLeft, component.overflowBottom, component.overflowRight
                );
                setBufferStore(component, buffer);
                component.bufferNeedsRefresh = true;
            }
            if (needsRefresh(component)) {
                component.drawComponent(renderer, buffer);
            }
            if ((buffer = getBufferStore(component)) != null) {
                drawBuffer(context, buffer, component.getX(), component.getY());
            }
        }

        @Override
        protected void clearBuffer(Renderer renderer, Component component) {
            if (bufferSizeChanged(getBufferStore(component), component.getX(), component.getY(), component.getWidth(), component.getHeight())) {
                component.bufferStore = null;
            } else {
                getBufferStore(component).reset();
            }
        }
    }

    /**
     * Create a buffer
     *
     * @param width          the width of the buffer
     * @param height         the height of the
     * @param translateX     the x position
     * @param translateY     the y position
     * @param overflowTop    the overflow on the top
     * @param overflowLeft   the overflow on the left
     * @param overflowBottom the overflow on the bottom
     * @param overflowRight  the overflow on the right
     * @return a new buffer
     */
    protected static GraphicsBuffer createBuffer(double width, double height, double translateX, double translateY, int overflowTop, int overflowLeft, int overflowBottom, int overflowRight) {
        return new BufferedImageExtended(width, height, translateX, translateY, overflowTop, overflowLeft, overflowBottom, overflowRight);
    }

    /**
     * Create a buffer off the main thread
     *
     * @param width          the width of the buffer
     * @param height         the height of the
     * @param translateX     the x position
     * @param translateY     the y position
     * @param overflowTop    the overflow on the top
     * @param overflowLeft   the overflow on the left
     * @param overflowBottom the overflow on the bottom
     * @param overflowRight  the overflow on the right
     * @return a new buffer
     */
    protected static GraphicsBuffer createBufferNonMain(double width, double height, double translateX, double translateY, int overflowTop, int overflowLeft, int overflowBottom, int overflowRight) {
        return new BufferedImageExtended(BUFFERING_PAINTER, width, height, translateX, translateY, overflowTop, overflowLeft, overflowBottom, overflowRight);

    }

    /**
     * Create a buffer for a component
     *
     * @param component the component
     * @param <C>       the type of the component
     * @return the new buffer
     */
    protected static <C extends Component> GraphicsBuffer createBuffer(final C component) {
        return createBuffer(component.sizeX, component.sizeY, component.posX, component.posY, component.overflowTop, component.overflowLeft, component.overflowBottom, component.overflowRight);
    }

    /**
     * Draw the buffer to a source context
     *
     * @param context the source context
     * @param buffer  the buffer
     * @param x       the x position
     * @param y       the y position
     */
    protected static void drawBuffer(GraphicsBuffer context, GraphicsBuffer buffer, double x, double y) {
        final BufferedImageExtended bufferedImageExtended = (BufferedImageExtended) buffer;
        context.drawImage(bufferedImageExtended, x - bufferedImageExtended.overflowLeft, y - bufferedImageExtended.overflowTop);
    }

    /**
     * Utility method to check if the buffer size has been changed and therefore should be removed
     *
     * @param buffer the source buffer
     * @param x      the new x
     * @param y      the new y
     * @param width  the new width
     * @param height the new height
     * @return whether the buffer size has changed
     */
    protected static boolean bufferSizeChanged(GraphicsBuffer buffer, double x, double y, double width, double height) {
        final BufferedImageExtended graphicsBuffer = (BufferedImageExtended) buffer;//TODO full size changed
        return (graphicsBuffer.width) != width || (graphicsBuffer.height) != height;
    }
}
