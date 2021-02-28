package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.io.ImageExporter;
import net.mahdilamb.charts.io.SVGExporter;
import net.mahdilamb.charts.utils.StringUtils;

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
}
