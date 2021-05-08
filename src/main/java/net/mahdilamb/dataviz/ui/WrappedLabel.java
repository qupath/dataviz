package net.mahdilamb.dataviz.ui;

import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataframe.utils.IntArrayList;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.HAlign;

import java.util.LinkedList;
import java.util.List;

/**
 * A title is text that takes up space in a layout
 */
public class WrappedLabel extends Label {

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
    public WrappedLabel(String text, Font font) {
        super(text, font);
    }

    @Override
    protected <T>void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
        if (!isVisible()) {
            setBoundsFromRect(minX, minY, 0, 0);
            return;
        }
        xOffsets.clear();
        yOffsets.clear();
        lines.clear();
        lineStarts.clear();
        numLines = 0;
        double maxWidth = maxX - minX - (paddingX * 2);
        double lineHeight = getTextLineHeight(renderer, font,text);
        double baselineOffset = getTextBaselineOffset(renderer, font);
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
            currentWidth += getTextCharWidth(renderer, font, c);
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
        setBoundsFromRect(minX, minY, maxWidth, actualHeight);

    }

    @Override
    protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {
        if (!isVisible()) {
            return;
        }
        canvas.setFill(color);
        canvas.setFont(font);
        for (int i = 0; i < numLines - 1; ++i) {
            canvas.fillText(lines.get(i), getX() + xOffsets.get(i), getY() + yOffsets.get(i));
        }
        if (numLines >= 1) {
            int i = numLines - 1;
            canvas.fillText(lines.get(i), getX() + xOffsets.get(i), getY() + yOffsets.get(i));
        }
    }

}
