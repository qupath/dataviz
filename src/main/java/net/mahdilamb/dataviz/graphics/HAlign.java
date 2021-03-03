package net.mahdilamb.dataviz.graphics;

/**
 * The horizontal alignment
 */
public enum HAlign {
    /**
     * Left
     */
    LEFT,
    /**
     * Center
     */
    CENTER,
    /**
     * Right
     */
    RIGHT;

    /**
     * Get the alignment from an int representation
     *
     * @param order the int
     * @return the associated alignment
     */
    public static HAlign get(int order) {
        switch (order) {
            case 0:
                return LEFT;
            case 2:
                return RIGHT;
            case 1:
            default:
                return CENTER;

        }
    }
}
