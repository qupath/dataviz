package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.utils.Numbers;

import java.text.DecimalFormat;
import java.util.Arrays;

import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;

/**
 * Axis component of a plot
 */
public abstract class Axis extends Component implements Themeable<Axis> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    static {
        DECIMAL_FORMAT.setGroupingUsed(false);
        DECIMAL_FORMAT.setMaximumFractionDigits(6);
    }

    abstract void layout(final Renderer<?> source, PlotLayout.XYLayout rectangular, double minX, double minY, double maxX, double maxY);

    static final class XAxis extends Axis {
        @Override
        public void layout(final Renderer<?> source, PlotLayout.XYLayout rectangular, double minX, double minY, double maxX, double maxY) {
            sizeY = 0;
            if (title.isVisible()) {
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

        }

        @Override
        public double getValueFromPosition(double x) {
            return lower + ((x - posX) * range / sizeX);

        }

        @Override
        public double getPositionFromValue(double val) {
            return posX + ((val - lower) * scale);
        }

        @Override
        public String toString() {
            return String.format("X-axis %s", super.toString());
        }

        @Override
        protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
            //ignored
        }

        @Override
        protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
            //ignored
        }

        @Override
        void drawGrid(Renderer<?> source, ChartCanvas<?> canvas, Axis yAxis) {
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

        @Override
        void updateScale() {
            range = upper - lower;
            scale = sizeX / range;
        }

        @Override
        void drawAxis(ChartCanvas<?> canvas) {
            if (showAxisLine && axisStroke.getWidth() > 0) {
                canvas.setStroke(axisStroke, axisColor);
                canvas.strokeLine(posX, posY, posX + sizeX, posY);
            }
        }

        @Override
        void draw(Renderer<?> source, ChartCanvas<?> canvas, Axis yAxis) {
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
            if (title.isVisible()) {
                double height = getTextLineHeight(source, title.getFont()) - source.getTextBaselineOffset(title.getFont());
                canvas.setFont(title.getFont());
                canvas.setFill(title.getColor());
                double width = 0;
                if (title.getAlignment() != HAlign.LEFT) {
                    width = sizeX - source.getTextWidth(title.getFont(), title.getText());
                    width *= title.getAlignment() == HAlign.CENTER ? 0.5 : 1;
                }
                canvas.fillText(title.getText(), posX + width, posY + sizeY - height);
            }

        }
    }

    static final class YAxis extends Axis {
        @Override
        public void layout(final Renderer<?> source, PlotLayout.XYLayout rectangular, double minX, double minY, double maxX, double maxY) {
            this.posX = minX;
            this.posY = minY;
            sizeX = 0;
            sizeY = 0;
            if (title.isVisible()) {
                sizeX += source.getTextLineHeight(title) + titlePadding;
            }
            if (showMajorTicks & !majorTicksInside) {
                sizeX += majorTickLength;
            } else if (showMinorTicks & !minorTicksInside) {
                sizeX += minorTickLength;
            }
            if (showLabels) {
                double start = getIterStart(lower, majorTickSpacing);
                double end = getIterEnd(upper, majorTickSpacing);
                double width = source.getTextWidth(labelFont, getLabel(start));
                while (start < end) {
                    width = Math.max(width, source.getTextWidth(labelFont, getLabel(start += majorTickSpacing)));
                }
                sizeX += width + labelPadding;
            }
        }

        @Override
        public double getValueFromPosition(double x) {
            return upper - ((x - posY) * range / sizeY);
        }

        @Override
        public double getPositionFromValue(double val) {
            return posY + (((upper - val) / range) * sizeY);
        }

        @Override
        public String toString() {
            return String.format("Y-axis %s", super.toString());
        }

        @Override
        protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
            //ignored
        }

        @Override
        protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
            //ignored
        }

        @Override
        void draw(Renderer<?> source, ChartCanvas<?> canvas, Axis xAxis) {
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
            if (title.isVisible()) {
                double x = posX + source.getTextLineHeight(title) * .5, y = posY;
                canvas.setFont(title.getFont());
                canvas.setFill(title.getColor());
                switch (title.getAlignment()) {
                    case CENTER:
                        y += (sizeY * .5);
                        canvas.fillText(title.getText(), x - (source.getTextWidth(title.getFont(), title.getText()) * .5), y + (0.5 * (source.getTextLineHeight(title.getFont()) - source.getTextBaselineOffset(title.getFont()))), -90, x, y);
                        break;
                    case LEFT:
                        y += (sizeY);
                        canvas.fillText(title.getText(), x, y + (0.5 * (source.getTextLineHeight(title.getFont()) - source.getTextBaselineOffset(title.getFont()))), -90, x, y);
                        break;
                    case RIGHT:
                        canvas.fillText(title.getText(), x - source.getTextWidth(title.getFont(), title.getText()), y + (0.5 * (source.getTextLineHeight(title.getFont()) - source.getTextBaselineOffset(title.getFont()))), -90, x, y);
                        break;
                }
            }

        }

        @Override
        void drawGrid(Renderer<?> source, ChartCanvas<?> canvas, Axis xAxis) {
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

        @Override
        void updateScale() {
            range = upper - lower;
            scale = sizeY / range;
        }

        @Override
        void drawAxis(ChartCanvas<?> canvas) {
            if (showAxisLine && axisStroke.getWidth() > 0) {
                canvas.setStroke(axisStroke, axisColor);
                canvas.strokeLine(posX + sizeX, posY, posX + sizeX, posY + sizeY);
            }
        }
    }

    Figure figure;
    private static final double MAX_TICKS = 10;
    final Title title = new Title(EMPTY_STRING, Font.DEFAULT_TITLE_FONT);
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
    double dataLower = Double.POSITIVE_INFINITY, dataUpper = Double.NEGATIVE_INFINITY, lower = Double.POSITIVE_INFINITY, upper = Double.NEGATIVE_INFINITY;
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

    double scale;
    double range;

    Axis() {

    }

    /**
     * Get the value from the display position
     *
     * @param x the display position
     * @return the value
     */
    public abstract double getValueFromPosition(double x);

    /**
     * Get the position from a value
     *
     * @param val the value
     * @return the display position
     */
    public abstract double getPositionFromValue(double val);

    /**
     * @return the title of the axis
     */
    public String getTitle() {
        return title.getText();
    }

    /**
     * Set the title of the axis
     *
     * @param title the axis title
     * @return this axis
     */
    public Axis setTitle(final String title) {
        this.title.setText(title);
        return this;
    }

    /**
     * Set whether to show major grid lines
     *
     * @param show whether to show major gridlines
     * @return this axis
     */
    public Axis showMajorGridlines(boolean show) {
        this.showMajorLine = show;
        return this;
    }

    /**
     * Set whether to show minor grid lines
     *
     * @param show whether to show minor gridlines
     * @return this axis
     */
    public Axis showMinorGridlines(boolean show) {
        this.showMinorLine = show;
        return this;
    }

    /**
     * Set whether to show the zero line
     *
     * @param show whether to show the zero line
     * @return this axis
     */
    public Axis showZeroLine(boolean show) {
        this.showZeroLine = show;
        return this;
    }

    /**
     * Set the range of the axis
     *
     * @param min the new minimum
     * @param max the new maximum
     * @return this axis
     */
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

    /**
     * @return the current lower bound of the axis
     */
    public double getLowerBound() {
        return lower;
    }

    /**
     * @return the current upper bound of the axis
     */
    public double getUpperBound() {
        return upper;
    }

    /**
     * @return the figure this axis belongs to
     */
    public Figure getFigure() {
        return figure;
    }

    /**
     * Get the label from a value
     *
     * @param val the value
     * @return the label at the value
     */
    protected String getLabel(double val) {
        if (labels != null) {
           if (val >= labels.length || val < 0){
               return EMPTY_STRING;
           }
           return labels[(int) val];
        }
        return DECIMAL_FORMAT.format(val);
    }

    static double getIterStart(double v, double spacing) {
        double remainder = Numbers.mod(v, spacing);
        if (remainder == 0) {
            return v;
        }
        return v - remainder + spacing;
    }

    static double getIterEnd(double v, double spacing) {
        double remainder = Numbers.mod(v, spacing);
        if (remainder == 0) {
            return v;
        }
        return v - remainder;
    }

    abstract void draw(Renderer<?> source, ChartCanvas<?> canvas, Axis otherAxis);

    abstract void drawGrid(Renderer<?> source, ChartCanvas<?> canvas, Axis otherAxis);

    abstract void drawAxis(ChartCanvas<?> canvas);

    abstract void updateScale();

    void reset(boolean useNiceFormatting) {
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

    @Override
    public Axis apply(Theme theme) {
        theme.axis.accept(this);
        return this;
    }

    @Override
    public String toString() {
        return String.format(
                "{%s%s%s%s%s}",
                !title.isVisible() ? "untitled" : String.format("title=\"%s\"", title.getText()),
                formatMmz("ticks", showMajorTicks, showMinorTicks, showZeroTick),
                formatMmz("lines", showMajorLine, showMinorLine, showZeroLine),
                labels == null ? EMPTY_STRING : String.format(", labels=%s", Arrays.toString(labels)),
                String.format(", range=%s:%s::%s::%s", getLowerBound(), getUpperBound(), majorTickSpacing, minorTickSpacing)
        );
    }

    //from https://stackoverflow.com/questions/8506881/nice-label-algorithm-for-charts-with-minimum-ticks#:~:text=For%20example%2C%20if%20the%20data,a%20tick%20spacing%20of%200.05.
    static double niceNum(double range, boolean round) {
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

    private static String formatMmz(final String prefix, boolean major, boolean minor, boolean zero) {
        if (major | minor | zero) {
            return String.format(", %s=%s%s%s", prefix, major ? 'M' : EMPTY_STRING, minor ? 'm' : EMPTY_STRING, zero ? 'z' : EMPTY_STRING);
        }
        return EMPTY_STRING;
    }


}
