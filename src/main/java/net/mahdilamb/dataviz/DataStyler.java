package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.dataframe.DoubleSeries;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataviz.utils.Interpolations;
import net.mahdilamb.stats.StatUtils;

import java.awt.*;
import java.util.Arrays;

import static net.mahdilamb.dataviz.utils.Interpolations.lerp;

/**
 * A plot trace - containing the information used to style shapes
 */
public abstract class DataStyler {

    /**
     * The possible attributes for a plot trace to have ownership over
     */
    public enum StyleAttribute {
        /**
         * Color attribute
         */
        COLOR,
        /**
         * Opacity attribute
         */
        OPACITY,
        /**
         * Shape attribute - applies only to markers
         */
        SHAPE,
        /**
         * Size attribute
         */
        SIZE,
        /**
         * Group attribute
         */
        GROUP
    }

    /**
     * A categorical trace, where each element has a category
     */
    public static final class Categorical extends DataStyler {
        String[] categories;
        boolean[] isVisible;
        int[] indices;

        Categorical(PlotData<?, ?> data, StyleAttribute attribute, String name, String[] categories) {
            super(data, attribute);
            this.name = name;
            final GroupBy<String> groupBy = new GroupBy<>(categories);
            this.categories = new String[groupBy.numGroups()];
            this.isVisible = new boolean[groupBy.numGroups()];
            for (final GroupBy.Group<String> g : groupBy) {
                this.categories[g.getID()] = g.get();
                this.isVisible[g.getID()] = true;
            }
            indices = groupBy.toMeltedArray();
        }

        /**
         * Create a categorical trace from a series (will be cast to string)
         *
         * @param data      the source data
         * @param attribute the attribute
         * @param series    the series
         */
        public Categorical(final PlotData<?, ?> data, final StyleAttribute attribute, final Series<?> series) {
            this(data, attribute, series.getName(), series.asString().toArray(new String[series.size()]));
            this.series = series;
        }

        Categorical(final PlotData<?, ?> data, final StyleAttribute attribute, String[] categories, int[] indices) {
            super(data, attribute);
            this.categories = categories;
            this.indices = indices;
            isVisible = new boolean[categories.length];
            Arrays.fill(isVisible, true);
        }

        /**
         * @param i the index
         * @return the category from the index
         */
        public String get(int i) {
            return categories[indices[i]];
        }

        int getRaw(int i) {
            return indices[i];
        }

        @Override
        Color calculateColor(Colormap colormap, int i) {
            return colormap.get(((float) getRaw(i) % colormap.size()) / (colormap.size() - 1));
        }

        @Override
        public String toString() {
            final StringBuilder stringBuilder = new StringBuilder("Trace {\"").append(name).append("\"}");
            for (String category : categories) {
                stringBuilder.append("\n\t* ").append(category);
            }
            return stringBuilder.toString();
        }

    }

    /**
     * A numeric trace where the values have a continuous distribution
     */
    public static final class Numeric extends DataStyler {
        /**
         * The default "compact" number of points to be used in the legend
         */
        public static final int MAX_POINTS = 6;
        /**
         * Raw values of the trace
         */
        protected DoubleSeries values;
        //the min and max of the raw values
        double valMin, valMax;
        //translation features - use log, reverse
        boolean useLog = false, reversed = false;
        //the output values to scale to
        double scaleMin, scaleMax;
        //store the number of unique values. -1 means that it has not yet been calculated
        int numUnique = -1;
        //store the current legend points
        private double[] legendPoints;
        boolean[] isVisible;
        //the filter
        double filterMin = Double.NEGATIVE_INFINITY, filterMax = Double.POSITIVE_INFINITY;

        ColorScales.ColorBar colorBar;

