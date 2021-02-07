package net.mahdilamb.charts.io;

import net.mahdilamb.charts.Chart;
import net.mahdilamb.charts.ChartExporter;
import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.geom2d.geometries.Geometries;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.zip.GZIPOutputStream;

import static net.mahdilamb.charts.io.SVGUtils.*;
import static net.mahdilamb.charts.utils.StringUtils.EMPTY_STRING;

/**
 * Exporter to save charts as SVGS
 */
public class SVGExporter extends ChartExporter {

    private static final class SVGWriter implements ChartCanvas<Object> {
        String header;
        final StringBuilder out = new StringBuilder();
        final StringBuilder indent = new StringBuilder("\t");
        SVGDefinitions defs;
        final Chart<?> chart;
        private final File output;

        boolean isClipped = false;
        Fill fill;
        Stroke stroke;

        private static final StringBuilder pathD = new StringBuilder();
        private Font font = new Font(Font.Family.SANS_SERIF, 12);
        boolean compressed;

        SVGWriter(File output, Chart<?> chart, boolean compressed) {
            this.compressed = compressed;
            this.chart = chart;
            this.output = output;
            layoutChart(this, chart);

        }

        SVGWriter(File output, Chart<?> chart) {
            this(output, chart, false);
        }

        @Override
        public void resetRect(double x, double y, double width, double height) {
            //ignored
        }

        @Override
        public void reset() {
            fill = Fill.BLACK_FILL;
            stroke = Stroke.BLACK_STROKE;
            isClipped = false;
            out.setLength(0);
            indent.setLength(0);
            indent.append('\t');
            defs = new SVGDefinitions();
            final String backgroundColor = chart.getBackgroundColor() == null ? EMPTY_STRING : String.format("style=\"background-color:%s\" ", convertToString(chart.getBackgroundColor()));
            header = String.format(
                    "<svg version=\"1.1\" baseProfile=\"full\" width=\"%s\" height=\"%s\" %sxmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">%n",
                    convertToString(chart.getWidth()),
                    convertToString(chart.getHeight()),
                    backgroundColor
            );

        }

        @Override
        public void beginPath() {
            pathD.setLength(0);
        }

        @Override
        public void moveTo(double endX, double endY) {
            pathD.append(String.format("M %s %s ", convertToString(endX), convertToString(endY)));
        }

        @Override
        public void lineTo(double endX, double endY) {
            pathD.append(String.format("L %s %s ", convertToString(endX), convertToString(endY)));

        }

        @Override
        public void quadTo(double cpX, double cpY, double endX, double endY) {
            pathD.append(String.format("Q %s %s, %s %s ", convertToString(cpX), convertToString(cpY), convertToString(endX), convertToString(endY)));

        }

        @Override
        public void strokePolygon(double[] xPoints, double[] yPoints, int numPoints) {
            out.append(polygonToString(xPoints, yPoints, numPoints, defs, indent, stroke, null));
        }


        @Override
        public void strokePolyline(double[] xPoints, double[] yPoints, int numPoints) {
            out.append(polylineToString(xPoints, yPoints, numPoints, defs, indent, stroke));

        }

        @Override
        public void fillPolygon(double[] xPoints, double[] yPoints, int numPoints) {
            out.append(polygonToString(xPoints, yPoints, numPoints, defs, indent, null, fill));

        }

        @Override
        public void fillText(String text, double x, double y) {
            out.append(textToString(text, x, y, font, defs, indent, null, fill));
        }

        @Override
        public void fillText(String text, double x, double y, double rotationDegrees, double pivotX, double pivotY) {
            out.append(rotatedTextToString(text, x, y, rotationDegrees, pivotX, pivotY, font, defs, indent, null, fill));

        }

        @Override
        public void setFont(Font font) {
            this.font = font;
        }

        @Override
        public void setClip(ClipShape shape, double x, double y, double width, double height) {
            out.append(String.format("%s<g clip-path=\"url(#%s)\">%n", indent, defs.addClip(shape, x, y, width, height)));

            indent.append('\t');
            isClipped = true;
        }

        @Override
        public void clearClip() {
            if (!isClipped) {
                return;
            }
            indent.deleteCharAt(indent.length() - 1);
            out.append(String.format("%s</g>%n", indent));
            isClipped = false;
        }

