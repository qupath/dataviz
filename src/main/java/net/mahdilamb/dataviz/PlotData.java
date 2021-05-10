package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
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
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Plot data is the source of data for generating traces which, in turn, generate shapes/glyph.
 *
 * @param <PL> the supported plot layout
 */
public abstract class PlotData<PD extends PlotData<PD, PL>, PL extends PlotLayout<PL>> implements FigureComponent<PD> {
    private static final Colormap DEFAULT_QUALITATIVE_COLORMAP = Colormaps.get("Plotly");
    private static final Colormap DEFAULT_SEQUENTIAL_COLORMAP = Colormaps.get("Viridis");

    protected Colormap qualitativeColormap = DEFAULT_QUALITATIVE_COLORMAP;
    protected Colormap sequentialColormap = DEFAULT_SEQUENTIAL_COLORMAP;

    private Figure figure;
    private PL layout;

    boolean anySelected = false;
    BooleanArrayList selected = null;

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
    final Map<PlotDataAttribute.Type, PlotDataAttribute> attributes = new EnumMap<>(PlotDataAttribute.Type.class);

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
        final PlotDataAttribute styler;
        if ((styler = getAttribute(PlotDataAttribute.Type.COLOR)) != null) {
            return styler.calculateColorOf(styler.getClass() == PlotDataAttribute.Categorical.class ?
                    qualitativeColormap :
                    sequentialColormap, i);
        }
        return qualitativeColormap.get(0);
    }

    protected double getSize(int i) {
        final PlotDataAttribute styler = getAttribute(PlotDataAttribute.Type.SIZE);
        if (styler != null) {
            if (styler.getClass() == PlotDataAttribute.Categorical.class) {
                //todo
            } else {
                return ((PlotDataAttribute.Numeric) styler).get(i);
            }
        }
        return Double.NaN;
    }


    @SuppressWarnings("unchecked")
    protected final PD refresh() {
        if (layout != null) {
            layout.clearCache();
            PlotLayout.redraw(layout);
        }
        return (PD) this;
    }

    protected final PlotDataAttribute addToHoverText(final PlotDataAttribute styler, String formatting, Supplier<?> supplier, String key, IntFunction<?> getter) {
        styler.defaultSeg = hoverFormatter.add(formatting, supplier);
        hoverFormatter.put(key, getter);
        //Todo clear overrideing
        /*

        boolean found = false;
        for (final Map.Entry<Attribute, PlotTrace> t : attributes()) {
            if (t.getValue() == trace) {
                found = true;
                break;
            }
        }
        if (found) {
            hoverFormatter.remove(trace.defaultSeg);
        }
         */
        return styler;
    }

    protected final void addAttribute(
            final String seriesName,
            final PlotDataAttribute.Type attribute,
            BiFunction<PlotDataAttribute.Type, DoubleSeries, PlotDataAttribute.Numeric> ifNumeric,
            BiFunction<PlotDataAttribute.Type, StringSeries, PlotDataAttribute.Categorical> ifCategorical
    ) throws DataFrameOnlyMethodException {
        if (dataFrame == null) {
            throw new DataFrameOnlyMethodException();
        }
        final Series<?> series = dataFrame.get(seriesName);
        final PlotDataAttribute styler;
        if (series.getType() == DataType.DOUBLE) {
            styler = ifNumeric.apply(attribute, series.asDouble());
        } else {
            styler = ifCategorical.apply(attribute, series.asString());
        }
        attributes.put(attribute, styler);
        refresh();
    }

    protected final void removeAttribute(final PlotDataAttribute.Type attribute) {
        final PlotDataAttribute styler = attributes.remove(attribute);
        if (styler != null) {
            hoverFormatter.remove(styler);
            //todo remove legend item/ colorbar
        }
        if (layout != null) {
            layout.clearCache();
            PlotLayout.redraw(layout);
        }
    }

    protected PlotDataAttribute getAttribute(final PlotDataAttribute.Type attribute) {
        return attributes.get(attribute);
    }

    protected boolean hasAttribute(final PlotDataAttribute.Type attribute) {
        return attributes.containsKey(attribute);
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

    protected abstract Legend.Glyph getGlyph(final PlotDataAttribute.Categorical attribute, int category);

    protected abstract Legend.Glyph getGlyph(final PlotDataAttribute.Numeric attribute, double value);

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
    protected static PlotShape<XYLayout> createMarker(PlotData<?, XYLayout> data, int i, double x, double y) {
        return new PlotShape.PlotMarker(data, i, x, y);
    }

    protected static PlotShape<XYLayout> createPolyLine(RelationalData<?> data, int i, IntArrayList ids) {
        return new PlotShape.PolyLine(data, i, ids);
    }

    public abstract int size();

    protected static double getRaw(PlotDataAttribute.Numeric styler, int i) {
        return styler.getRaw(i);
    }

    protected static Color calculateColor(final PlotDataAttribute.Categorical attribute, final Colormap colormap, int i) {
        return attribute.calculateColor(colormap, i);
    }
    protected static Color calculateColorOf(final PlotDataAttribute attribute, final Colormap colormap, int i) {
        return attribute.calculateColorOf(colormap, i);
    }
}
