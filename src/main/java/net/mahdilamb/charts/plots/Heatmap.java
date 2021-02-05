package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;

public class Heatmap extends PlotSeries.Matrix<Heatmap> {

    boolean useRowMajor = true, showText = false;
    final int rowMajorWidth, rowMajorHeight;
    double xOffset = 0, yOffset = 0, cellWidth = 1, cellHeight = 1;

    /**
     * Create a heatmap from a multidimensional array.
     *
     * @param data the data
     */
    public Heatmap(double[][] data) {
        super(data);
        this.rowMajorHeight = data.length;
        this.rowMajorWidth = calculateWidth(data);
    }

    static int calculateWidth(double[][] data) {
        int width = -1;
        for (final double[] d : data) {
            width = Math.max(width, d.length);
        }
        return width;
    }

    public Heatmap setRowMajor(boolean rowMajor) {
        this.useRowMajor = rowMajor;
        return requestDataUpdate();
    }

    public Heatmap showText(boolean showText) {
        this.showText = showText;
        return requestLayout();
    }

    public Heatmap setCellWidth(double width) {
        this.cellWidth = width;
        return requestDataUpdate();
    }

    public Heatmap setCellHeight(double height) {
        this.cellHeight = height;
        return requestDataUpdate();
    }
}
