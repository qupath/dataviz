package net.mahdilamb.charts;

import javafx.scene.image.Image;
import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.io.SVGFile;
import net.mahdilamb.charts.layouts.Plot;
import net.mahdilamb.charts.plots.PlotSeries;
import net.mahdilamb.charts.plots.Scatter;
import net.mahdilamb.charts.series.DataSeries;
import net.mahdilamb.charts.series.DataType;
import net.mahdilamb.charts.series.Dataset;
import net.mahdilamb.charts.series.NumericSeries;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.colormap.reference.qualitative.Plotly;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
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

    @SuppressWarnings("unchecked")
    final void layout(ChartCanvas<?> canvas) {
        canvas.reset();
        canvas.setClip(ClipShape.ELLIPSE, 10, 10, 150, 150);
        canvas.setStroke(new Stroke(Color.get("aqua"), 2));
        canvas.strokeOval(0, 0, 10, 10);
        canvas.setFill("orange");
        canvas.fillOval(5, 5, 10, 10);
        canvas.setFill(new Fill(Fill.GradientType.LINEAR, Colormaps.get("viridis"), 0, 0, 200, 200));
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
     * @param file the output file
     */
    public void saveAsSVG(File file) {
        SVGFile.to(file, this);
    }

    public void setBackgroundColor(String name) {
        setBackgroundColor(Color.get(name));
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        layout();
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

}