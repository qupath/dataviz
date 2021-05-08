package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.figure.BufferingStrategy;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.ui.Label;
import net.mahdilamb.dataviz.utils.rtree.RTree;

import java.awt.*;
import java.util.List;

public abstract class PlotArea<PL extends PlotLayout<PL>> extends Component {
    protected PL layout;
    boolean mouseDown = false;
    double startX, startY;

    protected PlotArea(PL layout, BufferingStrategy<? extends PlotArea<PL>, ?> bufferingStrategy) {
        super(bufferingStrategy);
        this.layout = layout;
    }

    protected PlotArea(PL layout) {
        super();
        this.layout = layout;
    }

    @Override
    protected final void onMouseDown(boolean ctrlDown, boolean shiftDown, double x, double y) {
        startX = x;
        startY = y;
        mouseDown = true;
        setTooltip(null);
        super.onMouseDown(ctrlDown, shiftDown, x, y);
    }

    @Override
    protected final void onMouseUp(boolean ctrlDown, boolean shiftDown, double x, double y) {
        mouseDown = false;
        super.onMouseUp(ctrlDown, shiftDown, x, y);
    }

    @Override
    protected final void onMouseEnter(boolean ctrlDown, boolean shiftDown, double x, double y) {
        getContext().getRenderer().setToolbarVisibility(true);
        super.onMouseEnter(ctrlDown, shiftDown, x, y);
    }

    @Override
    protected final void onMouseExit(boolean ctrlDown, boolean shiftDown, double x, double y) {
        getContext().getRenderer().setToolbarVisibility(false);
        super.onMouseExit(ctrlDown, shiftDown, x, y);
    }

    protected final InputMode.State getInputMode() {
        return ((Figure) getContext().getRenderer().getFigure()).getInputMode();
    }

    @Override
    protected final void onMouseMove(boolean ctrlDown, boolean shiftDown, double x, double y) {
        if (mouseDown) {
            if (getInputMode() == InputMode.State.POLYGON_SELECT) {

            } else if (getInputMode() == InputMode.State.PAN) {
                layout.panPlotArea(x - startX, y - startY);
                startX = x;
                startY = y;
            }
        } else {
            if (getInputMode() == InputMode.State.POLYGON_SELECT) {
                if (getSelection(layout) != null && !getSelection(layout).isClosed()) {
                    //todo
                }
            } else {
                if (((Figure) getContext().getRenderer().getFigure()).toggleHover.getValue()) {
                   //todo System.out.println(contains(x, getY(), getY() + getHeight()).size());
                    final List<? extends PlotShape<PL>> matches = contains(x, y);
                    if (!matches.isEmpty()) {
                        setTooltip(matches.get(matches.size() - 1).createTooltip());
                    } else {
                        setTooltip(null);
                    }
                }
            }
        }
        super.onMouseMove(ctrlDown, shiftDown, x, y);
    }

    protected abstract List<? extends PlotShape<PL>> contains(double x, double y);

    protected abstract List<? extends PlotShape<PL>> contains(double x, double minY, double maxY);


    @Override
    protected void onMouseScrolled(boolean controlDown, boolean shiftDown, double x, double y, double rotation) {
        //todo if allowed
        layout.zoomPlotArea(x, y, rotation);
    }

    protected static <PL extends PlotLayout<PL>> List<RTree<PlotShape<PL>>> getShapes(final PlotData<?, PL> data) {
        return data.shapes;
    }

    protected static <PL extends PlotLayout<PL>> List<PlotData<?, PL>> getData(PlotLayout<PL> layout) {
        return layout.data;
    }

    protected static <PL extends PlotLayout<PL>, T> void draw(PL layout, PlotShape<PL> shape, Renderer<T> renderer, GraphicsBuffer<T> canvas) {
        shape.draw(layout, renderer, canvas);
    }

    protected static <PL extends PlotLayout<PL>> Label getTitle(final PlotLayout<PL> layout) {
        return layout.title;
    }

    protected static <PL extends PlotLayout<PL>, T> void drawGrid(final PL layout, final PlotAxis<PL> axis, Renderer<T> renderer, GraphicsBuffer<T> canvas) {
        axis.drawGrid(layout, renderer, canvas);
    }

    protected static <PL extends PlotLayout<PL>> double getSearchPaddingX(PlotData<?, PL> data) {
        return data.getSearchPaddingX();
    }

    protected static <PL extends PlotLayout<PL>> double getSearchPaddingY(PlotData<?, PL> data) {
        return data.getSearchPaddingY();
    }

    protected static <PL extends PlotLayout<PL>> Color getColor(PlotData<?, PL> data, PlotShape<PL> shape) {
        final int i = shape == null ? 0 : shape.i;//TODO
        if (data.anySelected && !data.selected.get(i)) {
            return data.selectedColor;
        }
        return data.getColor(i);
    }

    protected static <PL extends PlotLayout<PL>> PlotSelection<PL> getSelection(PL layout) {
        return layout.selection;
    }

    protected static <PL extends PlotLayout<PL>> boolean isSelectionClosed(PL layout) {
        return layout.selection != null && layout.selection.isClosed();
    }

    protected static <PL extends PlotLayout<PL>> void setSelection(PL layout, PlotSelection<PL> selection) {
        layout.setSelection(selection);
        layout.clearCache();

    }

    protected static <PL extends PlotLayout<PL>> void clearSelection(PL layout) {
        layout.clearSelection();
        layout.clearCache();

    }

    protected static <PL extends PlotLayout<PL>> void applySelection(PL layout, PlotSelection<PL> selection) {
        selection.apply(layout);
        layout.clearCache();//todo clear region

    }

    protected static <PL extends PlotLayout<PL>> boolean hasSelection(PlotData<?, PL> layout) {
        return layout.anySelected;
    }

    protected abstract void clearCache();

    protected static boolean isSelection(final InputMode.State state) {
        return state != null && state.isSelection;
    }

    protected static <PL extends PlotLayout<PL>> double getScale(PlotAxis<PL> plotAxis) {
        return plotAxis.scale;
    }
}
