package net.mahdilamb.dataviz.graphics;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

/**
 * An object representing a gradient
 */
public class Gradient {

    /**
     * Enum for the types of gradients
     */
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

    private final NavigableMap<Float, Color> colorMap;
    protected double startX, startY, endX, endY;

    private final GradientType type;

    /**
     * Create a gradient from a colormap
     *
     * @param type     the type of the gradient
     * @param colorMap the colormap
     * @param startX   the start x
     * @param startY   the start y
     * @param endX     the end x
     * @param endY     the end y
     */
    public Gradient(final GradientType type, final Colormap colorMap, double startX, double startY, double endX, double endY) {
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

    /**
     *
     * @return get the start x
     */
    public double getStartX() {
        return startX;
    }

    /**
     *
     * @return the start y
     */
    public double getStartY() {
        return startY;
    }

    /**
     *
     * @return the end x
     */
    public double getEndX() {
        return endX;
    }

    /**
     *
     * @return the end y
     */
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
