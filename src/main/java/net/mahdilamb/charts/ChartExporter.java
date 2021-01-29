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

    protected static byte[] imageToBytesUnsafe(Object image, Chart<?, ?> source) {
        return source.bytesFromImage(image);
    }

    protected static double getImageHeightUnsafe(Object image, Chart<?, ?> source) {
        return source.getImageHeight(image);
    }

    protected static double getImageWidthUnsafe(Object image, Chart<?, ?> source) {
        return source.getImageWidth(image);
    }

}
