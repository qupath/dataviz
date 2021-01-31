package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Fill;
import net.mahdilamb.charts.graphics.Legend;
import net.mahdilamb.charts.graphics.Marker;

import java.util.Map;

public class LegendImpl extends KeyImpl<LegendImpl.LegendItem> implements Legend {
    double labelMarkerSpacing = 5;
    LegendImpl(Chart<?, ?> chart) {
        super(chart);
    }


    void calculateMetrics() {
        cellHeight = -1;
        cellWidth = -1;

        for (final Map.Entry<PlotSeries<?>, LegendItem> item : items.entrySet()) {
            final LegendItem legendItem = item.getValue();
            if (legendItem.getClass() == LegendImpl.GroupedLegendItem.class) {
                final LegendImpl.GroupedLegendItem gli = (LegendImpl.GroupedLegendItem) legendItem;

                if (side.isHorizontal()) {
                    double totalWidth = margin;
                    cellHeight = Math.max(cellHeight, chart.getTextLineHeight(labelFont));
                    cellWidth = Math.max(cellWidth, chart.getTextWidth(labelFont, gli.name));

                    gli.labelWidth = chart.getTextWidth(labelFont, gli.name);
                    totalWidth += gli.labelWidth;
                    totalWidth += hGap;
                    gli.itemHeight = cellHeight;

                    for (final LegendImpl.LegendItem li : gli.legendItems) {
                        cellHeight = Math.max(cellHeight, li.marker.getSize() + markerPadding * 2);
                        li.labelWidth = chart.getTextWidth(labelFont, li.getLabel());
                        li.itemHeight = cellHeight;
                        totalWidth += li.labelWidth + hGap * 2 + li.marker.getSize();
                        if (Fill.isNull(li.marker.face)) {
                            li.marker.face.set(Chart.getNextColor(chart));
                        }
                        cellWidth += chart.getTextWidth(labelFont, li.label);
                    }
                    gli.totalWidth = totalWidth;
                } else {
                    double totalHeight = margin;
                    cellHeight = Math.max(cellHeight, chart.getTextLineHeight(labelFont));
                    totalHeight += cellHeight;
                    cellWidth = Math.max(cellWidth, chart.getTextWidth(labelFont, gli.name));
                    for (final LegendImpl.LegendItem li : gli.legendItems) {
                        cellHeight = Math.max(cellHeight, li.marker.getSize() + markerPadding * 2);//todo different for items?
                        if (Fill.isNull(li.marker.face)) {
                            li.marker.face.set(Chart.getNextColor(chart));
                        }
                        cellWidth = Math.max(cellWidth, chart.getTextWidth(labelFont, li.label));
                    }
                    totalHeight += cellHeight * gli.legendItems.length;
                    gli.totalHeight=totalHeight;
                    gli.totalWidth = cellWidth;
                    gli.itemHeight = cellHeight;
                }
            } else {
                cellHeight = Math.max(cellHeight, chart.getTextLineHeight(labelFont));
                cellHeight = Math.max(cellHeight, legendItem.marker.getSize() + markerPadding * 2);
                cellWidth = chart.getTextWidth(labelFont, legendItem.label);
                if (Fill.isNull(legendItem.marker.face)) {
                    legendItem.marker.face.set(Chart.getNextColor(chart));
                }
                legendItem.labelWidth = chart.getTextWidth(labelFont, legendItem.getLabel());
                legendItem.itemHeight = cellHeight;
            }
        }

    }


