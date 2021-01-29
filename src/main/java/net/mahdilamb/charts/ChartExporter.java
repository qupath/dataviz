package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;

/**
 * An exporter for the chart that allows the chart to be laid out on a different canvas
 */
public class ChartExporter {
    protected static void layoutChart(ChartCanvas<?> dest, Chart<?, ?> source) {
        source.layout(dest);
    }
    @SuppressWarnings("unchecked")
    protected static ChartCanvas<Object> getCanvas(Chart<?, ?> chart) {
        return (ChartCanvas<Object>) chart.getCanvas();
    }

    protected static byte[] imageToBytes(Object image, Chart<?, ?> source) {
        return getCanvas(source).bytesFromImage(image);
    }

    protected static double getTextHeight(Text text) {
        return text.getHeight();
    }

    protected static double getTextWidth(Text text) {
        return text.getWidth();
    }

    protected static double getTextBaselineOffset(Text text) {
        return text.getBaselineOffset();
    }

}
