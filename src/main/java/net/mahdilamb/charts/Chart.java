package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.io.ImageExporter;
import net.mahdilamb.charts.io.SVGExporter;
import net.mahdilamb.charts.plots.CircularPlot;
import net.mahdilamb.charts.plots.RectangularPlot;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.geom2d.geometries.Point;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

public abstract class Chart<S extends PlotSeries<S>> extends ChartComponent {

    private static final Map<String, BiConsumer<File, Chart<?>>> supportedFormats = new HashMap<>();
    private static final Font DEFAULT_TITLE_FONT = new Font(Font.Family.SANS_SERIF, 20);

    static {
        supportedFormats.put(".jpg", ImageExporter::toJPEG);
        supportedFormats.put(".jpeg", ImageExporter::toJPEG);
        supportedFormats.put(".tif", ImageExporter::toTIFF);
        supportedFormats.put(".tiff", ImageExporter::toTIFF);
        supportedFormats.put(".svg", SVGExporter::toSVG);
        supportedFormats.put(".svgz", SVGExporter::toSVGZ);
        supportedFormats.put(".png", ImageExporter::toPNG);
        supportedFormats.put(".bmp", ImageExporter::toBMP);
    }


    protected abstract static class PlotLayout<S extends PlotSeries<S>> extends ChartComponent implements PlotArea<S> {


        @SafeVarargs
        PlotLayout(S... series) {
            this.series = series;

        }

        final S[] series;
        Color backgroundColor = Color.lightgrey;
        Stroke border = new Stroke(Color.darkgray, 1.5);

        @Override
        protected void assignToChart(Chart<?> chart) {
            super.assignToChart(chart);
            for (final S s : series) {
                s.assignToChart(chart);
            }
        }

        public Axis getXAxis() {
            return null;
        }

        public Axis getYAxis() {
            return null;
        }

        public Axis getRadialAxis() {
            return null;
        }

        public Axis getAngularAxis() {
            return null;
        }

        @Override
        public int numSeries() {
            return series.length;
        }

        @Override
        public S getSeries(int index) {
            return series[index];
        }

        @Override
        public void setBackground(Color color) {
            this.backgroundColor = color;
            requestLayout();
        }


        public abstract Point getPositionFromValue(final double x, final double y);

        public abstract Point getValueFromPosition(double x, double y);

