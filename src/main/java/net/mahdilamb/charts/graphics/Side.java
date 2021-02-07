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

    /**
     * @return whether this side is vertical
     */
    public boolean isVertical() {
        return this == LEFT || this == RIGHT;
    }

    /**
     * @return whether this side is horizontal
     */
    public boolean isHorizontal() {
        return this == TOP || this == BOTTOM;

    }
}
