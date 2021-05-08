package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataframe.*;
import net.mahdilamb.dataframe.utils.BooleanArrayList;
import net.mahdilamb.dataframe.utils.IntArrayList;
import net.mahdilamb.dataviz.data.RelationalData;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.plots.DataFrameOnlyMethodException;
import net.mahdilamb.dataviz.utils.rtree.RTree;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Plot data is the source of data for generating traces which, in turn, generate shapes/glyph.
 *
 * @param <PL> the supported plot layout
 */
public abstract class PlotData<PD extends PlotData<PD, PL>, PL extends PlotLayout<PL>> implements FigureComponent {
    private static final Colormap DEFAULT_QUALITATIVE_COLORMAP = Colormaps.get("Plotly");
    private static final Colormap DEFAULT_SEQUENTIAL_COLORMAP = Colormaps.get("Viridis");

    protected Colormap qualitativeColormap = DEFAULT_QUALITATIVE_COLORMAP;
    protected Colormap sequentialColormap = DEFAULT_SEQUENTIAL_COLORMAP;

    protected PlotBounds<PlotBounds.XY, XYLayout> bounds;


    private Figure figure;
    private PL layout;

    boolean anySelected = false;
    BooleanArrayList selected = null;
    Color selectedColor = Colors.lightgray;

    protected Color lineColor;
    protected Color fillColor;

    protected HoverText<PD, PL> hoverFormatter;


    /**
     * The backing dataframe
     */
    protected final DataFrame dataFrame;
    /**
     * List of the shapes in the data
     */
    final List<RTree<PlotShape<PL>>> shapes = new LinkedList<>();
    /**
     * Set of the traces that are used to style the data
     */
    final Map<DataStyler.StyleAttribute, DataStyler> stylers = new EnumMap<>(DataStyler.StyleAttribute.class);

    /**
     * Create a plot data that does not use a dataframe
     */
    protected PlotData() {
        this.dataFrame = null;
    }

    /**
     * Create a plot data that uses a dataframe
     *
     * @param dataFrame the dataframe
     */
    protected PlotData(DataFrame dataFrame) {
        Objects.requireNonNull(this.dataFrame = dataFrame);
    }

    /**
     * Add an array of shapes to be used by the plot area
     *
     * @param shapes             the array of shapes
     * @param createNewTreeOnAdd whether to append to the last data structure for shapes or create a new on
     */
    protected void addShapes(PlotShape<PL>[] shapes, boolean createNewTreeOnAdd) {
        final RTree<PlotShape<PL>> tree;
        if (createNewTreeOnAdd || this.shapes.isEmpty()) {
            tree = new RTree<>();
        } else {
            tree = this.shapes.get(this.shapes.size() - 1);
        }
        tree.putAll(shapes);
        this.shapes.add(tree);
        if (layout != null) {
            layout.clearCache();
        }
    }

    @SafeVarargs
    protected final void addShapes(PlotShape<PL>... shapes) {
        addShapes(shapes, true);
    }

    protected Color getColor(int i) {
        final DataStyler styler = getStyler(DataStyler.StyleAttribute.COLOR);
        if (styler != null) {
            return styler.calculateColor(styler.getClass() == DataStyler.Categorical.class ?
                    qualitativeColormap :
                    sequentialColormap, i);

        }
        return qualitativeColormap.get(0);
    }

    protected double getSize(int i) {
        final DataStyler styler = getStyler(DataStyler.StyleAttribute.SIZE);
        if (styler != null) {
            if (styler.getClass() == DataStyler.Categorical.class) {
        //todo
            } else {
                return ((DataStyler.Numeric) styler).get(i);
            }
        }
        return Double.NaN;
    }

    protected final void setStyler(final String seriesName, final DataStyler.StyleAttribute attribute, BiFunction<DataStyler.StyleAttribute, DoubleSeries, DataStyler.Numeric> ifNumeric, BiFunction<DataStyler.StyleAttribute, StringSeries, DataStyler.Categorical> ifCategorical) throws DataFrameOnlyMethodException {
        if (dataFrame == null) {
            throw new DataFrameOnlyMethodException();
        }
        final Series<?> series = dataFrame.get(seriesName);
        if (series.getType() == DataType.DOUBLE) {
            stylers.put(attribute, ifNumeric.apply(attribute, series.asDouble()));
        } else {
            stylers.put(attribute, ifCategorical.apply(attribute, series.asString()));
        }
        if (layout != null) {
            layout.clearCache();
            PlotLayout.redraw(layout);
        }

    }

    protected final void clearStyler(final DataStyler.StyleAttribute attribute) {
        stylers.remove(attribute);
        if (layout != null) {
            layout.clearCache();
            PlotLayout.redraw(layout);
        }

    }

    protected DataStyler getStyler(final DataStyler.StyleAttribute attribute) {
        return stylers.get(attribute);
    }

    protected boolean hasStyler(final DataStyler.StyleAttribute attribute) {
        return stylers.containsKey(attribute);
    }

    /**
     * @return an empty layout
     */
    protected abstract PL createLayout();

    protected abstract PlotBounds<? extends PlotBounds.Bounds<PL>, PL> getBoundPreferences();

    @Override
    public final Figure getFigure() {
        if (figure == null) {
            figure = new Figure()
                    .addData(this);
        }
        return figure;
    }

    /**
     * @return the layout of this data
     */
    protected final PL getLayout() {
        if (layout == null) {
            layout = createLayout();
            layout.addData(this);
        }
        return layout;
    }

    /**
     * @return the amount to expand the search window by in the x direction for finding shapes in the current view
     */
    protected double getSearchPaddingX() {
        return 0;
    }

    /**
     * @return the amount to expand the search window by in the y direction for finding shapes in the current view
     */
    protected double getSearchPaddingY() {
        return 0;
    }

    protected final PlotOptions getPlotOptions() {
        return getClass().getAnnotation(PlotOptions.class);
    }

    /**
     * @param i      the corresponding index for the rectangle
     * @param x      the min x position of the rectangle
     * @param y      the min y position of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @return a rectangle shape for the plot area
     */
    protected static PlotShape<XYLayout> createRectangle(PlotData<?, XYLayout> data, int i, double x, double y, double width, double height) {
        return new PlotShape.Rectangle(data, i, x, y, width, height);
    }

    /**
     * @param i the corresponding index for the marker
     * @param x the x position of the marker
     * @param y the y position of the marker
     * @return a marker to be used in the plot area
     */
    protected static PlotShape<XYLayout> createMarker(PlotData<?,XYLayout> data, int i, double x, double y) {
        return new PlotShape.PlotMarker(data, i, x, y);
    }

    protected static PlotShape<XYLayout> createPolyLine(RelationalData<?> data, int i, IntArrayList ids) {
        return new PlotShape.PolyLine(data, i, ids);
    }

    public abstract int size();
}
