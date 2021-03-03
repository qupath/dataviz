package net.mahdilamb.dataviz.graphics.shapes;

import net.mahdilamb.dataviz.graphics.ChartCanvas;

/**
 * A shape that can be draw on the chart canvas
 */
public interface Shape {
    /**
     * Draw the shape filled
     *
     * @param canvas the canvas to draw on
     */
    void fill(ChartCanvas<?> canvas);

    /**
     * Draw the shape stroked
     *
     * @param canvas the canvas to draw on
     */
    void stroke(ChartCanvas<?> canvas);


}
