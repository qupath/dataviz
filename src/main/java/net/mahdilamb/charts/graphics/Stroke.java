package net.mahdilamb.charts.graphics;


import net.mahdilamb.colormap.Color;

/**
 * A basic stroke made up of a width and a color
 */
//TODO add dashes, etc.
public class Stroke {
    /**
     * A default stroke - black and 1 pixel width. Use the {@link #copy} method to derive strokes
     */
    public static final Stroke BLACK_STROKE = new UnmodifiableStroke(Color.BLACK, 1.);
    private final double width;
    private final Color color;

    /**
     * Create a stroke from a color and a width
     *
     * @param color the color of the stroke
     * @param width the width of the stroke
     */
    public Stroke(final Color color, final double width) {
        this.width = width;
        this.color = color;
    }

    /**
     * Create a shallow copy of a stroke
     *
     * @param other the source stroke
     */
    private Stroke(Stroke other) {
        this.width = other.width;
        this.color = other.color;
    }

    /**
     * @return the stroke width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return the stroke color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return a shallow copy of the stroke
     */
    public Stroke copy() {
        return new Stroke(this);
    }

    @Override
    public String toString() {
        return "Stroke{" +
                "color=" + color +
                ", width=" + width +
                '}';
    }

    private static final class UnmodifiableStroke extends Stroke {

        UnmodifiableStroke(Color color, double width) {
            super(color, width);
        }
    }
}
