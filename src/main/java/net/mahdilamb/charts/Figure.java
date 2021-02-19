package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Side;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.io.ImageExporter;
import net.mahdilamb.charts.io.SVGExporter;
import net.mahdilamb.charts.plots.CircularPlot;
import net.mahdilamb.charts.plots.RectangularPlot;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.QualitativeColormap;
import net.mahdilamb.colormap.reference.qualitative.Plotly;
import net.mahdilamb.colormap.reference.sequential.Viridis;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * The root for the drawing elements in the chart
 *
 * @param <S>   the type of the series in the figure
 * @param <IMG> the image type of the canvas
 */
public abstract class Figure<S extends PlotSeries<S>, IMG> extends ChartNode {

    public static final QualitativeColormap DEFAULT_QUALITATIVE_COLORMAP = new Plotly();
    public static final Colormap DEFAULT_SEQUENTIAL_COLORMAP = new Viridis();

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

        ChartComponent singleAnnotation;
        List<ChartComponent> annotations;
        ChartKeyNode central; // to be used when adding colorscale/legend to the chart

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

    protected static double DEFAULT_WIDTH = 800;
    protected static double DEFAULT_HEIGHT = 640;
    protected static final Font DEFAULT_TITLE_FONT = new Font(Font.Family.SANS_SERIF, 18);
    protected Color backgroundColor;

    Title title;
    Font titleFont = DEFAULT_TITLE_FONT;

    double width, height;

    ColorScaleImpl<S> colorScales;
    LegendImpl<S> legend;

    ChartKeyNode topArea, leftArea, rightArea, bottomArea;
    final PlotImpl<S> plotArea;

    /**
     * Create a chart figure
     *
     * @param title  title of the chart
     * @param width  width of the chart
     * @param height height of the chart
     * @param plot   the layout, including series, of the chart
     */
    protected Figure(String title, double width, double height, PlotImpl<S> plot) {
        this.title = new Title(title, titleFont);
        this.title.assign(this);
        this.width = width;
        this.height = height;
        this.plotArea = plot;
        this.plotArea.assign(this);
        this.legend = new LegendImpl<>(plot);
        this.colorScales = new ColorScaleImpl<>(plot);
        legend.assign(this);
        colorScales.assign(this);
        setKeyArea(legend);
        setKeyArea(colorScales);
    }

