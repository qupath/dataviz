package net.mahdilamb.charts.io;


import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Paint;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.colormap.Color;

import java.text.DecimalFormat;
import java.util.Base64;

import static net.mahdilamb.charts.utils.StringUtils.EMPTY_STRING;

/**
 * Utility class for converting an artboard to an SVG
 */
public final class SVGUtils {

    private SVGUtils() {

    }

    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.000000");

    /**
     * Convert color to CSS compatible format
     *
     * @param color color to convert
     * @return CSS compatible representation
     */
    public static String convertToString(final Color color) {
        if (color.alpha() == 1.0) {
            return String.format("rgb(%d,%d,%d)", color.getRed(), color.getGreen(), color.getBlue());
        }
        return String.format("rgba(%d,%d,%d,%s)", color.getRed(), color.getGreen(), color.getBlue(), convertToString(color.alpha()));

    }

    /**
     * Get the shortest representation of a double
     *
     * @param v double to convert
     * @return shortest representation
     */
    public static String convertToString(final double v) {
        if (v == ((long) v)) {
            return Long.toString(Math.round(v));
        }
        String r = DECIMAL_FORMAT.format(v);
        final int dot = r.indexOf('.');

        char z = '0';
        int zero = r.lastIndexOf(z);
        if (zero <= 0 || zero != r.length() - 1) {
            return r;
        }
        //noinspection StatementWithEmptyBody
        while (r.charAt(--zero) == z && zero >= dot) ;
        return r.substring(0, zero + 1);
    }

    /**
     * Convert fill to css style
     *
     * @param defs the svg definition
     * @param fill fill to convert
     * @return SVG representation of the fill
     */
    public static String convertToString(final SVGExporter.SVGDefinitions defs, final Paint fill) {
        if (Paint.isNull(fill)) {
            return "fill:none; ";
        }
        if (fill.isGradient()) {
            return String.format("fill:url('#%s'); ", defs.addGradient(fill.getGradient()));
        } else {
            return String.format("fill:%s; ", convertToString(fill.getColor()));
        }
    }

    private static String convertToString(Stroke.LineJoin join) {
        if (join == Stroke.LineJoin.MITER) {
            return EMPTY_STRING;
        }
        return String.format("stroke-linejoin=\"%s\" ", join == Stroke.LineJoin.BEVEL ? "bevel" : "round");
    }

    private static String convertToString(Stroke.EndCap endCap) {
        if (endCap == Stroke.EndCap.BUTT) {
            return EMPTY_STRING;
        }
        return String.format("stroke-linecap=\"%s\" ", endCap == Stroke.EndCap.ROUND ? "round" : "square");
    }

    private static String convertToString(final String open, double[] arr, final String close) {
        if (arr == null || arr.length == 0) {
            return EMPTY_STRING;
        }
        if (arr.length == 1) {
            return convertToString(arr[0]);
        }
        final StringBuilder sb = new StringBuilder(open);
        for (double v : arr) {
            sb.append(convertToString(v)).append(' ');
        }
        return sb.append(close).toString();

    }

    /**
     * Convert stroke to css style
     *
     * @param stroke stroke to convert
     * @return SVG representation of the stroke
     */
    public static String convertToString(final Stroke stroke) {
        if (stroke == null) {
            return EMPTY_STRING;
        }
        return String.format("stroke-width=\"%s\" %s%s%s%s%s",
                convertToString(stroke.getWidth()),
                convertToString(stroke.getEndCap()),
                convertToString(stroke.getLineJoin()),
                Double.compare(stroke.getMiterLimit(), 4) == 0 ? EMPTY_STRING : String.format("stroke-miterlimit=\"%s\" ", convertToString(stroke.getMiterLimit())),
                Double.compare(stroke.getDashOffset(), 0) == 0 ? EMPTY_STRING : String.format("stroke-dashoffset=\"%s\" ", convertToString(stroke.getDashOffset())),
                stroke.numDashes() == 0 ? EMPTY_STRING : convertToString("stroke-dasharray=\"", stroke.getDashes(), "\" ")

        );
    }

    /**
     * Convert a style to svg-compatible, adding to definitions if needs be
     *
     * @param defs   svg definitions
     * @param fill   fill
     * @param stroke stroke
     * @return style string representing the style
     */
    static String convertToString(final SVGExporter.SVGDefinitions defs, final Paint fill, final Stroke stroke, final Color strokeColor) {
        return String.format("style=\"%s%s\" %s", convertToString(defs, fill), strokeColor == null ? EMPTY_STRING : String.format("stroke:%s", convertToString(strokeColor)), convertToString(stroke));
    }

