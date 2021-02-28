package net.mahdilamb.charts.graphics.shapes;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.MarkerShape;

public class Marker implements Shape {
    public static final Marker MARKER = new Marker();
    public MarkerShape shape = MarkerShape.CIRCLE;

    public double x = 8, y = 8, size = 16;

    @Override
    public void fill(ChartCanvas<?> canvas) {
        Markers.fill(canvas, x, y, size,shape);
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