    void setKeyArea(KeyAreaImpl<?> keyArea) {
        if (keyArea.parent != null) {
            (keyArea.parent).remove(keyArea);
        }
        if (keyArea.isFloating) {
            if (plotArea.central == null) {
                plotArea.central = new ChartKeyNode();
            }
            plotArea.central.add(keyArea);
            return;
        }
        switch (keyArea.side) {
            case LEFT:
                if (leftArea == null) {
                    leftArea = new ChartKeyNode(Side.LEFT);
                }
                leftArea.add(keyArea);
                return;
            case RIGHT:
                if (rightArea == null) {
                    rightArea = new ChartKeyNode(Side.RIGHT);
                }
                rightArea.add(keyArea);
                return;
            case BOTTOM:
                if (bottomArea == null) {
                    bottomArea = new ChartKeyNode(Side.BOTTOM);
                }
                bottomArea.add(keyArea);
                return;
            case TOP:
                if (topArea == null) {
                    topArea = new ChartKeyNode(Side.TOP);
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
                final double xRange = (t.getMaxX() - t.getMinX());
                final double yRange = (t.getMaxY() - t.getMinY());
                final Axis xAxis = new Axis(t.getXLabel(), t.getMinX(), t.getMaxX());
                final Axis yAxis = new Axis(t.getYLabel(), t.getMinY(), t.getMaxY());
                return new Chart.RectangularPlot<>(
                        xAxis,
                        yAxis,
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
            //all circular - try to combine
            //todo
        } else if (numRectangular > 0 && numCircular == 0) {
            //all rectangular - try to combine
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
            }
        }
        //todo grid plots
        return null;
    }

    @Override
    protected void layoutComponent(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
        if (title.isVisible()) {
            title.layoutComponent(source, minX, minY, maxX, maxY);
            minY += title.sizeY;
        } else {
            minY += 2;//todo padding
        }
        layoutComponent(leftArea, source, minX, minY, maxX, maxY);
        if (leftArea != null) {
            minX += leftArea.sizeX;
        }
        layoutComponent(rightArea, source, minX, minY, maxX, maxY);
        if (rightArea != null) {
            maxX -= rightArea.sizeX;
        }
        layoutComponent(topArea, source, minX, minY, maxX, maxY);
        if (topArea != null) {
            minY += topArea.sizeY;
        }
        layoutComponent(bottomArea, source, minX, minY, maxX, maxY);
        if (bottomArea != null) {
            maxY -= bottomArea.sizeY;
        }
        if (title.isVisible()) {
            title.layoutComponent(source, minX, 0, maxX, maxY);
        }
        final double height = maxY - minY;
        final double width = maxX - minX;
        if (rightArea != null) {
            rightArea.posX = maxX;
            rightArea.posY = minY;
            rightArea.sizeY = height;
            rightArea.alignChildren(source, height, width);
        }
        if (leftArea != null) {
            leftArea.posX = 0;
            leftArea.posY = minY;
            leftArea.sizeY = height;
            leftArea.alignChildren(source, height, width);
        }
        if (topArea != null) {
            topArea.posY = title.sizeY;
            topArea.posX = minX;
            topArea.sizeX = width;
            topArea.alignChildren(source, height, width);
        }
        if (bottomArea != null) {
            bottomArea.posY = maxY;
            bottomArea.posX = minX;
            bottomArea.sizeX = width;
            bottomArea.alignChildren(source, height, width);
        }
        plotArea.layoutComponent(source, minX, minY, maxX, maxY);
    }


    @Override
    protected void drawComponent(Figure<?, ?> source, ChartCanvas<?> canvas) {
        canvas.reset();
        title.drawComponent(source, canvas);
        plotArea.drawComponent(source, canvas);
        canvas.setStroke(new Stroke(Color.GREEN, 1));
        if (rightArea != null) {
            rightArea.draw(source, canvas);
        }
        if (leftArea != null) {
            leftArea.draw(source, canvas);
        }
        if (topArea != null) {
            topArea.draw(source, canvas);
        }
        if (bottomArea != null) {
            bottomArea.draw(source, canvas);
        }
        canvas.done();
    }

    private void layoutComponent(final ChartComponent component, Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
        if (component == null) {
            return;
        }
        component.layoutComponent(source, minX, minY, maxX, maxY);
    }

    /**
     * Draw the chart locally
     */
    protected final Figure<S, ?> redraw() {
        layoutComponent(this, 0, 0, width, height);
        drawComponent(this, getCanvas());
        return this;
    }

    /**
     * Draw the figure on the given canvas
     *
     * @param canvas the canvas to draw on
     * @return this figure
     */
    protected final Figure<S, ?> redraw(final ChartCanvas<?> canvas) {
        markDrawAsOld();
        layoutComponent(this, 0, 0, width, height);
        drawComponent(this, canvas);
        return this;
    }

    @Override
    protected void markLayoutAsOld() {
        if (leftArea != null) {
            leftArea.markLayoutAsOld();
        }
        if (rightArea != null) {
            rightArea.markLayoutAsOld();
        }
        if (topArea != null) {
            topArea.markLayoutAsOld();
        }
        if (bottomArea != null) {
            bottomArea.markLayoutAsOld();
        }
        if (plotArea != null) {
            plotArea.markLayoutAsOld();
        }
        super.markLayoutAsOld();
    }

    @Override
    protected void markDrawAsOld() {
        if (leftArea != null) {
            leftArea.markDrawAsOld();
        }
        if (rightArea != null) {
            rightArea.markDrawAsOld();
        }
        if (topArea != null) {
            topArea.markDrawAsOld();
        }
        if (bottomArea != null) {
            bottomArea.markDrawAsOld();
        }
        if (plotArea != null) {
            plotArea.markDrawAsOld();
        }
        super.markDrawAsOld();
    }
    /* Background methods */

    /**
     * Set the background color of this chart by the name
     *
     * @param colorName the name of the color
     */
    public final void setBackgroundColor(String colorName) {
        setBackgroundColor(StringUtils.convertToColor(colorName));
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

    /* Canvas methods */

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

    /*  Export methods  */

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
