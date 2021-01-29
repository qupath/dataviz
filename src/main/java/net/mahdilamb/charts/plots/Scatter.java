package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.graphics.MarkerMode;
import net.mahdilamb.charts.graphics.MarkerShape;

//TODO error bar
public interface Scatter extends PlotSeries<Scatter>, PlotWithLegend<Scatter>, PlotWithColorBar<Scatter> {

    /**
     * Set the size of the marker for all data points
     *
     * @param size the size to set the markers
     * @return this
     */
    Scatter setMarkerSize(double size);

    /**
     * Set the shape of the marker
     *
     * @param marker the marker shape
     * @return this scatter series
     */
    Scatter setMarker(MarkerShape marker);

    /**
     * Set the shape of the marker based on the short hand as is used in Matplotlib
     *
     * @param string the string containing the character
     * @return this scatter series
     */
    default Scatter setMarker(final String string) {
        return setMarker(MarkerShape.get(string));
    }

    /**
     * Set the shape of the marker based on the short hand as is used in Matplotlib
     *
     * @param character the character shortcut
     * @return this scatter series
     */
    default Scatter setMarker(final char character) {
        return setMarker(MarkerShape.get(character));
    }

    Scatter setMarkerMode(MarkerMode mode);

    Scatter setXMarginal(MarginalMode marginal);

    Scatter setYMarginal(MarginalMode marginal);

    Scatter setGroups(Iterable<String> groupings);

    //TODO error bars
    //TODO trendline
    //TODO cluster

}