        /**
         * Create a named numeric trace
         *
         * @param data      the source data
         * @param attribute the attribute
         * @param name      the name
         * @param values    the values
         * @param scaleMin  the output min value
         * @param scaleMax  the output max value
         */
        public Numeric(final PlotData<?, ?> data, final StyleAttribute attribute, String name, double[] values, double scaleMin, double scaleMax) {
            this(data, attribute, Series.of(name, values), scaleMin, scaleMax);

        }

        /**
         * Create a named numeric trace
         *
         * @param data      the source data
         * @param attribute the attribute
         * @param name      the name of the trace
         * @param values    the values
         */
        public Numeric(final PlotData<?, ?> data, final StyleAttribute attribute, String name, double[] values) {
            this(data, attribute, Series.of(name, values));
        }


        /**
         * Create a trace from a series. Must be castable to a double series
         *
         * @param data      the source data
         * @param attribute the attribute
         * @param series    the series
         */
        public Numeric(final PlotData<?, ?> data, final StyleAttribute attribute, final DoubleSeries series) {
            super(data, attribute);
            this.series = this.values = series;
            valMin = scaleMin = StatUtils.min(values::get, values.size());
            valMax = scaleMax = StatUtils.max(values::get, values.size());
            showInLegend = name != null && name.length() > 1 && attribute != StyleAttribute.COLOR;

        }

        /**
         * Create a trace from a series. Must be castable to a double series
         *
         * @param data      the source data
         * @param attribute the attribute
         * @param series    the series
         * @param scaleMin  the output min value
         * @param scaleMax  the output max value
         */
        public Numeric(final PlotData<?, ?> data, final StyleAttribute attribute, final DoubleSeries series, double scaleMin, double scaleMax) {
            this(data, attribute, series);
            this.scaleMin = scaleMin;
            this.scaleMax = scaleMax;
        }

        private void clear() {
            legendPoints = null;
            glyphWidth = -1;
            textWidth = -1;

        }

        /**
         * @param index the index
         * @return the scaled value of the index
         */
        public double get(int index) {
            return scale(getRaw(index));
        }

        private double scale(double value) {
            double t = ((value - valMin) / (valMax - valMin));
            double min = reversed ? scaleMax : scaleMin;
            double max = reversed ? scaleMin : scaleMax;
            return useLog ? logLerp(min, max, t) : lerp(min, max, t);
        }

        private static double logLerp(double min, double max, double t) {
            return lerp(min, max, Interpolations.easeInExpo(t));
        }

        double getRaw(int index) {
            return values.get(index);
        }

        @Override
        Color calculateColor(Colormap colormap, int i) {
            return colormap.get(get(i));
        }

        public double getMax() {
            return scaleMax;
        }
    }

    static final class UncategorizedTrace extends DataStyler {
        final Figure figure;
        PlotData<?, ?>[] data;
        Legend.LegendItem[] legendItems;
        boolean[] isVisible;

        UncategorizedTrace(Figure figure, PlotData<?, ?>[] data) {
            super(null, null);
            this.data = data;
            this.figure = figure;
        }

        @Override
        Color calculateColor(Colormap colormap, int i) {
            throw new UnsupportedOperationException();
        }
    }

    final PlotData<?, ?> data;
    final StyleAttribute attribute;
    double indent = 5,
            glyphWidth = -1,
            spacing = 5,
            textWidth = -1;

    Legend.LegendItem[] legendItems;
    boolean showInLegend = true;
    /**
     * The name of the trace
     */
    protected String name;
    Series<?> series;
    protected HoverText.Segment defaultSeg;//TODO

    DataStyler(PlotData<?, ?> data, final StyleAttribute attribute) {
        this.data = data;
        this.attribute = attribute;

    }

    public final StyleAttribute getAttribute() {
        return attribute;
    }

    abstract Color calculateColor(final Colormap colormap, int i);

    @Override
    public int hashCode() {
        return attribute.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DataStyler)) {
            return false;
        }
        return attribute == ((DataStyler) obj).attribute;
    }

    public final String getName() {
        return series == null ? name : series.getName();
    }

}
