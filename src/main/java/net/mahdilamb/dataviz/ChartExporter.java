package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.graphics.ChartCanvas;

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
    protected static <T> void layoutChart(ChartCanvas<T> dest, Renderer<?> source) {
        source.refresh(dest);
    }

    /**
     * @param image  the image
     * @param source the source chart
     * @return the bytes of an image encoded as PNG
     * @throws ClassCastException if the method is used on a chart which uses a different type of image
     */
    protected static <T> byte[] imageToBytes(T image, Renderer<T> source) {
        return source.bytesFromImage(image);
    }

    /**
     * @param image  the image
     * @param source the source chart
     * @return the width of an image
     * @throws ClassCastException if the method is used on a chart which uses a different type of image
     */
    protected static <T> double getImageHeight(T image, Renderer<T> source) {
        return source.getImageHeight(image);
    }

    /**
     * @param image  the image
     * @param source the source chart
     * @return the width of an image
     * @throws ClassCastException if the method is used on a chart which uses a different type of image
     */
    protected static <T> double getImageWidth(T image, Renderer<T> source) {
        return source.getImageWidth(image);
    }

}
