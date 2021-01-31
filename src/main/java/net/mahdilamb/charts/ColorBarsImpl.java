package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.ColorBar;
import net.mahdilamb.charts.graphics.Fill;
import net.mahdilamb.colormap.Colormap;

import java.util.Map;

public class ColorBarsImpl extends KeyImpl<ColorBarsImpl.ColorBarItem> implements ColorBar {
    ColorBarsImpl(Chart<?, ?> chart) {
        super(chart);
    }

    @Override
    protected void layout(ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
        if (isDirty) {
            if (side.isHorizontal()) {
                renderHeight = 0;//todo multiple rows
                renderWidth = maxX - minX;
                cellHeight = 20;
            } else {
                renderWidth = 0;
                renderHeight = maxY - minY;
                cellWidth = 20;
            }
            for (int i = 0; i < chart.plot.numSeries(); ++i) {
                //ignore visibility, just make items!
                final PlotSeries<?> series = (PlotSeries<?>) chart.plot.get(i);
                final ColorBarItem legendItem = series.getColorBarItem();
                items.put(series, legendItem);
                if (side.isHorizontal()) {
                    legendItem.fill = new Fill(Fill.GradientType.LINEAR, legendItem.colormap, 0, 0, maxX, 0);

                    legendItem.itemHeight = 20;
                    renderHeight += legendItem.itemHeight;
                } else {
                    legendItem.fill = new Fill(Fill.GradientType.LINEAR, legendItem.colormap, 0, 0, 0, maxY);

                    legendItem.labelWidth = 20;
                    renderWidth += legendItem.labelWidth;
                }
            }

            isDirty = false;
        }
        if (side.isHorizontal()) {

            double maxWidth = maxX - minX - (2 * margin);

            double currentX = minX + margin;
            double startX;
            canvas.setFont(labelFont);
            canvas.setFill(Fill.BLACK_FILL);
            for (final Map.Entry<PlotSeries<?>, ColorBarItem> item : items.entrySet()) {
                final ColorBarItem li = item.getValue();

                //TODO show scale and label. Also need to update positions on the color bar to be correct
                startX = ((1 - li.preferredSize) * maxWidth + hGap) * .5;
                currentX += startX;
                canvas.setFill(li.fill);
                canvas.fillRect(startX, minY, li.preferredSize * maxWidth, li.itemHeight);
                currentX += li.preferredSize * maxWidth;
                currentX += hGap;

            }

        } else {
            canvas.setFont(labelFont);
            canvas.setFill(Fill.BLACK_FILL);
            double maxWidth = maxX - minX - (2 * margin);
            double maxHeight = maxY - minY;
            renderWidth = margin + cellWidth * items.size() + 2 * hGap;
            double currentX = maxX - renderWidth;
            double startX = currentX;
            for (final Map.Entry<PlotSeries<?>, ColorBarItem> item : items.entrySet()) {
                final ColorBarItem li = item.getValue();
                canvas.setFill(li.fill);
                double height = li.preferredSize * maxHeight;
                currentX += hGap;
                canvas.fillRect(currentX, minY + ((.5 - li.preferredSize * .5)) * maxHeight, li.labelWidth, height);
                currentX += li.preferredSize * maxWidth;
                currentX += hGap;

            }

        }
    }

    static final class ColorBarItem extends KeyItem {
        double scaleMin, scaleMax;
        Colormap colormap;
        boolean showText = false;
        double preferredSize = .8, textSize = 12;
        Fill fill;

        public ColorBarItem(Colormap colormap, double scaleMin, double scaleMax) {
            this.scaleMin = scaleMin;
            this.scaleMax = scaleMax;
            this.colormap = colormap;
        }


    }
}
