package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.FigureBase;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.io.FigureExporter;
import net.mahdilamb.dataviz.ui.IconStore;
import net.mahdilamb.dataviz.ui.Toolbar;

import java.awt.*;

public final class Figure extends FigureBase<Figure> {
    private static final Colormap DEFAULT_QUALITATIVE_COLORMAP = Colormaps.get("Plotly");
    private static final Colormap DEFAULT_SEQUENTIAL_COLORMAP = Colormaps.get("Viridis");

    Colormap qualitativeColormap = DEFAULT_QUALITATIVE_COLORMAP;
    Colormap sequentialColormap = DEFAULT_SEQUENTIAL_COLORMAP;

    double paddingLeft = 5, paddingRight = 5, paddingTop = 30, paddingBottom = 5;

    PlotLayout<?> layout;

    final Legend legend = new Legend();
    final ColorScales colorScales = new ColorScales();
    private InputMode inputMode;


    /**
     * Create an empty figure
     */
    public Figure() {
        addAll(title, legend, colorScales);
    }

    /**
     * Add data to the figure
     *
     * @param plotData the data to add
     * @return this figure
     */
    public Figure addData(final PlotData<?> plotData) {
        if (layout != null && layout.getClass() != plotData.getLayout().getClass()) {
            System.err.println("Old layout removed while adding data");
            remove(layout);
        }
        add(layout = plotData.getLayout());
        update();
        return this;
    }

    @Override
    protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
        setBoundsFromExtent(minX, minY, maxX, maxY);
        minX += paddingLeft;
        maxX -= paddingRight;
        minY += paddingTop;
        maxY -= paddingBottom;
        if (title.isVisible()) {
            layout(title, renderer, minX, minY, maxX, maxY);
            minY = title.getWidth() + title.getY();
        }
        if (layout != null) {
            layout(layout, renderer, minX, minY, maxX, maxY);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T getMaterialIcon(IconStore.MaterialIconKey key, Color backgroundColor) {
        return Component.getMaterialIcon(((Renderer<T>) getContext().getRenderer()), key, backgroundColor);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getDataVizIcon(IconStore.DataVizIconKey key, Color backgroundColor) {
        return Component.getDataVizIcon(((Renderer<T>) getContext().getRenderer()), key, backgroundColor);
    }

    @Override
    protected Toolbar createToolbar() {
        boolean enableZoom = true;
        final Toolbar toolbar = new Toolbar();
        toolbar.addIconButton(getMaterialIcon(IconStore.MaterialIconKey.CAMERA_ALT, null), "Save")
                .setOnMouseClick(() -> getContext().getRenderer().saveAs(getOutputPath(getContext().getRenderer(), FigureExporter.getSupportedExtensions(), ".svg")));
        for (final PlotData<?> data : layout.data) {
            enableZoom &= data.getPlotOptions().supportsZoom();
        }
        if (inputMode == null) {
            inputMode = new InputMode(this, layout);
        }
        if (inputMode.size() != 0) {
            toolbar.addSpacing();
            toolbar.addToggleButtonGroup(inputMode);
            toolbar.addSpacing();
        }
        if (enableZoom) {
            toolbar.addIconButton(getMaterialIcon(IconStore.MaterialIconKey.ADD_CIRCLE, null), "Zoom in")
                    .setOnMouseClick(layout::increaseZoom);
            toolbar.addIconButton(getMaterialIcon(IconStore.MaterialIconKey.REMOVE_CIRCLE, null), "Zoom out")
                    .setOnMouseClick(layout::decreaseZoom);
            toolbar.addSpacing();
        }
        return toolbar;
    }

    public InputMode.State getInputMode() {
        if (inputMode == null) {
            return null;
        }
        return inputMode.state;
    }

}
