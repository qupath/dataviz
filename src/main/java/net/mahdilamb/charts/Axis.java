package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.geom2d.utils.Numbers;

import java.text.DecimalFormat;

public final class Axis extends ChartComponent {
    public enum Mode {
        LINEAR,
        LOGARITHMIC,
        CATEGORICAL
    }

    final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    static {
        DECIMAL_FORMAT.setGroupingUsed(false);
        DECIMAL_FORMAT.setMaximumFractionDigits(6);
    }

    Mode mode = Mode.LINEAR;
    double minorTickSpacing,
            majorTickSpacing;

    boolean showMajorTicks = true,
            showMinorTicks = false,
            showLabels = true,
            showMajorGridLines = true,
            showMinorGridLines = false,
            showZeroLine = true;
    boolean majorTicksInside = false,
            minorTicksInside = false;
    double majorTickLength = 5,
            minorTickLength = 3;

    double lowerLimit, upperLimit;
    boolean reversed = false;
    boolean autoRanged = true;
    boolean fullRange = true;
    double currentLowerBound, currentUpperBound;

    Title title;
    double titlePadding = 5;

    Stroke majorGridStroke = new Stroke(Color.white, 1.5),
            minorGridStroke = new Stroke(Color.white, .75),
            zeroGridStroke = new Stroke(Color.white, 2.25);

    Stroke majorLineStroke = new Stroke(Color.black, 1.5),
            minorLineStroke = new Stroke(Color.black, .75),
            axisStroke = null;

    Font labelFont = Font.DEFAULT_FONT;
    Fill labelColor = Fill.BLACK_FILL;
    double labelPadding = 2;
    double labelRotation = 0;
    Boundary labelPosition;
    HAlign hLabelAlignment = HAlign.CENTER;
    VAlign vLabelAlignment = VAlign.MIDDLE;
    double[] tickPositions;
    String[] tickLabels;

    double scale;
    double range;

    private static final int MAX_TICKS = 10;
    private static final double TICK_BUFFER = 0.05;

    /**
     * Create a new axis with the given initial min and max
     *
     * @param label the title of the axis
     * @param min   the initial min of the range
     * @param max   the initial max of the range
     */
    Axis(final String label, double min, double max) {
        this.title = new Title(label, Font.DEFAULT_TITLE_FONT);
        this.lowerLimit = min;
        this.upperLimit = max;
        updateRangeToBest();

    }

