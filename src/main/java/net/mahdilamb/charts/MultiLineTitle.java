package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.HAlign;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A title is text that takes up space in a layout
 */
public class MultiLineTitle extends Title {

    static final int DEFAULT_NUM_LINES = 3;

    double lineSpacing = 1;

    int[] lineStarts = new int[1];
    double[] xOffsets = new double[1];
    double[] yOffsets = new double[1];
    final List<String> lines = new LinkedList<>();
    int numLines;


    /**
     * Create a title with the following details
     *
     * @param text the text
     * @param font the font to use
     */
    public MultiLineTitle(String text, Font font) {
        super(text, font);
    }

    private double getAlignFrac() {
        switch (textAlign) {
            case CENTER:
                return 0.5;
            case RIGHT:
                return 1;
            case LEFT:
                return 0;
            default:
                throw new UnsupportedOperationException();
        }
    }


    @Override
    protected void layout(ChartCanvas<?> canvas, Chart<?, ?> source, double minX, double minY, double maxX, double maxY) {
        if (!isVisible()) {
            return;
        }
        canvas.setFont(font);
        if (!metricsSet) {
            calculateBounds(canvas, source, minX, minY, maxX, maxY);
            this.boundsX = minX;
            this.boundsY = minY;

        }
        for (int i = 0; i < numLines - 1; ++i) {
            canvas.fillText(lines.get(i), minX + xOffsets[i], minY + yOffsets[i]);
        }
        if (numLines >= 1) {
            int i = numLines - 1;
            canvas.fillText(lines.get(i), minX + xOffsets[i], minY + yOffsets[i]);
        }

    }

    @Override
    protected void calculateBounds(ChartCanvas<?> canvas, Chart<?, ?> source, double minX, double minY, double maxX, double maxY) {
        double maxWidth = maxX - minX;
        double lineHeight = source.getTextLineHeight(font);
        double baselineOffset = source.getTextBaselineOffset(font);
        double xOffset = paddingX * .5;
        double yOffset = baselineOffset + paddingY * .5;
        double lineWidth = 0;
        double actualWidth = 0;
        int i = 0, j = 0, k = 0, lineI = 0;
        double currentWidth = 0;
        while (i < text.length()) {
            final char c = text.charAt(i++);
            if (Character.isWhitespace(c)) {
                j = i;
                lineWidth = currentWidth;
            }
            currentWidth += source.getCharWidth(font, c);
            if (currentWidth > maxWidth || c == '\n') {
                actualWidth = Math.max(actualWidth, lineWidth);
                double lineOffset = textAlign == HAlign.LEFT ? 0 : (getAlignFrac() * (maxWidth - lineWidth));
                xOffsets = ensureCapacity(xOffsets, lineI + 1);
                xOffsets[lineI] = lineOffset + xOffset;
                yOffsets = ensureCapacity(yOffsets, xOffsets.length);
                yOffsets[lineI] = yOffset + (lineSpacing * lineHeight * lineI);
                lines.add(text.substring(k, j));
                lineStarts = ensureCapacity(lineStarts, xOffsets.length);
                lineStarts[lineI++] = k;
                currentWidth = 0;
                k = j;
                i = j;
            }
        }
        if (j < text.length()) {
            actualWidth = Math.max(actualWidth, currentWidth);
            double lineOffset = textAlign == HAlign.LEFT ? 0 : (getAlignFrac() * (maxWidth - currentWidth));
            xOffsets = ensureCapacity(xOffsets, lineI + 1);
            xOffsets[lineI] = lineOffset + xOffset;
            yOffsets = ensureCapacity(yOffsets, yOffsets.length);
            yOffsets[lineI] = yOffset + (lineSpacing * lineHeight * lineI);
            lines.add(text.substring(k));
            lineStarts = ensureCapacity(lineStarts, yOffsets.length);
            lineStarts[lineI++] = k;
        }
        double actualHeight = lineSpacing * lineHeight * lineI;
        numLines = lineI;

        this.boundsWidth = actualWidth + paddingX;
        this.boundsHeight = actualHeight + paddingY;
    }

    static int[] ensureCapacity(int[] array, int maxIndex) {
        if (array == null) {
            return new int[Math.max(DEFAULT_NUM_LINES, maxIndex)];
        }
        if (maxIndex < array.length) {
            return array;
        }
        int newLength = Math.floorDiv(maxIndex + 4, 4) * 4;
        int[] result = new int[newLength];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;

    }

    static double[] ensureCapacity(double[] array, int maxIndex) {
        if (array == null) {
            return new double[Math.max(DEFAULT_NUM_LINES, maxIndex)];
        }
        if (maxIndex < array.length) {
            return array;
        }
        int newLength = Math.floorDiv(maxIndex + 4, 4) * 4;
        double[] result = new double[newLength];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;

    }


}
