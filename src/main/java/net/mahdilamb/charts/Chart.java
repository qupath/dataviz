package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Theme;
import net.mahdilamb.charts.io.ImageExporter;
import net.mahdilamb.charts.io.SVGFile;
import net.mahdilamb.charts.statistics.StatUtils;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.reference.qualitative.Plotly;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

public abstract class Chart<P, S> extends ChartComponent<P, S> {

    static class CategoricalSeries {
        String[] labels;
        double[] values;
    }

    static class StatsSeries {
        double[] values;
    }

    static class XYSeries {

    }

    static class XYSeriesBuilder {
        final double[] x;
        final double[] y;
        final double xMin, xMax, yMin, yMax;

        XYSeriesBuilder(double[] x, double[] y) {
            if (x.length != y.length) {
                throw new IllegalArgumentException("x and y must be the same length");
            }
            this.x = x;
            this.y = y;
            this.xMin = StatUtils.min(x);
            this.xMax = StatUtils.max(x);
            this.yMin = StatUtils.min(y);
            this.yMax = StatUtils.max(y);
        }

    }

    private static final class ColorBy {
        String name;
    }

    private static final class ColorByScale {
        String name;
        boolean showInLegend, showColorScale;
    }

    private static final class ColorByGroup {
        String name;
        boolean showInLegend;
    }

    public static class Scatter extends XYSeriesBuilder {

        int[] colorByGroup;
        String[] colorByGroupName;

        String colorByName;
        Color[] colors;
        double colorMin, colorMax;
        boolean useColorScale = false;

        public Scatter(double[] x, double[] y) {
            super(x, y);

        }

        public Scatter setColorBy(final String name, final Colormap colormap, double[] colorBy, boolean normalize) {
            if (colorBy.length != x.length) {
                //todo check if divisible
            }
            this.colorByName = name;
            colors = new Color[x.length];
            colorMin = StatUtils.min(colorBy);
            colorMax = StatUtils.max(colorBy);
            double range = colorMax - colorMin;
            for (int i = 0; i < x.length; ++i) {
                colors[i] = colormap.get(normalize ? ((colorBy[i] - colorMin) / range) : colorBy[i]);
            }
            useColorScale = true;
            return this;
        }

        public Scatter setColorBy(final String name, String[] colorNames) {
            if (colorNames.length != x.length) {
                //todo
            }
            this.colorByName = name;
            colors = new Color[x.length];
            for (int i = 0; i < x.length; ++i) {
                colors[i] = Color.get(colorNames[i]);
            }
            useColorScale = false;
            return this;
        }

        public Scatter setGroupBy(final String name, Iterable<String> groups) {
            colorByName = name;
            final Set<String> colors = new LinkedHashSet<>();
            int num = 0;
            for (String s : groups) {
                colors.add(s);
                ++num;
            }
            if (num != x.length) {
                throw new IllegalArgumentException("number of colors must equal that of scatter points");
            }
            final Map<String, Integer> map = new Hashtable<>(colors.size());
            int i = 0;
            for (String color : colors) {
                map.put(color, i++);
            }
            this.colorByGroup = new int[x.length];
            this.colorByGroupName = new String[x.length];
            int j = 0;
            for (final String s : groups) {
                this.colorByGroup[j] = map.get(s);
                this.colorByGroupName[j++] = s;
            }

            useColorScale = false;
            return this;
        }

        public Scatter setColorBy(final String name, Color[] colors) {
            if (colors.length != x.length) {
                //todo
            }
            this.colorByName = name;
            this.colors = colors;
            useColorScale = false;
            return this;
        }


    }

    public abstract static class PlotLayout<S> extends ChartComponent<Object, S> {

        @Override
        protected void calculateBounds(ChartCanvas<?> canvas, Chart<?, ? extends S> source, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected void layout(ChartCanvas<?> canvas, Chart<?, ? extends S> source, double minX, double minY, double maxX, double maxY) {
            setBoundsFromExtent(minX, minY, maxX, maxY);
            canvas.fillOval(0, 0, 505, 50);
        }
    }

    public static class XYPlot<S> extends PlotLayout<S> {

        private final Axis xAxis;
        private final Axis yAxis;

        public XYPlot(Axis xAxis, Axis yAxis) {
            super();
            this.xAxis = xAxis;
            this.yAxis = yAxis;
            System.out.println(xAxis);
        }
    }

    private static final Map<String, BiConsumer<File, Chart<?, ?>>> supportedFormats = new HashMap<>();

    static {
        supportedFormats.put(".jpg", ImageExporter::toJPEG);
        supportedFormats.put(".jpeg", ImageExporter::toJPEG);
        supportedFormats.put(".tif", ImageExporter::toTIFF);
        supportedFormats.put(".tiff", ImageExporter::toTIFF);
        supportedFormats.put(".svg", SVGFile::to);
        supportedFormats.put(".svgz", SVGFile::toCompressed);
        supportedFormats.put(".png", ImageExporter::toPNG);
        supportedFormats.put(".bmp", ImageExporter::toBMP);

    }

    protected static double DEFAULT_WIDTH = 800;
    protected static double DEFAULT_HEIGHT = 640;
    /**
     * The default chart theme
     */
    public static final Theme DEFAULT_THEME = new ThemeImpl(new Plotly(), Color.WHITE, Color.LIGHT_GRAY, Color.BLACK, 2, 2);

