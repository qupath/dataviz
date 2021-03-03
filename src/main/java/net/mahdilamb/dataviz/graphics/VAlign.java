package net.mahdilamb.dataviz.graphics;

/**
 * The vertical alignment
 */
public enum VAlign {
    /**
     * Top
     */
    TOP,
    /**
     * Middle
     */
    MIDDLE,
    /**
     * Bottom
     */
    BOTTOM;

    /**
     * Get the alignment from an int representation
     *
     * @param order the int
     * @return the associated alignment
     */
    public static VAlign get(int order) {
        switch (order) {
            case 0:
                return TOP;
            case 2:
                return MIDDLE;
            case 1:
            default:
                return BOTTOM;

        }
    }
}
