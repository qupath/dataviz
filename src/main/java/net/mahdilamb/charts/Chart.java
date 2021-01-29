package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Fill;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.io.ImageExporter;
import net.mahdilamb.charts.io.SVGFile;
import net.mahdilamb.charts.layouts.Plot;
import net.mahdilamb.charts.plots.PlotSeries;
import net.mahdilamb.charts.plots.Scatter;
import net.mahdilamb.charts.series.DataSeries;
import net.mahdilamb.charts.series.DataType;
import net.mahdilamb.charts.series.Dataset;
import net.mahdilamb.charts.series.NumericSeries;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.colormap.reference.qualitative.Plotly;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public abstract class Chart<P extends Plot<S>, S extends PlotSeries<S>> {
    /**
     * Create a scatter series from an array of x and y data
     *
     * @param x the x data
     * @param y the y data
     * @return the scatter series
     */
    public static Scatter scatter(double[] x, double[] y) {
        return new PlotSeriesImpl.AbstractScatter.FromArray(x, y);
    }

    /**
     * Create a scatter series from a dataset
     *
     * @param dataset the dataset
     * @param x       the name of the series containing the x data
     * @param y       the name of the series containing the y data
     * @return the scatter series
     * @throws UnsupportedOperationException of either series is not numeric
     * @throws NullPointerException          if the series cannot be found
     */
    public static Scatter scatter(Dataset dataset, String x, String y) {
        final DataSeries<?> xSeries = dataset.get(x);
        final DataSeries<?> ySeries = dataset.get(y);
        if (DataType.isNumeric(xSeries.getType()) && DataType.isNumeric(ySeries.getType())) {
            return new PlotSeriesImpl.AbstractScatter.FromIterable((NumericSeries<?>) xSeries, (NumericSeries<?>) ySeries);
        }
        throw new UnsupportedOperationException("Both series must be numeric");
    }

    Title title;
    P plot;
    Legend legend;
    final Colormap colormap = new Plotly();
    Iterator<Float> colormapIt;
    Color backgroundColor = Color.WHITE;

    double width, height;
    double titleWidth, titleHeight;

    protected Chart(String title, double width, double height, P plot) {
        //this.title.setTitle(title);//TODO
        this.plot = plot;
        this.width = width;
        this.height = height;

    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.setTitle(title);
        layout();
    }

    public Legend getLegend() {
        return legend;
    }

    public ColorBar getColorBars() {
        //TODO
        return null;
    }

    public P getPlot() {
        return plot;
    }

    final void layout(ChartCanvas<?> canvas) {
        canvas.reset();
        // canvas.setClip(ClipShape.ELLIPSE, 10, 10, 150, 150);
        canvas.setStroke(new Stroke(Color.get("aqua"), 2));
        canvas.strokeOval(0, 0, 10, 10);
        canvas.setFill("orange");
        canvas.fillOval(5, 5, 10, 10);
        canvas.setFill(new Fill(Fill.GradientType.LINEAR, Colormaps.buildSequential().addColor(Color.salmon,Color.WHITE).build(), 0, 0, 200, 200));
        canvas.strokeLine(0, 0, 20, 20);
        canvas.strokeRoundRect(30, 30, 50, 50, 5, 5);
        canvas.beginPath();
        canvas.moveTo(20, 20);
        canvas.curveTo(10, 10, 30, 30, 50, 50);
        canvas.quadTo(0.5, 0.5, 45, 213);
        canvas.closePath();
        canvas.fill();
        canvas.fillPolygon(new double[]{50, 100, 0}, new double[]{0, 100, 100}, 3);
        canvas.clearClip();

        canvas.strokePolyline(new double[]{100, 150, 150, 25}, new double[]{100, 25, 75, 0}, 4);
        canvas.done();

    }

    protected final void layout() {
        layout(getCanvas());
    }

    protected abstract ChartCanvas<?> getCanvas();

    /**
     * Get the baseline offset of a font
     *
     * @param font the font
     * @return the baseline offset
     */
    protected abstract double getTextBaselineOffset(final Font font);

    /**
     * @param font the font
     * @param text the text
     * @return the width of the text with the given font
     */
    protected abstract double getTextWidth(final Font font, String text);

    protected abstract double getImageWidth(Object image);

    protected abstract double getImageHeight(Object image);

    protected abstract byte[] bytesFromImage(Object image);

    static Color getNextColor(Chart<?, ?> chart) {
        if (chart.colormapIt == null || !chart.colormapIt.hasNext()) {
            chart.colormapIt = chart.colormap.iterator();
        }
        return chart.colormap.get(chart.colormapIt.next());
    }

    /**
     * @return the width of the chart
     */
    public final double getWidth() {
        return width;
    }

    /**
     * @return the height of the chart
     */
    public final double getHeight() {
        return height;
    }

    /**
     * Save this chart as an svg
     *
     * @param file the output file.
     * @throws IOException if the file cannot be written to
     */
    public void saveAsSVG(File file) throws IOException {
        SVGFile.to(file, this);
    }

    /**
     * Save the file as a png
     *
     * @param file the file path to write to
     * @throws IOException if the file cannot be written to
     */
    public void saveAsPNG(File file) throws IOException {
        ImageExporter.toPNG(file, this);
    }

    /**
     * Save the file as a bmp
     *
     * @param file the file path to write to
     * @throws IOException if the file cannot be written to
     */
    public void saveAsBMP(File file) throws IOException {
        ImageExporter.toBMP(file, this);
    }

    /**
     * Save the file as a jpeg
     *
     * @param file the file path to write to
     * @throws IOException if the file cannot be written to
     */
    public void saveAsJPEG(File file) throws IOException {
        ImageExporter.toJPEG(file, this);
    }

    /**
     * Save a file based on its extension (svg, jpg, bmp, png are supported)
     *
     * @param file the file to save as
     * @throws UnsupportedOperationException if the file format is not supported
     * @throws IOException                   if the file cannot be written to
     */
    public final void saveAs(File file) throws IOException {
        final String fileExt = StringUtils.getLastCharactersToLowerCase(new char[5], file.toString());
        switch (fileExt.indexOf(".")) {
            //four letter extension
            case 0:
                switch (fileExt) {
                    case ".jpeg":
                        saveAsJPEG(file);
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
                break;
            //three letter extension
            case 1:
                switch (fileExt.substring(1, 5)) {
                    case ".jpg":
                        saveAsJPEG(file);
                        break;
                    case ".png":
                        saveAsPNG(file);
                        break;
                    case ".svg":
                        saveAsSVG(file);
                        break;
                    case ".bmp":
                        saveAsBMP(file);
                        break;
                    default:
                        throw new UnsupportedOperationException();

                }
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Set the background color of this chart by the name
     *
     * @param colorName the name of the color
     */
    public void setBackgroundColor(String colorName) {
        setBackgroundColor(Color.get(colorName));
    }

    /**
     * Set the background color of this chart
     *
     * @param color the color to set
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        layout();
    }

    /**
     * @return the background color of this chart
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

}