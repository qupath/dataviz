package net.mahdilamb.dataviz.graphics;

import net.mahdilamb.dataviz.figure.Renderer;

/**
 * Interface for the canvas element in a chart
 */
public interface GraphicsContext extends GraphicsBuffer {
    /**
     * @return the renderer of this canvas
     */
    Renderer getRenderer();

}
