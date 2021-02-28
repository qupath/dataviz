package net.mahdilamb.charts;

import net.mahdilamb.charts.utils.Interpolations;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataframe.utils.UnsortedDoubleSet;
import net.mahdilamb.statistics.StatUtils;

import static net.mahdilamb.charts.utils.Interpolations.lerp;
import static net.mahdilamb.statistics.ArrayUtils.flipInPlace;
import static net.mahdilamb.statistics.ArrayUtils.linearlySpaced;

abstract class Trace {


    public static final class Categorical extends Trace {

        protected String[] categories;
        boolean[] isVisible;
        int[] indices;

        private Categorical(String name, String[] categories) {
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

        public Categorical(final Series<?> series) {
            this(series.getName(), series.asString().toArray(new String[series.size()]));
            this.series = series;
        }

        public String get(int i) {
            return categories[indices[i]];
        }

        int getRaw(int i) {
            return indices[i];
        }

        @Override
        Color get(Colormap colormap, int i) {
            return colormap.get(((float) getRaw(i) % colormap.size()) / (colormap.size() - 1));
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

   public static final class Numeric extends Trace {
        public static final int MAX_POINTS = 6;
        protected double[] values;
        double valMin, valMax;
        boolean useLog = false, reversed = false;
        double scaleMin = 0, scaleMax = 1;
        int numUnique = -1;
        private double[] legendPoints;
        double filterMin = Double.NEGATIVE_INFINITY, filterMax = Double.POSITIVE_INFINITY;

        Numeric(String name, double[] values) {
            this.name = name;
            this.values = values;
            valMin = StatUtils.min(values);
            valMax = StatUtils.max(values);
        }

        Numeric(final Series<?> series) {
            this(series.getName(), series.asDouble().toArray(new double[series.size()]));
            this.series = series;
        }

        double get(int index) {
            double t = ((getRaw(index) - valMin) / (valMax - valMin));
            double min = reversed ? scaleMax : scaleMin;
            double max = reversed ? scaleMin : scaleMax;
            return useLog ? lerp(min, max, t, Interpolations::easeOutExpo) : lerp(min, max, t);
        }

        public double getRaw(int index) {
            return values[index];
        }

        @Override
        Color get(Colormap colormap, int i) {
            return colormap.get(get(i));
        }

        @Override
        boolean isVisible(int index) {
            return values[index] >= filterMin && values[index] <= filterMax;
        }

        int numUnique() {
            if (numUnique == -1) {
                final UnsortedDoubleSet values = new UnsortedDoubleSet(this.values);
                numUnique = values.size();
            }
            return numUnique;
        }

        double[] points() {
            if (legendPoints == null) {
                final double min = Math.max(filterMin, valMin);
                final double max = Math.min(filterMax, valMax);
                final double range = max - min;
                legendPoints = flipInPlace(linearlySpaced(min, max, Math.min(numUnique(), MAX_POINTS)));
                if (useLog) {
                    for (int i = 0; i < legendPoints.length; ++i) {
                        legendPoints[i] = (Interpolations.easeOutExpo((legendPoints[i] - min) / range) * range) + min;
                    }
                }
            }
            return legendPoints;
        }


        @Override
        public String toString() {
            final StringBuilder stringBuilder = new StringBuilder("Trace {\"").append(name).append("\"}");
            for (final double d : points()) {
                stringBuilder.append("\n\t* ").append(d);
            }
            return stringBuilder.toString();
        }
    }

    boolean showInLegend = true;
    protected String name;
    protected PlotData.HoverText.Segment defaultSeg;
    Series<?> series;

    Trace next;

    abstract boolean isVisible(int index);

    abstract Color get(final Colormap colormap, int index);


}
