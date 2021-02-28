package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.HAlign;
import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataframe.utils.IntArrayList;

import java.util.LinkedList;
import java.util.List;

/**
 * A title is text that takes up space in a layout
 */
public class WrappedTitle extends Title {

    static final int DEFAULT_NUM_LINES = 3;

    double lineSpacing = 1;

    IntArrayList lineStarts = new IntArrayList(1);
    DoubleArrayList xOffsets = new DoubleArrayList(1);
    DoubleArrayList yOffsets = new DoubleArrayList(1);
    final List<String> lines = new LinkedList<>();
    int numLines;


    /**
     * Create a title with the following details
     *
     * @param text the text
     * @param font the font to use
     */
    public WrappedTitle(String text, Font font) {
        super(text, font);
    }


    @Override
    protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        xOffsets.clear();
        yOffsets.clear();
        lines.clear();
        lineStarts.clear();
        numLines =0;
        double maxWidth = maxX - minX - (paddingX * 2);
        double lineHeight = source.getTextLineHeight(font);
        double baselineOffset = source.getTextBaselineOffset(font);
        double lineWidth = 0;
        double actualWidth = 0;
        int i = 0, j = 0, k = 0;
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
                double lineOffset = textAlign == HAlign.LEFT ? 0 : (getAlignFrac(textAlign) * (maxWidth - lineWidth));
                xOffsets.add(lineOffset);
                yOffsets.add(baselineOffset + (lineSpacing * lineHeight * numLines));
                lines.add(text.substring(k, j));
                lineStarts.add(k);
                numLines++;
                currentWidth = 0;
                k = j;
                i = j;
            }
        }
        if (j < text.length()) {
            double lineOffset = textAlign == HAlign.LEFT ? 0 : (getAlignFrac(textAlign) * (maxWidth - currentWidth));
            xOffsets.add(lineOffset);
            yOffsets.add(baselineOffset + (lineSpacing * lineHeight * numLines));
            lines.add(text.substring(k));
            lineStarts.add(k);
            numLines++;
        }

        double actualHeight = lineSpacing * lineHeight * numLines;

        this.sizeX = maxWidth;
        this.sizeY = actualHeight;
        this.posX = paddingX;
        this.posY = paddingY;

    }

    @Override
    protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
        if (!isVisible()) {
            return;
        }
        canvas.setFont(font);
        for (int i = 0; i < numLines - 1; ++i) {
            canvas.fillText(lines.get(i), posX + xOffsets.get(i), posY + yOffsets.get(i));
        }
        if (numLines >= 1) {
            int i = numLines - 1;
            canvas.fillText(lines.get(i), posX + xOffsets.get(i), posY + yOffsets.get(i));
        }

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
