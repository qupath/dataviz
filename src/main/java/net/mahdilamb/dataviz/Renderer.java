package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.graphics.ChartCanvas;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.Title;
import net.mahdilamb.dataviz.io.ImageExporter;
import net.mahdilamb.dataviz.io.SVGExporter;
import net.mahdilamb.dataviz.utils.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class Renderer<IMG> {
    private static final Map<String, BiConsumer<File, Renderer<?>>> supportedFormats = new HashMap<>();

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


    protected double startX, startY;
    protected boolean horizontalInputEnabled = true,
            verticalInputEnabled = true;
    protected final Figure figure;

    protected Renderer(final Figure figure) {
        this.figure = figure;
        figure.renderer = this;
    }

    protected abstract ChartCanvas<IMG> getCanvas();

    protected void refresh(ChartCanvas<?> canvas) {
        figure.update(canvas);
    }

    protected void refresh() {
        refresh(getCanvas());
    }

    protected void forceRefresh() {
        figure.markLayoutAsOld();
        refresh();
    }

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
    protected static void registerFileWriter(final String extensionWithDot, BiConsumer<File, Renderer<?>> writer) {
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
        final BiConsumer<File, Renderer<?>> fileWriter = supportedFormats.get(fileExt);
        if (fileWriter == null) {
            throw new UnsupportedOperationException("Cannot write to " + filePath + ". File type not supported");
        }
        fileWriter.accept(file, this);
    }

    public Figure getFigure() {
        return figure;
    }

    protected void mouseInit(double x, double y) {
        startX = x;
        startY = y;
        horizontalInputEnabled = verticalInputEnabled = true; // TODO
        // initMouseInput(e.getX(), e.getY());

    }

    protected void pan(double x, double y) {
        if (!horizontalInputEnabled && !verticalInputEnabled) {
            return;
        }
        double minX = figure.getLayout().getXAxis().getLowerBound(),
                minY = figure.getLayout().getYAxis().getLowerBound(),
                maxX = figure.getLayout().getXAxis().getUpperBound(),
                maxY = figure.getLayout().getYAxis().getUpperBound();
        //move in X direction
        if (horizontalInputEnabled) {
            final double dX = x - startX;
            final double offsetX = (dX / (getWidth(figure.getLayout()))) * (minX - maxX);
            minX += offsetX;
            maxX += offsetX;
        }
        //move in Y direction
        if (verticalInputEnabled) {
            final double dY = y - startY;
            final double offsetY = dY / getHeight(figure.getLayout().getYAxis()) * (maxY - minY);
            minY += offsetY;
            maxY += offsetY;
        }
        figure.getLayout().setRange(minX, maxX, minY, maxY);
        startX = x;
        startY = y;
    }

    protected void zoom(double ex, double ey, double zoom) {
          /*if (!initMouseInput(e.getX(), e.getY())) {
            return;
        }*/
        //calculate a scaleFactor from the scroll amount
        final double scaleFactor = 1 + zoom;

        //TODO scrollamount dimensions

        //zoom into current mouse position by the an amount proportionate to the scroll amount
        final Axis x = figure.getLayout().getXAxis();
        final Axis y = figure.getLayout().getYAxis();
        double minX = x.getLowerBound();
        double maxX = x.getUpperBound();
        double minY = y.getLowerBound();
        double maxY = y.getUpperBound();

        if (horizontalInputEnabled) {
            final double px = x.getValueFromPosition(ex);
            double newXRange = (x.getUpperBound() - x.getLowerBound()) * scaleFactor;
            double left = (ex - getX(x)) * newXRange / getWidth(x);
            minX = px - left;
            maxX = px + newXRange - left;

        }
        if (verticalInputEnabled) {
            final double py = y.getValueFromPosition(ey);
            double newYRange = (y.getUpperBound() - y.getLowerBound()) * scaleFactor;
            double top = (ey - getY(y)) * newYRange / getHeight(y);

            minY = top + py;
            maxY = top + py - newYRange;

        }
        figure.getLayout().setRange(minX, maxX, minY, maxY);
    }

    private boolean isMouseOverPlot(final double x, final double y) {
        return x >= getX(figure.getLayout())
                && x <= (getX(figure.getLayout()) + getWidth(figure.getLayout()))
                && y >= getY(figure.getLayout())
                && y <= (getY(figure.getLayout()) + getHeight(figure.getLayout()));
    }

    private boolean initMouseInput(final double x, final double y) {
        //TODO simplify
        if (isMouseOverPlot(x, y)) {
            horizontalInputEnabled = verticalInputEnabled = false;
            return false;
        }
        horizontalInputEnabled = x >= getX(figure.getLayout()) && x <= getX(figure.getLayout()) + getWidth(figure.getLayout());
        verticalInputEnabled = y <= getY(figure.getLayout()) + getHeight(figure.getLayout()) && y >= getY(figure.getLayout());
        return horizontalInputEnabled || verticalInputEnabled;
    }

    protected static double getWidth(final Component component) {
        return component.sizeX;
    }

    protected static double getHeight(final Component component) {
        return component.sizeY;
    }

    protected static double getX(final Component component) {
        return component.posX;
    }

    protected static double getY(final Component component) {
        return component.posY;
    }

    protected static double getRange(final Axis a) {
        return a.range;
    }

    protected static double transformX(PlotLayout layout, final double x) {
        return layout.transformX(x);
    }

    protected static double transformY(PlotLayout layout, final double y) {
        return layout.transformY(y);
    }
}
