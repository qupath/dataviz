package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.GraphicsBuffer;

/**
 * Buffering strategies for drawing components
 *
 * @param <IMG> the type of the image in the buffered context
 */
public abstract class BufferingStrategy<C extends Component, IMG> {
    /**
     * A "no buffering strategy" - the content is drawn directly on the canvas
     */
    public static final BufferingStrategy<Component, ?> NO_BUFFERING = new NoBuffering<>();
    /**
     * Simple double buffering - the content is drawn to a buffer and the buffer is displayed
     */
    public static final BufferingStrategy<Component, ?> BASIC_BUFFERING = new SimpleBuffering<>();

    BufferingStrategy() {

    }

    /**
     * Draw the component on the context
     *
     * @param component the component
     * @param renderer  the renderer
     * @param context   the context
     */
    abstract void draw(final AbstractComponent component, Renderer<IMG> renderer, GraphicsBuffer<IMG> context);

    protected abstract void clearBuffer(Renderer<IMG> renderer, final C component);


    /**
     * Abstract buffered strategy
     *
     * @param <IMG>   the type of the image
     * @param <STORE> the type of the store
     */
    private static abstract class BufferedStrategyImpl<C extends Component, IMG, STORE> extends BufferingStrategy<C, IMG> {
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
        final void draw(AbstractComponent component, Renderer<IMG> renderer, GraphicsBuffer<IMG> context) {
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

        protected final void drawUnbuffered(C component, Renderer<IMG> renderer, GraphicsBuffer<IMG> context) {
            component.drawComponent(renderer, context);
        }

        /**
         * Draw the component on the given buffer
         *
         * @param component the component
         * @param renderer  the renderer
         * @param context   the buffer
         */
        protected abstract void drawBuffered(C component, Renderer<IMG> renderer, GraphicsBuffer<IMG> context);
    }

    /**
     * Create a custom buffering strategy
     *
     * @param <C>     the type of the component
     * @param <IMG>   the type of the image in the renderer
     * @param <STORE> the type of the store
     */
    public static abstract class CustomBufferedStrategy<C extends Component, IMG, STORE> extends BufferedStrategyImpl<C, IMG, STORE> {

        /**
         * Create a custom buffered strategy
         */
        protected CustomBufferedStrategy() {
        }

        @Override
        protected abstract void drawBuffered(final C component, Renderer<IMG> renderer, GraphicsBuffer<IMG> context);

        protected GraphicsBuffer<IMG> createBuffer(Renderer<IMG> renderer, double width, double height, double translateX, double translateY, int overflowTop, int overflowLeft, int overflowBottom, int overflowRight) {
            return renderer.createBuffer(width, height, translateX, translateY, overflowTop, overflowLeft, overflowBottom, overflowRight);
        }

        protected GraphicsBuffer<IMG> createBuffer(Renderer<IMG> renderer, final C component) {
            return renderer.createBuffer(component.sizeX, component.sizeY, component.posX, component.posY, component.overflowTop, component.overflowLeft, component.overflowBottom, component.overflowRight);
        }

        protected void drawBuffer(Renderer<IMG> renderer, GraphicsBuffer<IMG> context, GraphicsBuffer<IMG> buffer, double x, double y) {
            renderer.drawBuffer(context, buffer, x, y);
        }

    }

    private static final class NoBuffering<IMG> extends BufferingStrategy<Component, IMG> {

        @Override
        void draw(AbstractComponent component, Renderer<IMG> renderer, GraphicsBuffer<IMG> context) {
            if (context == null) {
                return;
            }
            component.drawComponent(renderer, context);

        }

        @Override
        protected void clearBuffer(Renderer<IMG> renderer, Component component) {
            //ignored
        }
    }

    private static final class SimpleBuffering<IMG> extends BufferedStrategyImpl<Component, IMG, GraphicsBuffer<IMG>> {

        @Override
        protected void drawBuffered(Component component, Renderer<IMG> renderer, GraphicsBuffer<IMG> context) {
            GraphicsBuffer<IMG> buffer = getBufferStore(component);
            if (buffer == null) {
                buffer = renderer.createBuffer(
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
                renderer.drawBuffer(context, buffer, component.getX(), component.getY());
            }
        }

        @Override
        protected void clearBuffer(Renderer<IMG> renderer, Component component) {
            if (renderer.bufferSizeChanged(getBufferStore(component), component.getX(), component.getY(), component.getWidth(), component.getHeight())) {
                component.bufferStore = null;
            } else {
                getBufferStore(component).reset();
            }
        }
    }
}