    void updateRangeToBest() {

        currentLowerBound = fullRange ? lowerLimit : 0;
        currentUpperBound = upperLimit;
        final double range = niceNum(currentUpperBound - currentLowerBound, false);
        majorTickSpacing = niceNum(range / (MAX_TICKS - 1), true);
        minorTickSpacing = majorTickSpacing * .2;
        this.currentLowerBound = Math.floor(currentLowerBound / majorTickSpacing) * majorTickSpacing;
        this.currentUpperBound = Math.ceil(currentUpperBound / majorTickSpacing) * majorTickSpacing;
        final double buffered = (upperLimit - lowerLimit) * TICK_BUFFER;
        if (!fullRange) {
            currentLowerBound = 0;
        } else {
            currentLowerBound -= buffered;
        }
        currentUpperBound += buffered;

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

    public Axis setAxisMode(final Mode mode) {
        this.mode = mode;
        return redraw();
    }

    public Axis setTitle(String newTitle) {
        title.setTitle(newTitle);
        return redraw();
    }

    public Axis setTitleFont(final Font font) {
        title.setFont(font);
        return redraw();
    }

    public Axis setTitleColor(final Color color) {
        title.color = color;
        return redraw();
    }

    public Axis showTitle(boolean visible) {
        title.setVisible(visible);
        return redraw();
    }

    public Axis setMajorGridStroke(final Stroke stroke) {
        majorGridStroke = stroke;
        return redraw();
    }

    public Axis setMajorTickStroke(final Stroke stroke) {
        majorLineStroke = stroke;
        return redraw();
    }

    public Axis showMajorTicks(boolean visible) {
        this.showMajorTicks = visible;
        return redraw();
    }

    public Axis setMajorTickSpacing(double majorTickSpacing) {
        tickLabels = null;
        tickPositions = null;
        minorTickSpacing = Math.min(minorTickSpacing, majorTickSpacing);
        this.majorTickSpacing = majorTickSpacing;
        return redraw();
    }

    public Axis setMajorTickLabels(double[] tickPositions, String[] tickLabels) {
        this.tickPositions = tickPositions;
        this.tickLabels = tickLabels;
        return redraw();
    }

    public Axis setMajorTicksInside() {
        majorTicksInside = true;
        return redraw();
    }

    public Axis setMajorTicksOutside() {
        majorTicksInside = false;
        return redraw();
    }

    public Axis showMajorGridLines(boolean visible) {
        showMajorGridLines = visible;
        return redraw();
    }

    public Axis setMinorGridStroke(final Stroke stroke) {
        minorGridStroke = stroke;
        return redraw();
    }

    public Axis setMinorTickStroke(final Stroke stroke) {
        minorLineStroke = stroke;
        return redraw();
    }

    public Axis showMinorGridLines(boolean visible) {
        showMinorGridLines = visible;
        return redraw();
    }

    public Axis showZeroLine(boolean visible) {
        showZeroLine = visible;
        return redraw();
    }

    public Axis setZeroLineStroke(final Stroke stroke) {
        this.zeroGridStroke = stroke;
        return redraw();
    }

    public Axis setMinorTicksInside() {
        minorTicksInside = true;
        return redraw();
    }

    public Axis setMinorTicksOutside() {
        minorTicksInside = false;
        return redraw();
    }

    public Axis showMinorTicks(boolean visible) {
        this.showMinorTicks = visible;
        return redraw();
    }

    public Axis setLabelAlignment(final HAlign alignment) {
        this.hLabelAlignment = alignment;
        return redraw();
    }

    public Axis setLabelPosition(final Boundary boundary) {
        this.labelPosition = boundary;
        return redraw();
    }

    public Axis setLabelFont(final Font font) {
        this.labelFont = font;
        return redraw();
    }

    public Axis setLabelRotation(final double rotationDegrees) {
        this.labelRotation = rotationDegrees;
        return redraw();
    }

    public Axis showLabels(boolean visible) {
        showLabels = visible;
        return redraw();
    }

    public Axis setAxisStroke(final Stroke stroke) {
        this.axisStroke = stroke;
        return redraw();
    }

    public Axis setRange(double lowerBound, double upperBound) {
        if (!Double.isFinite(lowerBound) || !Double.isFinite(upperBound)) {
            autoRanged = true;
            updateRangeToBest();
            return redraw();
        }
        autoRanged = false;
        this.currentLowerBound = lowerBound;
        this.currentUpperBound = upperBound;
        return redraw();
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

    protected Axis redraw() {

        return (Axis) super.redraw();
    }

    @Override
    protected void drawComponent(Figure<?, ?> source, ChartCanvas<?> canvas) {
        //ignored
    }

    @Override
    protected void layoutComponent(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
        //ignored
    }

    void partialLayoutYAxis(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {

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
            final String a = getLabel(getIterStart(currentLowerBound, majorTickSpacing));
            final String b = getLabel(getIterEnd(currentUpperBound, majorTickSpacing));
            sizeX += source.getTextWidth(labelFont, StringUtils.longerString(a, b)) + labelPadding;
        }
    }


    void partialLayoutXAxis(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
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

    void drawXGrid(Figure<?, ?> source, ChartCanvas<?> canvas, Axis yAxis) {
        double y = posY - yAxis.sizeY;

        switch (mode) {
            case LINEAR:
                if (showMajorGridLines) {
                    canvas.setStroke(majorGridStroke);
                    for (double d = getIterStart(currentLowerBound, majorTickSpacing); d <= currentUpperBound; d += majorTickSpacing) {
                        double e = posX + ((d - currentLowerBound) * scale);
                        canvas.strokeLine(e, posY, e, y);
                    }
                }
                if (showMinorGridLines) {
                    canvas.setStroke(minorGridStroke);
                    for (double d = getIterStart(currentLowerBound, minorTickSpacing); d <= currentUpperBound; d += minorTickSpacing) {
                        double e = posX + ((d - currentLowerBound) * scale);
                        canvas.strokeLine(e, posY, e, y);
                    }
                }
                if (showZeroLine && 0 >= currentLowerBound && 0 <= currentUpperBound) {
                    canvas.setStroke(zeroGridStroke);
                    double e = posX + ((0 - currentLowerBound) * scale);
                    canvas.strokeLine(e, posY, e, y);

                }
                break;
        }
    }

    void drawXAxis(Figure<?, ?> source, ChartCanvas<?> canvas, Axis yAxis) {
        switch (mode) {
            case LINEAR:
                if (showMinorTicks) {
                    canvas.setStroke(minorLineStroke);
                    double y = minorTicksInside ? (posY - minorTickLength) : (posY + minorTickLength);
                    for (double d = getIterStart(currentLowerBound, minorTickSpacing); d <= currentUpperBound; d += minorTickSpacing) {
                        double e = posX + ((d - currentLowerBound) * scale);
                        canvas.strokeLine(e, posY, e, y);
                    }
                }
                double yLabel = posY;
                if (showMajorTicks) {
                    canvas.setStroke(majorLineStroke);
                    canvas.setFont(labelFont);
                    canvas.setFill(labelColor);
                    double y = majorTicksInside ? (posY - majorTickLength) : (posY + majorTickLength);
                    for (double d = getIterStart(currentLowerBound, majorTickSpacing); d <= currentUpperBound; d += majorTickSpacing) {
                        double e = posX + ((d - currentLowerBound) * scale);
                        canvas.strokeLine(e, posY, e, y);
                    }
                    if (!majorTicksInside) {
                        yLabel += majorTickLength;
                    }
                }
                if (showLabels) {
                    yLabel += source.getTextBaselineOffset(labelFont) + labelPadding;
                    for (double d = getIterStart(currentLowerBound, majorTickSpacing); d <= currentUpperBound; d += majorTickSpacing) {
                        double e = posX + ((d - currentLowerBound) * scale);
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

                break;
            default:
                throw new UnsupportedOperationException(mode.name());
        }

    }

    private static double getIterStart(double v, double spacing) {
        double remainder = Numbers.mod(v, spacing);
        return v - remainder + spacing;
    }

    private static double getIterEnd(double v, double spacing) {
        double remainder = Numbers.mod(v, spacing);
        return v - remainder;
    }

    void drawYAxis(Figure<?, ?> source, ChartCanvas<?> canvas, Axis xAxis) {
        switch (mode) {
            case LINEAR:
                if (showMinorTicks) {
                    canvas.setStroke(minorLineStroke);
                    double x0 = posX + sizeX;
                    double x1 = minorTicksInside ? (x0 + minorTickLength) : (x0 - minorTickLength);
                    for (double d = getIterStart(currentLowerBound, minorTickSpacing); d <= currentUpperBound; d += minorTickSpacing) {
                        double dRev = ((currentUpperBound - d) / range);
                        double e = posY + (dRev * sizeY);
                        canvas.strokeLine(x0, e, x1, e);
                    }
                }
                double xLabel = posX + sizeX;
                if (showMajorTicks) {
                    canvas.setStroke(majorLineStroke);
                    canvas.setFont(labelFont);
                    canvas.setFill(labelColor);
                    double x0 = xLabel;
                    double x1 = majorTicksInside ? (x0 + majorTickLength) : (x0 - majorTickLength);
                    xLabel = x0 - labelPadding - majorTickLength;

                    for (double d = getIterStart(currentLowerBound, majorTickSpacing); d <= currentUpperBound; d += majorTickSpacing) {
                        double dRev = ((currentUpperBound - d) / range);
                        double e = posY + (dRev * sizeY);
                        canvas.strokeLine(x0, e, x1, e);
                    }
                }
                if (showLabels) {
                    double yOff = (vLabelAlignment == VAlign.TOP ? 0 : ((vLabelAlignment == VAlign.MIDDLE ? 0.5 : 1) * source.getTextLineHeight(labelFont))) - source.getTextBaselineOffset(labelFont);
                    for (double d = getIterStart(currentLowerBound, majorTickSpacing); d <= currentUpperBound; d += majorTickSpacing) {
                        double dRev = ((currentUpperBound - d) / range);
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
                break;
        }
    }

    void drawYGrid(Figure<?, ?> source, ChartCanvas<?> canvas, Axis xAxis) {
        double x0 = posX + sizeX;
        double x1 = x0 + xAxis.sizeX;
        switch (mode) {
            case LINEAR:
                if (showMajorGridLines) {
                    canvas.setStroke(majorGridStroke);
                    for (double d = getIterStart(currentLowerBound, majorTickSpacing); d <= currentUpperBound; d += majorTickSpacing) {
                        double dRev = ((currentUpperBound - d) / range);
                        double e = posY + (dRev * sizeY);
                        canvas.strokeLine(x0, e, x1, e);
                    }
                }
                if (showMinorGridLines) {
                    canvas.setStroke(minorGridStroke);
                    for (double d = getIterStart(currentLowerBound, minorTickSpacing); d <= currentUpperBound; d += minorTickSpacing) {
                        double dRev = ((currentUpperBound - d) / range);
                        double e = posY + (dRev * sizeY);
                        canvas.strokeLine(x0, e, x1, e);
                    }
                }
                if (showZeroLine && 0 >= currentLowerBound && 0 <= currentUpperBound) {
                    canvas.setStroke(zeroGridStroke);
                    double dRev = ((currentUpperBound - 0) / range);
                    double e = posY + (dRev * sizeY);
                    canvas.strokeLine(x0, e, x1, e);

                }
                break;
        }
    }

    void updateYAxisScale() {
        range = currentUpperBound - currentLowerBound;
        scale = sizeY / range;

    }

    void updateXAxisScale() {
        range = currentUpperBound - currentLowerBound;
        scale = sizeX / range;

    }

    @Override
    public String toString() {
        return String.format("Axis between %.3f and %.3f", currentLowerBound, currentUpperBound);
    }
}
