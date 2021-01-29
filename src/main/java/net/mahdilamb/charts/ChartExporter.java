package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;

/**
 * An exporter for the chart that allows the chart to be laid out on a different canvas.
 * <p>
 * Many of the methods are unsafe and thus this class is not generally intended to be used except for exporting charts.
 * These methods could also be implemented with reflection, if the details are needed elsewhere
 */
public abstract class ChartExporter {
    /**
     * Lays out a chart on a destination canvas
     *
     * @param dest   the destination canvas
     * @param source the source chart
     */
    protected static void layoutChart(ChartCanvas<?> dest, Chart<?, ?> source) {
        source.layout(dest);
    }

    /**
     * @param image  the image
     * @param source the source chart
     * @return the bytes of an image encoded as PNG
     * @throws ClassCastException if the method is used on a chart which uses a different type of image
     */
    protected static byte[] imageToBytes(Object image, Chart<?, ?> source) throws ClassCastException {
        return source.bytesFromImage(image);
    }

    /**
     * @param image  the image
     * @param source the source chart
     * @return the width of an image
     * @throws ClassCastException if the method is used on a chart which uses a different type of image
     */
    protected static double getImageHeight(Object image, Chart<?, ?> source) throws ClassCastException {
        return source.getImageHeight(image);
    }

    /**
     * @param image  the image
     * @param source the source chart
     * @return the width of an image
     * @throws ClassCastException if the method is used on a chart which uses a different type of image
     */
    protected static double getImageWidth(Object image, Chart<?, ?> source) throws ClassCastException {
        return source.getImageWidth(image);
    }

}
