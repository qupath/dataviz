package net.mahdilamb.dataviz.graphics;

import net.mahdilamb.dataviz.figure.Renderer;

/**
 * Interface for the canvas element in a chart
 */
public interface GraphicsContext<IMAGE> extends GraphicsBuffer<IMAGE> {
    /**
     * @return the renderer of this canvas
     */
    Renderer<IMAGE> getRenderer();

}
