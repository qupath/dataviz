package net.mahdilamb.charts.graphics;

/**
 * Enum for the size
 */
public enum Side {
    /**
     * Left
     */
    LEFT,
    /**
     * Top
     */
    TOP,
    /**
     * Right
     */
    RIGHT,
    /**
     * Bottom
     */
    BOTTOM;

    public boolean isVertical() {
        return this == LEFT || this == RIGHT;
    }

    public boolean isHorizontal() {
        return this == TOP || this == BOTTOM;

    }
}
