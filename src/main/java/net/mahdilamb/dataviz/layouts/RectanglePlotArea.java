package net.mahdilamb.dataviz.layouts;

import net.mahdilamb.dataviz.*;
import net.mahdilamb.dataviz.figure.BufferingStrategy;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.ClipShape;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.dataviz.utils.SpatialCache;

import java.awt.*;

public class RectanglePlotArea extends PlotArea<XYLayout> {
    /**
     * LRU based buffer for the plot area
     *
     * @param <IMG> the type of the image in the renderer
     */
    static final class RectangularPlotAreaBufferStrategy<IMG> extends BufferingStrategy.CustomBufferedStrategy<RectanglePlotArea, IMG, SpatialCache<GraphicsBuffer<IMG>>> {
        static final RectangularPlotAreaBufferStrategy<?> INSTANCE = new RectangularPlotAreaBufferStrategy<>();

        private RectangularPlotAreaBufferStrategy() {
            super();
        }

        protected GraphicsBuffer<IMG> createTile(final RectanglePlotArea component, Renderer<IMG> renderer, GraphicsBuffer<IMG> context, double minX, double minY, long width, long height) {
            final double xMin = component.layout.getXAxis().getValueFromPosition(minX + component.getX() + (component.layout.getXAxis().reversed ? width : 0)),
                    yMin = component.layout.getYAxis().getValueFromPosition(minY + component.getY() + (component.layout.getYAxis().reversed ? height : 0)),
                    xMax = component.layout.getXAxis().getValueFromPosition(minX + component.getX() + (component.layout.getXAxis().reversed ? 0 : width)),
                    yMax = component.layout.getYAxis().getValueFromPosition(minY + component.getY() + (component.layout.getYAxis().reversed ? 0 : height));
            if (!component.containsShapes(xMin, yMin, xMax, yMax)) {
                return null;
            }
            final GraphicsBuffer<IMG> tile = createBuffer(renderer, width, height, minX + component.getX(), minY + component.getY(), 1, 1, 1, 1);
            component.drawShapes(renderer, tile, xMin, yMin, xMax, yMax);
            // tile.setStroke(Color.black);
            // tile.strokeRect(minX + component.getX(), minY + component.getY(),width,height);
            return tile;
        }

        @Override
        protected void drawBuffered(final RectanglePlotArea plotArea, Renderer<IMG> renderer, GraphicsBuffer<IMG> context) {
            context.setClip(ClipShape.RECTANGLE, plotArea.getX(), plotArea.getY(), plotArea.getWidth(), plotArea.getHeight());
            plotArea.drawGrid(renderer, context);
            SpatialCache<GraphicsBuffer<IMG>> cache;
            if ((cache = getBufferStore(plotArea)) == null) {
                //todo all selection
                final int tileSize = plotArea.getInputMode() == InputMode.State.POLYGON_SELECT ? 48 : 256;
                cache = setBufferStore(plotArea, new SpatialCache<>(128, tileSize, tileSize,
                        (a, b, c, d) -> createTile(plotArea, renderer, context, a, b, c, d),
                        (x, y, tile) -> drawBuffer(renderer, context, tile, plotArea.getX() + x, plotArea.getY() + y)
                ));
            }
            cache.draw(
                    plotArea.getWidth(), plotArea.getHeight(),
                    plotArea.layout.getXAxis().reversed, plotArea.layout.getYAxis().reversed,
                    plotArea.layout.getXAxis().lower, plotArea.layout.getYAxis().lower, plotArea.layout.getXAxis().upper, plotArea.layout.getYAxis().upper);

            plotArea.drawSelection(renderer, context);
            context.clearClip();

        }

        @Override
        protected void clearBuffer(Renderer<IMG> renderer, RectanglePlotArea component) {
            //TODO check if the change requires buffer change
        }

        @Override
        public SpatialCache<GraphicsBuffer<IMG>> getBufferStore(RectanglePlotArea component) {
            return super.getBufferStore(component);
        }

        @Override
        public SpatialCache<GraphicsBuffer<IMG>> setBufferStore(RectanglePlotArea component, SpatialCache<GraphicsBuffer<IMG>> graphicsBufferSpatialCache) {
            return super.setBufferStore(component, graphicsBufferSpatialCache);
        }
    }

    Color background = new Color(229, 236, 246);

    public RectanglePlotArea(XYLayout layout) {
        super(layout, RectangularPlotAreaBufferStrategy.INSTANCE);

    }

    @Override
    protected void clearCache() {
        RectangularPlotAreaBufferStrategy.INSTANCE.setBufferStore(this, null);
    }

    @SuppressWarnings("unchecked")
    final <IMG> SpatialCache<GraphicsBuffer<IMG>> getCache() {
        return ((RectangularPlotAreaBufferStrategy<IMG>) RectangularPlotAreaBufferStrategy.INSTANCE).getBufferStore(this);
    }

