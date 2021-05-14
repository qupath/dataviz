package net.mahdilamb.dataviz.graphics;

import net.mahdilamb.dataviz.swing.SwingUtils;

import java.awt.*;
import java.util.Map;
import java.util.WeakHashMap;

public final class FontUtils {
    private FontUtils() {

    }
    static final Map<Font, java.awt.Font> fontsToAWT = new WeakHashMap<>();

    /**
     * Convert generic font to AWY font
     *
     * @param font generic font
     * @return AWT font
     */
    public static java.awt.Font convert(net.mahdilamb.dataviz.graphics.Font font) {
        final java.awt.Font cached = fontsToAWT.get(font);
        if (cached != null) {
            return cached;
        }
        final String family;
        switch (font.getFamily()) {
            case SERIF:
                family = java.awt.Font.SERIF;
                break;
            case MONOSPACE:
                family = java.awt.Font.MONOSPACED;
                break;
            default:
                family = java.awt.Font.SANS_SERIF;
                break;
        }

        int style = java.awt.Font.PLAIN;
        if (font.getStyle() == net.mahdilamb.dataviz.graphics.Font.Style.ITALIC) {
            style |= java.awt.Font.ITALIC;
        }
        if (font.getWeight() == net.mahdilamb.dataviz.graphics.Font.Weight.BOLD) {
            style |= java.awt.Font.BOLD;
        }
        return new java.awt.Font(family, style, SwingUtils.convert(font.getSize()));
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

    public static double drawMultilineTextLeft(final Graphics2D g, String text, double x, double y, double lineSpacing, double width) {
        return drawMultilineText(g, text, x, y, lineSpacing, width, 0);

    }

    public static double drawMultilineTextCenter(final Graphics2D g, String text, double x, double y, double lineSpacing, double width) {
        return drawMultilineText(g, text, x, y, lineSpacing, width, .5);
    }

    private static double drawMultilineText(final Graphics2D g, String text, double x, double y, double lineSpacing, double width, double frac) {
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
        return currentY;
    }

    public static double getLineHeight(final FontMetrics fontMetrics, String text, double lineSpacing) {
        int lineHeight = fontMetrics.getHeight();
        if (text == null) {
            return lineHeight;
        }
        int currentY = 0;
        int i = 0;
        int wordStart = 0;
        while (i < text.length()) {
            char c = text.charAt(i++);
            if (c == '\n') {
                currentY += lineHeight * lineSpacing;
                wordStart = i;
            }
        }
        if (wordStart < text.length()) {
            currentY += lineHeight * lineSpacing;
        }
        currentY -= (lineSpacing - 1) * lineHeight;

        return currentY;
    }

    public static void drawMultilineTextRight(final Graphics2D g, String text, double x, double y, double lineSpacing, double width) {
        drawMultilineText(g, text, x, y, lineSpacing, width, 1);
    }

    public static double getLineHeight(FontMetrics fontMetrics) {
        return fontMetrics.getHeight();
    }

}
