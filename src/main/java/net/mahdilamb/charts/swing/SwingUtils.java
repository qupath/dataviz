package net.mahdilamb.charts.swing;

import net.mahdilamb.charts.graphics.Fill;
import net.mahdilamb.geom2d.geometries.Geometries;
import net.mahdilamb.geom2d.geometries.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

final class SwingUtils {
    private SwingUtils() {

    }

    /**
     * Convert a double to int
     *
     * @param value the value to convert
     * @return the rounded value
     */
    public static int convert(final double value) {
        return (int) Math.round(value);
    }

    /**
     * Convert an abstract color to an AWT color
     *
     * @param color the abstract color
     * @return the AWT color
     */
    public static java.awt.Color convert(final net.mahdilamb.colormap.Color color) {
        return new java.awt.Color(color.red(), color.green(), color.blue(), color.alpha());
    }

    /**
     * Convert a color to its abstract representation
     *
     * @param color the AWT color
     * @return an abstract color
     */
    public static Color convert(final java.awt.Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * Convert a 2D coordinate to an AWT point
     *
     * @param coordinate the coordinate to convert
     * @return the output point
     */
    public static Point2D convertToShape(final Point coordinate) {
        return convertToShape(new Point2D.Double(), coordinate);
    }

    public static Point2D convertToShape(Point2D out, final Point coordinate) {
        out.setLocation(coordinate.getX(), coordinate.getY());
        return out;
    }

    /**
     * Convert a gradient to an AWT gradient
     *
     * @param gradient the source gradient
     * @return AWT gradient
     */
    public static MultipleGradientPaint convert(final Fill.Gradient gradient) {
        final Point2D start = new Point2D.Double(gradient.getStartX(), gradient.getStartY());
        final float[] dist = new float[gradient.getColorMap().size()];
        final java.awt.Color[] colors = new java.awt.Color[dist.length];
        int i = 0;
        for (final Map.Entry<Float, net.mahdilamb.colormap.Color> entry : gradient.getColorMap().entrySet()) {
            dist[i] = entry.getKey();
            colors[i] = convert(entry.getValue());
            ++i;
        }
        if (gradient.getType() == Fill.GradientType.LINEAR) {
            final Point2D end = new Point2D.Double(gradient.getEndX(), gradient.getEndY());
            return new LinearGradientPaint(start, end, dist, colors);
        } else {
            return new RadialGradientPaint(start, (float) Geometries.distance(gradient.getStartX(), gradient.getStartY(), gradient.getEndX(), gradient.getEndY()), dist, colors);
        }
    }


    static Color convert(ChartSwing.ModifiableAWTColor dest, net.mahdilamb.colormap.Color source) {
        dest.r = source.red();
        dest.g = source.green();
        dest.b = source.blue();
        dest.a = source.alpha();
        return dest;
    }

    /**
     * Convert a buffered image to a PNG byte array
     *
     * @param image image to convert
     * @return png byte array representation
     */
    public static byte[] convertToByteArray(final BufferedImage image) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    /**
     * Convert generic font to AWY font
     *
     * @param font generic font
     * @return AWT font
     */
    public static java.awt.Font convert(net.mahdilamb.charts.graphics.Font font) {
        final String family;
        switch (font.getFamily()) {
            case SERIF:
                family = Font.SERIF;
                break;
            case MONOSPACE:
                family = Font.MONOSPACED;
                break;
            default:
                family = Font.SANS_SERIF;
                break;
        }

        int style = Font.PLAIN;
        if (font.getStyle() == net.mahdilamb.charts.graphics.Font.Style.ITALIC) {
            style |= Font.ITALIC;
        }
        if (font.getWeight() == net.mahdilamb.charts.graphics.Font.Weight.BOLD) {
            style |= Font.BOLD;
        }

        return new Font(family, style, convert(font.getSize()));
    }


}
