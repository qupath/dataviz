package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.dataframe.DoubleSeries;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataframe.utils.UnsortedDoubleSet;
import net.mahdilamb.dataviz.utils.Interpolations;
import net.mahdilamb.dataviz.utils.Numbers;
import net.mahdilamb.stats.ArrayUtils;
import net.mahdilamb.stats.StatUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static net.mahdilamb.dataviz.utils.Interpolations.lerp;

/**
 * A plot trace - containing the information used to style shapes
 */
public abstract class PlotDataAttribute {

    /**
     * The possible attributes for a plot trace to have ownership over
     */
    public enum Type {
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
    public static final class Categorical extends PlotDataAttribute {
        String[] categories;
        boolean[] isVisible;
        int[] indices;

        Categorical(PlotData<?, ?> data, Type attribute, String name, String[] categories) {
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
        public Categorical(final PlotData<?, ?> data, final Type attribute, final Series<?> series) {
            this(data, attribute, series.getName(), series.asString().toArray(new String[series.size()]));
            this.series = series;
        }

        Categorical(final PlotData<?, ?> data, final Type attribute, String[] categories, int[] indices) {
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
        Color calculateColorOf(Colormap colormap, int i) {
            return calculateColor(colormap, getRaw(i));
        }

        Color calculateColor(Colormap colormap, int i) {
            return colormap.get(((float) i % colormap.size()) / (colormap.size() - 1));

        }

        @Override
        final Legend.Group getLegendGroup(Legend legend) {
            if (legendGroup == null) {
                final java.util.List<Legend.Item> items = new ArrayList<>(categories.length);
                int i = 0;
                for (String category : categories) {
                    final Legend.TogglableItem item = new Legend.TogglableItem(legend, data.getGlyph(this, i++), category);
                    items.add(item);
                }
                legendGroup = new Legend.Group(legend, this, items);
                legendGroup.setOnMouseClick((x, y) -> {
                    final Legend.Item item;
                    if ((item = legendGroup.getItemAt(x, y)) != null) {
                        setVisibility(item.label, ((Legend.TogglableItem) item).toggleVisibility());
                    }
                });
            }
            return legendGroup;
        }

        @Override
        public PlotDataAttribute setVisibility(String category, boolean visibility) throws UnsupportedOperationException {
            for (int j = 0; j < categories.length; ++j) {
                if (category.equals(categories[j])) {
                    isVisible[j] = visibility;
                    return refresh();
                }
            }
            return refresh();
        }

        @Override
        public PlotDataAttribute setFilter(double min, double max) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        boolean isVisible(int index) {
            return isVisible[indices[index]];
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
    public static final class Numeric extends PlotDataAttribute {
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
        public Numeric(final PlotData<?, ?> data, final Type attribute, String name, double[] values, double scaleMin, double scaleMax) {
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
        public Numeric(final PlotData<?, ?> data, final Type attribute, String name, double[] values) {
            this(data, attribute, Series.of(name, values));
        }


        /**
         * Create a trace from a series. Must be castable to a double series
         *
         * @param data      the source data
         * @param attribute the attribute
         * @param series    the series
         */
        public Numeric(final PlotData<?, ?> data, final Type attribute, final DoubleSeries series) {
            super(data, attribute);
            this.series = this.values = series;
            valMin = scaleMin = StatUtils.min(values::get, values.size());
            valMax = scaleMax = StatUtils.max(values::get, values.size());
            showInLegend = getName() != null && getName().length() > 1 && attribute != Type.COLOR;

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
        public Numeric(final PlotData<?, ?> data, final Type attribute, final DoubleSeries series, double scaleMin, double scaleMax) {
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

        final double scale(double value) {
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
        Color calculateColorOf(Colormap colormap, int i) {
            return colormap.get(get(i));
        }

        public double getMax() {
            return scaleMax;
        }

        @Override
        Legend.Group getLegendGroup(Legend legend) {
            if (legendGroup == null) {
                final java.util.List<Legend.Item> items = new ArrayList<>(points().length);
                for (double v : legendPoints) {
                    final Legend.Item item = new Legend.Item(legend, data.getGlyph(this, v), Double.toString(Numbers.approximateDouble(v)));
                    //   item.setOnMouseClick(() -> setVisibility(category, item.toggleVisibility()));
                    items.add(item);
                }
                legendGroup = new Legend.Group(legend, this, items);
                legendGroup.setOnMouseEnter(() -> {
                    System.out.println("enter");
                });
                legendGroup.setOnMouseExit(() -> {
                    System.out.println("out");
                });
            }
            return legendGroup;
        }

        @Override
        public PlotDataAttribute setVisibility(String category, boolean visibility) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public PlotDataAttribute setFilter(double min, double max) throws UnsupportedOperationException {
            filterMin = Math.min(min, max);
            filterMax = Math.max(max, min);
            return this;
        }

        int numUnique() {
            if (numUnique == -1) {
                final UnsortedDoubleSet values = new UnsortedDoubleSet(this.values.toArray(new double[this.values.size()]));
                numUnique = values.size();
            }
            return numUnique;
        }

        double[] points() {
            if (legendPoints == null) {
                final double min = Math.max(filterMin, valMin);
                final double max = Math.min(filterMax, valMax);
                final double range = max - min;
                legendPoints = ArrayUtils.linearlySpaced(min, max, Math.min(numUnique(), MAX_POINTS));
                isVisible = new boolean[legendPoints.length];
                Arrays.fill(isVisible, true);
                if (useLog) {
                    for (int i = 0; i < legendPoints.length; ++i) {
                        legendPoints[i] = (Interpolations.easeOutExpo((legendPoints[i] - min) / range) * range) + min;
                    }
                }
            }
            return legendPoints;
        }

        private int getLegendIndex(double value) {
            if (value < valMin || value > valMax) {
                return -1;
            }
            if (useLog) {
                throw new UnsupportedOperationException();//TODO log
            }
            return (int) ((points().length) * ((value - valMin) / (valMax - valMin)));
        }

        @Override
        boolean isVisible(int index) {
            int j = getLegendIndex(values.get(index)) - 1;//TODO check
            if (j > 0 && !isVisible[j]) {
                return false;
            }
            return values.get(index) >= filterMin && values.get(index) <= filterMax;
        }

        @Override
        public String toString() {
            final StringBuilder stringBuilder = new StringBuilder("Trace {\"").append(getName()).append("\"}");
            for (final double d : points()) {
                stringBuilder.append("\n\t* ").append(Numbers.approximateDouble(d));
            }
            return stringBuilder.toString();
        }
    }

    static final class UncategorizedTrace extends PlotDataAttribute {
        final Figure figure;
        PlotData<?, ?>[] data;
        Legend.Group[] legendGroups;
        boolean[] isVisible;

        UncategorizedTrace(Figure figure, PlotData<?, ?>[] data) {
            super(null, null);
            this.data = data;
            this.figure = figure;
        }

        @Override
        Color calculateColorOf(Colormap colormap, int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        Legend.Group getLegendGroup(Legend legend) {
            //TODO
            return null;
        }

        @Override
        public PlotDataAttribute setVisibility(String category, boolean visibility) throws UnsupportedOperationException {
            /*todo
               int i = 0;
            for (final PlotData<?, ?> plotData : data) {
                if (category.equals(plotData.name)) {
                    if (isVisible == null) {
                        isVisible = new boolean[data.length];
                    }
                    isVisible[i] = visibility;
                    return this;
                }
                ++i;
            }*/
            return this;
        }

        @Override
        public PlotDataAttribute setFilter(double min, double max) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        boolean isVisible(int index) {
            if (isVisible == null) {
                return true;
            }
            return isVisible[index];
        }
    }

    final PlotData<?, ?> data;
    final Type attribute;
    double indent = 5,
            glyphWidth = -1,
            spacing = 5,
            textWidth = -1;

    Legend.Group legendGroup;
    Legend.Group[] legendGroups;
    boolean showInLegend = true;
    /**
     * The name of the trace
     */
    String name;
    Series<?> series;
    protected HoverText.Segment defaultSeg;//TODO

    PlotDataAttribute(PlotData<?, ?> data, final Type attribute) {
        this.data = data;
        this.attribute = attribute;

    }

    public final Type getType() {
        return attribute;
    }

    abstract Color calculateColorOf(final Colormap colormap, int i);

    public final String getName() {
        return series == null ?
                name :
                series.getName()
                ;
    }

    /**
     * @return whether this trace group should be shown in the legend
     */
    public boolean showInLegend() {
        return showInLegend;
    }

    abstract Legend.Group getLegendGroup(Legend legend);

    /**
     * Set the visibility of a category (ignored by numerical traces)
     *
     * @param category   the trace
     * @param visibility the visibility
     * @return this data attribute
     */
    public abstract PlotDataAttribute setVisibility(final String category, boolean visibility) throws UnsupportedOperationException;

    /**
     * Filter a numerical trace - this method is ignored by categorical traces
     *
     * @param min filter min
     * @param max filter max
     * @return this data attribute
     */
    public abstract PlotDataAttribute setFilter(double min, double max) throws UnsupportedOperationException;

    abstract boolean isVisible(int index);

    @Override
    public int hashCode() {
        return attribute.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlotDataAttribute)) {
            return false;
        }
        return attribute == ((PlotDataAttribute) obj).attribute;
    }

    protected PlotDataAttribute refresh() {
        data.getLayout().clearCache();
        data.refresh();
        return this;
    }


}
