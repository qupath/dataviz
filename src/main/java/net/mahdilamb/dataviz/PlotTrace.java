package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataframe.utils.UnsortedDoubleSet;
import net.mahdilamb.dataviz.graphics.ChartCanvas;
import net.mahdilamb.dataviz.graphics.Orientation;
import net.mahdilamb.dataviz.plots.Line;
import net.mahdilamb.dataviz.plots.Scatter;
import net.mahdilamb.dataviz.plots.ScatterMode;
import net.mahdilamb.dataviz.utils.Interpolations;
import net.mahdilamb.dataviz.utils.Numbers;
import net.mahdilamb.statistics.StatUtils;

import java.util.Arrays;

import static net.mahdilamb.dataviz.utils.Interpolations.easeOutExpo;
import static net.mahdilamb.dataviz.utils.Interpolations.lerp;
import static net.mahdilamb.statistics.ArrayUtils.linearlySpaced;

/**
 * A subset of data that contains either a categorical or scalar characteristic
 */
public abstract class PlotTrace extends Component {


    /**
     * A categorical trace
     */
    public static final class Categorical extends PlotTrace {

        protected String[] categories;

        boolean[] isVisible;
        int[] indices;

        private Categorical(final PlotData<?> data, final PlotData.Attribute attribute, String name, String[] categories) {
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
         * @param series the series
         */
        public Categorical(final PlotData<?> data, final PlotData.Attribute attribute, final Series<?> series) {
            this(data, attribute, series.getName(), series.asString().toArray(new String[series.size()]));
            this.series = series;
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
        Color get(Colormap colormap, int i) {
            return colormap.get(((float) getRaw(i) % colormap.size()) / (colormap.size() - 1));
        }

        @Override
        int numLegendItems() {
            return categories.length;
        }

        @Override
        Legend.LegendItem getLegendItem(int i) {
            if (legendItems == null) {
                legendItems = new Legend.LegendItem[categories.length];
                for (int j = 0; j < categories.length; ++j) {
                    legendItems[j] = createLegendItem(data, this, j);
                }
            }
            return legendItems[i];
        }

        @Override
        public PlotTrace filter(double min, double max) {
            System.err.println("Numeric filter cannot be applied to categorical trace");
            return this;
        }

        @Override
        public PlotTrace setVisibility(String category, boolean visibility) {
            for (int j = 0; j < categories.length; ++j) {
                if (category.equals(categories[j])) {
                    isVisible[j] = visibility;
                    return this;
                }
            }
            return this;
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
     * A scalar trace
     */
    public static final class Numeric extends PlotTrace {
        /**
         * The default "compact" number of points to be used in the legend
         */
        public static final int MAX_POINTS = 6;
        //the raw values
        protected double[] values;
        //the min and max of the raw values
        double valMin, valMax;
        //translation features - use log, reverse
        boolean useLog = false, reversed = false;
        //the output values to scale to
        double scaleMin = 0, scaleMax = 1;
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
         * @param name   the name of the trace
         * @param values the values
         */
        public Numeric(final PlotData<?> data, final PlotData.Attribute attribute, String name, double[] values) {
            super(data, attribute);
            this.name = name;
            this.values = values;
            valMin = scaleMin = StatUtils.min(values);
            valMax = scaleMax = StatUtils.max(values);
            showInLegend = name != null && name.length() > 1;

        }

        /**
         * Create a named numeric trace
         *
         * @param name     the name
         * @param values   the values
         * @param scaleMin the output min value
         * @param scaleMax the output max value
         */
        public Numeric(final PlotData<?> data, final PlotData.Attribute attribute, String name, double[] values, double scaleMin, double scaleMax) {
            this(data, attribute, name, values);
            this.scaleMin = scaleMin;
            this.scaleMax = scaleMax;
        }

        /**
         * Create a trace from a series. Must be castable to a double series
         *
         * @param series the series
         */
        public Numeric(final PlotData<?> data, final PlotData.Attribute attribute, final Series<?> series) {
            this(data, attribute, series.getName(), series.asDouble().toArray(new double[series.size()]));
            this.series = series;
        }

        /**
         * Create a trace from a series. Must be castable to a double series
         *
         * @param series   the series
         * @param scaleMin the output min value
         * @param scaleMax the output max value
         */
        public Numeric(final PlotData<?> data, final PlotData.Attribute attribute, final Series<?> series, double scaleMin, double scaleMax) {
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
            return lerp(min, max, easeOutExpo(t));
        }

        double getRaw(int index) {
            return values[index];
        }

        @Override
        Color get(Colormap colormap, int i) {
            return colormap.get(get(i));
        }

        @Override
        int numLegendItems() {
            return points().length;
        }

        @Override
        Legend.LegendItem getLegendItem(int i) {
            if (legendItems == null) {
                legendItems = new Legend.LegendItem[points().length];
                for (int j = 0; j < legendItems.length; ++j) {
                    legendItems[j] = new Legend.LegendItem(String.valueOf(Numbers.approximateDouble(points()[j])), new Legend.XYDataGlyph(ScatterMode.MARKER_ONLY));
                }
                if (attribute == PlotData.Attribute.SIZE) {
                    for (int j = 0; j < legendItems.length; ++j) {
                        legendItems[j].glyph.sizeX = legendItems[j].glyph.sizeY = scale(points()[j]);
                    }
                }
            }
            return legendItems[i];
        }

        @Override
        public PlotTrace filter(double min, double max) {
            filterMin = Math.min(min, max);
            filterMax = Math.max(max, min);
            clear();
            return this;
        }

        @Override
        public PlotTrace setVisibility(String category, boolean visibility) {
            for (int i = 0; i < points().length; ++i) {
                if (category.equals(getLegendItem(i).label)) {
                    isVisible[i] = visibility;
                    return this;
                }
            }
            return this;
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
            int j = getLegendIndex(values[index]) - 1;//TODO check
            if (j > 0 && !isVisible[j]) {
                return false;
            }
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
                legendPoints = linearlySpaced(min, max, Math.min(numUnique(), MAX_POINTS));
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

        @Override
        public String toString() {
            final StringBuilder stringBuilder = new StringBuilder("Trace {\"").append(name).append("\"}");
            for (final double d : points()) {
                stringBuilder.append("\n\t* ").append(d);
            }
            return stringBuilder.toString();
        }

        ColorScales.ColorBar getColorBar() {
            if (colorBar == null) {
                colorBar = new ColorScales.ColorBar(this);
            }
            return colorBar;
        }

        void layoutColorBar(Renderer<?> source, ColorScales scales) {
            getColorBar().computeSize(source, scales);
        }

        void drawColorBar(Renderer<?> source, ChartCanvas<?> canvas, ColorScales scales) {
            getColorBar().drawColorBar(source, canvas, scales);
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

        @Override
        public PlotTrace filter(double min, double max) {
            System.err.println("Filter not supported");
            return this;
        }

        @Override
        public PlotTrace setVisibility(String category, boolean visibility) {
            int i = 0;
            for (final PlotData<?> plotData : data) {
                if (category.equals(plotData.name)) {
                    if (isVisible == null) {
                        isVisible = new boolean[data.length];
                    }
                    isVisible[i] = visibility;
                    return this;
                }
                ++i;
            }
            return this;
        }

        @Override
        boolean isVisible(int index) {
            if (isVisible == null) {
                return true;
            }
            return isVisible[index];
        }

        @Override
        Color get(Colormap colormap, int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        int numLegendItems() {
            return data.length;
        }

        @Override
        Legend.LegendItem getLegendItem(int i) {
            if (legendItems == null) {
                legendItems = new Legend.LegendItem[data.length];
                for (int j = 0; j < numLegendItems(); ++j) {
                    legendItems[j] = createLegendItem(this, j);

                }

            }
            return legendItems[i];
        }
    }

    final PlotData<?> data;
    final PlotData.Attribute attribute;
    double indent = 5,
            glyphWidth = -1,
            spacing = 5,
            textWidth = -1;
    protected Legend.LegendItem[] legendItems;
    boolean showInLegend = true;

    protected String name;
    protected HoverText.Segment defaultSeg;
    Series<?> series;

    PlotTrace next;

    PlotTrace(PlotData<?> data, final PlotData.Attribute attribute) {
        this.data = data;
        this.attribute = attribute;

    }

    /**
     * @return the name of this trace
     */
    public String getName() {
        return name;
    }

    /**
     * @return whether this trace group should be shown in the legend
     */
    public boolean showInLegend() {
        return showInLegend;
    }

    /**
     * Filter a numerical trace - this method is ignored by categorical traces
     *
     * @param min filter min
     * @param max filter max
     * @return this trace
     */
    public abstract PlotTrace filter(double min, double max);

    /**
     * Set the visibility of a category (ignored by numerical traces)
     *
     * @param category   the trace
     * @param visibility the visibility
     * @return this trace
     */
    public abstract PlotTrace setVisibility(final String category, boolean visibility);

    abstract boolean isVisible(int index);

    abstract Color get(final Colormap colormap, int index);

    abstract int numLegendItems();

    abstract Legend.LegendItem getLegendItem(int i);

    @Override
    protected final void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        //ignored
    }

    @Override
    protected final void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
        //ignored
    }

    //update sizes, positions to be updated by legend
    final void calculateSize(Renderer<?> source, final Legend legend) {
        sizeX = 0;
        sizeY = 0;
        if (!showInLegend) {
            //todo clear items
            return;
        }
        if (name != null) {
            sizeX = source.getTextWidth(legend.titleFont, name);
            sizeY = legend.itemSpacing + source.getTextLineHeight(legend.titleFont);
        }
        glyphWidth = Scatter.DEFAULT_MARKER_SIZE;
        textWidth = -1;
        for (int i = 0; i < numLegendItems(); ++i) {
            glyphWidth = Math.max(glyphWidth, getLegendItem(i).glyph.sizeX);
            textWidth = Math.max(source.getTextWidth(legend.itemFont, getLegendItem(i).label), textWidth);
            getLegendItem(i).sizeX = indent + spacing + getLegendItem(i).glyph.sizeX + source.getTextWidth(legend.itemFont, getLegendItem(i).label);
            getLegendItem(i).sizeY = Math.max(getLegendItem(i).glyph.sizeY, source.getTextLineHeight(legend.itemFont));
            if (legend.orientation == Orientation.HORIZONTAL) {
                sizeY = Math.max(sizeY, getLegendItem(i).sizeY);
                sizeX += getLegendItem(i).sizeX;
            } else {
                sizeY += legend.itemSpacing + getLegendItem(i).sizeY;
                sizeX = Math.max(sizeX, getLegendItem(i).sizeX);
            }
        }
    }

    final void updateItems(final Legend legend) {
        if (legend.orientation == Orientation.VERTICAL) {
            double endY = posY + sizeY - legend.itemSpacing;
            double width = glyphWidth + spacing + textWidth + indent;
            for (int i = numLegendItems() - 1; i >= 0; --i) {
                endY -= getLegendItem(i).sizeY;
                getLegendItem(i).posX = posX;
                getLegendItem(i).posY = endY;
                getLegendItem(i).sizeX = width;
                endY -= legend.itemSpacing;
            }
        } else {
            //TODO
            throw new UnsupportedOperationException();
        }
    }

    final void drawComponent(Renderer<?> source, ChartCanvas<?> canvas, Legend legend) {
        if (!showInLegend) {
            return;
        }
        if (legend.orientation == Orientation.VERTICAL) {
            if (name != null) {
                canvas.setFill(legend.titleColor);
                canvas.setFont(legend.titleFont);
                canvas.fillText(name, posX, posY + source.getTextBaselineOffset(legend.titleFont));
            }

            canvas.setFont(legend.itemFont);
            double textX = indent + spacing + glyphWidth + posX;
            double textHeight = source.getTextLineHeight(legend.itemFont);
            for (int i = 0; i < numLegendItems(); ++i) {
                canvas.setFill(legend.itemColor);
                canvas.fillText(getLegendItem(i).label, textX, .5 * (getLegendItem(i).sizeY - textHeight) + getLegendItem(i).posY + source.getTextBaselineOffset(legend.itemFont));
                double glyphYOffset = (getLegendItem(i).sizeY - getLegendItem(i).glyph.sizeY) * .5;
                getLegendItem(i).glyph.draw(source, canvas, .5 * (glyphWidth - getLegendItem(i).glyph.sizeX) + indent + posX, getLegendItem(i).posY + glyphYOffset);
            }
        } else {
            //todo
            throw new UnsupportedOperationException();
        }

    }

    protected static Legend.LegendItem createLegendItem(final PlotData<?> data, final Categorical trace, int j) {
        if (data instanceof Line) {
            Legend.LegendItem out = new Legend.LegendItem(trace.categories[j], new Legend.XYDataGlyph(ScatterMode.LINE_ONLY));
            ((Legend.XYDataGlyph) out.glyph).stroke = ((Line) data).lineStroke;
            final Color baseColor = data.getColormap().get(((float) j % data.getColormap().size()) / (data.getColormap().size() - 1));
            out.glyph.color = new Color(baseColor.red(), baseColor.green(), baseColor.blue(), trace.attribute == PlotData.Attribute.COLOR ? .8 : 1);
            return out;
        } else {
            final Legend.LegendItem out = new Legend.LegendItem(trace.categories[j], new Legend.XYDataGlyph(ScatterMode.MARKER_ONLY));
            //TODO update color
            return out;
        }
    }

    protected static Legend.LegendItem createLegendItem(final UncategorizedTrace trace, int j) {
        final PlotData<?> data = trace.data[j];
        Legend.LegendItem out;
        if (data instanceof PlotData.XYData) {
            out = new Legend.LegendItem(data.name, new Legend.XYDataGlyph(((PlotData.XYData<?>) data).markerMode));
            ((Legend.XYDataGlyph) out.glyph).edgeStroke = data.getEdgeStroke();
            ((Legend.XYDataGlyph) out.glyph).edgeColor = data.getEdgeColor();
        } else {
            out = new Legend.LegendItem(data.name, new Legend.XYDataGlyph(ScatterMode.MARKER_ONLY));
        }
        out.glyph.color = data.getColor(-1);
        return out;

    }
}