        @Override
        public void drawImage(Object o, double x, double y) {
            out.append(imageToString(imageToBytes(o, chart), x, y, getImageWidth(o, chart), getImageHeight(o, chart), indent));
        }

        @Override
        public void curveTo(double cp1X, double cp1Y, double cp2X, double cp2Y, double endX, double endY) {
            pathD.append(String.format(
                    "C %s %s, %s %s, %s %s ",
                    convertToString(cp1X),
                    convertToString(cp1Y),
                    convertToString(cp2X),
                    convertToString(cp2Y),
                    convertToString(endX),
                    convertToString(endY)
                    )
            );
        }

        @Override
        public void closePath() {
            pathD.append("Z ");

        }

        @Override
        public void fill() {
            out.append(pathToString(pathD.toString(), defs, indent, null, fill));

        }

        @Override
        public void stroke() {
            out.append(pathToString(pathD.toString(), defs, indent, stroke, null));

        }

        @Override
        public void strokeRect(double x, double y, double width, double height) {
            out.append(rectangleToString(x, y, width, height, defs, indent, stroke, null));
        }

        @Override
        public void fillRect(double x, double y, double width, double height) {
            out.append(rectangleToString(x, y, width, height, defs, indent, null, fill));
        }

        @Override
        public void strokeRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
            out.append(roundedRectangleToString(x, y, width, height, arcWidth, arcHeight, defs, indent, stroke, null));
        }

