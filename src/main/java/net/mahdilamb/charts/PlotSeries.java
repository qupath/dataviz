package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.plots.MarginalMode;
import net.mahdilamb.charts.statistics.StatUtils;
import net.mahdilamb.charts.statistics.utils.DoubleArrayList;
import net.mahdilamb.charts.statistics.utils.GroupBy;
import net.mahdilamb.charts.statistics.utils.IntArrayList;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.reference.qualitative.Plotly;
import net.mahdilamb.colormap.reference.sequential.Viridis;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * A series of data elements that can be added to a plot area
 *
 * @param <S> the type of the series
 */
public abstract class PlotSeries<S extends PlotSeries<S>> {

    public static final Colormap DEFAULT_SEQUENTIAL_COLORMAP = new Viridis();
    public static final Colormap DEFAULT_QUALITATIVE_COLORMAP = new Plotly();

    Chart<S> chart;

    protected String groupName;
    protected GroupBy<String> groups;
    protected GroupAttributes[] groupAttributes;
    protected int[] groupSort;//todo allow groups to have a custom sort - this stores the indices

    protected static final class GroupAttributes {
        boolean showInLegend = true;
        Color markerColor;
        Stroke edge;
        String name;
        Stroke line;
        GroupBy.Group<String> group;
        IntArrayList subGroups;

        public GroupAttributes(GroupBy.Group<String> group) {
            this.group = group;
            this.name = group.get();
        }


        public Stroke getStroke() {
            return edge;
        }

        public void setMarkerColor(final Color color) {
            this.markerColor = color;
        }
    }

    SelectedStyle selectedStyle = SelectedStyle.DEFAULT_SELECTED_STYLE;
    UnselectedStyle unselectedStyle = UnselectedStyle.DEFAULT_UNSELECTED_STYLE;

    String name;
    /**
     * Series display
     */
    protected boolean showInLegend = true, showInColorBars = false;

    /**
     * The colormap used for each group when a color hasn't been specified is populated using this colormap
     */
    protected Colormap groupColormap = DEFAULT_QUALITATIVE_COLORMAP;

    /**
     * @return this series after firing a redraw request if the chart is specified
     */
    @SuppressWarnings("unchecked")
    protected S requestLayout() {
        if (chart != null) {
            chart.requestLayout();
        }
        return (S) this;
    }

    /**
     * Request a data update
     *
     * @return this series
     */
    protected S requestDataUpdate() {
        return requestLayout();
    }

    /**
     * Set the name of this series
     *
     * @param name the name to set
     * @return this series
     */
    public S setName(String name) {
        this.name = name;
        return requestLayout();
    }

    /**
     * An abstract XY series
     *
     * @param <S> the concrete type of the series
     */
    public static abstract class XY<S extends XY<S>> extends PlotSeries<S> {
        /**
         * The name of the x data
         */
        String xName;
        /**
         * The name of the y data
         */
        String yName;
        protected final DoubleArrayList x;
        protected final DoubleArrayList y;
        /**
         * Stores the min and max of each axis
         */
        double minX, maxX, minY, maxY;
        protected Color color;


        /**
         * Create an abstract XY series
         *
         * @param name the name of the series
         * @param x    the x data
         * @param y    the y data
         * @throws IllegalArgumentException if the x and y series are not the same length
         */
        protected XY(final String name, double[] x, double[] y) {
            if (x.length != y.length) {
                throw new IllegalArgumentException("X and Y must be of the same length");
            }
            this.name = name;
            this.x = new DoubleArrayList(x);
            this.y = new DoubleArrayList(y);
            minX = StatUtils.min(x);
            minY = StatUtils.min(y);
            maxX = StatUtils.max(x);
            maxY = StatUtils.max(y);
        }

        /**
         * Create an abstract XY series
         *
         * @param x the x data
         * @param y the y data
         * @throws IllegalArgumentException if the x and y series are not the same length
         */
        protected XY(double[] x, double[] y) {
            this(null, x, y);
            showInLegend(false);
        }

