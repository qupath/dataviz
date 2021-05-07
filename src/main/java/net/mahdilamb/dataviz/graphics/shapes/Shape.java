package net.mahdilamb.dataviz.graphics.shapes;

import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.GraphicsContext;

/**
 * A shape that can be draw on the chart canvas
 */
public interface Shape {
    /**
     * Draw the shape filled
     *
     * @param canvas the canvas to draw on
     */
    void fill(GraphicsBuffer<?> canvas);

    /**
     * Draw the shape stroked
     *
     * @param canvas the canvas to draw on
     */
    void stroke(GraphicsBuffer<?> canvas);


}
