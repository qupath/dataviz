package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.utils.BooleanArrayList;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.utils.rtree.RTree;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Plot data is the source of data for generating traces which, in turn, generate shapes/glyph.
 *
 * @param <PL> the supported plot layout
 */
public abstract class PlotData<PL extends PlotLayout<PL>> implements FigureComponent {

    private Figure figure;
    private PL layout;

    boolean anySelected = false;
    BooleanArrayList selected = null;
    Color selectedColor = Colors.lightgray;

    /**
     * Whether to draw all the elements after setting the global style, or style and draw each shape individually
     */
    boolean singleStyle = true;
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
    final Set<PlotTrace> traces = new LinkedHashSet<>(PlotTrace.Attribute.values().length);

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
        layout.clearCache();
    }

    protected void addShapes(PlotShape<PL>[] shapes) {
        addShapes(shapes, true);
    }

    protected abstract Color getColor(int i);

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
    protected static PlotShape<XYLayout> createRectangle(PlotData<XYLayout> data, int i, double x, double y, double width, double height) {
        return new PlotShape.Rectangle(data, i, x, y, width, height);
    }

    /**
     * @param i    the corresponding index for the marker
     * @param x    the x position of the marker
     * @param y    the y position of the marker
     * @param size the size of the marker
     * @return a marker to be used in the plot area
     */
    protected static PlotShape<XYLayout> createMarker(PlotData<XYLayout> data, int i, double x, double y, double size) {
        return new PlotShape.PlotMarker(data, i, x, y, size);
    }

    public abstract int size();
}