    Theme theme = Theme.DEFAULT;
    Iterator<Float> colormapIt = theme.getDefaultColormap().iterator();

    Title title;
    ChartNode<P, S> topArea, leftArea, rightArea, bottomArea;
    final PlotLayout<S> plotArea;
    ChartNode<P, S> central; // to be used when adding colorscale/legend to the chart
    ChartNode<P, S> overlay;//for interaction
    double width, height;

    S singleData;
    List<S> data;
    ChartComponent<P, S> singleAnnotation;
    List<ChartComponent<P, S>> annotations;

    protected Chart(String title, double width, double height, PlotLayout<S> plot) {
        this.title = new Title(title, new Font(Font.Family.SANS_SERIF, theme.getTitleSize()));//todo
        this.title.chart = this;
        this.plotArea = plot;
        this.plotArea.chart = this;
        this.width = width;
        this.height = height;
    }


    public void addData(S data) {
        //todo update key/color bar

        if (this.data == null) {
            if (singleData == null) {
                singleData = data;
                return;
            }
            this.data = new ArrayList<>();
            this.data.add(singleData);
            this.data.add(data);
            singleData = null;
            return;
        }
        this.data.add(data);

    }

    public void addAnnotation(ChartComponent<P, S> annotation) {
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
     * Save a file based on its extension (svg, jpg, bmp, png are supported)
     *
     * @param file the file to save as
     * @throws UnsupportedOperationException if the file format is not supported
     * @throws IOException                   if the file cannot be written to
     */
    public final void saveAs(File file) throws IOException {
        final String filePath = file.toString();
        final String fileExt = StringUtils.getLastCharactersToLowerCase(new char[filePath.length() - filePath.lastIndexOf('.')], file.toString());
        final BiConsumer<File, Chart<?, ?>> fileWriter = supportedFormats.get(fileExt);
        if (fileWriter == null) {
            throw new UnsupportedOperationException("Cannot write to " + filePath + ". File type not supported");
        }
        fileWriter.accept(file, this);
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
        requestLayout();
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
        layout(canvas, this, 0, 0, width, height);

    }

    /**
     * Get the height of a line
     *
     * @param title the text
     * @return the height of a line of the given texty
     */
    protected abstract double getTextLineHeight(Title title);

    @Override
    protected void layout(ChartCanvas<?> canvas, Chart<? extends P, ? extends S> source, double minX, double minY, double maxX, double maxY) {
        canvas.reset();

        //title.calculateBounds(canvas, source, minX, minY, maxX, maxY);
        plotArea.layout(canvas, source, minX, minY, maxX, maxY);

        canvas.done();
    }

    boolean isEmpty(final ChartNode<?, ?> node) {
        return node == null || node.size() == 0;
    }

    /**
     * Layout the chart locally
     */
    protected final void requestLayout() {
        layout(getCanvas());
    }

    @Override
    protected void calculateBounds(ChartCanvas<?> canvas, Chart<? extends P, ? extends S> source, double minX, double minY, double maxX, double maxY) {

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
     * Get the next color in the default colormap
     *
     * @param chart the chart
     * @return the next color in the chart
     */
    protected static Color getNextColor(Chart<?, ?> chart) {
        if (chart.colormapIt == null || !chart.colormapIt.hasNext()) {
            chart.colormapIt = chart.theme.getDefaultColormap().iterator();
        }
        return chart.theme.getDefaultColormap().get(chart.colormapIt.next());
    }

    /**
     * Register a file format
     *
     * @param extensionWithDot the extension with the dot
     * @param writer           the writer
     */
    protected static void registerFileWriter(final String extensionWithDot, BiConsumer<File, Chart<?, ?>> writer) {
        supportedFormats.put(extensionWithDot, writer);
    }

    protected static final double USE_PREFERRED_HEIGHT = -1;
    protected static final double USE_PREFERRED_WIDTH = -1;

    /**
     * Assign a component to a chart
     *
     * @param chart the chart
     * @param a     the component
     */
    protected static <P, S> void assignToChart(Chart<P, S> chart, ChartComponent<P, S> a) {
        a.chart = chart;
    }

    /**
     * Assigns a chart component to a chart
     *
     * @param chart the chart to assign to
     * @param a     component a
     * @param b     component b
     */
    protected static <P, S> void assignToChart(Chart<P, S> chart, ChartComponent<P, S> a, ChartComponent<P, S> b) {
        assignToChart(chart, a);
        assignToChart(chart, b);
    }

    @SuppressWarnings("unchecked")
    protected static <P extends XYPlot<S>, S> P toPlot(final String xAxisLabel, Axis xAxis, final Axis yAxis, S series) {
        return (P) new XYPlot<>(xAxis, yAxis);

    }
/*
    TODO
    @SuppressWarnings("unchecked")
    protected static <P extends PlotLayoutImpl.XYMarginal<S>, S> P toPlot(double xMin, double xMax, double yMin, double yMax, MarginalMode xMarginal, MarginalMode yMarginal, S series) {
        return (P) new PlotLayoutImpl.XYMarginal<>(new LinearAxis(xMin, xMax), new LinearAxis(yMin, yMax), ((PlotSeries<S>) series).prepare());

    }
*/
}