    protected void layout(ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {

        if (isDirty) {
            for (int i = 0; i < chart.plot.numSeries(); ++i) {
                //ignore visibility, just make items!
                final PlotSeries<?> series = (PlotSeries<?>) chart.plot.get(i);
                final LegendItem legendItem = series.getLegendItem();
                items.put(series, legendItem);
            }
            calculateMetrics();
            isDirty = false;

        }
        if (side.isHorizontal()) {

            renderHeight = cellHeight;//todo multiple rows
            renderWidth = maxX;

            double maxWidth = maxX - (2 * margin);

            double currentX = minX + margin;
            double startX;
            canvas.setFont(labelFont);
            canvas.setFill(Fill.BLACK_FILL);
            for (final Map.Entry<PlotSeries<?>, LegendItem> item : items.entrySet()) {
                final LegendItem legendItem = item.getValue();
                if (legendItem.getClass() == LegendImpl.GroupedLegendItem.class) {//TODO, go to next row - also should be done reversed if at bottom
                    final LegendImpl.GroupedLegendItem gli = (LegendImpl.GroupedLegendItem) legendItem;
                    startX = (maxWidth - gli.totalWidth) * .5;
                    currentX += startX;
                    canvas.fillText(gli.name, currentX, minY + chart.getTextBaselineOffset(labelFont));
                    currentX += gli.labelWidth;
                    currentX += hGap;
                    for (final LegendImpl.LegendItem li : gli.legendItems) {
                        currentX += li.marker.getSize() * .5;
                        Markers.draw(canvas, currentX, minY + cellHeight * .5, li.marker);
                        currentX += li.marker.getSize() * .5;
                        currentX += hGap;
                        canvas.setFill(Fill.BLACK_FILL);
                        canvas.fillText(li.getLabel(), currentX, minY + chart.getTextBaselineOffset(labelFont));
                        currentX += li.labelWidth + hGap;

                    }

                } else if (legendItem.getClass() == LegendImpl.LegendItem.class) {

                    startX = (maxWidth - legendItem.labelWidth + legendItem.marker.size + hGap) * .5;
                    currentX += startX;
                    currentX += legendItem.marker.getSize() * .5;
                    Markers.draw(canvas, currentX, minY + cellHeight * .5, legendItem.marker);
                    currentX += legendItem.marker.getSize() * .5;
                    currentX += hGap;
                    canvas.setFill(Fill.BLACK_FILL);
                    canvas.fillText(legendItem.getLabel(), currentX, minY + chart.getTextBaselineOffset(labelFont));
                    currentX += legendItem.labelWidth + hGap;
                    //TODO
                }

            }
            //TODO deal with border


        } else {
            //vertical
            double offsetX = chart.width;//todo for left
            double startY = minY + margin;
            double maxHeight = maxY - (2 * margin);
            double currentY;

            canvas.setFont(labelFont);
            canvas.setFill(Fill.BLACK_FILL);
            for (final Map.Entry<PlotSeries<?>, LegendItem> item : items.entrySet()) {
                final LegendItem legendItem = item.getValue();
                if (legendItem.getClass() == LegendImpl.GroupedLegendItem.class) {
                    final LegendImpl.GroupedLegendItem gli = (LegendImpl.GroupedLegendItem) legendItem;
                    offsetX -= gli.totalWidth;
                    startY += (maxHeight - gli.totalHeight) * .5;
                    currentY = startY;
                    canvas.fillText(gli.name, offsetX, currentY + chart.getTextBaselineOffset(labelFont));
                    currentY += gli.itemHeight;
                    currentY += vGap;
                    for (final LegendImpl.LegendItem li : gli.legendItems) {
                       // currentY += li.marker.getSize() * .5;
                        Markers.draw(canvas, offsetX+li.marker.getSize() * .5, currentY+gli.itemHeight * .5 , li.marker);
                        canvas.setFill(Fill.BLACK_FILL);
                        canvas.fillText(li.getLabel(), offsetX+li.marker.getSize()+labelMarkerSpacing, currentY + chart.getTextBaselineOffset(labelFont));
                        currentY += gli.itemHeight;
                        currentY +=  vGap;

                    }

                } else if (legendItem.getClass() == LegendImpl.LegendItem.class) {
                    offsetX-=legendItem.labelWidth+legendItem.marker.getSize()+labelMarkerSpacing;
                    startY += (maxHeight - legendItem.itemHeight) * .5;
                    currentY = startY;
                    Markers.draw(canvas, offsetX+legendItem.marker.getSize() * .5, currentY+legendItem.itemHeight * .5 , legendItem.marker);
                    canvas.setFill(Fill.BLACK_FILL);
                    canvas.fillText(legendItem.getLabel(), offsetX+legendItem.marker.getSize()+labelMarkerSpacing, currentY + chart.getTextBaselineOffset(labelFont));
                    currentY += legendItem.itemHeight;
                    currentY +=  vGap;
                    //TODO
                }

            }
            renderWidth = chart.width-offsetX;
        }
    }

    /**
     * An item in the legend
     */
    static class LegendItem extends KeyImpl.KeyItem {
        private PlotSeries.MarkerImpl marker;
        private String label;

        LegendItem() {

        }

        LegendItem(String label, PlotSeries.MarkerImpl marker) {
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


    }

    static class GroupedLegendItem extends LegendItem {
        private final LegendItem[] legendItems;
        private final String name;
        double totalWidth;
        double totalHeight;

        GroupedLegendItem(final String groupName, LegendItem[] legendItems) {
            this.name = groupName;
            this.legendItems = legendItems;
        }

    }
}