        /**
         * Create an abstract XY series using a given x series and function that maps x to y
         *
         * @param x           the x data
         * @param toYFunction the function that maps x to y
         */
        protected XY(double[] x, DoubleUnaryOperator toYFunction) {
            this(x, map(x, toYFunction));
        }

        /**
         * Create an array of y mapped from x
         *
         * @param x           the x data
         * @param toYFunction the map function
         * @return the mapped data
         */
        private static double[] map(double[] x, DoubleUnaryOperator toYFunction) {
            double[] y = new double[x.length];
            for (int i = 0; i < x.length; i++) {
                y[i] = toYFunction.applyAsDouble(x[i]);
            }
            return y;
        }

        /**
         * Set whether the series should be present in the legend
         *
         * @param showInLegend whether the series should be present in the legend
         * @return this XY series
         */
        public S showInLegend(boolean showInLegend) {
            this.showInLegend = showInLegend;
            return requestLayout();
        }

        /**
         * Set whether the series should be show in the color (only applicable if there is an associated colormap)
         *
         * @param showColorBar whether to show the series as a color bar
         * @return this series
         */
        public S showColorBar(boolean showColorBar) {
            this.showInColorBars = showColorBar;
            return requestLayout();
        }

        /**
         * Set the label of the x data
         *
         * @param name the name of the x data
         * @return this series
         */
        protected S setXLabel(String name) {
            xName = name;
            return requestLayout();
        }

        /**
         * Set the label of the y data
         *
         * @param name the name of the y data
         * @return this series
         */
        protected S setYLabel(String name) {
            yName = name;
            return requestLayout();
        }

        /**
         * Set the x and y labels at the same time
         *
         * @param xLabel the name of the x data
         * @param yLabel the name of the data
         * @return this series
         */
        public S setLabels(final String xLabel, final String yLabel) {
            return setXLabel(xLabel).setYLabel(yLabel);
        }

    }

    /**
     * A one-dimensional plot series with distribution data
     *
     * @param <S> the concrete type of this series
     */
    public abstract static class Distribution<S extends Distribution<S>> extends PlotSeries<S> {
        protected final DoubleArrayList values;

        /**
         * Create a distribution series from the given series
         *
         * @param values the values to create the distribution series of
         */
        protected Distribution(double[] values) {
            this.values = new DoubleArrayList(values);
        }
    }

    /**
     * An abstract 2D distribution series
     *
     * @param <S> the concrete type of the series
     */
    public static abstract class Distribution2D<S extends Distribution2D<S>> extends XY<S> {
        MarginalMode marginalModeX = MarginalMode.NONE;
        MarginalMode marginalModeY = MarginalMode.NONE;

        /**
         * Create a 2D distribution series
         *
         * @param x the x data
         * @param y the y data
         */
        protected Distribution2D(double[] x, double[] y) {
            super(x, y);
        }

        /**
         * Set the marginal mode of the x data
         *
         * @param mode the mode
         * @return this series
         */
        public S setMarginalX(MarginalMode mode) {
            this.marginalModeX = mode;
            return requestDataUpdate();
        }

        /**
         * Set the marginal mode of the y data
         *
         * @param mode the mode
         * @return this series
         */
        public S setMarginalY(MarginalMode mode) {
            this.marginalModeY = mode;
            return requestDataUpdate();
        }
    }

    public static abstract class Categorical<S extends Categorical<S>> extends PlotSeries<S> {
        protected final List<String> categories;
        protected final DoubleArrayList values;
        final double valueMin, valueMax;

        public Categorical(String[] names, double[] values) {
            if (names.length != values.length) {
                throw new IllegalArgumentException();
            }
            this.categories = Arrays.asList(names);
            this.values = new DoubleArrayList(values);
            valueMin = StatUtils.min(values);
            valueMax = StatUtils.max(values);
        }

    }

    public static abstract class Matrix<S extends Matrix<S>> extends PlotSeries<S> {
        protected final List<double[]> data;


