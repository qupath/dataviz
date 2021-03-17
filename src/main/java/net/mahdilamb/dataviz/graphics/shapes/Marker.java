package net.mahdilamb.dataviz.graphics.shapes;

import net.mahdilamb.dataviz.graphics.ChartCanvas;

/**
 * Marker shape
 */
public class Marker implements Shape {
    /**
     * Reusable marker - can be used for single-thread applications
     */
    public static final Marker MARKER = new Marker();
    /**
     * The shape of the marker
     */
    public MarkerShape shape = MarkerShape.CIRCLE;
    /**
     * The position and size of the marker
     */
    public double x = 8, y = 8, size = 16;

    @Override
    public void fill(ChartCanvas<?> canvas) {
        shape.fill.paint(canvas, x, y, size);
    }

    @Override
    public void stroke(ChartCanvas<?> canvas) {
        shape.stroke.paint(canvas, x, y, size);
    }

    @Override
    public String toString() {
        return String.format("Marker {%s, x: %s, y: %s, size: %s}", shape, x, y, size);
    }
}
