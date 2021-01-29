package net.mahdilamb.charts.graphics;

import net.mahdilamb.colormap.Color;

/**
 * The style of a marker
 */
public interface Marker {
    /**
     * @return the face color
     */
    Color getFillColor();

    /**
     * @return the edge color
     */
    Color getStrokeColor();

    /**
     * @return the width of the stroke
     */
    double getStrokeWidth();

    /**
     * @return the size of the marker
     */
    double getSize();

    /**
     * @return the type of the marker
     */
    MarkerShape getType();
}
