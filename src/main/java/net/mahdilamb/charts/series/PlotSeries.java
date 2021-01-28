package net.mahdilamb.charts.series;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

public interface PlotSeries<S extends PlotSeries<S>> {
    /**
     * Set the face color of all the scatter markers
     *
     * @param colorName the name of the color
     * @return this scatter series
     */
    S setColor(Color colorName);

    /**
     * Set the face color of all the scatter markers
     *
     * @param colorName the name of the color
     * @return this scatter series
     */
    default S setColor(String colorName) {
        return setColor((Color) Color.get(colorName));
    }

    /**
     * Set the face color of each of the data points
     *
     * @param colorNames the name of the colors
     * @return this scatter series
     */
    S setColors(String... colorNames);

    /**
     * Set the face color of each of the data points
     *
     * @param colorNames the name of the colors
     * @return this scatter series
     */
    S setColors(Iterable<String> colorNames);

    /**
     * Set the color based on a scalar value that corresponds to a position in a colormap
     *
     * @param colormap the colormap to use
     * @param scalars  an iterable of the scalars
     * @return this scatter series
     */
    S setColors(Colormap colormap, Iterable<? extends Number> scalars);

    /**
     * Set the color based on a scalar value that corresponds to a position in a colormap
     *
     * @param colormap the colormap to use
     * @param scalars  an iterable of the scalars
     * @return this scatter series
     */
    S setColors(Colormap colormap, double... scalars);


    /**
     * Set the marker edge size
     *
     * @param size the size of the marker edges
     * @return this scatter series
     */
    S setEdgeSize(double size);

    /**
     * Set the marker edge color. This will enable show edges
     *
     * @param color the color to set the markers
     * @return this scatter series
     */
    S setEdgeColor(Color color);

    /**
     * Set the marker edge color. This will enable show edges
     *
     * @param color the name of the color
     * @return this scatter series
     */
    default S setEdgeColor(String color) {
        return setEdgeColor((Color) Color.get(color));
    }



}
