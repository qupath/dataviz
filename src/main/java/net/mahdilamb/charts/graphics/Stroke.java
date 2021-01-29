package net.mahdilamb.charts.graphics;


import net.mahdilamb.colormap.Color;

public class Stroke {
    public static final Stroke BLACK_STROKE = new Stroke(Color.BLACK, 1.);
    private final double width;
    private final Color color;

    @Override
    public String toString() {
        return "Stroke{" +
                "color=" + color +
                ", width=" + width +
                '}';
    }

    public Stroke(final Color color, final double width) {
        this.width = width;
        this.color = color;
    }

    public double getWidth() {
        return width;
    }

    public Color getColor() {
        return color;
    }
}
