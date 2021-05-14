package net.mahdilamb.dataviz.layouts;

import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.VAlign;

public class YAxis extends XYAxis {
    public YAxis(boolean reversed) {
        this.reversed = reversed;
    }

    public YAxis() {
    }

    @Override
    protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
        final double posX = layout.plotArea.getX(),
                posY = layout.plotArea.getY(),
                sizeX = layout.plotArea.getWidth(),
                sizeY = layout.plotArea.getHeight();
        if (showMinorTicks) {
            canvas.setStroke(minorLineStroke);
            canvas.setStroke(minorLineColor);
            double x0 = posX + sizeX;//TODO check
            double x1 = minorTicksInside ? (x0 + minorTickLength) : (x0 - minorTickLength);
            for (double d = getIterStart(lower, minorTickSpacing); d <= upper; d += minorTickSpacing) {
                double dRev = ((upper - d) / range);
                double e = posY + (dRev * sizeY);
                canvas.strokeLine(x0, e, x1, e);
            }
        }

        double xLabel = posX;
        if (showMajorTicks) {
            canvas.setStroke(majorLineStroke);
            canvas.setStroke(majorLineColor);
            canvas.setFont(labelFont);
            canvas.setFill(labelColor);

            double x0 = xLabel;
            double x1 = majorTicksInside ? (x0 + majorTickLength) : (x0 - majorTickLength);
            xLabel = x0 - labelPadding - majorTickLength;
            for (double d = getIterStart(lower, majorTickSpacing); d <= upper; d += majorTickSpacing) {
                double dRev = ((upper - d) / range);
                double e = posY + (dRev * sizeY);
                canvas.strokeLine(x0, e, x1, e);
            }
        }
        if (showLabels) {
            double yOff = (vLabelAlignment == VAlign.TOP ? 0 : ((vLabelAlignment == VAlign.MIDDLE ? 0.5 : 1) * getTextLineHeight(renderer, labelFont, title.getText()))) - getTextBaselineOffset(renderer, labelFont);
            for (double d = getIterStart(lower, majorTickSpacing); d <= upper; d += majorTickSpacing) {
                double dRev = ((upper - d) / range);
                double e = posY + (dRev * sizeY);
                final String label = getLabel(roundToMajorTick(d));
                double width = getTextWidth(renderer, labelFont, label);
                canvas.fillText(label, xLabel - width, e - yOff);
            }
        }
        if (title.isVisible()) {
            double x = getX() + getTextLineHeight(renderer, title.getFont(), title.getText()) * .5, y = getY();
            canvas.setFont(title.getFont());
            canvas.setFill(title.getColor());
            switch (title.getAlignment()) {
                case CENTER:
                    y += (sizeY * .5);
                    canvas.fillText(title.getText(), x - (getTextWidth(renderer, title.getFont(), title.getText()) * .5), y + (0.5 * (getTextLineHeight(renderer, title.getFont(), title.getText()) - getTextBaselineOffset(renderer, title.getFont()))), -90, x, y);
                    break;
                case LEFT:
                    y += (sizeY);
                    canvas.fillText(title.getText(), x, y + (0.5 * (getTextLineHeight(renderer, title.getFont(), title.getText()) - getTextBaselineOffset(renderer, title.getFont()))), -90, x, y);
                    break;
                case RIGHT:
                    canvas.fillText(title.getText(), x - getTextWidth(renderer, title.getFont(), title.getText()), y + (0.5 * (getTextLineHeight(renderer, title.getFont(), title.getText()) - getTextBaselineOffset(renderer, title.getFont()))), -90, x, y);
                    break;
            }
        }
        if (showAxisLine && axisStroke.getWidth() > 0) {
            canvas.setStroke(axisStroke);
            canvas.setStroke(axisColor);
            canvas.strokeLine(posX, posY, posX, posY + sizeY);
        }

    }

    @Override
    protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        double sizeX = 0;
        if (title.isVisible()) {
            sizeX += getTextLineHeight(renderer, title.getFont(), title.getText()) + titlePadding;
        }
        if (showMajorTicks & !majorTicksInside) {
            sizeX += majorTickLength;
        } else if (showMinorTicks & !minorTicksInside) {
            sizeX += minorTickLength;
        }
        if (showLabels) {
            double start = getIterStart(getMin(), majorTickSpacing);
            double end = getIterEnd(getMax(), majorTickSpacing);
            double width = getTextWidth(renderer, labelFont, getLabel(roundToMajorTick(start)));
            while (start < end) {
                width = Math.max(width, getTextWidth(renderer, labelFont, getLabel(roundToMajorTick(start))));
                start += majorTickSpacing;
            }
            sizeX += width + labelPadding;
        }
        setBoundsFromRect(minX, minY, sizeX, maxY - minY);
    }


    @Override
    protected void drawGrid(XYLayout layout, Renderer renderer, GraphicsBuffer canvas) {
        double x0 = layout.plotArea.getX();
        double x1 = x0 + layout.plotArea.getWidth();

        if (showMajorLine) {
            canvas.setStroke(majorGridStroke);
            canvas.setStroke(majorLineColor);
            for (double d = getIterStart(lower, majorTickSpacing); d <= upper; d += majorTickSpacing) {
                double dRev = ((upper - d) / range);
                double e = layout.plotArea.getY() + (dRev * layout.plotArea.getHeight());
                canvas.strokeLine(x0, e, x1, e);
            }
        }
        if (showMinorLine) {
            canvas.setStroke(minorGridStroke);
            canvas.setStroke(minorLineColor);
            for (double d = getIterStart(lower, minorTickSpacing); d <= upper; d += minorTickSpacing) {
                double dRev = ((upper - d) / range);
                double e = layout.plotArea.getY() + (dRev * layout.plotArea.getHeight());
                canvas.strokeLine(x0, e, x1, e);
            }
        }
        if (showZeroLine && 0 >= lower && 0 <= upper) {
            canvas.setStroke(zeroGridStroke);
            canvas.setStroke(zeroLineColor);
            double dRev = ((upper - 0) / range);
            double e = layout.plotArea.getY() + (dRev * layout.plotArea.getHeight());
            canvas.strokeLine(x0, e, x1, e);
        }
    }

    @Override
    void updateScale() {
        range = upper - lower;
        scale = layout.plotArea.getHeight() / range;
    }

    @Override
    public double getValueFromPosition(double ex) {
        return upper - ((ex - layout.plotArea.getY()) * range / layout.plotArea.getHeight());
    }

    @Override
    public double getPositionFromValue(double v) {
        return layout.plotArea.getY() + (((upper - v) / range) * layout.plotArea.getHeight());
    }
}