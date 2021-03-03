package net.mahdilamb.dataviz.graphics.shapes;

import net.mahdilamb.dataviz.graphics.ChartCanvas;
import net.mahdilamb.dataviz.graphics.MarkerShape;

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
        Markers.fill(canvas, x, y, size, shape);
    }

    @Override
    public void stroke(ChartCanvas<?> canvas) {
        Markers.stroke(canvas, x, y, size, shape);
    }

    @Override
    public String toString() {
        return String.format("Marker {%s, x: %s, y: %s, size: %s}", shape, x, y, size);
    }
}
