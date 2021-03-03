package net.mahdilamb.dataviz.graphics;

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
     * @return whether this side is vertical (left or right)
     */
    public boolean isVertical() {
        return this == LEFT || this == RIGHT;
    }

    /**
     * @return whether this side is horizontal (top or bottom)
     */
    public boolean isHorizontal() {
        return this == TOP || this == BOTTOM;
    }
}
