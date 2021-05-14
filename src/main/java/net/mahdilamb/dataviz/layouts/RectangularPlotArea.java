package net.mahdilamb.dataviz.layouts;

import net.mahdilamb.dataviz.*;
import net.mahdilamb.dataviz.figure.BufferingStrategy;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.ClipShape;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.utils.rtree.Node2D;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.dataviz.utils.SpatialCache;
import net.mahdilamb.dataviz.utils.rtree.RectangularNode;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RectangularPlotArea extends PlotArea<XYLayout> {
    /**
     * LRU based buffer for the plot area
     */
    static final class RectangularPlotAreaBufferStrategy extends BufferingStrategy.CustomBufferedStrategy<RectangularPlotArea, SpatialCache<GraphicsBuffer>> {
        static final RectangularPlotAreaBufferStrategy INSTANCE = new RectangularPlotAreaBufferStrategy();
        private volatile ExecutorService background;
        private RectangularPlotAreaBufferStrategy() {
            super();
        }

        protected GraphicsBuffer createTile(final RectangularPlotArea component, Renderer renderer, GraphicsBuffer context, double minX, double minY, long width, long height) {
            final double xMin = component.layout.getXAxis().getValueFromPosition(minX + component.getX() + (component.layout.getXAxis().reversed ? width : 0)),
                    yMin = component.layout.getYAxis().getValueFromPosition(minY + component.getY() + (component.layout.getYAxis().reversed ? height : 0)),
                    xMax = component.layout.getXAxis().getValueFromPosition(minX + component.getX() + (component.layout.getXAxis().reversed ? 0 : width)),
                    yMax = component.layout.getYAxis().getValueFromPosition(minY + component.getY() + (component.layout.getYAxis().reversed ? 0 : height));
            if (!component.containsShapes(xMin, yMin, xMax, yMax)) {
                return null;
            }
            final GraphicsBuffer tile = createBuffer(width, height, minX + component.getX(), minY + component.getY(), 0, 0, 0, 0);
            component.drawShapes(renderer, tile, xMin, yMin, xMax, yMax);
            return tile;
        }
        protected GraphicsBuffer createBackgroundTile(final RectangularPlotArea component, Renderer renderer, GraphicsBuffer context, double minX, double minY, long width, long height) {
            final double xMin = component.layout.getXAxis().getValueFromPosition(minX + component.getX() + (component.layout.getXAxis().reversed ? width : 0)),
                    yMin = component.layout.getYAxis().getValueFromPosition(minY + component.getY() + (component.layout.getYAxis().reversed ? height : 0)),
                    xMax = component.layout.getXAxis().getValueFromPosition(minX + component.getX() + (component.layout.getXAxis().reversed ? 0 : width)),
                    yMax = component.layout.getYAxis().getValueFromPosition(minY + component.getY() + (component.layout.getYAxis().reversed ? 0 : height));
            if (!component.containsShapes(xMin, yMin, xMax, yMax)) {
                return null;
            }
            final GraphicsBuffer tile = createBufferNonMain(width, height, minX + component.getX(), minY + component.getY(), 0, 0, 0, 0);
            component.drawShapes(renderer, tile, xMin, yMin, xMax, yMax);
            return tile;
        }
        @Override
        protected void drawBuffered(final RectangularPlotArea plotArea, Renderer renderer, GraphicsBuffer context) {
            context.setClip(ClipShape.RECTANGLE, plotArea.getX(), plotArea.getY(), plotArea.getWidth(), plotArea.getHeight());
            plotArea.drawGrid(renderer, context);
            SpatialCache<GraphicsBuffer> cache;
            if ((cache = getBufferStore(plotArea)) == null) {
                final int tileSize = isSelection(plotArea.getInputMode()) ? 48 : 256;
                cache = setBufferStore(plotArea, new SpatialCache<>(128, tileSize, tileSize,
                        (a, b, c, d) -> createTile(plotArea, renderer, context, a, b, c, d),
                        (a, b, c, d) -> createBackgroundTile(plotArea, renderer, context, a, b, c, d),
                        (x, y, tile) -> drawBuffer(context, tile, plotArea.getX() + x, plotArea.getY() + y)
                ));
            }
            cache.draw(
                    plotArea.getWidth(), plotArea.getHeight(),
                    plotArea.layout.getXAxis().reversed, plotArea.layout.getYAxis().reversed,
                    plotArea.layout.getXAxis().lower, plotArea.layout.getYAxis().lower, plotArea.layout.getXAxis().upper, plotArea.layout.getYAxis().upper
            );
            if (background!=null){
                background.shutdown();
                background=null;
            }
            background= Executors.newSingleThreadExecutor();
            SpatialCache<GraphicsBuffer> finalCache = cache;
            background.submit(()->{

                finalCache.backgroundCreate(
                        plotArea.getWidth(), plotArea.getHeight(),
                        plotArea.layout.getXAxis().reversed, plotArea.layout.getYAxis().reversed,
                        plotArea.layout.getXAxis().lower, plotArea.layout.getYAxis().lower,
                        plotArea.layout.getXAxis().upper, plotArea.layout.getYAxis().upper,
                        1,1
                );
            });
            plotArea.drawSelection(renderer, context);
            context.clearClip();

        }

        @Override
        protected void clearBuffer(Renderer renderer, RectangularPlotArea component) {
            //TODO check if the change requires buffer change
        }

        @Override
        public SpatialCache<GraphicsBuffer> getBufferStore(RectangularPlotArea component) {
            return super.getBufferStore(component);
        }

        @Override
        public SpatialCache<GraphicsBuffer> setBufferStore(RectangularPlotArea component, SpatialCache<GraphicsBuffer> graphicsBufferSpatialCache) {
            return super.setBufferStore(component, graphicsBufferSpatialCache);
        }
    }


    public RectangularPlotArea(XYLayout layout) {
        super(layout, RectangularPlotAreaBufferStrategy.INSTANCE);

    }

    private boolean pointIntersectsPaddedNode(Node2D node, double x, double y, double w, double h) {
        return RectangularNode.intersects(x, y, x, y, node.getMinX() - w, node.getMinY() - h, node.getMaxX() + w, node.getMaxY() + h);
    }

    @Override
    protected List<? extends PlotShape<XYLayout>> contains(double x, double y) {
        final List<PlotShape<XYLayout>> out = new LinkedList<>();
        for (final PlotData<?, XYLayout> data : getData(layout)) {
            final double searchX = getSearchPaddingX(data) / getScale(layout.getXAxis()),
                    searchY = getSearchPaddingY(data) / getScale(layout.getYAxis());
            for (final RTree<PlotShape<XYLayout>> shapes : getShapes(data)) {
                layout.transformPositionToValue(x, y, (_x, _y) ->
                        shapes.findAll(out,
                                node2D -> pointIntersectsPaddedNode(node2D, _x, _y, searchX, searchY),
                                node2D -> isVisible(node2D) && node2D.contains(_x, _y, _x, _y)));
            }
        }
        return out;
    }

    @Override
    protected List<? extends PlotShape<XYLayout>> contains(double x, double minY, double maxY) {
        //todo
        final List<PlotShape<XYLayout>> out = new LinkedList<>();
        for (final PlotData<?, XYLayout> data : getData(layout)) {
            final double searchX = getSearchPaddingX(data) / getScale(layout.getXAxis()),
                    searchY = getSearchPaddingY(data) / getScale(layout.getYAxis());
            for (final RTree<PlotShape<XYLayout>> shapes : getShapes(data)) {
                double _x = layout.getXAxis().getValueFromPosition(x);
                double _minY = layout.getYAxis().getValueFromPosition(minY);
                double _maxY = layout.getYAxis().getValueFromPosition(maxY);
                shapes.findAll(
                        out,
                        node2D -> node2D.intersects(_x - searchX, Math.min(_minY, _maxY), _x + searchX, Math.max(_minY, _maxY)),
                        node2D -> Math.abs(node2D.getMidX() - _x) < 1 / getScale(layout.getXAxis())
                );
            }
        }
        return out;
    }


    @Override
    protected void clearCache() {
        RectangularPlotAreaBufferStrategy.INSTANCE.setBufferStore(this, null);
    }

    final SpatialCache<GraphicsBuffer> getCache() {
        return (RectangularPlotAreaBufferStrategy.INSTANCE).getBufferStore(this);
    }

    @Override
    protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        setBoundsFromExtent(minX, minY, maxX, maxY);

    }

    void drawGrid(Renderer renderer, GraphicsBuffer canvas) {
        canvas.setFill(layout.getBackgroundColor());
        canvas.fillRect(getX(), getY(), getWidth(), getHeight());
        drawGrid(layout, layout.xAxis, renderer, canvas);
        drawGrid(layout, layout.yAxis, renderer, canvas);
        if (layout.secondaryXAxis != null) {
            drawGrid(layout, layout.secondaryXAxis, renderer, canvas);
        }
        if (layout.secondaryYAxis != null) {
            drawGrid(layout, layout.secondaryYAxis, renderer, canvas);
        }
    }

    protected void drawSelection(Renderer renderer, GraphicsBuffer canvas) {
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

    protected void drawShapes(Renderer renderer, GraphicsBuffer canvas, double xMin, double yMin, double xMax, double yMax) {
        for (final PlotData<?, XYLayout> data : getData(layout)) {
            final double searchXMin = xMin - getSearchPaddingX(data) / getScale(layout.getXAxis()),
                    searchYMin = yMin - getSearchPaddingY(data) / getScale(layout.getYAxis()),
                    searchXMax = xMax + getSearchPaddingX(data) / getScale(layout.getXAxis()),
                    searchYMax = yMax + getSearchPaddingY(data) / getScale(layout.getYAxis());

            canvas.setStroke(Color.white);
            canvas.setStroke(Stroke.SOLID);
            for (final RTree<PlotShape<XYLayout>> tree : getShapes(data)) {
                for (final PlotShape<XYLayout> shape : tree.search(searchXMin, searchYMin, searchXMax, searchYMax)) {
                    if (isVisible(shape)) {
                        canvas.setFill(getColor(data, shape));
                        draw(layout, shape, renderer, canvas);
                    }
                }
            }

        }
    }

    protected boolean containsShapes(double xMin, double yMin, double xMax, double yMax) {
        for (final PlotData<?, XYLayout> data : getData(layout)) {
            for (final RTree<PlotShape<XYLayout>> tree : getShapes(data)) {
                final double searchXMin = xMin - getSearchPaddingX(data) / getScale(layout.getXAxis()),
                        searchYMin = yMin - getSearchPaddingY(data) / getScale(layout.getYAxis()),
                        searchXMax = xMax + getSearchPaddingX(data) / getScale(layout.getXAxis()),
                        searchYMax = yMax + getSearchPaddingY(data) / getScale(layout.getYAxis());
                if (tree.collides(searchXMin, searchYMin, searchXMax, searchYMax)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
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
    protected void onMouseDoubleClick(boolean ctrlDown, boolean shiftDown, double x, double y) {
        if (getSelection(layout) != null) {
            ((PlotSelection.Polygon) getSelection(layout)).close();//TODO
            applySelection(layout, getSelection(layout));
            redraw();
        }
    }


}
