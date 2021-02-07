package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;

public class Heatmap extends PlotSeries.Matrix<Heatmap> implements RectangularPlot{

    boolean useRowMajor = true, showText = false;
    double xOffset = 0, yOffset = 0, cellWidth = 1, cellHeight = 1;

    /**
     * Create a heatmap from a multidimensional array.
     *
     * @param data the data
     */
    public Heatmap(double[][] data) {
        super(data);

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
