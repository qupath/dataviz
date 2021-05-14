package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.figure.AbstractComponent;
import net.mahdilamb.dataviz.figure.Group;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.layouts.XAxis;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.layouts.YAxis;
import net.mahdilamb.dataviz.ui.Label;
import net.mahdilamb.dataviz.utils.functions.BiDoubleConsumer;
import net.mahdilamb.dataviz.utils.functions.QuadDoubleConsumer;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    protected boolean supportsWheelZoom = true;
    Color background = new Color(229, 236, 246);
    Map<PlotData<?, PL>, Color> uncategorizedColors;
    PlotDataAttribute.UncategorizedTrace uncategorized;

    /**
     * Create an empty plot layout only containing the plot area
     */
    protected PlotLayout() {

    }

    public abstract PlotArea<PL> getPlotArea();

    public abstract XAxis getXAxis() throws UnsupportedOperationException;

    public abstract YAxis getYAxis() throws UnsupportedOperationException;


    /**
     * Add data to the plot layout
     *
     * @param data the data to add
     */
    @SuppressWarnings("unchecked")
    protected final void addData(PlotData<?, PL> data) {
        this.data.add(data);
        data.layout = (PL) this;
        uncategorized = null;
        onAdd(data);

    }

    protected abstract void onAdd(PlotData<?, PL> data);

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

    protected void clearSelection() {
        for (final PlotData<?, PL> data : data) {
            data.selected = null;
        }
        this.selection = null;
        redraw();
    }

    /**
     * @return the selection in the layout. May be {@code null}
     */
    public PlotSelection<PL> getSelection() {
        return selection;
    }

    /**
     * @return the background color of the plot area
     */
    public Color getBackgroundColor() {
        return background;
    }

    protected abstract void panPlotArea(double dx, double dy);

    protected abstract void zoomPlotArea(double x, double y, double zoom) throws UnsupportedOperationException;

    public abstract void increaseZoom() throws UnsupportedOperationException;

    public abstract void decreaseZoom() throws UnsupportedOperationException;

    public abstract void transformValueToPosition(double x, double y, BiDoubleConsumer xy) throws UnsupportedOperationException;

    public abstract void transformPositionToValue(double x, double y, BiDoubleConsumer xy) throws UnsupportedOperationException;

    public abstract void transformValueToPosition(double x0, double y0, double x1, double y1, QuadDoubleConsumer xy) throws UnsupportedOperationException;

    public abstract void transformPositionToValue(double x0, double y0, double x1, double y1, QuadDoubleConsumer xy) throws UnsupportedOperationException;

    PlotDataAttribute.UncategorizedTrace getUncategorized(final Figure figure) {
        if (uncategorized == null) {
            int numUncategorized = 0;
            for (final PlotData<?, ?> data : figure.layout.data) {
                if (data.attributes.size() == 0) {
                    if (data.showInLegend && data.name != null && data.name.length() > 0) {
                        ++numUncategorized;
                    }
                }
            }
            if (numUncategorized > 0) {
                final PlotData<?, ?>[] uncategorized = new PlotData[numUncategorized];
                numUncategorized = 0;
                for (final PlotData<?, ?> data : figure.layout.data) {
                    if (data.attributes.size() == 0) {
                        if (data.showInLegend && data.name != null && data.name.length() > 0) {
                            uncategorized[numUncategorized++] = data;
                        }
                    }
                }
                this.uncategorized = new PlotDataAttribute.UncategorizedTrace(figure, uncategorized);
            }
        }
        return uncategorized;
    }

    protected abstract void inputModeChanged(InputMode.State state);

    protected void clearCache() {
        getPlotArea().clearCache();
    }

    static <PL extends PlotLayout<PL>> void redraw(final PlotLayout<PL> layout) {
        layout.redraw();
    }

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

    protected static <PL extends PlotLayout<PL>> double getScale(final PlotAxis<PL> plotAxis) {
        return plotAxis.scale;
    }

    protected static <PL extends PlotLayout<PL>> void clearTooltip(PlotArea<PL> plotArea) {
        plotArea.clearTooltip();
    }

    protected static <PL extends PlotLayout<PL>> boolean supportsWheelZoom(PlotData<?, PL> data) {
        return data.supportsWheelZoom();
    }

    protected static <PL extends PlotLayout<PL>> boolean isUncategorized(PlotData<?, PL> data) {
        return data.dataFrame == null && data.name != null;
    }

    Color getColor(PlotData<?, PL> data) {
        if (uncategorizedColors == null) {
            uncategorizedColors = new HashMap<>();
        }
        final Color cached;
        if ((cached = uncategorizedColors.get(data)) != null) {
            return cached;
        }
        final Color c = data.getQualitativeColormap().get(((float) (uncategorizedColors.size()) % data.getQualitativeColormap().size()) / (data.getQualitativeColormap().size() - 1));
        uncategorizedColors.put(data, c);
        return c;
    }
}