        protected Matrix(double[][] data) {//TODO column or row major?
            this.data = Arrays.asList(data);
        }
    }


    /**
     * Set the group of each data element
     *
     * @param name   the name of the groupings
     * @param groups the groups
     * @return this XY series
     */
    protected S setColors(String name, Iterable<String> groups) {
        this.groupName = name;
        this.groups = new GroupBy<>(groups);
        groupAttributes = new GroupAttributes[this.groups.numGroups()];
        int i = 0;
        for (final GroupBy.Group<String> g : this.groups) {
            groupAttributes[i++] = new GroupAttributes(g);
        }

        return requestLayout();
    }

    protected int numGroups() {
        return groups == null ? 0 : groups.numGroups();
    }

    protected GroupAttributes getGroupAttribute(int index) {
        if (groups == null) {
            return null;
        }
        return groupAttributes[index];
    }

    protected GroupAttributes getGroupAttribute(String name) {
        if (groups == null) {
            return null;
        }
        for (final GroupAttributes g : groupAttributes) {
            if (name.equals(g.name)) {
                return g;
            }
        }
        return null;
    }

    protected S setGroupName(final int group, final String name) {
        GroupAttributes a = getGroupAttribute(group);
        if (a != null) {
            a.name = name;
        }
        return requestLayout();
    }

    protected S setGroupColormap(final Colormap colormap) {
        this.groupColormap = colormap;
        return requestLayout();
    }

    protected S setGroupColor(final int group, final Color color) {
        GroupAttributes a = getGroupAttribute(group);
        if (a != null) {
            a.markerColor = color;
        }
        return requestLayout();
    }

    protected S setGroupStroke(final int group, final Stroke color) {
        GroupAttributes a = getGroupAttribute(group);
        if (a != null) {
            a.edge = color;
        }
        return requestLayout();
    }

    protected S showGroupInLegend(final int group, boolean showInLegend) {
        GroupAttributes a = getGroupAttribute(group);
        if (a != null) {
            a.showInLegend = showInLegend;
        }
        return requestLayout();
    }

    protected S setGroupLine(final int group, Stroke line) {
        GroupAttributes a = getGroupAttribute(group);
        if (a != null) {
            a.line = line;
        }
        return requestLayout();
    }

    protected S setGroupName(final String group, final String name) {
        GroupAttributes a = getGroupAttribute(group);
        if (a != null) {
            a.name = name;
        }
        return requestLayout();
    }

    protected S setGroupColor(final String group, final Color color) {
        GroupAttributes a = getGroupAttribute(group);
        if (a != null) {
            a.markerColor = color;
        }
        return requestLayout();
    }

    protected S setGroupStroke(final String group, final Stroke color) {
        GroupAttributes a = getGroupAttribute(group);
        if (a != null) {
            a.edge = color;
        }
        return requestLayout();
    }

    protected S showGroupInLegend(final String group, boolean showInLegend) {
        GroupAttributes a = getGroupAttribute(group);
        if (a != null) {
            a.showInLegend = showInLegend;
        }
        return requestLayout();
    }

    protected S setGroupLine(final String group, Stroke line) {
        GroupAttributes a = getGroupAttribute(group);
        if (a != null) {
            a.line = line;
        }
        return requestLayout();
    }

    protected static final class MarkerImpl implements Marker {
        Fill face;
        Stroke edge;
        double size;
        MarkerShape markerShape;

        MarkerImpl(MarkerShape markerShape, double size, Color face, double edgeWidth, Color edge) {
            this(markerShape, size, face, new Stroke(edge, edgeWidth));
        }

        public MarkerImpl(MarkerShape markerShape, double size, Color face, final Stroke edge) {
            this.edge = edge;
            this.markerShape = markerShape;
            this.face = new Fill(face);
            this.size = size;
        }

        @Override
        public Fill getFill() {
            return face;
        }

        @Override
        public Stroke getStroke() {
            return edge;
        }

        @Override
        public double getSize() {
            return size;
        }

        @Override
        public MarkerShape getShape() {
            return markerShape;
        }
    }
}
