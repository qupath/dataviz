package net.mahdilamb.charts.graphics;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.geom2d.geometries.Point;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

/**
 * A fill - currently only supports a single fill color or linear gradient
 */
public final class Fill {
    public static final Fill BLACK_FILL = new Fill(Color.BLACK);

    public enum GradientType {
        /**
         * A linear gradient
         */
        LINEAR,
        /**
         * A radial gradient
         */
        RADIAL
    }

    private final Object fill;

    /**
     * An object representing a gradient
     */
    public static final class Gradient {

        private final NavigableMap<Float, Color> colorMap;
        double startX, startY, endX, endY;

        private final GradientType type;

        private Gradient(final GradientType type, final Colormap colorMap, double startX, double startY, double endX, double endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;

            this.colorMap = new TreeMap<>();
            for (final float i : colorMap) {
                this.colorMap.put(i, colorMap.get(i));
            }
            this.type = type;
        }

        private Gradient(final GradientType type, final Colormap colorMap, final Point start, final Point end) {
            this(type, colorMap, start.getX(), start.getY(), end.getX(), end.getY());
        }

        /**
         * Copy constructor
         *
         * @param other the gradient to copy from
         */
        public Gradient(final Gradient other) {
            colorMap = new TreeMap<>(other.colorMap);
            this.startX = other.startX;
            this.startY = other.startY;
            this.endX = other.endX;
            this.endY = other.endY;
            type = other.type;
        }

        /**
         * @return the type of the gradient
         */
        public final GradientType getType() {
            return type;
        }

        /**
         * @return an ordered map representing the stops (0-1) mapped to the colors
         */
        public final NavigableMap<Float, Color> getColorMap() {
            return Collections.unmodifiableNavigableMap(colorMap);
        }


        public double getStartX() {
            return startX;
        }

        public double getStartY() {
            return startY;
        }

        public double getEndX() {
            return endX;
        }

        public double getEndY() {
            return endY;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Gradient)) return false;
            final Gradient gradient = (Gradient) o;
            return type == gradient.type && startX == gradient.startX && startY == gradient.startY && endX == gradient.endX && endY == gradient.endY && colorMap.equals(gradient.colorMap);
        }

        @Override
        public int hashCode() {
            return Objects.hash(colorMap, startX, startY, endX, endY, type);
        }
    }

    /**
     * Construct a single-color fill
     *
     * @param color the color
     */
    public Fill(final Color color) {
        this.fill = color;
    }


    public Fill(final GradientType gradientType, final Colormap colorMap, double startX, double startY, double endX, double endY) {
        this.fill = new Gradient(gradientType, colorMap, startX, startY, endX, endY);
    }

    /**
     * Construct a linear gradient fill
     *
     * @param gradientType the type of the gradient (either linear or radial)
     * @param colorMap     the colormap source of gradient fill
     * @param start        the position (relative to the top-left of the canvas) of the start of the gradient
     * @param end          the position (relative to the top-left of the canvas) of the end of the gradient
     */
    public Fill(final GradientType gradientType, final Colormap colorMap, final Point start, final Point end) {
        this.fill = new Gradient(gradientType, colorMap, start, end);
    }

    /**
     * Copy constructor
     *
     * @param other copy this fill
     */
    public Fill(final Fill other) {
        if (other.isGradient()) {
            this.fill = new Gradient(other.getGradient());
        } else {
            this.fill = new Color(other.getColor());
        }
    }

    /**
     * @return if this fill is a gradient
     */
    public final boolean isGradient() {
        return fill.getClass() == Gradient.class;
    }

    /**
     * @return the fill as a gradient
     */
    public final Gradient getGradient() {
        return (Gradient) fill;
    }

    /**
     * @return the fill as a color
     */
    public final Color getColor() {
        return (Color) fill;
    }

    /**
     * @return if the fill is null
     */
    public static boolean isNull(Fill fill) {
        return Objects.isNull(fill) || Objects.isNull(fill.fill);
    }

    @Override
    public final String toString() {
        if (isNull(this)) {
            return "Fill {null}";
        }
        if (isGradient()) {
            return String.format("Fill {gradient: %s}", getGradient().getType());
        } else {
            return String.format("Fill {color: %s}", getColor());
        }
    }

    /**
     * Create a linear gradient
     *
     * @param colorMap the colormap source of the gradient
     * @param start    the start position of the gradient
     * @param end      the end position of the gradient
     * @return a linear gradient
     */
    public static Fill createLinearGradient(final Colormap colorMap, final Point start, final Point end) {
        return new Fill(GradientType.LINEAR, colorMap, start, end);
    }

    /**
     * Create a radial gradient
     *
     * @param colorMap the colormap source of the gradient
     * @param start    the start position of the gradient
     * @param end      the end position of the gradient
     * @return a radial gradient
     */
    public static Fill createRadialGradient(final Colormap colorMap, final Point start, final Point end) {
        return new Fill(GradientType.RADIAL, colorMap, start, end);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fill)) return false;
        return fill.equals(((Fill) o).fill);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(fill);
    }

}