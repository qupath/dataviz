package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Side;
import net.mahdilamb.charts.swing.SwingRenderer;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Figure extends Component {
    private static final Colormap DEFAULT_QUALITATIVE_COLORMAP = Colormaps.get("Plotly");
    private static final Colormap DEFAULT_SEQUENTIAL_COLORMAP = Colormaps.get("Viridis");
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 640;

    Colormap qualitativeColormap = DEFAULT_QUALITATIVE_COLORMAP;
    Colormap sequentialColormap = DEFAULT_SEQUENTIAL_COLORMAP;
    Color backgroundColor = Color.white;

    final List<PlotLayout> plots = new LinkedList<>();
    int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;
    int numColumns = -1;

    String titleText;
    Font titleFont = Font.DEFAULT_TITLE_FONT;
    WrappedTitle title;

    final Legend legend = new Legend(this);
    final ColorScales colorScales = new ColorScales(this);

    /**
     * Add a trace to the figure
     *
     * @param trace the trace to add
     * @return this figure
     */
    public Figure addTrace(final PlotData<?> trace) {
        //See if the trace can be added to an existing plot
        for (final PlotLayout plot : plots) {
            if (plot.add(trace)) {
                return this;
            }
        }
        if (trace.facets != null) {
            plots.add(new PlotLayout.RectangularFacetGrid(this, trace));
        } else {
            //If not, create a new plot
            if (trace instanceof PlotData.XYData) {
                final PlotLayout.Rectangular plot = new PlotLayout.Rectangular();
                plot.figure = this;
                plot.title = trace.title;
                plot.x.title.setText(((PlotData.XYData<?>) trace).xLab);
                plot.x.labels = ((PlotData.XYData<?>) trace).xLabels;
                plot.x.figure = this;
                plot.y.title.setText(((PlotData.XYData<?>) trace).yLab);
                plot.y.figure = this;
                if (!plot.add(trace)) {
                    throw new RuntimeException("Rectangular plot seems to be unable to add this XY trace");
                }
                plots.add(plot);
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return this;
    }

    /**
     * Add multiple traces
     *
     * @param traces the traces
     * @return this figure
     */

    @SafeVarargs
    public final <T extends PlotData<T>> Figure addTraces(T... traces) {
        for (final PlotData<?> t : traces) {
            addTrace(t);
        }
        return this;
    }

    @SuppressWarnings("rawtypes")
    public Renderer show() {
        return show(SwingRenderer::new);
    }

    public <T extends Renderer<?>> T show(Function<Figure, T> creator) {
        return creator.apply(this);
    }

    /**
     * Update all of the traces of a specific type
     *
     * @param traceType the class of the traces
     * @param apply     the function to apply
     * @param <T>       the type of the trace
     * @return this figure
     */
    public <T extends PlotData<T>> Figure updateTraces(Class<? extends T> traceType, Consumer<T> apply) {
        for (final PlotLayout plot : plots) {
            for (final PlotData<?> t : plot.traces) {
                if (traceType.isInstance(t)) {
                    apply.accept(traceType.cast(t));
                }
            }
        }
        return this;
    }

    public Figure updateTrace(String traceName, Consumer<Trace> apply) {
        for (final PlotLayout plot : plots) {
            for (final PlotData<?> t : plot.traces) {
                for (final Map.Entry<PlotData.Attribute, Trace> a : t.attributes()) {
                    if (traceName.equals(a.getValue().name)) {
                        t.updateTrace(a.getValue(), apply);
                        break;
                    }
                }
            }
        }
        return this;
    }

    public Figure updateLayouts(Consumer<PlotLayout> fn) {
        for (final PlotLayout plot : plots) {
            fn.accept(plot);
        }
        return this;
    }

    public Figure updateLayout(Consumer<PlotLayout> fn) {
        final PlotLayout lastPlot = getLayout();
        if (lastPlot == null) {
            return this;
        }
        fn.accept(lastPlot);
        return this;
    }

    public PlotLayout getLayout() {
        if (plots.isEmpty()) {
            return null;
        }
        return plots.get(plots.size() - 1);
    }

    private PlotData<?> getTrace() {
        final PlotLayout lastPlot = getLayout();
        if (lastPlot == null) {
            return null;
        }
        return lastPlot.getTrace();
    }

    <T extends PlotData<T>> Figure updateTrace(Class<? extends T> traceType, Consumer<T> apply) {
        final PlotData<?> lastTrace = getTrace();
        if (lastTrace == null) {
            return this;
        }
        if (traceType.isInstance(lastTrace)) {
            apply.accept(traceType.cast(lastTrace));
        } else {
            System.err.println("The last trace is not a " + traceType.getName());
        }
        return this;
    }

    protected void update() {
        update(renderer.getCanvas());
    }

    protected void update(final ChartCanvas<?> canvas) {
        if (renderer != null) {
            layout(renderer, 0, 0, width, height);
            draw(renderer, canvas);
            return;
        }
        System.err.println("No renderer");
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    private WrappedTitle getTitle() {
        if (title == null && titleText != null) {
            title = new WrappedTitle(titleText, titleFont);
        }
        return title;
    }

    private static boolean isVisible(WrappedTitle title) {
        return title != null && title.isVisible();
    }

    public Figure setTitle(final String title) {
        this.titleText = title;
        this.title = null;
        markLayoutAsOld();
        return this;
    }

    static boolean using(final KeyArea a, final KeyArea b, final Side side) {
        return a.side == side | b.side == side;
    }

    static void layoutIfUsing(KeyArea a, KeyArea b, final Side side, Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        //draw legend closest to the plot
        boolean swap = false;
        switch (side) {
            case TOP:
            case RIGHT:
                swap = a instanceof Legend;//do colorscales first
                break;
            case LEFT:
            case BOTTOM:
                swap = a instanceof ColorScales;
                break;
        }
        if (swap) {
            KeyArea tmp = a;
            a = b;
            b = tmp;
        }
        if (!a.isFloating && a.side == side) {
            a.layout(source, minX, minY, maxX, maxY);
        }
        if (!b.isFloating && b.side == side) {
            b.layout(source, minX, minY, maxX, maxY);
        }
    }

    @Override
    protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        layoutIfUsing(legend, colorScales, Side.RIGHT, source, minX, minY, maxX, maxY);
        layoutIfUsing(legend, colorScales, Side.LEFT, source, minX, minY, maxX, maxY);
        if (legend.side == Side.RIGHT) {
            maxX -= legend.sizeX;
        } else {
            throw new UnsupportedOperationException();
        }
        if (isVisible(getTitle())) {
            getTitle().layout(source, minX, minY, maxX, maxY);
        }
        int cols = numColumns == -1 ? plots.size() : numColumns;
        int rows = (int) Math.ceil((double) plots.size() / cols);
        double width = (maxX - minX) / cols;
        double height = (maxY - minY) / rows;
        for (int r = 0; r < rows; ++r) {
            for (int c = 0, i = r * cols; c < cols && i < plots.size(); ++c, ++i) {
                double x = c * width;
                double y = r * height;
                plots.get(i).layout(source, x, y, x + width, y + height);
            }
        }

    }

    @Override
    protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
        canvas.reset();
        if (isVisible(getTitle())) {
            getTitle().draw(source, canvas);
        }
        legend.draw(source, canvas);
        colorScales.draw(source, canvas);
        for (final PlotLayout layout : plots) {
            layout.draw(source, canvas);
        }
        canvas.done();
    }

    /*@Override
    protected void markLayoutAsOld() {
        if (leftArea != null) {
            leftArea.markLayoutAsOld();
        }
        if (rightArea != null) {
            rightArea.markLayoutAsOld();
        }
        if (topArea != null) {
            topArea.markLayoutAsOld();
        }
        if (bottomArea != null) {
            bottomArea.markLayoutAsOld();
        }
        if (centerArea != null) {
            centerArea.markLayoutAsOld();
        }
        super.markLayoutAsOld();
    }*/
/*
    @Override
    protected void markDrawAsOld() {
        if (leftArea != null) {
            leftArea.markDrawAsOld();
        }
        if (rightArea != null) {
            rightArea.markDrawAsOld();
        }
        if (topArea != null) {
            topArea.markDrawAsOld();
        }
        if (bottomArea != null) {
            bottomArea.markDrawAsOld();
        }
        if (centerArea != null) {
            centerArea.markDrawAsOld();
        }
        super.markDrawAsOld();
    }*/

    /*void setKeyArea(KeyArea keyArea) {
        if (keyArea.parent != null) {
            (keyArea.parent).remove(keyArea);
        }
        if (keyArea.isFloating) {
            if (centerArea == null) {
                centerArea = new KeyAreaNode();
            }
            centerArea.add(keyArea);
            return;
        }
        switch (keyArea.side) {
            case LEFT:
                if (leftArea == null) {
                    leftArea = new KeyAreaNode(Side.LEFT);
                }
                leftArea.add(keyArea);
                return;
            case RIGHT:
                if (rightArea == null) {
                    rightArea = new KeyAreaNode(Side.RIGHT);
                }
                rightArea.add(keyArea);
                return;
            case BOTTOM:
                if (bottomArea == null) {
                    bottomArea = new KeyAreaNode(Side.BOTTOM);
                }
                bottomArea.add(keyArea);
                return;
            case TOP:
                if (topArea == null) {
                    topArea = new KeyAreaNode(Side.TOP);
                }
                topArea.add(keyArea);
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }*/

}
