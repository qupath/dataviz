package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.utils.Numbers;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;

import java.text.DecimalFormat;
import java.util.Arrays;

import static net.mahdilamb.charts.utils.StringUtils.EMPTY_STRING;

public abstract class Axis extends Component {
    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    static {
        DECIMAL_FORMAT.setGroupingUsed(false);
        DECIMAL_FORMAT.setMaximumFractionDigits(6);
    }

    public abstract void layout(final Renderer<?> source, PlotLayout.Rectangular rectangular, double minX, double minY, double maxX, double maxY);

    static final class XAxis extends Axis {
        @Override
        public void layout(final Renderer<?> source, PlotLayout.Rectangular rectangular, double minX, double minY, double maxX, double maxY) {
            sizeY = 0;
            if (title != null && title.isVisible()) {
                sizeY += source.getTextLineHeight(title) + titlePadding;
            }
            if (showMajorTicks & !majorTicksInside) {
                sizeY += majorTickLength;
            } else if (showMinorTicks & !minorTicksInside) {
                sizeY += minorTickLength;
            }
            if (showLabels) {
                sizeY += source.getTextLineHeight(labelFont) + labelPadding;
            }
            posY = maxY - sizeY;
            sizeX = maxX - posX;
        }

        @Override
        public String toString() {
            return String.format("X-axis %s", super.toString());
        }

        @Override
        protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
            drawBounds(canvas, this);

        }

        void drawXGrid(Renderer<?> source, ChartCanvas<?> canvas, Axis yAxis) {
            double y = posY - yAxis.sizeY;

            if (showMajorLine) {
                canvas.setStroke(majorGridStroke);
                canvas.setStroke(majorLineColor);
                for (double d = getIterStart(lower, majorTickSpacing); d <= upper; d += majorTickSpacing) {
                    double e = posX + ((d - lower) * scale);
                    canvas.strokeLine(e, posY, e, y);
                }
            }
            if (showMinorLine) {
                canvas.setStroke(minorGridStroke);
                canvas.setStroke(minorLineColor);
                for (double d = getIterStart(lower, minorTickSpacing); d <= upper; d += minorTickSpacing) {
                    double e = posX + ((d - lower) * scale);
                    canvas.strokeLine(e, posY, e, y);
                }
            }
            if (showZeroLine && 0 >= lower && 0 <= upper) {
                canvas.setStroke(zeroGridStroke);
                canvas.setStroke(zeroLineColor);
                double e = posX + ((0 - lower) * scale);
                canvas.strokeLine(e, posY, e, y);

            }

        }

