package net.mahdilamb.dataviz.graphics.shapes;

import net.mahdilamb.dataviz.graphics.GraphicsBuffer;

/**
 * Marker shape
 */
public class Marker implements Shape {
    /**
     * The shape of the marker
     */
    public MarkerShape shape = MarkerShape.CIRCLE;
    /**
     * The position and size of the marker
     */
    public double x = 8, y = 8, size = 16;
public Marker(){

}
    public Marker(MarkerShape shape, double x, double y, double size) {
        this.shape = shape;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    @Override
    public void fill(GraphicsBuffer<?> buffer) {
        shape.fill.paint(buffer, x, y, size);
    }

    @Override
    public void stroke(GraphicsBuffer<?> buffer) {
        shape.stroke.paint(buffer, x, y, size);
    }

    @Override
    public String toString() {
        return String.format("Marker {%s, x: %s, y: %s, size: %s}", shape, x, y, size);
    }
}
