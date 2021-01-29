package net.mahdilamb.charts.graphics;

import net.mahdilamb.colormap.Color;

/**
 * The style of a marker
 */
public interface Marker {
    /**
     * @return the face color
     */
    Color getColor();

    /**
     * @return the edge color
     */
    Color getEdgeColor();

    /**
     * @return the width of the stroke
     */
    double getEdgeWidth();

    /**
     * @return the size of the marker
     */
    double getSize();

    /**
     * @return the type of the marker
     */
    MarkerShape getShape();
}
