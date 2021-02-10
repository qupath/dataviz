package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Orientation;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.io.ImageExporter;
import net.mahdilamb.charts.io.SVGExporter;
import net.mahdilamb.charts.plots.CircularPlot;
import net.mahdilamb.charts.plots.RectangularPlot;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.geom2d.geometries.Point;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * The root for the drawing elements in the chart
 *
 * @param <S>   the type of the series in the figure
 * @param <IMG> the image type of the canvas
 */
public abstract class Figure<S extends PlotSeries<S>, IMG> extends ChartNode<ChartComponent> {

    private static final Map<String, BiConsumer<File, Figure<?, ?>>> supportedFormats = new HashMap<>();

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

    protected abstract static class PlotImpl<S extends PlotSeries<S>> extends ChartComponent implements Plot<S> {

        KeyAreaImpl.LegendImpl.LegendGroup legendGroup;

        ChartComponent singleAnnotation;
        List<ChartComponent> annotations;
        PairedChartNode central; // to be used when adding colorscale/legend to the chart

        @SafeVarargs
        PlotImpl(S... series) {
            this.series = series;

        }

        final S[] series;
        Color backgroundColor = Color.lightgrey;
        Stroke border = new Stroke(Color.darkgray, 1.5);

        @Override
        protected void assign(Figure<?, ?> chart) {
            super.assign(chart);
            int i = 0;
            for (final S s : series) {
                s.assign(chart);
                if (s.name == null) {
                    s.name = String.format("trace%d", i);
                }
                i++;
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
            redraw();
        }


        public abstract Point getPositionFromValue(final double x, final double y);

        public abstract Point getValueFromPosition(double x, double y);

        @Override
        protected void layout(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {

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
    }

    private static final Font DEFAULT_TITLE_FONT = new Font(Font.Family.SANS_SERIF, 20);

    protected static double DEFAULT_WIDTH = 800;
    protected static double DEFAULT_HEIGHT = 640;

    protected Color backgroundColor;

    Title title;
    Font titleFont = DEFAULT_TITLE_FONT;

    double width, height;

    KeyAreaImpl.ColorScaleImpl<S> colorScales;
    KeyAreaImpl.LegendImpl<S> legend;

    PairedChartNode topArea, leftArea, rightArea, bottomArea;
    final PlotImpl<S> plotArea;

    /**
     * Create a chart figure
     *
     * @param title  title of the chart
     * @param width  width of the chart
     * @param height height of the chart
     * @param layout the layout, including series, of the chart
     */
    protected Figure(String title, double width, double height, PlotImpl<S> layout) {
        this.title = new Title(title, titleFont);
        this.title.assign(this);
        this.width = width;
        this.height = height;
        this.plotArea = layout;
        this.plotArea.assign(this);
        this.legend = new KeyAreaImpl.LegendImpl<>();
        this.colorScales = new KeyAreaImpl.ColorScaleImpl<>();
        legend.assign(this);
        colorScales.assign(this);
        setKeyArea(legend);
        setKeyArea(colorScales);
    }

    void setKeyArea(KeyAreaImpl<?> keyArea) {
        if (keyArea.parentNode != null) {
            keyArea.parentNode.remove(keyArea);
        }
        if (keyArea.isFloating) {
            if (plotArea.central == null) {
                plotArea.central = new PairedChartNode(null);
            }
            plotArea.central.add(keyArea);
            return;
        }
        switch (keyArea.side) {
            case LEFT:
                if (leftArea == null) {
                    leftArea = new PairedChartNode(Orientation.VERTICAL);
                }
                leftArea.add(keyArea);
                return;
            case RIGHT:
                if (rightArea == null) {
                    rightArea = new PairedChartNode(Orientation.VERTICAL);
                }
                rightArea.add(keyArea);
                return;
            case BOTTOM:
                if (bottomArea == null) {
                    bottomArea = new PairedChartNode(Orientation.HORIZONTAL);
                }
                bottomArea.add(keyArea);
                return;
            case TOP:
                if (topArea == null) {
                    topArea = new PairedChartNode(Orientation.HORIZONTAL);
                }
                topArea.add(keyArea);
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected boolean remove(ChartComponent keyArea) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void add(ChartComponent keyArea) {
        throw new UnsupportedOperationException();
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

    public Plot<S> getPlot() {
        return plotArea;
    }

    /**
     * Get the most compatible layout for the given series. If similar plots share the same axis name, they are plotted together
     *
     * @param series the series
     * @param <S>    the type of the series
     * @return a layout for the series
     */
    //todo non-condensed version - one packed, one all rows
    @SafeVarargs
    protected static <S extends PlotSeries<S>> PlotImpl<S> getGroupedLayoutForSeries(S... series) {

        if (series.length == 0) {
            //empty plot
            return new Chart.RectangularPlot<>(new Axis(null, 0, 100), new Axis(null, 0, 100), series);
        }
        if (series.length == 1) {
            if (series[0].data != null && (series[0].facetCol != null || series[0].facetRow != null)) {
                return new Chart.FacetPlot<>(series[0]);
            }
            if (series[0] instanceof RectangularPlot) {
                final RectangularPlot t = (RectangularPlot) series[0];
                return new Chart.RectangularPlot<>(
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
                return new Chart.RectangularPlot<>(xAxis, yAxis, series);
            } else {
                //TODO multiplot of rectangular plots
            }
        } else {
            //TODO mixed rectangular and circular
        }
        return null;
    }

    @Override
    protected void layout(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
        if (!layoutNeedsRefresh) {
            return;
        }
        if (title.isVisible()) {
            title.layout(source, canvas, minX, minY, maxX, maxY);
            minY += title.sizeY;
        }
        layoutComponent(leftArea, canvas, source, minX, minY, maxX, maxY);
        if (leftArea != null) {
            minX += leftArea.sizeX;
        }
        layoutComponent(rightArea, canvas, source, minX, minY, maxX, maxY);
        if (rightArea != null) {
            maxX -= rightArea.sizeX;
        }
        layoutComponent(topArea, canvas, source, minX, minY, maxX, maxY);
        if (topArea != null) {
            minY += topArea.sizeY;
        }
        layoutComponent(bottomArea, canvas, source, minX, minY, maxX, maxY);
        if (bottomArea != null) {
            maxY -= bottomArea.sizeY;
        }
        //todo update positions
        layoutNeedsRefresh = false;
    }

    private void layoutComponent(final ChartComponent component, ChartCanvas<?> canvas, Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
        if (component == null) {
            return;
        }
        component.layout(source, canvas, minX, minY, maxX, maxY);
    }

    @Override
    protected void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
        canvas.reset();

        //  drawBounds(canvas, leftArea);
        canvas.strokeRect(0, 0, 50, 60);

        canvas.done();
    }

    /**
     * Layout the chart locally
     */
    protected final Figure<S, ?> redraw() {
        return redraw(getCanvas());
    }

    protected final Figure<S, ?> redraw(final ChartCanvas<?> canvas) {
        layout(this, canvas, 0, 0, width, height);
        draw(this, canvas, 0, 0, width, height);
        return this;
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
        redraw();
    }

    /**
     * Method called when the background has changed
     */
    protected abstract void backgroundChanged();

    /**
     * @return the background color of this chart
     */
    public final Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @return the canvas of the chart
     */
    protected abstract ChartCanvas<IMG> getCanvas();

    /**
     * Get the height of a line
     *
     * @param title the text
     * @return the height of a line of the given text
     */
    protected abstract double getTextLineHeight(Title title);

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
     */
    protected abstract double getImageWidth(IMG image);

    /**
     * @param image the image to get the height
     * @return the height of an image
     */
    protected abstract double getImageHeight(IMG image);

    /**
     * @param image the image to get the bytes of
     * @return the image as a byte array of PNG encoding bytes
     */
    protected abstract byte[] bytesFromImage(IMG image);

    /**
     * Get a packed int from an image, laid out as alpha, red, green, blue
     *
     * @param image the image (native to its canvas)
     * @param x     the x position
     * @param y     the y position
     * @return the packed into from an image at a specific position
     */
    protected abstract int argbFromImage(IMG image, int x, int y);

    /**
     * Register a file format
     *
     * @param extensionWithDot the extension with the dot
     * @param writer           the writer
     */
    protected static void registerFileWriter(final String extensionWithDot, BiConsumer<File, Figure<?, ?>> writer) {
        supportedFormats.put(extensionWithDot, writer);
    }

    /**
     * Save a file based on its extension (svg, jpg, bmp, png are supported)
     *
     * @param file the file to save as
     */
    public final void saveAs(File file) {
        final String filePath = file.toString();
        final String fileExt = StringUtils.getLastCharactersToLowerCase(new char[filePath.length() - filePath.lastIndexOf('.')], file.toString());
        final BiConsumer<File, Figure<?, ?>> fileWriter = supportedFormats.get(fileExt);
        if (fileWriter == null) {
            throw new UnsupportedOperationException("Cannot write to " + filePath + ". File type not supported");
        }
        fileWriter.accept(file, this);
    }


}
