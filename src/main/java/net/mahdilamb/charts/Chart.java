package net.mahdilamb.charts;

import net.mahdilamb.charts.axes.LinearAxis;
import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.io.ImageExporter;
import net.mahdilamb.charts.io.SVGFile;
import net.mahdilamb.charts.layouts.PlotLayout;
import net.mahdilamb.charts.layouts.XYMarginalPlot;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.reference.qualitative.Plotly;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public abstract class Chart<P extends PlotLayout<S>, S> {

    /**
     * The default chart theme
     */
    public static final Theme DEFAULT_THEME = new ThemeImpl(new Plotly(), Color.WHITE, Color.LIGHT_GRAY, Color.BLACK, 2, 2);

    Theme theme = Theme.DEFAULT;
    Iterator<Float> colormapIt = theme.getDefaultColormap().iterator();

    final Title title;
    P plot;
    final LegendImpl legend;
    final ColorBarsImpl colorBar;
    double width, height;

    protected Chart(String title, double width, double height, P plot) {
        this.title = new Title(title, new Font(Font.Family.SANS_SERIF, theme.getTitleSize()), Alignment.CENTER);
        this.plot = plot;
        this.width = width;
        this.height = height;
        this.legend = new LegendImpl(this);
        this.colorBar = new ColorBarsImpl(this);
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

    private double drawLineByLine(ChartCanvas<?> canvas, Title title, double x, double y, double lineSpacing) {
        int i = 0;
        int lineI = 0;
        int wordStart = 0;
        while (i < title.text.length()) {
            char c = title.text.charAt(i++);
            if (c == '\n') {
                double lineOffset = title.lineOffsets[lineI];
                if (Double.isNaN(lineOffset)) {//NaN is sentinel
                    break;
                }
                final String line = title.text.substring(wordStart, i);
                canvas.fillText(line, x - lineOffset, y + title.baselineOffset + (lineSpacing * title.lineHeight * lineI++));
                wordStart = i;
            }
        }
        if (wordStart < title.text.length()) {
            canvas.fillText(title.text.substring(wordStart), x - title.lineOffsets[lineI], y + title.baselineOffset + (lineSpacing * title.lineHeight * lineI++));
        }
        return lineI * title.lineHeight * lineSpacing;
    }


    /**
     * Layout the chart
     *
     * @param canvas the canvas to layout on
     */
    final void layout(ChartCanvas<?> canvas) {
        canvas.reset();
        double minY = 0, minX = 0, maxX = width, maxY = height;
        boolean colorBarDrawn = !colorBar.visible, legendDrawn = !colorBar.visible;
        //layout title
        if (title.isVisible()) {
            minY += title.paddingY;
            double titleWidth = width - (title.paddingX * 2);
            canvas.setFill(Fill.BLACK_FILL);//Todo fill optional color
            canvas.setFont(title.getFont());
            title.setMetrics(t -> t.setMetrics(getTextWidth(t.getFont(), t.getText()), getTextLineHeight(t, titleWidth, t.lineHeight), getLineHeight(t), getTextBaselineOffset(t.getFont()), getTextLineOffsets(t, t.width)));
            minY = drawLineByLine(canvas, title, titleWidth * .5, minY, title.lineSpacing);
            minY += title.paddingY + title.baselineOffset - title.lineHeight;
        }

        //layout "keys"
        if (occupies(legend, colorBar, Side.TOP)) {

            if (!legendDrawn && legend.isVisible() && legend.side == Side.TOP) {
                legend.layout(canvas, 0, minY, width, USE_PREFERRED_HEIGHT);
                //todo update yoffset
            }
            if (!colorBarDrawn && colorBar.isVisible() && colorBar.side == Side.TOP) {
                colorBar.layout(canvas, 0, minY, width, USE_PREFERRED_HEIGHT);
                //todo update yoffset
            }
        } else if (occupies(legend, colorBar, Side.LEFT)) {

        } else if (occupies(legend, colorBar, Side.RIGHT)) {
            if (!legendDrawn && legend.isVisible() && legend.side == Side.RIGHT) {
                legend.layout(canvas, 0, minY, maxX, height - minY);
                maxX -= legend.renderWidth;
            }
            if (!colorBarDrawn && colorBar.isVisible() && colorBar.side == Side.RIGHT) {
                colorBar.layout(canvas, 0, minY, maxX, height - minY);
                //todo update yoffset
                maxX-=colorBar.renderWidth;
            }
        } else {
            //BOTTOM
        }

        //layout plot

        //layout floating keys
        if (!colorBarDrawn || !legendDrawn) {

        }

        canvas.done();
    }

    /**
     * Test to see if the different regions are occupied
     *
     * @param a key a
     * @param b key b
     * @param q the side to test on
     * @return whether that side is occupied
     */
    private static boolean occupies(final KeyImpl<?> a, final KeyImpl<?> b, final Side q) {
        boolean aSide = a.isVisible() && !a.isFloating && a.side == q;
        boolean bSide = b.isVisible() && !b.isFloating && b.side == q;
        return aSide | bSide;
    }

    /**
     * Get the height of a line
     *
     * @param title the text
     * @return the height of a line of the given texty
     */
    protected abstract double getLineHeight(Title title);

    protected abstract double[] getTextLineOffsets(Title title, double maxWidth);

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

    protected abstract double getTextLineHeight(final Title title, double maxWidth, double lineSpacing);

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
     * @param font the font
     * @return the height of the text with the given font
     */
    protected abstract double getTextLineHeight(final Font font);

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

    protected static final double USE_PREFERRED_HEIGHT = -1;
    protected static final double USE_PREFERRED_WIDTH = -1;


    @SuppressWarnings("unchecked")
    protected static <S> XYMarginalPlot<S> toPlot(S series, double xMin, double xMax, double yMin, double yMax) {
        return new Layouts.RectangularPlot<>(new LinearAxis(xMin, xMax), new LinearAxis(yMin, yMax), ((PlotSeries<XYMarginalPlot<S>, S>) series).prepare());

    }
}