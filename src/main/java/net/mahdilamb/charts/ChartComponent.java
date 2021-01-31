package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;

abstract class ChartComponent {
    Chart<?, ?> chart;
    double boundsX, boundsY, boundsWidth, boundsHeight;

    protected abstract void layout(Chart<?, ?> chart, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY);

    protected void requestLayout() {
        if (chart != null) {
            chart.requestLayout();
        }
    }
}
