package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Marker;
import net.mahdilamb.charts.graphics.Side;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.mahdilamb.charts.Chart.USE_PREFERRED_HEIGHT;

//TODO gaps, etc.
public final class Legend extends Key {
    double itemWidth = 0, height, hGap = 2, vGap = 2;
    boolean isDirty = true;
    Map<PlotSeries<?,?>, KeyItem> items = new LinkedHashMap<>();


    Legend(Chart<?, ?> chart) {
        super(chart);
        side = Side.TOP;

    }

    @Override
    protected void layout(ChartCanvas<?> canvas, double x, double y, double width, double height) {
        if (isDirty) {
            this.itemWidth = 0;
            for (int i = 0; i < chart.getPlot().numSeries(); ++i) {
                final PlotSeries<?, ?> series = (PlotSeries<?, ?>) chart.getPlot().get(i);
                items.put(series, series.getLegendItem());
                this.itemWidth = Math.max(this.itemWidth, series.getLegendItem().getItemWidth(chart));
            }
            this.itemWidth += 30;//todo marker size
            isDirty = false;
        }
        if (height == USE_PREFERRED_HEIGHT) {
            int rows = (int) Math.ceil(items.size() * itemWidth / width);
            int itemsPerRow = (int) Math.ceil(((double) items.size()) / rows);
            Iterator<Key.KeyItem> it = items.values().iterator();
            double rowHeight = USE_PREFERRED_HEIGHT;
            int lastRow = rows - 1;
            for (int row = 0; row < rows && it.hasNext(); ++row) {
                double xOffset = (width - ((row == lastRow ? (items.size() % itemsPerRow) : itemsPerRow) * itemWidth)) * .5;
                for (int i = 0; i < itemsPerRow && it.hasNext(); ++i) {
                    Key.KeyItem ki = it.next();
                    rowHeight = ki.layout(chart, canvas, xOffset * itemWidth * row, rowHeight == USE_PREFERRED_HEIGHT ? 0 : row * rowHeight, itemWidth, rowHeight);
                }

            }
            //horizontal
        } else {
            //vertical
        }

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

    /**
     * An item in the legend
     */
    static class LegendItem extends KeyItem {
        private final Marker marker;
        private final String label;

        public LegendItem(String label, Marker marker) {
            this.label = label;
            this.marker = marker;
        }

        @Override
        public String toString() {
            return "Legend item for " + label;
        }

        /**
         * @return the label of this legend item
         */
        public String getLabel() {
            return label;
        }

        /**
         * @return the marker associated with this legend item
         */
        public Marker getMarker() {
            return marker;
        }

        @Override
        protected double layout(Chart<?, ?> chart, ChartCanvas<?> canvas, double x, double y, double width, double height) {

            canvas.fillText(label, x, y);

            //TODO
            return 10;
        }

        @Override
        protected double getItemWidth(Chart<?, ?> chart) {
            return chart.getTextWidth(Font.DEFAULT_FONT, label) + 30;//todo marker size
        }
    }

    static class GroupedLegendItem extends KeyItem {
        private final LegendItem[] legendItems;
        private final String name;

        GroupedLegendItem(final String groupName, LegendItem[] legendItems) {
            this.name = groupName;
            this.legendItems = legendItems;
        }

        @Override
        protected double layout(Chart<?, ?> chart, ChartCanvas<?> canvas, double x, double y, double width, double height) {
            if (height == USE_PREFERRED_HEIGHT) {
                double itemHeight = USE_PREFERRED_HEIGHT;
                double itemY = 0;
                for (final LegendItem li : legendItems) {
                    itemHeight = li.layout(chart, canvas, x, itemY, width, itemHeight);
                    itemY += itemHeight;
                }
                return itemHeight;
            }
            return 0;//TODO horizontal
        }

        @Override
        protected double getItemWidth(Chart<?, ?> chart) {
            double maxWidth = 0;
            for (final LegendItem li : legendItems) {
                maxWidth = Math.max(maxWidth, chart.getTextWidth(Font.DEFAULT_FONT, li.label));
            }
            return Math.max(maxWidth + 30, chart.getTextWidth(Font.DEFAULT_FONT, name));//todo marker size
        }
    }
}
