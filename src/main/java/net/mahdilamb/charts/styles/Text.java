package net.mahdilamb.charts.styles;

/**
 * Formatting for an axis label
 */
public interface Text {
    /**
     * @return the anchor position of the label
     */
    default Alignment getAnchor() {
        return Alignment.CENTER;
    }

    /**
     * @return the offset (relative to 0) of the label to the tick mark
     */
    default double getOffset() {
        return 0;
    }

    /**
     * @return the rotation of the text
     */
    default double getRotation() {
        return 0;
    }

    double getFontSize();

    double getPaddingX();

    double getPaddingY();
}
