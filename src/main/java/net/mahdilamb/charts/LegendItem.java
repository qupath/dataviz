package net.mahdilamb.charts;

import net.mahdilamb.charts.styles.Marker;
import net.mahdilamb.charts.styles.Text;

import java.util.Objects;

/**
 * An item in the legend
 */
public class LegendItem {
    private final Marker marker;
    private final Text label;

    public LegendItem(Text label, Marker marker) {
        this.label = Objects.requireNonNull(label);
        this.marker = marker;
    }

    /**
     * @return the label of this legend item
     */
    public Text getLabel() {
        return label;
    }

    /**
     * @return the marker associated with this legend item
     */
    public Marker getMarker() {
        return marker;
    }

}
