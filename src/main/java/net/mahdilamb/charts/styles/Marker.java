package net.mahdilamb.charts.styles;

import net.mahdilamb.colormap.RGBA;

/**
 * The style of a marker
 */
public interface Marker {
    /**
     * @return the face color
     */
    RGBA getFillColor();

    /**
     * @return the edge color
     */
    RGBA getStrokeColor();

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
