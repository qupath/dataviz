package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.io.ImageExporter;
import net.mahdilamb.charts.io.SVGFile;
import net.mahdilamb.charts.layouts.Plot;
import net.mahdilamb.charts.plots.PlotSeries;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.colormap.reference.qualitative.Plotly;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public abstract class Chart<P extends Plot<S>, S extends PlotSeries<S>> {

    /**
     * The default chart theme
     */
    public static final Theme DEFAULT_THEME = new ThemeImpl(new Plotly(), Color.WHITE, Color.LIGHT_GRAY, Color.BLACK, 2, 2);

    Theme theme = Theme.DEFAULT;
    Iterator<Float> colormapIt = theme.getDefaultColormap().iterator();

    final Title title = new Title(null, Font.DEFAULT_FONT, Alignment.CENTER);
    P plot;
    Legend legend = new Legend(this);
    ColorBar colorBar = new ColorBar(this);
    double width, height;

    protected Chart(String title, double width, double height, P plot) {
        this.title.setTitle(title);
        this.plot = plot;
        this.width = width;
        this.height = height;
    }

    /**
     * @return the title of the chart
     */
    public final Title getTitle() {
        return title;
    }

    /**
     * Set the title of the chart
     *
     * @param text the title of the chart
     */
    public final void setTitle(String text) {
        this.title.setTitle(text);
        layout();
    }

    /**
     * @return the legend of the chart.
     */
    public final Legend getLegend() {
        return legend;
    }

    /**
     * @return the color bar area of the chart
     */
    public final ColorBar getColorBars() {
        return colorBar;
    }

    /**
     * @return the plot area of the chart
     */
    public final P getPlot() {
        return plot;
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
    public final void setBackgroundColor(String colorName) {
        setBackgroundColor(Color.get(colorName));
    }

    /**
     * Set the background color of this chart
     *
     * @param color the color to set
     */
    public final void setBackgroundColor(Color color) {
        getTheme().background = color;
        backgroundChanged();
        layout();
    }

    /**
     * @return the background color of this chart
     */
    public final Color getBackgroundColor() {
        return theme.getBackgroundColor();
    }

    /**
     * Method called when the background has changed
     */
    protected abstract void backgroundChanged();

    private ThemeImpl getTheme() {
        //If theme is default or not Impl, get a copy
        if (theme == Theme.DEFAULT || !(theme instanceof ThemeImpl)) {
            theme = new ThemeImpl(theme);
        }
        return ((ThemeImpl) theme);
    }

    /**
     * Layout the chart
     *
     * @param canvas the canvas to layout on
     */
    final void layout(ChartCanvas<?> canvas) {
        canvas.reset();
        // canvas.setClip(ClipShape.ELLIPSE, 10, 10, 150, 150);
        canvas.setStroke(new Stroke(Color.get("aqua"), 2));
        canvas.strokeOval(0, 0, 10, 10);
        canvas.setFill("orange");
        canvas.fillOval(5, 5, 10, 10);
        canvas.setFill(new Fill(Fill.GradientType.LINEAR, Colormaps.buildSequential().addColor(Color.salmon, Color.WHITE).build(), 0, 0, 200, 200));
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

    /**
     * Layout the chart locally
     */
    protected final void layout() {
        layout(getCanvas());
    }

    /**
     * @return the canvas of the chart
     */
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

    /**
     * @param image the image to get the width of
     * @return the width of an image
     * @apiNote Note that this is unsafe and should not be exposed. This is just used for the chart exporter
     */
    protected abstract double getImageWidth(Object image) throws ClassCastException;

    /**
     * @param image the image to get the height
     * @return the height of an image
     * @apiNote Note that this is unsafe and should not be exposed. This is just used for the chart exporter
     */
    protected abstract double getImageHeight(Object image) throws ClassCastException;

    /**
     * @param image the image to get the bytes of
     * @return the image as a byte array of PNG encoding bytes
     * @apiNote Note that this is unsafe and should not be exposed. This is just used for the chart exporter
     */
    protected abstract byte[] bytesFromImage(Object image) throws ClassCastException;

    protected static Color getNextColor(Chart<?, ?> chart) {
        if (chart.colormapIt == null || !chart.colormapIt.hasNext()) {
            chart.colormapIt = chart.theme.getDefaultColormap().iterator();
        }
        return chart.theme.getDefaultColormap().get(chart.colormapIt.next());
    }

}