    @Override
    protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
        setBoundsFromExtent(minX, minY, maxX, maxY);

    }

    <T> void drawGrid(Renderer<T> renderer, GraphicsBuffer<T> canvas) {
        canvas.setFill(background);
        canvas.fillRect(getX(), getY(), getWidth(), getHeight());
        drawGrid(layout, layout.xAxis, renderer, canvas);
        drawGrid(layout, layout.yAxis, renderer, canvas);
        drawGrid(layout, layout.secondaryXAxis, renderer, canvas);
        drawGrid(layout, layout.secondaryYAxis, renderer, canvas);
    }

    protected <T> void drawSelection(Renderer<T> renderer, GraphicsBuffer<T> canvas) {
        final PlotSelection.Polygon selection;
        if (getInputMode() == InputMode.State.POLYGON_SELECT && getSelection(layout) != null && (selection = (PlotSelection.Polygon) getSelection(layout)).size() != 0) {
            canvas.setStroke(isSelectionClosed(layout) ? Stroke.SOLID : Stroke.DASHED);
            canvas.setStroke(Color.DARK_GRAY);
            canvas.setFill(new Color(.2f, .2f, .2f, .2f));
            canvas.beginPath();
            for (int i = 0; i < selection.size(); ++i) {
                int finalI = i;
                layout.transformValueToPosition(selection.getX(i), selection.getY(i),
                        (x, y) -> {
                            if (finalI == 0) {
                                canvas.moveTo(x, y);
                            } else {
                                canvas.lineTo(x, y);
                            }
                        });
            }
            canvas.closePath();
            canvas.fill();
            canvas.stroke();
        }

    }

    protected <T> void drawShapes(Renderer<T> renderer, GraphicsBuffer<T> canvas, double xMin, double yMin, double xMax, double yMax) {
        for (final PlotData<XYLayout> data : getData(layout)) {
            final double searchXMin = xMin - getSearchPaddingX(data) / layout.getXAxis().scale,
                    searchYMin = yMin - getSearchPaddingY(data) / layout.getYAxis().scale,
                    searchXMax = xMax + getSearchPaddingX(data) / layout.getXAxis().scale,
                    searchYMax = yMax + getSearchPaddingY(data) / layout.getYAxis().scale;

            canvas.setStroke(Color.white);
            canvas.setStroke(Stroke.SOLID);
            if (hasSelection(data)) {
                canvas.setStroke(Color.white);
                canvas.setStroke(Stroke.SOLID);
                for (final RTree<PlotShape<XYLayout>> tree : getShapes(data)) {
                    for (final PlotShape<XYLayout> shape : tree.search(searchXMin, searchYMin, searchXMax, searchYMax)) {
                        canvas.setFill(getColor(data, shape));
                        fill(layout, shape, renderer, canvas);
                        stroke(layout, shape, renderer, canvas);
                    }
                }
            } else {
                canvas.setStroke(Color.white);
                canvas.setStroke(Stroke.SOLID);
                canvas.setFill(getColor(data, null));
                for (final RTree<PlotShape<XYLayout>> tree : getShapes(data)) {
                    for (final PlotShape<XYLayout> shape : tree.search(searchXMin, searchYMin, searchXMax, searchYMax)) {
                        fill(layout, shape, renderer, canvas);
                        stroke(layout, shape, renderer, canvas);
                    }
                }
            }
        }
    }

    protected boolean containsShapes(double xMin, double yMin, double xMax, double yMax) {
        for (final PlotData<XYLayout> data : getData(layout)) {
            for (final RTree<PlotShape<XYLayout>> tree : getShapes(data)) {
                final double searchXMin = xMin - getSearchPaddingX(data) / layout.getXAxis().scale,
                        searchYMin = yMin - getSearchPaddingY(data) / layout.getYAxis().scale,
                        searchXMax = xMax + getSearchPaddingX(data) / layout.getXAxis().scale,
                        searchYMax = yMax + getSearchPaddingY(data) / layout.getYAxis().scale;
                if (tree.collides(searchXMin, searchYMin, searchXMax, searchYMax)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {
        canvas.setClip(ClipShape.RECTANGLE, getX(), getY(), getWidth(), getHeight());
        drawGrid(renderer, canvas);
        drawShapes(renderer, canvas, layout.getXAxis().lower, layout.getYAxis().lower, layout.getXAxis().upper, layout.getYAxis().upper);
        drawSelection(renderer, canvas);
        canvas.clearClip();
    }


    @Override
    protected void onMouseClick(boolean ctrlDown, boolean shiftDown, double x, double y) {
        if (getInputMode() == InputMode.State.POLYGON_SELECT) {
            layout.transformPositionToValue(x, y, (_x, _y) -> {
                if (getSelection(layout) == null) {
                    setSelection(layout, new PlotSelection.Polygon(true));
                }
                ((PlotSelection.Polygon) getSelection(layout)).add(_x, _y);
            });
            applySelection(layout, getSelection(layout));
            redraw();
        }
    }

    @Override
    protected void onMouseDoubleClicked(boolean ctrlDown, boolean shiftDown, double x, double y) {
        if (getSelection(layout) != null) {
            ((PlotSelection.Polygon) getSelection(layout)).close();//TODO
            applySelection(layout, getSelection(layout));
            redraw();
        }
    }


}
