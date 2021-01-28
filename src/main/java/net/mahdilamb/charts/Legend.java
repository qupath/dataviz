package net.mahdilamb.charts;

import net.mahdilamb.charts.styles.Marker;
import net.mahdilamb.charts.styles.Text;

import java.util.Objects;

public final class Legend extends Key {
    double width, height, hGap = 2, vGap = 2, yOffset;


    Legend(Chart<?, ?> chart) {
        super(chart);
    }

    /**
     * @return the horizontal gap between legend items
     */
    public double getHorizontalGap() {
        return hGap;
    }

    /**
     * @return the vertical gap between legend items
     */
    public double getVerticalGap() {
        return vGap;
    }


    void layout(double yOffset) {
        if (title != null && title.isVisible()) {

        }
        for (final LegendItem legendItem : ((PlotImpl<?>) chart.plot).getLegendItems()) {

        }
        //TODO
    }

    /**
     * An item in the legend
     */
    public static class LegendItem {
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
}