        @Override
        protected void calculateBounds(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {

        }

        protected Axis getAxis(final String name) {
            if (hasXAxis() && getXAxis().title != null && getXAxis().title.getText().equals(name)) {
                return getXAxis();
            }
            if (hasYAxis() && getYAxis().title != null && getYAxis().title.getText().equals(name)) {
                return getYAxis();
            }
            if (hasAngularAxis() && getAngularAxis().title != null && getAngularAxis().title.getText().equals(name)) {
                return getAngularAxis();
            }
            if (hasRadialAxis() && getRadialAxis().title != null && getRadialAxis().title.getText().equals(name)) {
                return getRadialAxis();
            }
            return null;
        }


        /**
         * An XY plot layout
         */
        public static class RectangularPlot<S extends PlotSeries<S>> extends PlotLayout<S> {


            private Axis xAxis, yAxis;

            @SafeVarargs
            public RectangularPlot(Axis xAxis, Axis yAxis, S... series) {
                super(series);
                this.xAxis = Objects.requireNonNull(xAxis);
                this.yAxis = Objects.requireNonNull(yAxis);

            }

            protected void calculateBounds(Chart<?> chart, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {

            }


            @Override
            protected void layout(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {
            }

            public Axis getXAxis() {
                return xAxis;
            }

            public Axis getYAxis() {
                return yAxis;
            }

            @Override
            public Point getPositionFromValue(double x, double y) {
                //TODO
                return null;
            }

            @Override
            public Point getValueFromPosition(double x, double y) {
                //TODO
                return null;
            }
        }

    }


    protected static double DEFAULT_WIDTH = 800;
    protected static double DEFAULT_HEIGHT = 640;
    /**
     * The default chart theme
     */
    protected Color backgroundColor;

    Title title;
    Font titleFont = DEFAULT_TITLE_FONT;

    double width, height;

    KeyAreaFormattingImpl.ColorScaleFormattingImpl<S> colorScaleFormatting;
    KeyAreaFormattingImpl.LegendFormattingImpl<S> legendFormatting;

    ChartNode topArea, leftArea, rightArea, bottomArea;
    final PlotLayout<S> plotArea;
    ChartNode central; // to be used when adding colorscale/legend to the chart
    ChartNode overlay;//for interaction

    ChartComponent singleAnnotation;
    List<ChartComponent> annotations;

    /**
     * Create a chart figure
     *
     * @param title  title of the chart
     * @param width  width of the chart
     * @param height height of the chart
     * @param layout the layout, including series, of the chart
     */
    protected Chart(String title, double width, double height, PlotLayout<S> layout) {
        this.title = new Title(title, titleFont);
        this.title.assignToChart(this);
        this.width = width;
        this.height = height;
        this.plotArea = layout;
        this.plotArea.assignToChart(this);
        this.legendFormatting = new KeyAreaFormattingImpl.LegendFormattingImpl<>(layout.series);
        this.colorScaleFormatting = new KeyAreaFormattingImpl.ColorScaleFormattingImpl<>(layout.series);
    }


    /**
     * Get the most compatible layout for the given series. If similar plots share the same axis name, they are plotted together
     *
     * @param series the series
     * @param <S>    the type of the series
     * @return a layout for the series
     */
    //todo non-condensed version - one packed, one all rows
    protected static <S extends PlotSeries<S>> PlotLayout<S> getGroupedLayoutForSeries(S[] series) {
        if (series.length == 0) {
            //empty plot
            return new PlotLayout.RectangularPlot<>(new Axis(null, 0, 100), new Axis(null, 0, 100), series);
        }
        if (series.length == 1) {
            if (series[0] instanceof RectangularPlot) {
                final RectangularPlot t = (RectangularPlot) series[0];
                return new PlotLayout.RectangularPlot<>(
                        new Axis(t.getXLabel(), t.getMinX(), t.getMaxX()),
                        new Axis(t.getYLabel(), t.getMinY(), t.getMaxY()),
                        series
                );
            } else {
                //TODO single circular plot
            }
        }
        int numCircular = 0, numRectangular = 0;
        for (final S s : series) {
            if (s instanceof CircularPlot) {
                ++numCircular;
            } else {
                ++numRectangular;
            }
        }
        if (numCircular > 0 && numRectangular == 0) {
            //all circular
            //todo
        } else if (numRectangular > 0 && numCircular == 0) {
            //all rectangular
            final Map<String, List<S>> xAxes = new HashMap<>(numRectangular);
            final Map<String, List<S>> yAxes = new HashMap<>(numRectangular);
            for (final S s : series) {
                final List<S> xs = xAxes.computeIfAbsent(((RectangularPlot) s).getXLabel(), k -> new LinkedList<>());
                xs.add(s);
                final List<S> ys = yAxes.computeIfAbsent(((RectangularPlot) s).getYLabel(), k -> new LinkedList<>());
                ys.add(s);
            }
            if (xAxes.size() <= 1 && yAxes.size() <= 1) {
                double xMin = Double.NaN, xMax = Double.NaN, yMin = Double.NaN, yMax = Double.NaN;
                for (int i = 0; i < series.length; i++) {
                    final RectangularPlot t = (RectangularPlot) series[i];
                    xMin = i == 0 ? t.getMinX() : Math.min(xMin, t.getMinX());
                    xMax = i == 0 ? t.getMaxX() : Math.max(xMax, t.getMaxX());
                    yMin = i == 0 ? t.getMinY() : Math.min(yMin, t.getMinY());
                    yMax = i == 0 ? t.getMaxY() : Math.max(yMax, t.getMaxY());
                }
                final Axis xAxis = new Axis(xAxes.size() == 0 ? null : ((RectangularPlot) series[0]).getXLabel(), xMin, xMax);
                final Axis yAxis = new Axis(yAxes.size() == 0 ? null : ((RectangularPlot) series[0]).getYLabel(), yMin, yMax);
                return new PlotLayout.RectangularPlot<>(xAxis, yAxis, series);
            } else {
                //TODO multiplot of rectangular plots
            }
        } else {
            //TODO mixed rectangular and circular
        }
        return null;
    }

    public void addAnnotation(ChartComponent annotation) {
        if (annotations == null) {
            if (singleAnnotation == null) {
                singleAnnotation = annotation;
                return;
            }
            this.annotations = new ArrayList<>();
            this.annotations.add(singleAnnotation);
            this.annotations.add(annotation);
            singleAnnotation = null;
            return;
        }
        this.annotations.add(annotation);
        ///todo request layout
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
        requestLayout();
    }

    /**
     * @return the plot area
     */
    public final PlotArea<S> getPlot() {
        return plotArea;
    }

    /**
     * @return the colorbars
     */
    public final ColorScaleFormatting getColorScale() {
        return colorScaleFormatting;
    }

    /**
     * @return the legend
     */
    public final LegendFormatting getLegend() {
        return legendFormatting;
    }

    /**
     * Set an axis title
     *
     * @param oldTitle the current title name
     * @param newTitle the new title name
     * @return whether the title was able to be updated
     */
    public final boolean setAxisTitle(final String oldTitle, final String newTitle) {
        final Axis axis = plotArea.getAxis(oldTitle);
        if (axis == null) {
            return false;
        }
        axis.setTitle(newTitle);
        return true;
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
        backgroundColor = color;
        backgroundChanged();
        requestLayout();
    }

    /**
     * @return the background color of this chart
     */
    public final Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Method called when the background has changed
     */
    protected abstract void backgroundChanged();

    /**
     * Layout the chart
     *
     * @param canvas the canvas to layout on
     */
    final void layout(ChartCanvas<?> canvas) {
        layout(canvas, this, 0, 0, width, height);
    }


    @Override
    protected void layout(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {
        canvas.reset();
        title.calculateBounds(canvas, source, minX, minY, maxX, maxY);
        drawBounds(canvas, title);
        plotArea.layout(canvas, source, minX, minY, maxX, maxY);

        canvas.done();
    }


    /**
     * Layout the chart locally
     */
    protected final Chart<S> requestLayout() {
        layout(getCanvas());
        return this;
    }

    @Override
    protected void calculateBounds(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {

    }

    protected boolean isEmpty(final ChartNode node) {
        return node == null || node.size() == 0;
    }

    /**
     * Get the height of a line
     *
     * @param title the text
     * @return the height of a line of the given text
     */
    protected abstract double getTextLineHeight(Title title);

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

    protected abstract double getCharWidth(final Font font, char character);

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

    /**
     * Get a packed int from an image, laid out as alpha, red, green, blue
     *
     * @param image the image (native to its canvas)
     * @param x     the x position
     * @param y     the y position
     * @return the packed into from an image at a specific position
     */
    protected abstract int argbFromImage(Object image, int x, int y);

    /**
     * Register a file format
     *
     * @param extensionWithDot the extension with the dot
     * @param writer           the writer
     */
    protected static void registerFileWriter(final String extensionWithDot, BiConsumer<File, Chart<?>> writer) {
        supportedFormats.put(extensionWithDot, writer);
    }

    /**
     * Save a file based on its extension (svg, jpg, bmp, png are supported)
     *
     * @param file the file to save as
     * @throws UnsupportedOperationException if the file format is not supported
     * @throws IOException                   if the file cannot be written to
     */
    public final void saveAs(File file) throws IOException {
        final String filePath = file.toString();
        final String fileExt = StringUtils.getLastCharactersToLowerCase(new char[filePath.length() - filePath.lastIndexOf('.')], file.toString());
        final BiConsumer<File, Chart<?>> fileWriter = supportedFormats.get(fileExt);
        if (fileWriter == null) {
            throw new UnsupportedOperationException("Cannot write to " + filePath + ". File type not supported");
        }
        fileWriter.accept(file, this);
    }

}