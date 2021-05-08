package net.mahdilamb.dataviz.layouts;

import net.mahdilamb.dataviz.PlotAxis;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.utils.Numbers;

import java.awt.*;

public abstract class XYAxis extends PlotAxis<XYLayout> {


    public static final class XAxis extends XYAxis {
        public XAxis(boolean reversed) {
            this.reversed = reversed;
        }

        public XAxis() {
        }

        @Override
        protected final <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
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
        protected final <T> void drawGrid(XYLayout layout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {
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
        protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {
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
        protected String getLabel(double value) {
            return Double.toString(Numbers.approximateDouble(value));
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

    public static final class YAxis extends XYAxis {
        public YAxis(boolean reversed) {
            this.reversed = reversed;
        }

        public YAxis() {
        }

        @Override
        protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {
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
                    final String label = getLabel(d);
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
        protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
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
                double width = getTextWidth(renderer, labelFont, getLabel(start));
                while (start < end) {
                    width = Math.max(width, getTextWidth(renderer, labelFont, getLabel(start += majorTickSpacing)));
                }
                sizeX += width + labelPadding;
            }
            setBoundsFromRect(minX, minY, sizeX, maxY - minY);
        }


        @Override
        protected <T> void drawGrid(XYLayout layout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {
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
        protected String getLabel(double value) {
            return Double.toString(Numbers.approximateDouble(value));
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

    private static final double MAX_TICKS = 10;
    boolean fullRange = true;
    boolean showLabels = true;
    boolean majorTicksInside = false,
            minorTicksInside = false;

    double majorTickLength = 5,
            minorTickLength = 3;

    boolean showMajorLine = true,
            showMinorLine = false,
            showZeroLine = true,
            showAxisLine = true;

    boolean showMajorTicks = true,
            showMinorTicks = false,
            showZeroTick = true;
    String[] labels;
    boolean reversed = false;
    double lower = Double.POSITIVE_INFINITY, upper = Double.NEGATIVE_INFINITY;

    double majorTickSpacing = Double.NaN, minorTickSpacing = Double.NaN;

    double titlePadding = 5;

    Stroke majorGridStroke = new Stroke(1.5),
            minorGridStroke = new Stroke(.75),
            zeroGridStroke = new Stroke(2.5);

    Stroke majorLineStroke = majorGridStroke,
            minorLineStroke = minorGridStroke,
            axisStroke = majorGridStroke;
    Color majorLineColor = Color.white,
            minorLineColor = Color.white,
            zeroLineColor = Color.white,
            axisColor = Color.black;
    Font labelFont = Font.DEFAULT_FONT;
    Color labelColor = Color.BLACK;
    double labelPadding = 2;
    double labelRotation = 0;
    Boundary labelPosition;
    HAlign hLabelAlignment = HAlign.CENTER;
    VAlign vLabelAlignment = VAlign.MIDDLE;

    double range;

    public final double getMin() {
        return lower;
    }

    public final double getMax() {
        return upper;
    }

    abstract void updateScale();

    void reset(boolean useNiceFormatting, double dataLower, double dataUpper) {
        if (useNiceFormatting) {
            lower = fullRange ? dataLower : 0;
            upper = dataUpper;
            final double range = niceNum(upper - lower, false);
            majorTickSpacing = niceNum(range / (MAX_TICKS - 1), true);
            minorTickSpacing = majorTickSpacing * .2;
            this.lower = Math.floor(lower / majorTickSpacing) * majorTickSpacing;
            this.upper = Math.ceil(upper / majorTickSpacing) * majorTickSpacing;
        } else {
            lower = dataLower;
            upper = dataUpper;
            final double range = upper - lower;
            majorTickSpacing = niceNum(range / (MAX_TICKS - 1), true);
            minorTickSpacing = majorTickSpacing * .2;
        }
        if (!fullRange) {
            lower = 0;
        }
        updateScale();
    }

    public abstract double getValueFromPosition(double v);

    public abstract double getPositionFromValue(double v);


}