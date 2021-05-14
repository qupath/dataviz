package net.mahdilamb.dataviz.layouts;

import net.mahdilamb.dataviz.PlotAxis;
import net.mahdilamb.dataviz.figure.AbstractComponent;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.utils.Numbers;

import java.awt.*;
import java.text.DecimalFormat;

abstract class XYAxis extends PlotAxis<XYLayout> {

    String format = null;
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
    boolean reversed = false;
    double lower = Double.POSITIVE_INFINITY, upper = Double.NEGATIVE_INFINITY;

    double majorTickSpacing = Double.NaN, minorTickSpacing = Double.NaN;

    double titlePadding = 5;


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

    @Override
    protected String getLabel(double value) {
        if (format != null) {
            return String.format(format, value);
        }
        return Double.toString(Numbers.approximateDouble(value));
    }

    protected double roundToMajorTick(double value) {
        final int dp = Double.toString(majorTickSpacing).indexOf('.');
        return Double.parseDouble(String.format("%." + dp + "f", value));
    }
}