    /**
     * Convert font style to svg attribute
     *
     * @param style style to convert
     * @return svg attribute
     */
    public static String convertToString(final Font.Style style) {
        if (style == Font.Style.NORMAL) {
            return EMPTY_STRING;
        }
        return "font-style=\"italic\" ";
    }

    /**
     * Convert font-weight to svg attribute
     *
     * @param weight font weight to convert
     * @return svg attribute
     */
    public static String convertToString(final Font.Weight weight) {
        if (weight == Font.Weight.NORMAL) {
            return EMPTY_STRING;
        }
        return "font-weight=\"bold\" ";
    }

    /**
     * Convert font-family to svg attribute
     *
     * @param family font family to convert
     * @return svg attribute
     */
    public static String convertToString(final Font.Family family) {
        switch (family) {
            case MONOSPACE:
                return "font-family=\"monospace\" ";
            case SANS_SERIF:
                return "font-family=\"sans-serif\" ";
            default:
                return "font-family=\"serif\" ";
        }
    }

    /**
     * Convert a rectangle to an SVG element representation
     *
     * @param x      top-left x of rectangle
     * @param y      top-left y of rectangle
     * @param width  width of rectangle
     * @param height height of rectangle
     * @param defs   the SVG definitions to add to, if required
     * @param indent the current indent
     * @param stroke the stroke of the rectangle
     * @param fill   the fill of the rectangle
     * @return the SVG element of the rectangle
     */
    static String rectangleToString(double x, double y, double width, double height, final SVGExporter.SVGDefinitions defs, final StringBuilder indent, final Stroke stroke, final Paint fill, final Color strokeColor) {
        return String.format(
                "%s<rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" %s/>%n",
                indent.toString(),
                convertToString(x),
                convertToString(y),
                convertToString(width),
                convertToString(height),
                convertToString(defs, fill, stroke, strokeColor)
        );
    }

    /**
     * Convert an ellipse to an SVG element
     *
     * @param x      top-left x of ellipse
     * @param y      top-left y of ellipse
     * @param width  width of ellipse
     * @param height height of ellipse
     * @param defs   the SVG definitions to add to, if required
     * @param indent the current indent
     * @param stroke the stroke of the ellipse
     * @param fill   the fill of the ellipse
     * @return the SVG element of the ellipse
     */
    static String ellipseToString(double x, double y, double width, double height, final SVGExporter.SVGDefinitions defs, final StringBuilder indent, final Stroke stroke, final Paint fill, final Color strokeColor) {
        final double rx = width * .5;
        final double ry = height * .5;
        return String.format(
                "%s<ellipse cx=\"%s\" cy=\"%s\" rx=\"%s\" ry=\"%s\" %s/>%n",
                indent.toString(),
                convertToString(x + rx),
                convertToString(y + ry),
                convertToString(rx),
                convertToString(ry),
                convertToString(defs, fill, stroke, strokeColor)
        );
    }

    /**
     * Convert a rounded rectangle to an SVG element representation
     *
     * @param x         top-left x of rounded rectangle
     * @param y         top-left y of rounded rectangle
     * @param width     width of rounded rectangle
     * @param height    height of rounded rectangle
     * @param arcHeight the arc-height of the rounded rectangle
     * @param arcWidth  the arc-width of the rounded rectangle
     * @param defs      the SVG definitions to add to, if required
     * @param indent    the current indent
     * @param stroke    the stroke of the rounded rectangle
     * @param fill      the fill of the rounded rectangle
     * @return the SVG element of the rounded rectangle
     */
    static String roundedRectangleToString(double x, double y, double width, double height,
                                           double arcWidth, double arcHeight, final SVGExporter.SVGDefinitions defs, final StringBuilder indent, final Stroke stroke, final Paint fill, final Color strokeColor) {
        return String.format(
                "%s<rect x=\"%s\" y=\"%s\" rx=\"%s\" ry=\"%s\" width=\"%s\" height=\"%s\" %s/>%n",
                indent.toString(),
                convertToString(x),
                convertToString(y),
                convertToString(arcWidth * .5),
                convertToString(arcHeight * .5),
                convertToString(width),
                convertToString(height),
                convertToString(defs, fill, stroke, strokeColor)
        );
    }

    /**
     * Convert a line to an SVG element
     *
     * @param x0     the start x
     * @param y0     the start y
     * @param x1     the end x
     * @param y1     the end y
     * @param defs   the SVG definitions to add to, if required
     * @param indent the current indent
     * @param stroke the stroke of the rounded rectangle
     * @return an SVG element representation of the line
     */
    static String lineToString(double x0, double y0, double x1, double y1, final SVGExporter.SVGDefinitions defs, final StringBuilder indent, final Stroke stroke, final Color strokeColor) {
        return String.format(
                "%s<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" %s/>%n",
                indent.toString(),
                convertToString(x0),
                convertToString(y0),
                convertToString(x1),
                convertToString(y1),
                convertToString(defs, null, stroke, strokeColor)
        );
    }

