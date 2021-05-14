package net.mahdilamb.dataviz.io;

import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.utils.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static net.mahdilamb.dataviz.io.ImageExporter.toBufferedImage;

/**
 * Figure exporter service
 */
public interface FigureExporter {

    /**
     * An SVG exporter
     */
    final class SVGExporter implements FigureExporter {
        private final static List<String> extensions = Collections.singletonList(".svg");

        @Override
        public List<String> getFileExtensions() {
            return extensions;
        }

        @Override
        public void export(File file, Renderer renderer) {
            new net.mahdilamb.dataviz.io.SVGExporter.SVGWriter(file, renderer);
        }
    }

    /**
     * An SVGZ exporter
     */
    final class SVGZExporter implements FigureExporter {
        private final static List<String> extensions = Collections.singletonList(".svgz");

        @Override
        public List<String> getFileExtensions() {
            return extensions;
        }

        @Override
        public void export(File file, Renderer renderer) {
            new net.mahdilamb.dataviz.io.SVGExporter.SVGWriter(file, renderer, true);
        }
    }

    /**
     * Create a PNG version of a chart
     */
    final class PNGExporter implements FigureExporter {
        private final static List<String> extensions = Collections.singletonList(".png");

        @Override
        public List<String> getFileExtensions() {
            return extensions;
        }

        @Override
        public void export(File file, Renderer renderer) {
            try {
                ImageIO.write(toBufferedImage(BufferedImage.TYPE_INT_ARGB, renderer), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a TIFF version of a chart
     */
    final class TIFFExporter implements FigureExporter {
        private final static List<String> extensions = List.of(".tiff", ".tif");

        @Override
        public List<String> getFileExtensions() {
            return extensions;
        }

        @Override
        public void export(File file, Renderer renderer) {
            try {
                ImageIO.write(toBufferedImage(BufferedImage.TYPE_INT_ARGB, renderer), "tiff", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a JPEG version of a chart
     */
    final class JPEGExporter implements FigureExporter {

        private final static List<String> extensions = List.of(".jpeg", ".jpg");

        @Override
        public List<String> getFileExtensions() {
            return extensions;
        }

        @Override
        public void export(File file, Renderer renderer) {
            try {
                ImageIO.write(toBufferedImage(BufferedImage.TYPE_INT_RGB, renderer), "jpeg", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a BMP version of a chart
     */
    final class BMPExporter implements FigureExporter {
        private final static List<String> extensions = Collections.singletonList(".bmp");

        @Override
        public List<String> getFileExtensions() {
            return extensions;
        }

        @Override
        public void export(File file, Renderer renderer) {
            try {
                ImageIO.write(toBufferedImage(BufferedImage.TYPE_INT_ARGB, renderer), "bmp", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the file extension including the initial dot
     */
    List<String> getFileExtensions();

    /**
     * @return the default file extension
     */
    default String getDefaultExtension() {
        return getFileExtensions().get(0);
    }

    /**
     * Export a figure
     *
     * @param file     the file to save
     * @param renderer the renderer used to draw the figure
     */
    void export(File file, Renderer renderer);

    /**
     * Export a figure
     *
     * @param file     the output path
     * @param renderer the renderer of the figure
     */
    static void exportFigure(File file, Renderer renderer) {
        final String filePath = file.toString();
        final String fileExt = StringUtils.getLastCharactersToLowerCase(new char[filePath.length() - filePath.lastIndexOf('.')], file.toString());
        for (FigureExporter exporter : ServiceLoader.load(FigureExporter.class)) {
            for (final String ext : exporter.getFileExtensions()) {
                if (ext.equalsIgnoreCase(fileExt)) {
                    exporter.export(file, renderer);
                    return;
                }
            }
        }
        throw new UnsupportedOperationException("File type not supported: cannot write to " + filePath + ".");

    }

    static List<String> getSupportedExtensions() {
        final List<String> extensions = new ArrayList<>();
        for (final FigureExporter exporter : ServiceLoader.load(FigureExporter.class)) {
            extensions.add(exporter.getDefaultExtension());
        }
        extensions.sort(Comparator.naturalOrder());
        return extensions;
    }
}
