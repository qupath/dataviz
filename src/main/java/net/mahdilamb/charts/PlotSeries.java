package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.SelectedStyle;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.graphics.UnselectedStyle;
import net.mahdilamb.charts.plots.MarginalMode;
import net.mahdilamb.charts.plots.RectangularPlot;
import net.mahdilamb.charts.statistics.StatUtils;
import net.mahdilamb.charts.statistics.utils.GroupBy;
import net.mahdilamb.charts.statistics.utils.IntArrayList;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.reference.qualitative.Plotly;
import net.mahdilamb.colormap.reference.sequential.Viridis;

import java.util.function.BiConsumer;
import java.util.function.ObjDoubleConsumer;

/**
 * A series of data elements that can be added to a plot area
 *
 * @param <S> the type of the series
 */
public abstract class PlotSeries<S extends PlotSeries<S>> extends ChartComponent {

    public static final Colormap DEFAULT_QUALITATIVE_COLORMAP = new Plotly();

    protected String groupName;
    protected GroupBy<String> groups;
    protected GroupAttributes[] groupAttributes;
    protected int[] groupSort;//todo allow groups to have a custom sort - this stores the indices

    protected static final class GroupAttributes {
        public boolean showInLegend = true;
        public Color markerColor;
        public Stroke edge;
        public String name;
        public Stroke line;
        public GroupBy.Group<String> group;
        public IntArrayList subGroups;

        public GroupAttributes(GroupBy.Group<String> group) {
            this.group = group;
            this.name = group.get();
        }

    }

    protected static final class ColorScaleAttributes {
        static final Colormap DEFAULT_COLORMAP = new Viridis();
        public Colormap colormap = DEFAULT_COLORMAP;
        public boolean useLogarithmic = false;
        //the actual min and max values to use from the value range
        public double valueMin = Double.NaN, valueMax = Double.NaN;
        //the minimum values to map to in the colormap
        public double colorScaleMin = 0, colorScaleMax = 1;
        double[] values;
        String[] labels;

        public ColorScaleAttributes() {
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

    @Override
    protected void calculateBounds(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {
        //TODO
    }

    @Override
    protected void layout(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {
        //TODO
    }

    /**
     * @return this series after firing a redraw request if the chart is specified
     */
    @SuppressWarnings("unchecked")
    @Override
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

    @SuppressWarnings("unchecked")
    protected void ifAssigned(ObjDoubleConsumer<Chart<?>> chartSetter, ObjDoubleConsumer<S> seriesSetter, double val) {
        if (this.chart == null) {
            seriesSetter.accept((S) this, val);
        } else {
            chartSetter.accept(this.chart, val);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> void ifAssigned(BiConsumer<Chart<?>, T> chartSetter, BiConsumer<S, T> seriesSetter, T val) {
        if (this.chart == null) {
            seriesSetter.accept((S) this, val);
        } else {
            chartSetter.accept(this.chart, val);
        }
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
     * A one-dimensional plot series with distribution data
     *
     * @param <S> the concrete type of this series
     */
    public abstract static class Distribution<S extends Distribution<S>> extends PlotSeries<S> implements RectangularPlot {
        protected final double[] values;
        String xLabel, yLabel;
        protected double valueMin, valueMax;

        /**
         * Create a distribution series from the given series
         *
         * @param values the values to create the distribution series of
         */
        protected Distribution(double[] values) {
            this.values = values;
            valueMin = StatUtils.min(values);
            valueMax = StatUtils.max(values);
        }

        @Override
        public String getXLabel() {
            return xLabel;
        }

        @Override
        public String getYLabel() {
            return yLabel;
        }

        @Override
        public double getMinX() {
            return 0;
        }

        @Override
        public double getMaxX() {
            return 1;
        }

        @Override
        public double getMinY() {
            return valueMin;
        }

        @Override
        public double getMaxY() {
            return valueMax;
        }
    }

    /**
     * An abstract 2D distribution series
     *
     * @param <S> the concrete type of the series
     */
    public static abstract class Distribution2D<S extends Distribution2D<S>> extends PlotSeries<S> implements RectangularPlot {
        private final double[] x;
        private final double[] y;
        MarginalMode marginalModeX = MarginalMode.NONE;
        MarginalMode marginalModeY = MarginalMode.NONE;
        String xLabel, yLabel;
        protected final double xMin, xMax, yMin, yMax;

        /**
         * Create a 2D distribution series
         *
         * @param x the x data
         * @param y the y data
         */
        protected Distribution2D(double[] x, double[] y) {
            this.x = x;
            this.y = y;
            xMin = StatUtils.min(x);
            xMax = StatUtils.max(x);
            yMin = StatUtils.min(y);
            yMax = StatUtils.max(y);

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

        @Override
        public String getXLabel() {
            return xLabel;
        }

        @Override
        public double getMinX() {
            return xMin;
        }

        @Override
        public double getMaxX() {
            return xMax;
        }

        @Override
        public double getMinY() {
            return yMin;
        }

        @Override
        public double getMaxY() {
            return yMax;
        }

        @Override
        public String getYLabel() {
            return yLabel;
        }
    }

    public static abstract class Categorical<S extends Categorical<S>> extends PlotSeries<S> {
        protected final String[] names;
        protected final double[] values;
        protected final double valueMin, valueMax;

        public Categorical(String[] names, double[] values) {
            if (names.length != values.length) {
                throw new IllegalArgumentException();
            }
            this.names = names;
            this.values = values;
            valueMin = StatUtils.min(values);
            valueMax = StatUtils.max(values);
        }

        protected double getMinX() {
            return 0;
        }

        protected double getMaxX() {
            return names.length;
        }

        protected double getMinY() {
            return valueMin;
        }

        protected double getMaxY() {
            return valueMax;
        }

    }

    public static abstract class Matrix<S extends Matrix<S>> extends PlotSeries<S> implements RectangularPlot {
        String xLabel, yLabel;
        protected final double[][] data;
        final int rowMajorWidth, rowMajorHeight;

        protected Matrix(double[][] data) {
            this.data = data;
            this.rowMajorHeight = data.length;
            this.rowMajorWidth = calculateWidth(data);
        }

        @Override
        public double getMinX() {
            return -.5;
        }

        @Override
        public double getMaxX() {
            return rowMajorWidth - .5;
        }

        @Override
        public double getMinY() {
            return -.5;
        }

        @Override
        public double getMaxY() {
            return rowMajorHeight - .5;
        }

        static int calculateWidth(double[][] data) {
            int width = -1;
            for (final double[] d : data) {
                width = Math.max(width, d.length);
            }
            return width;
        }

        @Override
        public String getXLabel() {
            return xLabel;
        }

        @Override
        public String getYLabel() {
            return yLabel;
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


}
