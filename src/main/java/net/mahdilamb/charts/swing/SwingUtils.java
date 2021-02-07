package net.mahdilamb.charts.swing;

import net.mahdilamb.charts.graphics.Fill;
import net.mahdilamb.geom2d.geometries.Geometries;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class SwingUtils {
    private SwingUtils() {

    }

    static final Map<net.mahdilamb.charts.graphics.Font, java.awt.Font> fontsToAWT = new HashMap<>();
    static final Map<net.mahdilamb.colormap.Color, java.awt.Color> colorsToAWT = new HashMap<>();


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
        final Color cached = colorsToAWT.get(color);
        if (cached != null) {
            return cached;

        }
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
        final java.awt.Font cached = fontsToAWT.get(font);
        if (cached != null) {
            return cached;
        }
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

    public static double getTextWidth(final FontMetrics fontMetrics, String text) {
        double maxWidth = 0;
        int i = 0;
        int lineStart = 0;
        while (i < text.length()) {
            char c = text.charAt(i++);
            if (c == '\n') {
                maxWidth = Math.max(fontMetrics.stringWidth(text.substring(lineStart, i)), maxWidth);
                lineStart = i;
            }
        }
        if (lineStart < text.length()) {
            maxWidth = Math.max(fontMetrics.stringWidth(text.substring(lineStart)), maxWidth);

        }
        return maxWidth;
    }

    public static void drawMultilineTextLeft(final Graphics2D g, String text, double x, double y, double lineSpacing, double width) {
        drawMultilineText(g, text, x, y, lineSpacing, width, 0);

    }

    public static void drawMultilineTextCenter(final Graphics2D g, String text, double x, double y, double lineSpacing, double width) {
        drawMultilineText(g, text, x, y, lineSpacing, width, .5);
    }

    private static void drawMultilineText(final Graphics2D g, String text, double x, double y, double lineSpacing, double width, double frac) {
        int lineHeight = g.getFontMetrics().getHeight();
        int currentY = 0;
        int i = 0;
        int wordStart = 0;
        while (i < text.length()) {
            char c = text.charAt(i++);
            if (c == '\n') {
                final String line = text.substring(wordStart, i);
                double pad = frac == 0 ? 0 : ((g.getFontMetrics().stringWidth(line) - width) * frac);
                g.drawString(line, SwingUtils.convert(x - pad), currentY + SwingUtils.convert(y));
                currentY += lineHeight * lineSpacing;
                wordStart = i;
            }
        }
        if (wordStart < text.length()) {
            final String line = text.substring(wordStart);
            double pad = frac == 0 ? 0 : ((g.getFontMetrics().stringWidth(line) - width) * frac);
            g.drawString(line, SwingUtils.convert(x - pad), currentY + SwingUtils.convert(y));
        }
    }

    public static void drawMultilineTextRight(final Graphics2D g, String text, double x, double y, double lineSpacing, double width) {
        drawMultilineText(g, text, x, y, lineSpacing, width, 1);
    }

    public static double getLineHeight(FontMetrics fontMetrics) {
        return fontMetrics.getHeight();
    }


}
