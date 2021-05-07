package net.mahdilamb.dataviz;

import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataviz.utils.Interpolations;
import net.mahdilamb.stats.StatUtils;

import java.util.Arrays;

import static net.mahdilamb.dataviz.utils.Interpolations.easeOutExpo;
import static net.mahdilamb.dataviz.utils.Interpolations.lerp;

/**
 * A plot trace - containing the information used to style shapes
 */
public abstract class PlotTrace {
    /**
     * The possible attributes for a plot trace to have ownership over
     */
    public enum Attribute {
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
    public static final class Categorical extends PlotTrace {
        String[] categories;
        boolean[] isVisible;
        int[] indices;

        Categorical(PlotData<?> data, Attribute attribute, String name, String[] categories) {
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
        public Categorical(final PlotData<?> data, final Attribute attribute, final Series<?> series) {
            this(data, attribute, series.getName(), series.asString().toArray(new String[series.size()]));
            this.series = series;
        }

        public Categorical(final PlotData<?> data, final Attribute attribute, String[] categories, int[] indices) {
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
    public static final class Numeric extends PlotTrace {
        /**
         * The default "compact" number of points to be used in the legend
         */
        public static final int MAX_POINTS = 6;
        /**
         * Raw values of the trace
         */
        protected double[] values;
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
         * @param name      the name of the trace
         * @param values    the values
         */
        public Numeric(final PlotData<?> data, final Attribute attribute, String name, double[] values) {
            super(data, attribute);
            this.name = name;
            this.values = values;
            valMin = scaleMin = StatUtils.min(values);
            valMax = scaleMax = StatUtils.max(values);
            showInLegend = name != null && name.length() > 1 && attribute != Attribute.COLOR;

        }

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
        public Numeric(final PlotData<?> data, final Attribute attribute, String name, double[] values, double scaleMin, double scaleMax) {
            this(data, attribute, name, values);
            this.scaleMin = scaleMin;
            this.scaleMax = scaleMax;
        }

        /**
         * Create a trace from a series. Must be castable to a double series
         *
         * @param data      the source data
         * @param attribute the attribute
         * @param series    the series
         */
        public Numeric(final PlotData<?> data, final Attribute attribute, final Series<?> series) {
            this(data, attribute, series.getName(), series.asDouble().toArray(new double[series.size()]));
            this.series = series;
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
        public Numeric(final PlotData<?> data, final Attribute attribute, final Series<?> series, double scaleMin, double scaleMax) {
            this(data, attribute, series.getName(), series.asDouble().toArray(new double[series.size()]));
            this.series = series;
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
            return values[index];
        }
    }

    static final class UncategorizedTrace extends PlotTrace {
        final Figure figure;
        PlotData<?>[] data;
        Legend.LegendItem[] legendItems;
        boolean[] isVisible;

        UncategorizedTrace(Figure figure, PlotData<?>[] data) {
            super(null, null);
            this.data = data;
            this.figure = figure;
        }
    }

    final PlotData<?> data;
    final Attribute attribute;
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

    PlotTrace next;

    PlotTrace(PlotData<?> data, final Attribute attribute) {
        this.data = data;
        this.attribute = attribute;

    }

    public final Attribute getAttribute() {
        return attribute;
    }


    @Override
    public int hashCode() {
        return attribute.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlotTrace)) {
            return false;
        }
        return attribute == ((PlotTrace) obj).attribute;
    }
}
