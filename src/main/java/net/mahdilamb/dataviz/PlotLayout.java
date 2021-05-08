package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.figure.Group;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.ui.Label;
import net.mahdilamb.dataviz.utils.functions.BiDoubleConsumer;

import java.util.ArrayList;
import java.util.List;

import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;

/**
 * An abstract layout
 *
 * @param <PL> the concrete type of the plot layout
 */
public abstract class PlotLayout<PL extends PlotLayout<PL>> extends Group {

    /**
     * A list of the data in this layout
     */
    protected final List<PlotData<?, PL>> data = new ArrayList<>();
    protected Label title = new Label(EMPTY_STRING, Font.DEFAULT_FONT);
    protected PlotSelection<PL> selection;

    /**
     * Create an empty plot layout only containing the plot area
     */
    protected PlotLayout() {

    }

    /**
     * Add data to the plot layout
     *
     * @param data the data to add
     */
    protected final void addData(PlotData<?, PL> data) {
        this.data.add(data);
        onAdd(data);
    }

    @SuppressWarnings("unchecked")
    protected final void addAxis(PlotAxis<PL> axis) {
        axis.layout = (PL) this;
        add(axis);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    protected final void addAxes(PlotAxis<PL>... axes) {
        for (final PlotAxis<PL> axis : axes) {
            axis.layout = (PL) this;
        }
        addAll(axes);
    }

    @SuppressWarnings("unchecked")
    protected void setSelection(final PlotSelection<PL> selection) {
        this.selection = selection;
        selection.apply((PL) this);
        redraw();
    }

    @SuppressWarnings("unchecked")
    protected void clearSelection() {
        this.selection.clear((PL) this);
        this.selection = null;
        redraw();
    }

    static <PL extends PlotLayout<PL>> void redraw(final PlotLayout<PL> layout) {
        layout.redraw();
    }

    protected abstract void onAdd(PlotData<?, PL> data);

    protected abstract void panPlotArea(double dx, double dy);

    protected abstract void zoomPlotArea(double x, double y, double zoom) throws UnsupportedOperationException;

    public abstract void transformValueToPosition(double x, double y, BiDoubleConsumer xy);

    public abstract void transformPositionToValue(double x, double y, BiDoubleConsumer xy);

    protected static double getHomeMinX(PlotData<?, XYLayout> data) {
        return ((PlotBounds.XY) data.getBoundPreferences().home).getMinX();
    }

    protected static double getHomeMinY(PlotData<?, XYLayout> data) {
        return ((PlotBounds.XY) data.getBoundPreferences().home).getMinY();
    }

    protected static double getHomeMaxX(PlotData<?, XYLayout> data) {
        return ((PlotBounds.XY) data.getBoundPreferences().home).getMaxX();
    }

    protected static double getHomeMaxY(PlotData<?, XYLayout> data) {
        return ((PlotBounds.XY) data.getBoundPreferences().home).getMaxY();
    }

    public abstract PlotArea<PL> getPlotArea();

    public abstract void increaseZoom() throws UnsupportedOperationException;

    public abstract void decreaseZoom() throws UnsupportedOperationException;

    protected abstract void inputModeChanged(InputMode.State state);

    protected void clearCache() {
        getPlotArea().clearCache();
    }

    protected static <PL extends PlotLayout<PL>> double getScale(final PlotAxis<PL> plotAxis) {
        return plotAxis.scale;
    }

}
