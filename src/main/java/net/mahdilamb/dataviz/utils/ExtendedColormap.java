package net.mahdilamb.dataviz.utils;

import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;

import java.awt.*;
import java.util.Collection;
import java.util.Objects;

/**
 * A colormap that can be statically scaled between values. Supports reversing a colormap and use logarithmic, rather
 * than linearly scale if desired. Does not watch colors.
 */
public class ExtendedColormap implements Colormap {
    private Colormap colormap;
    private double mapMin = 0, mapMax = 1;
    private boolean useLog = false, reversed = false;

    /**
     * Extend the given colormap
     *
     * @param colormap the colormap to extend
     */
    public ExtendedColormap(Colormap colormap) {
        Objects.requireNonNull(this.colormap = colormap);
    }

    /**
     * Extend a colormap given its name
     *
     * @param colormapName the name of the colormap
     */
    public ExtendedColormap(String colormapName) {
        Objects.requireNonNull(this.colormap = Colormaps.get(colormapName));
    }

    /**
     * Set the range of the min and max of the colormap
     *
     * @param valMin the value min
     * @param valMax the value max
     * @return this extended colormap
     * @throws IllegalArgumentException if the range is 0, if min is greater than max or either min or max are not finite
     */
    public ExtendedColormap setRange(double valMin, double valMax) throws IllegalArgumentException {
        if (valMax == valMin) {
            throw new IllegalArgumentException("range cannot be 0");
        }
        if (!Double.isFinite(valMin) || !Double.isFinite(valMax)) {
            throw new IllegalArgumentException("min and max must be finite numbers");
        }
        if (valMin > valMax) {
            throw new IllegalArgumentException("min must be less than max");
        }
        this.mapMin = valMin;
        this.mapMax = valMax;
        return this;
    }

    /**
     * Set the colormap to use a logarithmic scale
     *
     * @param useLog whether to use the log scale
     * @return this extended colormap
     */
    public ExtendedColormap setLogarithmic(boolean useLog) {
        this.useLog = useLog;
        return this;
    }

    /**
     * Set the colormap to be reversed
     *
     * @param reversed whether to reverse the colormap
     * @return this extended colormap
     */
    public ExtendedColormap setReversed(boolean reversed) {
        this.reversed = reversed;
        return this;
    }

    /**
     * @param colormap the colormap to use in the extended colormap
     * @return the source colormap
     */
    public ExtendedColormap setColormap(final Colormap colormap) {
        Objects.requireNonNull(this.colormap = colormap);
        return this;
    }

    /**
     * @return whether the colormap is using the log scale
     */
    public boolean usingLog() {
        return useLog;
    }

    /**
     * @return whether the colormap is reversed
     */
    public boolean isReversed() {
        return reversed;
    }

    @Override
    public Color get(double value) {
        if (Double.isNaN(mapMax) || Double.isNaN(mapMin) || Double.isNaN(value)) {
            return getNaNColor();
        } else if (Double.isInfinite(value)) {
            return getNaNColor();//if the value is infinite, we'll give it NaN color
        } else if (value <= mapMin) {
            return reversed ? getHighColor() : getLowColor();
        } else if (value >= mapMax) {
            return reversed ? getLowColor() : getHighColor();
        }
        //reverse value if needed
        value = reversed ? (mapMin + (mapMax - value)) : value;
        //scale the value
        double t = ((value - mapMin) / (mapMax - mapMin));
        if (useLog) {
            t = Interpolations.easeInExpo(t);
        }
        return colormap.get(Float.valueOf((float) t));
    }

    @Override
    public Color get(Float position) {
        if (position == null) {
            return getNaNColor();
        }
        return get(position.doubleValue());
    }

    @Override
    public Color getNaNColor() {
        return colormap.getNaNColor();
    }

    @Override
    public Color getLowColor() {
        return colormap.getLowColor();
    }

    @Override
    public Color getHighColor() {
        return colormap.getHighColor();
    }

    @Override
    public Collection<Float> getDefinedPositions() {
        return colormap.getDefinedPositions();
    }

    @Override
    public boolean isQualitative() {
        return colormap.isQualitative();
    }
}
