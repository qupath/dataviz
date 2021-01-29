package net.mahdilamb.charts.io;


import net.mahdilamb.charts.graphics.Fill;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.colormap.Color;

import java.text.DecimalFormat;

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
    public static String convertToString(final SVGDefinitions defs, final Fill fill) {
        if (Fill.isNull(fill)) {
            return "fill:none; ";
        }
        if (fill.isGradient()) {
            return String.format("fill:url('#%s'); ", defs.addGradient(fill.getGradient()));
        } else {
            return String.format("fill:%s; ", convertToString(fill.getColor()));
        }
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
        return String.format("stroke-width:%s; stroke:%s; ", convertToString(stroke.getWidth()), convertToString(stroke.getColor()));
    }

    /**
     * Convert a style to svg-compatible, adding to definitions if needs be
     *
     * @param defs   svg definitions
     * @param fill   fill
     * @param stroke stroke
     * @return style string representing the style
     */
    static String convertToString(final SVGDefinitions defs, final Fill fill, final Stroke stroke) {
        return String.format("style=\"%s%s\" ", convertToString(defs, fill), convertToString(stroke));
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

    static String rectangleToString(double x, double y, double width, double height, final SVGDefinitions defs, final StringBuilder indent, final Stroke stroke, final Fill fill) {
        return String.format(
                "%s<rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" %s/>%n",
                indent.toString(),
                convertToString(x),
                convertToString(y),
                convertToString(width),
                convertToString(height),
                convertToString(defs, fill, stroke)
        );
    }

    static String ellipseToString(double x, double y, double width, double height, final SVGDefinitions defs, final StringBuilder indent, final Stroke stroke, final Fill fill) {
        final double rx = width * .5;
        final double ry = height * .5;
        return String.format(
                "%s<ellipse cx=\"%s\" cy=\"%s\" rx=\"%s\" ry=\"%s\" %s/>%n",
                indent.toString(),
                convertToString(x + rx),
                convertToString(y + ry),
                convertToString(rx),
                convertToString(ry),
                convertToString(defs, fill, stroke)
        );
    }

    static String roundedRectangleToString(double x, double y, double width, double height, double arcWidth, double arcHeight, final SVGDefinitions defs, final StringBuilder indent, final Stroke stroke, final Fill fill) {
        return String.format(
                "%s<rect x=\"%s\" y=\"%s\" rx=\"%s\" ry=\"%s\" width=\"%s\" height=\"%s\" %s/>%n",
                indent.toString(),
                convertToString(x),
                convertToString(y),
                convertToString(arcWidth * .5),
                convertToString(arcHeight * .5),
                convertToString(width),
                convertToString(height),
                convertToString(defs, fill, stroke)
        );

    }

    static String lineToString(double x0, double y0, double x1, double y1, final SVGDefinitions defs, final StringBuilder indent, final Stroke stroke) {
        return String.format(
                "%s<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" %s/>%n",
                indent.toString(),
                convertToString(x0),
                convertToString(y0),
                convertToString(x1),
                convertToString(y1),
                convertToString(defs, null, stroke)
        );
    }

    static String circleToString(double x, double y, double r, final SVGDefinitions defs, final StringBuilder indent, final Stroke stroke, final Fill fill) {
        return String.format(
                "%s<circle cx=\"%s\" cy=\"%s\" r=\"%s\" %s/>%n",
                indent.toString(),
                convertToString(x + r),
                convertToString(y + r),
                convertToString(r),
                convertToString(defs, fill, stroke)
        );
    }

    static String pathToString(String pathD, final SVGDefinitions defs, final StringBuilder indent, final Stroke stroke, final Fill fill) {
        return String.format(
                "%s<path d=\"%s\" %s/>%n",
                indent.toString(),
                pathD,
                convertToString(defs, fill, stroke));
    }

    static String polygonToString(double[] xPoints, double[] yPoints, int numPoints, SVGDefinitions defs, StringBuilder indent, Stroke stroke, Fill fill) {
        final StringBuilder points = new StringBuilder(indent.toString()).append("<polygon points=\"");
        for (int i = 0; i < numPoints; ++i) {
            points.append(String.format("%s,%s ", convertToString(xPoints[i]), convertToString(yPoints[i])));
        }
        return points.append("\" ").append(convertToString(defs, fill, stroke)).append("/>\n").toString();
    }

    static String polylineToString(double[] xPoints, double[] yPoints, int numPoints, SVGDefinitions defs, StringBuilder indent, Stroke stroke) {
        final StringBuilder points = new StringBuilder(indent.toString()).append("<polyline points=\"");
        for (int i = 0; i < numPoints; ++i) {
            points.append(String.format("%s,%s ", convertToString(xPoints[i]), convertToString(yPoints[i])));
        }
        return points.append("\" ").append(convertToString(defs, null, stroke)).append("/>\n").toString();
    }
    public static String textToString(final String text, double x, double y, net.mahdilamb.charts.graphics.Font font, SVGDefinitions defs, StringBuilder indent, Stroke stroke, Fill fill ){
        return String.format(
                "%s<text x=\"%s\" y=\"%s\" %sfont-size=\"%s\" %s%s%s>%s</text>%n",
                indent.toString(),
                convertToString(x),
                convertToString(y),
                convertToString(font.getFamily()),
                convertToString(font.getSize()),
                convertToString(font.getWeight()),
                convertToString(font.getStyle()),
                convertToString(defs, fill, stroke),
                text
        );

    }
}
