package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.FigureBase;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.Side;
import net.mahdilamb.dataviz.io.FigureExporter;
import net.mahdilamb.dataviz.ui.IconStore;
import net.mahdilamb.dataviz.ui.ToggleButton;
import net.mahdilamb.dataviz.ui.Toolbar;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public final class Figure extends FigureBase<Figure> implements FigureComponent<Figure>, Themeable<Figure> {

    double paddingLeft = 5, paddingRight = 5, paddingTop = 5, paddingBottom = 5;

    PlotLayout<?> layout;

    final Legend legend = new Legend(this);
    final ColorScales colorScales = new ColorScales(this);
    private InputMode inputMode;
    ToggleButton toggleHover;

    /**
     * Create an empty figure
     */
    public Figure() {
        addAll(title, legend, colorScales);
    }

    /**
     * @return the plot layout
     */
    public PlotLayout<?> getLayout() {
        return layout;
    }

    /**
     * Update the most recent (last) layout
     *
     * @param fn the function to apply to the layout
     * @return this figure
     */
    public Figure updateLayout(Consumer<PlotLayout<?>> fn) {
        fn.accept(layout);
        update();
        return this;
    }

    /**
     * @return the legend in this figure
     */
    public Legend getLegend() {
        return legend;
    }

    /**
     * Apply a function to the legend
     *
     * @param legendConsumer the function to apply
     * @return this figure
     */
    public Figure updateLegend(Consumer<Legend> legendConsumer) {
        legendConsumer.accept(legend);
        markComponentLayoutAsOld(legend);
        update();
        return this;
    }

    @Override
    public final Figure getFigure() {
        return this;
    }

    @Override
    public Figure updateFigure(Consumer<Figure> fn) {
        fn.accept(this);
        update();
        return this;
    }

    /**
     * @return the current state of the input mode
     */
    public final InputMode.State getInputMode() {
        if (inputMode == null) {
            return null;
        }
        return inputMode.state;
    }

    /**
     * Add data to the figure
     *
     * @param plotData the data to add
     * @return this figure
     */
    public Figure addData(final PlotData<?, ?> plotData) {
        if (layout != null && layout.getClass() != plotData.getLayout().getClass()) {
            System.err.println("Old layout removed while adding data");
            remove(layout);
        }
        remove(colorScales);
        remove(legend);
        addAll(layout = plotData.getLayout(), legend, colorScales);
        markComponentLayoutAsOld(legend);
        markComponentLayoutAsOld(colorScales);
        update();
        return this;
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <PL extends PlotLayout<PL>> Figure addData(final PlotData<?, PL> first, final PlotData<?, PL>... rest) {
        if (layout != null && layout.getClass() != first.getLayout().getClass()) {
            System.err.println("Old layout removed while adding data");
            remove(layout);
        }
        remove(colorScales);
        remove(legend);
        addAll(layout = first.getLayout(), legend, colorScales);
        for (final PlotData<?, PL> d : rest) {
            ((PL) layout).addData(d);
        }
        markComponentLayoutAsOld(legend);
        markComponentLayoutAsOld(colorScales);
        update();
        return this;
    }


    public <PD extends PlotData<PD, ?>> Figure updateData(final Class<? extends PD> type, Consumer<PD> func) {
        for (final PlotData<?, ?> data : layout.data) {
            if (type.isInstance(data)) {
                func.accept(type.cast(data));
            }
        }
        update();
        return this;
    }

    static boolean layoutIfUsing(KeyArea<?> a, KeyArea<?> b, final Side side, Renderer renderer, double minX, double minY, double maxX, double maxY) {
        boolean layedOut = false;
        //TODO simplify
        //draw legend closest to the plot
        boolean swap = false;
        switch (side) {
            case TOP:
            case RIGHT:
                swap = a.getClass() == Legend.class;//do colorscales first
                break;
            case LEFT:
            case BOTTOM:
                swap = a.getClass() == ColorScales.class;
                break;
        }
        if (swap) {
            KeyArea<?> tmp = a;
            a = b;
            b = tmp;
        }

        if (!a.isFloating && a.side == side) {
            layout(a, renderer, minX, minY, maxX, maxY);
            layedOut = true;
            if (!a.isFloating) {
                switch (a.side) {
                    case RIGHT:
                        maxX -= a.getWidth();
                        break;
                    case LEFT:
                        minX += a.getWidth();
                        break;
                    case TOP:
                        minY += a.getHeight();
                        break;
                    case BOTTOM:
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
        }
        if (!b.isFloating && b.side == side) {
            layout(b, renderer, minX, minY, maxX, maxY);
            layedOut = true;
        }
        return layedOut;
    }

    @Override
    protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        boolean titleDrawn = false;
        setBoundsFromExtent(minX, minY, maxX, maxY);
        minX += paddingLeft;
        maxX -= paddingRight;
        minY += paddingTop;
        maxY -= paddingBottom;
        if (title.isVisible()) {
            layout(title, renderer, minX, minY, maxX, maxY);
            minY += title.getHeight();
            titleDrawn = true;
        } else {
            minY += 25;
        }
        layoutIfUsing(legend, colorScales, Side.RIGHT, renderer, minX, minY, maxX, maxY);
        layoutIfUsing(legend, colorScales, Side.LEFT, renderer, minX, minY, maxX, maxY);
        layoutIfUsing(legend, colorScales, Side.TOP, renderer, minX, minY, maxX, maxY);
        if (!legend.isFloating) {
            switch (legend.side) {
                case RIGHT:
                    maxX -= legend.getWidth();
                    break;
                case LEFT:
                    minX += legend.getWidth();
                    break;
                case TOP:
                    minY += legend.getHeight();
                    break;
                case BOTTOM:
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        if (!colorScales.isFloating) {
            switch (colorScales.side) {
                case RIGHT:
                    maxX -= colorScales.getWidth();
                    break;
                case LEFT:
                    minX += colorScales.getWidth();
                    break;
                case TOP:
                    minY += colorScales.getHeight();
                    break;
                case BOTTOM:
                    maxY -= colorScales.getHeight();
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        if (layoutIfUsing(legend, colorScales, Side.BOTTOM, renderer, minX, minY, maxX, maxY)) {
            maxY -= legend.getHeight();
        }
        if (!titleDrawn && title.isVisible()) {
            layout(title, renderer, minX, minY, maxX, maxY);
            minY += title.getHeight();
        }
        if (layout != null) {
            layout(layout, renderer, minX, minY, maxX, maxY);
        }
        if (legend.isFloating) {
            layout(legend, renderer, minX, minY, maxX, maxY);

        }
        if (colorScales.isFloating) {
            layout(colorScales, renderer, minX, minY, maxX, maxY);
        }

    }

    protected BufferedImage getMaterialIcon(IconStore.MaterialIconKey key, Color backgroundColor) {
        return Component.getMaterialIcon(getContext().getRenderer(), key, backgroundColor);
    }

    protected BufferedImage getDataVizIcon(IconStore.DataVizIconKey key, Color backgroundColor) {
        return Component.getDataVizIcon(getContext().getRenderer(), key, backgroundColor);
    }

    @Override
    protected Toolbar createToolbar() {
        boolean enableZoom = true;
        final Toolbar toolbar = new Toolbar();
        toolbar.addIconButton(getMaterialIcon(IconStore.MaterialIconKey.CAMERA_ALT, null), "Save")
                .setOnMouseClick(() -> getContext().getRenderer().saveAs(getOutputPath(getContext().getRenderer(), FigureExporter.getSupportedExtensions(), ".svg")));
        for (final PlotData<?, ?> data : layout.data) {
            if (data.getPlotOptions() == null) {
                continue;
            }
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
        toggleHover = toolbar.addToggleButton(getMaterialIcon(IconStore.MaterialIconKey.LABEL, null), "Toggle showing data on hover", true);
        return toolbar;
    }

    @Override
    public Figure apply(Theme theme) {
        theme.figure.accept(this);
        update();
        return this;
    }

    void setDefaultQualitativeColormap(final Colormap colormap) {
        for (final PlotData<?, ?> data : layout.data) {
            if (data.qualitativeColormap == null) {
                data.qualitativeColormap = colormap;
            }
        }
    }

    void setDefaultSequentialColormap(final Colormap colormap) {
        for (final PlotData<?, ?> data : layout.data) {
            if (data.sequentialColormap == null) {
                data.sequentialColormap = colormap;
            }
        }
    }
}