        void drawXAxis(Renderer<?> source, ChartCanvas<?> canvas, Axis yAxis) {
            if (showMinorTicks) {
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
                double y = majorTicksInside ? (posY - majorTickLength) : (posY + majorTickLength);
                for (double d = getIterStart(lower, majorTickSpacing); d <= upper; d += majorTickSpacing) {
                    double e = posX + ((d - lower) * scale);
                    canvas.strokeLine(e, posY, e, y);
                }
                if (!majorTicksInside) {
                    yLabel += majorTickLength;
                }
            }
            if (showLabels) {
                yLabel += source.getTextBaselineOffset(labelFont) + labelPadding;
                for (double d = getIterStart(lower, majorTickSpacing); d <= upper; d += majorTickSpacing) {
                    double e = posX + ((d - lower) * scale);
                    final String label = getLabel(d);
                    if (hLabelAlignment != HAlign.LEFT) {
                        e -= source.getTextWidth(labelFont, label) * (hLabelAlignment == HAlign.CENTER ? 0.5 : 1);
                    }
                    canvas.fillText(label, e, yLabel);
                }
            }
            if (title != null && title.isVisible()) {
                double height = source.getTextLineHeight(title) - source.getTextBaselineOffset(title.font);
                canvas.setFont(title.font);
                canvas.setFill(title.color);
                double width = 0;
                if (title.textAlign != HAlign.LEFT) {
                    width = sizeX - source.getTextWidth(title.font, title.text);
                    width *= title.textAlign == HAlign.CENTER ? 0.5 : 1;
                }
                canvas.fillText(title.text, posX + width, posY + sizeY - height);
            }


        }
    }

    static final class YAxis extends Axis {
        @Override
        public void layout(final Renderer<?> source, PlotLayout.Rectangular rectangular, double minX, double minY, double maxX, double maxY) {
            this.posX = minX;
            this.posY = minY;
            sizeX = 0;
            if (title != null && title.isVisible()) {
                sizeX += source.getTextLineHeight(title) + titlePadding;
            }
            if (showMajorTicks & !majorTicksInside) {
                sizeX += majorTickLength;
            } else if (showMinorTicks & !minorTicksInside) {
                sizeX += minorTickLength;
            }
            if (showLabels) {
                final String a = getLabel(getIterStart(lower, majorTickSpacing));
                final String b = getLabel(getIterEnd(upper, majorTickSpacing));
                sizeX += source.getTextWidth(labelFont, StringUtils.longerString(a, b)) + labelPadding;
            }
        }

        @Override
        public String toString() {
            return String.format("Y-axis %s", super.toString());
        }

        @Override
        protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
            drawBounds(canvas, this);
        }

        void drawYAxis(Renderer<?> source, ChartCanvas<?> canvas, Axis xAxis) {
            if (showMinorTicks) {
                canvas.setStroke(minorLineStroke);
                canvas.setStroke(minorLineColor);
                double x0 = posX + sizeX;
                double x1 = minorTicksInside ? (x0 + minorTickLength) : (x0 - minorTickLength);
                for (double d = getIterStart(lower, minorTickSpacing); d <= upper; d += minorTickSpacing) {
                    double dRev = ((upper - d) / range);
                    double e = posY + (dRev * sizeY);
                    canvas.strokeLine(x0, e, x1, e);
                }
            }
            double xLabel = posX + sizeX;
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
                double yOff = (vLabelAlignment == VAlign.TOP ? 0 : ((vLabelAlignment == VAlign.MIDDLE ? 0.5 : 1) * source.getTextLineHeight(labelFont))) - source.getTextBaselineOffset(labelFont);
                for (double d = getIterStart(lower, majorTickSpacing); d <= upper; d += majorTickSpacing) {
                    double dRev = ((upper - d) / range);
                    double e = posY + (dRev * sizeY);
                    final String label = getLabel(d);
                    double width = source.getTextWidth(labelFont, label);
                    canvas.fillText(label, xLabel - width, e - yOff);
                }
            }
            if (title != null && title.isVisible()) {
                double x = posX + source.getTextLineHeight(title) * .5, y = posY;
                canvas.setFont(title.font);
                canvas.setFill(title.color);
                switch (title.textAlign) {
                    case CENTER:
                        y += (sizeY * .5);
                        canvas.fillText(title.text, x - (source.getTextWidth(title.font, title.text) * .5), y + (0.5 * (source.getTextLineHeight(title.font) - source.getTextBaselineOffset(title.font))), -90, x, y);
                        break;
                    case LEFT:
                        y += (sizeY);
                        canvas.fillText(title.text, x, y + (0.5 * (source.getTextLineHeight(title.font) - source.getTextBaselineOffset(title.font))), -90, x, y);
                        break;
                    case RIGHT:
                        canvas.fillText(title.text, x - source.getTextWidth(title.font, title.text), y + (0.5 * (source.getTextLineHeight(title.font) - source.getTextBaselineOffset(title.font))), -90, x, y);
                        break;
                }
            }

        }

        void drawYGrid(Renderer<?> source, ChartCanvas<?> canvas, Axis xAxis) {
            double x0 = posX + sizeX;
            double x1 = x0 + xAxis.sizeX;

            if (showMajorLine) {
                canvas.setStroke(majorGridStroke);
                canvas.setStroke(majorLineColor);
                for (double d = getIterStart(lower, majorTickSpacing); d <= upper; d += majorTickSpacing) {
                    double dRev = ((upper - d) / range);
                    double e = posY + (dRev * sizeY);
                    canvas.strokeLine(x0, e, x1, e);
                }
            }
            if (showMinorLine) {
                canvas.setStroke(minorGridStroke);
                canvas.setStroke(minorLineColor);
                for (double d = getIterStart(lower, minorTickSpacing); d <= upper; d += minorTickSpacing) {
                    double dRev = ((upper - d) / range);
                    double e = posY + (dRev * sizeY);
                    canvas.strokeLine(x0, e, x1, e);
                }
            }
            if (showZeroLine && 0 >= lower && 0 <= upper) {
                canvas.setStroke(zeroGridStroke);
                canvas.setStroke(zeroLineColor);
                double dRev = ((upper - 0) / range);
                double e = posY + (dRev * sizeY);
                canvas.strokeLine(x0, e, x1, e);

            }

        }
    }

    Figure figure;
    private static final double MAX_TICKS = 10;
    Title title = new Title(EMPTY_STRING, Font.DEFAULT_TITLE_FONT);
    boolean fullRange = true;
    boolean showLabels = true;
    boolean majorTicksInside = false,
            minorTicksInside = false;
    double majorTickLength = 5,
            minorTickLength = 3;

    boolean showMajorLine = true,
            showMinorLine = false,
            showZeroLine = true;

    boolean showMajorTicks = true,
            showMinorTicks = false,
            showZeroTick = true;
    String[] labels;
    double dataLower = Double.POSITIVE_INFINITY, dataUpper = Double.NEGATIVE_INFINITY, lower = Double.POSITIVE_INFINITY, upper = Double.NEGATIVE_INFINITY;
    double majorTickSpacing = Double.NaN, minorTickSpacing = Double.NaN;

    double titlePadding = 5;

    Stroke majorGridStroke = new Stroke(1.5),
            minorGridStroke = new Stroke(.75),
            zeroGridStroke = new Stroke(2.5);

    Stroke majorLineStroke = new Stroke(1.5),
            minorLineStroke = new Stroke(.75),
            axisStroke = null;
    Color majorLineColor = Color.white,
            minorLineColor = Color.white,
            zeroLineColor = Color.white,
            axisColor = null;
    Font labelFont = Font.DEFAULT_FONT;
    Paint labelColor = Paint.BLACK_FILL;
    double labelPadding = 2;
    double labelRotation = 0;
    Boundary labelPosition;
    HAlign hLabelAlignment = HAlign.CENTER;
    VAlign vLabelAlignment = VAlign.MIDDLE;

    double scale;
    double range;

    static double getIterStart(double v, double spacing) {
        double remainder = Numbers.mod(v, spacing);
        return v - remainder + spacing;
    }

    static double getIterEnd(double v, double spacing) {
        double remainder = Numbers.mod(v, spacing);
        return v - remainder;
    }

    /**
     * Get the label from a value
     *
     * @param val the value
     * @return the label at the value
     */
    protected String getLabel(double val) {
        return DECIMAL_FORMAT.format(val);

    }

    public String getTitle() {
        return title.getText();
    }

    public Axis setTitle(final String title) {
        this.title.setText(title);
        return this;
    }

    public Axis showMajorGridlines(boolean show) {
        this.showMajorLine = show;
        return this;
    }

    public Axis showMinorGridlines(boolean show) {
        this.showMinorLine = show;
        return this;
    }

    public Axis showZeroGridlines(boolean show) {
        this.showZeroLine = show;
        return this;
    }

    private static String formatMmz(final String prefix, boolean major, boolean minor, boolean zero) {
        if (major | minor | zero) {
            return String.format(", %s=%s%s%s", prefix, major ? 'M' : EMPTY_STRING, minor ? 'm' : EMPTY_STRING, zero ? 'z' : EMPTY_STRING);
        }
        return EMPTY_STRING;
    }

    public double getLower() {
        return lower;
    }

    public double getUpper() {
        return upper;
    }

    public Axis setRange(double min, double max) {
        if (min == max) {
            max += Math.ulp(1);
        }
        lower = Math.min(min, max);
        upper = Math.max(min, max);
        if (figure != null) {
            figure.update();
        }
        return this;
    }

    void reset(boolean useNiceFormatting) {
        if (useNiceFormatting) {
            lower = fullRange ? dataLower : 0;
            upper = dataUpper;
            final double range = niceNum(upper - lower, false);
            majorTickSpacing = niceNum(range / (MAX_TICKS - 1), true);
            minorTickSpacing = majorTickSpacing * .2;
            this.lower = Math.floor(lower / majorTickSpacing) * majorTickSpacing;
            this.upper = Math.ceil(upper / majorTickSpacing) * majorTickSpacing;
            if (!fullRange) {
                lower = 0;
            }
        } else {
            lower = dataLower;
            upper = dataUpper;
        }
    }

    //from https://stackoverflow.com/questions/8506881/nice-label-algorithm-for-charts-with-minimum-ticks#:~:text=For%20example%2C%20if%20the%20data,a%20tick%20spacing%20of%200.05.
    private static double niceNum(double range, boolean round) {
        double exponent; /* exponent of range */
        double fraction; /* fractional part of range */
        double niceFraction; /* nice, rounded fraction */

        exponent = Math.floor(Math.log10(range));
        fraction = range / Math.pow(10, exponent);

        if (round) {
            if (fraction < 1.5) {
                niceFraction = 1;
            } else if (fraction < 3) {
                niceFraction = 2;
            } else if (fraction < 7) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (fraction <= 1) {
                niceFraction = 1;
            } else if (fraction <= 2) {
                niceFraction = 2;
            } else if (fraction <= 5) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }
        return niceFraction * Math.pow(10, exponent);
    }

    @Override
    public String toString() {
        return String.format(
                "{%s%s%s%s%s}",
                !title.isVisible() ? "untitled" : String.format("title=\"%s\"", title.getText()),
                formatMmz("ticks", showMajorTicks, showMinorTicks, showZeroTick),
                formatMmz("lines", showMajorLine, showMinorLine, showZeroLine),
                labels == null ? EMPTY_STRING : String.format(", labels=%s", Arrays.toString(labels)),
                String.format(", range=%s:%s::%s::%s", getLower(), getUpper(), majorTickSpacing, minorTickSpacing)
        );
    }
    void updateYAxisScale() {
        range = upper - lower;
        scale = sizeY / range;

    }

    void updateXAxisScale() {
        range = upper - lower;
        scale = sizeX / range;

    }

}
