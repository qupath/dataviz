package net.mahdilamb.dataviz.layouts;

import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.HAlign;

public class XAxis extends XYAxis {
    public XAxis(boolean reversed) {
        this.reversed = reversed;
    }

    public XAxis() {
    }

    @Override
    protected final void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        double sizeY = 0;
        if (title.isVisible()) {
            sizeY += getTextLineHeight(renderer, title.getFont(), title.getText()) + titlePadding;
        }
        if (showMajorTicks & !majorTicksInside) {
            sizeY += majorTickLength;
        } else if (showMinorTicks & !minorTicksInside) {
            sizeY += minorTickLength;
        }
        if (showLabels) {
            sizeY += getTextLineHeight(renderer, labelFont, title.getText()) + labelPadding;
        }
        setBoundsFromRect(minX, maxY - sizeY, maxX - minX, sizeY);
    }

    @Override
    protected final void drawGrid(XYLayout layout, Renderer renderer, GraphicsBuffer canvas) {
        double y = layout.plotArea.getY() + layout.plotArea.getHeight();

        if (showMajorLine) {
            canvas.setStroke(majorGridStroke);
            canvas.setStroke(majorLineColor);
            for (double d = getIterStart(getMin(), majorTickSpacing); d <= getMax(); d += majorTickSpacing) {
                double e = layout.plotArea.getX() + ((d - lower) * scale);
                canvas.strokeLine(e, layout.plotArea.getY(), e, y);
            }
        }

        if (showMinorLine) {
            canvas.setStroke(minorGridStroke);
            canvas.setStroke(minorLineColor);
            for (double d = getIterStart(lower, minorTickSpacing); d <= upper; d += minorTickSpacing) {
                double e = layout.plotArea.getX() + ((d - lower) * scale);
                canvas.strokeLine(e, layout.plotArea.getY(), e, y);
            }
        }
        if (showZeroLine && 0 >= lower && 0 <= upper) {
            canvas.setStroke(zeroGridStroke);
            canvas.setStroke(zeroLineColor);
            double e = layout.plotArea.getX() + ((0 - lower) * scale);
            canvas.strokeLine(e, layout.plotArea.getY(), e, y);
        }
    }

    @Override
    protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
        final double posX = layout.plotArea.getX(),
                posY = layout.plotArea.getY(),
                sizeX = layout.plotArea.getWidth(),
                sizeY = layout.plotArea.getHeight();
        if (showMinorTicks) {//TODO check
            canvas.setStroke(minorLineStroke);
            canvas.setStroke(minorLineColor);
            double y = minorTicksInside ? (posY - minorTickLength) : (posY + minorTickLength);
            for (double d = getIterStart(lower, minorTickSpacing); d <= upper; d += minorTickSpacing) {
                double e = posX + ((d - lower) * scale);
                canvas.strokeLine(e, posY, e, y);
            }
        }
        double yLabel = posY;
        if (showMajorTicks) {
            canvas.setStroke(majorLineStroke);
            canvas.setStroke(majorLineColor);
            canvas.setFont(labelFont);
            canvas.setFill(labelColor);
            double y = (majorTicksInside ? (posY - majorTickLength) : (posY + majorTickLength)) + sizeY;
            for (double d = getIterStart(lower, majorTickSpacing); d <= upper; d += majorTickSpacing) {
                double e = posX + ((d - lower) * scale);
                canvas.strokeLine(e, posY + sizeY, e, y);
            }
            if (!majorTicksInside) {
                yLabel += majorTickLength;
            }
        }
        if (showLabels) {
            yLabel += getTextBaselineOffset(renderer, labelFont) + labelPadding + sizeY;
            for (double d = getIterStart(lower, majorTickSpacing); d <= upper; d += majorTickSpacing) {
                double e = posX + ((d - lower) * scale);
                final String label = getLabel(d);
                if (hLabelAlignment != HAlign.LEFT) {
                    e -= getTextWidth(renderer, labelFont, label) * (hLabelAlignment == HAlign.CENTER ? 0.5 : 1);
                }
                canvas.fillText(label, e, yLabel);
            }
        }
        if (title.isVisible()) {
            double height = getTextLineHeight(renderer, title.getFont(), title.getText()) - getTextBaselineOffset(renderer, title.getFont());
            canvas.setFont(title.getFont());
            canvas.setFill(title.getColor());
            double width = 0;
            if (title.getAlignment() != HAlign.LEFT) {
                width = sizeX - getTextWidth(renderer, title.getFont(), title.getText());
                width *= title.getAlignment() == HAlign.CENTER ? 0.5 : 1;
            }
            canvas.fillText(title.getText(), posX + width, getY() + getHeight() - height);
        }
        if (showAxisLine && axisStroke.getWidth() > 0) {
            canvas.setStroke(axisStroke);
            canvas.setStroke(axisColor);
            canvas.strokeLine(posX, posY + sizeY, posX + sizeX, posY + sizeY);
        }
    }


    @Override
    void updateScale() {
        range = upper - lower;
        scale = layout.plotArea.getWidth() / range;
    }

    @Override
    public double getValueFromPosition(double ex) {
        return lower + ((ex - layout.plotArea.getX()) * range / layout.plotArea.getWidth());
    }

    @Override
    public double getPositionFromValue(double v) {
        return layout.plotArea.getX() + ((v - lower) * scale);
    }
}