    /**
     * Convert a circle to an SVG representation
     *
     * @param x      the top-left x
     * @param y      the top-left x
     * @param r      the radius
     * @param defs   the SVG definitions to add to, if required
     * @param indent the current indent
     * @param stroke the stroke of the rounded rectangle
     * @param fill   the fill of the rounded rectangle
     * @return an SVG element representation of a circle
     */
    static String circleToString(double x, double y, double r, final SVGExporter.SVGDefinitions defs, final StringBuilder indent, final Stroke stroke, final Paint fill, final Color strokeColor) {
        return String.format(
                "%s<circle cx=\"%s\" cy=\"%s\" r=\"%s\" %s/>%n",
                indent.toString(),
                convertToString(x + r),
                convertToString(y + r),
                convertToString(r),
                convertToString(defs, fill, stroke, strokeColor)
        );
    }

    static String pathToString(String pathD, final SVGExporter.SVGDefinitions defs, final StringBuilder indent, final Stroke stroke, final Paint fill, final Color strokeColor) {
        return String.format(
                "%s<path d=\"%s\" %s/>%n",
                indent.toString(),
                pathD,
                convertToString(defs, fill, stroke, strokeColor));
    }

    static String polygonToString(double[] xPoints, double[] yPoints, int numPoints, SVGExporter.SVGDefinitions defs, StringBuilder indent, Stroke stroke, Paint fill, final Color strokeColor) {
        final StringBuilder points = new StringBuilder(indent.toString()).append("<polygon points=\"");
        for (int i = 0; i < numPoints; ++i) {
            points.append(String.format("%s,%s ", convertToString(xPoints[i]), convertToString(yPoints[i])));
        }
        return points.append("\" ").append(convertToString(defs, fill, stroke, strokeColor)).append("/>\n").toString();
    }

    static String polylineToString(double[] xPoints, double[] yPoints, int numPoints, SVGExporter.SVGDefinitions defs, StringBuilder indent, Stroke stroke, final Color strokeColor) {
        final StringBuilder points = new StringBuilder(indent.toString()).append("<polyline points=\"");
        for (int i = 0; i < numPoints; ++i) {
            points.append(String.format("%s,%s ", convertToString(xPoints[i]), convertToString(yPoints[i])));
        }
        return points.append("\" ").append(convertToString(defs, null, stroke, strokeColor)).append("/>\n").toString();
    }

    public static String textToString(final String text, double x, double y, net.mahdilamb.charts.graphics.Font font, SVGExporter.SVGDefinitions defs, StringBuilder indent, Stroke stroke, Paint fill, final Color strokeColor) {
        return String.format(
                "%s<text x=\"%s\" y=\"%s\" %sfont-size=\"%s\" %s%s%s>%s</text>%n",
                indent.toString(),
                convertToString(x),
                convertToString(y),
                convertToString(font.getFamily()),
                convertToString(font.getSize()),
                convertToString(font.getWeight()),
                convertToString(font.getStyle()),
                convertToString(defs, fill, stroke, strokeColor),
                text
        );

    }

    public static String rotatedTextToString(final String text, double x, double y, double rotationDegrees, double pivotX, double pivotY, net.mahdilamb.charts.graphics.Font font, SVGExporter.SVGDefinitions defs, StringBuilder indent, Stroke stroke, Paint fill, final Color strokeColor) {
        return String.format(
                "%s<text x=\"%s\" y=\"%s\" %sfont-size=\"%s\" %s%s%s transform=\"rotate(%s,%s,%s)\">%s</text>%n",
                indent.toString(),
                convertToString(x),
                convertToString(y),
                convertToString(font.getFamily()),
                convertToString(font.getSize()),
                convertToString(font.getWeight()),
                convertToString(font.getStyle()),
                convertToString(defs, fill, stroke, strokeColor),
                convertToString(rotationDegrees),
                convertToString(pivotX),
                convertToString(pivotY),
                text
        );

    }

    public static String imageToString(final byte[] bytes, double x, double y, double width, double height, StringBuilder indent) {
        return String.format("%s<image x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" xlink:href=\"data:image/png;base64,%s\" />%n",
                indent,
                convertToString(x),
                convertToString(y),
                convertToString(width),
                convertToString(height),
                Base64.getMimeEncoder().encodeToString(bytes)
        );
    }
}