        @Override
        public void fillRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
            out.append(roundedRectangleToString(x, y, width, height, arcWidth, arcHeight, defs, indent, null, fill));
        }

        @Override
        public void strokeOval(double x, double y, double width, double height) {
            if (width == height) {
                out.append(circleToString(x, y, width * .5, defs, indent, stroke, null));
            } else {
                out.append(ellipseToString(x, y, width, height, defs, indent, stroke, null));
            }
        }

        @Override
        public void fillOval(double x, double y, double width, double height) {
            if (width == height) {
                out.append(circleToString(x, y, width * .5, defs, indent, null, fill));
            } else {
                out.append(ellipseToString(x, y, width, height, defs, indent, null, fill));
            }
        }

        @Override
        public void strokeLine(double x0, double y0, double x1, double y1) {
            out.append(lineToString(x0, y0, x1, y1, defs, indent, stroke));
        }


        @Override
        public void setFill(Fill fill) {
            this.fill = fill;
        }


        @Override
        public void setStroke(Stroke stroke) {
            this.stroke = stroke;
        }

        @Override
        public void done() {
            if (isClipped) {
                clearClip();
            }
            out.append("</svg>");
            try (final Writer writer = compressed ? new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(output)), StandardCharsets.UTF_8) : new FileWriter(output)) {
                writer.write(header);
                writer.write(defs.get());
                writer.write(out.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Convert a chart to an svg
     *
     * @param output the file output
     * @param chart  the chart to convert
     */
    public static void toSVG(final File output, final Chart<?> chart) {
        new SVGWriter(output, chart);
    }

    /**
     * Convert a chart to a compressed svg
     *
     * @param file  the output file
     * @param chart the chart to convert
     */
    public static void toSVGZ(File file, Chart<?> chart) {
        new SVGWriter(file, chart, true);
    }

    /**
     * A class to store the definitions used by an SVG file.
     *
     * @apiNote this is intentionally not an inner class to protect against mistakes when adding definitions.
     * Most of the methods are either package-protected or private for the same reason.
     */
    static final class SVGDefinitions {
        private final StringBuilder buffer = new StringBuilder();
        private final Map<Object, String> currentDefinitions = new HashMap<>();
        private final Map<String, Integer> prefixCounter = new HashMap<>();
        private boolean isClosed = false;

        /**
         * Add a prefix and return the count
         *
         * @param prefix the prefix to add
         * @return the count of the prefix
         */
        private String putPrefix(final String prefix) {
            final Integer count = prefixCounter.get(prefix);
            if (count == null) {
                prefixCounter.put(prefix, 0);
                return String.format("%s%d", prefix, 0);
            }
            final Integer incremented = count + 1;
            prefixCounter.put(prefix, incremented);
            return String.format("%s%s", prefix, incremented);

        }

        /**
         * Add definition to the buffer
         *
         * @param prefix     prefix for id
         * @param type       the element type
         * @param content    the content of the element
         * @param attributes the attributes of the element
         * @return the id of the definition
         */
        private String add(final String prefix, final String type, final String content, final String attributes) {
            if (buffer.isEmpty()) {
                buffer.append("\t<defs>\n");
            }
            final String prefixId = putPrefix(prefix);
            buffer.append(String.format(
                    "\t\t<%s %sid=\"%s\">%n%s\t\t</%s>%n",
                    type,
                    attributes,
                    prefixId,
                    content,
                    type
            ));
            return prefixId;
        }

        /**
         * Add an object to the definitions. If object  is already present (as defined by {@link HashMap#get(Object)}, then returns key.
         *
         * @param object object to add to definition
         * @param orAdd  the function used to generate a new definition if object is absent
         * @return either the associated key (if present), or the new key (if added by the function).
         */
        private <T> String add(final T object, final Supplier<String> orAdd) {
            if (isClosed) {
                throw new UnsupportedOperationException("Definitions are closed. No more can be added to the store.");
            }
            final String storedKey = currentDefinitions.get(object);
            if (storedKey != null) {
                return storedKey;
            }
            final String newKey = orAdd.get();
            currentDefinitions.put(object, newKey);
            return newKey;
        }

        /**
         * Add a gradient and return the id
         *
         * @param gradient the gradient to add
         * @return the id of the gradient
         */
        final String addGradient(final Fill.Gradient gradient) {
            return add(gradient, () -> {
                final StringBuilder stops = new StringBuilder();
                for (final Map.Entry<Float, Color> entry : gradient.getColorMap().entrySet()) {
                    stops.append(String.format(
                            "\t\t\t<stop offset=\"%s\" stop-color=\"%s\"%s />%n",
                            convertToString(entry.getKey()),
                            convertToString(entry.getValue()),
                            entry.getValue().alpha() == 1 ? EMPTY_STRING : String.format(" stop-opacity=\"%s\"", convertToString(entry.getValue().alpha()))
                    ));
                }
                if (gradient.getType() == Fill.GradientType.LINEAR) {
                    return add(
                            "linear-gradient-",
                            "linearGradient",
                            stops.toString(),
                            String.format(
                                    "gradientUnits=\"userSpaceOnUse\" x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" ",
                                    convertToString(gradient.getStartX()),
                                    convertToString(gradient.getStartY()),
                                    convertToString(gradient.getEndX()),
                                    convertToString(gradient.getEndY())
                            )
                    );
                } else {
                    return add("radial-gradient-",
                            "radialGradient",
                            stops.toString(),
                            String.format(
                                    "gradientUnits=\"userSpaceOnUse\" cx=\"%s\" cy=\"%s\" r=\"%s\" ",
                                    convertToString(gradient.getStartX()),
                                    convertToString(gradient.getStartY()),
                                    convertToString(Geometries.distance(gradient.getStartX(), gradient.getStartY(), gradient.getEndX(), gradient.getEndY()))
                            )
                    );
                }
            });

        }


        /**
         * @return the SVG definitions as a string
         */
        final String get() {
            if (buffer.isEmpty()) {
                return buffer.toString();
            }
            if (isClosed) {
                return buffer.toString();
            }
            isClosed = true;
            return buffer.append("\t</defs>\n").toString();
        }


        public String addClip(ClipShape shape, double x, double y, double width, double height) {
            int hash = Objects.hash(shape, x, y, width, height);
            switch (shape) {
                case ELLIPSE:
                    final double rx = width * .5;
                    final double ry = height * .5;
                    return add(hash, () -> add(
                            "clipping-path-",
                            "clipPath",
                            String.format("\t\t\t<ellipse cx=\"%s\" cy=\"%s\" rx=\"%s\" ry=\"%s\" />\n",
                                    convertToString(x + rx),
                                    convertToString(y + ry),
                                    convertToString(rx),
                                    convertToString(ry)
                            ),
                            EMPTY_STRING
                            )
                    );
                case RECTANGLE:
                    return add(hash, () -> add(
                            "clipping-path-",
                            "clipPath",
                            String.format("\t\t\t<rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" />\n",
                                    convertToString(x),
                                    convertToString(y),
                                    convertToString(width),
                                    convertToString(height)
                            ),
                            EMPTY_STRING
                            )
                    );
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }
}
