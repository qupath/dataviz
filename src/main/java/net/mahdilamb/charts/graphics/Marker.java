package net.mahdilamb.charts.graphics;

/**
 * The style of a marker
 */
public interface Marker {
    /**
     * @return the face color
     */
    Fill getFill();

    /**
     * @return the edge color
     */
    Stroke getStroke();

    /**
     * @return the size of the marker
     */
    double getSize();

    /**
     * @return the type of the marker
     */
    MarkerShape getShape();

}

