package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.swing.SwingRenderer;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;

public final class Figure extends Component implements FigureComponent<Figure> {
    private static final Colormap DEFAULT_QUALITATIVE_COLORMAP = Colormaps.get("Plotly");
    private static final Colormap DEFAULT_SEQUENTIAL_COLORMAP = Colormaps.get("Viridis");
    private static final double DEFAULT_WIDTH = 800;
    private static final double DEFAULT_HEIGHT = 640;

    Colormap qualitativeColormap = DEFAULT_QUALITATIVE_COLORMAP;
    Colormap sequentialColormap = DEFAULT_SEQUENTIAL_COLORMAP;

    Color backgroundColor = Color.white;

    double width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;
    double paddingLeft = 5, paddingRight = 5, paddingTop = 10, paddingBottom = 5;

    PlotLayout layout;
    Map<PlotData<?>, Color> defaultColor = Collections.emptyMap();

    Color titleColor = Color.BLACK;
    final WrappedTitle title = new WrappedTitle(EMPTY_STRING, new Font(Font.Family.SANS_SERIF, 24));

    final Legend legend = new Legend(this);
    final ColorScales colorScales = new ColorScales(this);

    /**
     * Create an empty figure
     */
    public Figure() {
    }


    /**
     * Add a trace to the figure
     *
     * @param trace the trace to add
     * @return this figure
     */
    public Figure addTrace(final PlotData<?> trace) {
        //See if the trace can be added to an existing plot

        if (layout != null) {
            if (layout.add(trace)) {
                return this;
            } else {
                throw new UnsupportedOperationException("Create a subplot, and add");//TODO
            }
        }
        if (trace.facets != null) {
            layout = new PlotLayout.RectangularFacetGrid(this, trace);
        } else {
            //If not, create a new plot
            if (trace instanceof PlotData.XYData) {
                final PlotLayout.Rectangular plot = new PlotLayout.Rectangular();
                plot.figure = this;
                if (trace.title != null){
                    plot.title = trace.title;
                }
                title.setText(plot.title);
                plot.x.title.setText(((PlotData.XYData<?>) trace).xLab);
                plot.x.labels = ((PlotData.XYData<?>) trace).xLabels;
                plot.x.figure = this;
                plot.y.title.setText(((PlotData.XYData<?>) trace).yLab);
                plot.y.figure = this;
                if (!plot.add(trace)) {
                    throw new RuntimeException("Rectangular plot seems to be unable to add this XY trace");
                }
                layout = plot;
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

    /**
     * Set the title of the figure
     *
     * @param title the title
     * @return this figure
     */
    public Figure setTitle(final String title) {
        this.title.setText(title);
        update();
        return this;
    }

    /**
     * Apply a theme to the plot
     *
     * @param theme the theme to apply
     * @return this figure
     */
    public Figure apply(final Theme theme) {
        theme.apply(this);
        update();
        return this;
    }

    /**
     * Show this figure in Swing
     *
     * @implNote This method is terminal because this will spawn a new thread and no longer be in sync with the calling
     * thread
     */
    public void show() {
        show(SwingRenderer::new);
    }

    /**
     * Show this figure in a non-Swing renderer
     *
     * @param creator the function that renders a figure
     */
    @Override
    public void show(Function<Figure, ? extends Renderer<?>> creator) {
        creator.apply(Figure.this);
    }

    /**
     * Save this figure
     *
     * @param file the path
     * @implNote as with showing, this *can* lead to asyncrhony (except for non-bitmap output, hence this is also a
     * terminal operation
     */
    @Override
    public void saveAs(final File file) {
        (renderer != null ? renderer : new SwingRenderer(this, true)).saveAs(file);
    }

    /**
     * @return the title of the figure
     */
    public String getTitle() {
        return title.getText();
    }

    /**
     * Update the title
     *
     * @param fn the function to apply to the title
     * @return this figure
     */
    public Figure updateTitle(final Consumer<Title> fn) {
        fn.accept(title);
        update();
        return this;
    }

    /**
     * @return the plot layout
     */
    public PlotLayout getLayout() {
        return layout;
    }

    /**
     * @return the background color of the figure
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @return the width of the figure
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return the height of the figure
     */
    public double getHeight() {
        return height;
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
        update();
        return this;
    }

    @Override
    public Figure updateFigure(Consumer<Figure> fn) {
        fn.accept(this);
        update();
        return this;
    }

    /**
     * Update a trace
     *
     * @param traceName the name of the trace
     * @param apply     the function to apply to the trace
     * @return this figure
     */
    public Figure updateTrace(String traceName, Consumer<PlotTrace> apply) {
        for (final PlotData<?> t : layout.data) {
            for (final Map.Entry<PlotData.Attribute, PlotTrace> a : t.attributes()) {
                if (traceName.equals(a.getValue().name)) {
                    t.updateTrace(a.getValue(), apply);
                    break;
                }
            }
        }

        return this;
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
        for (final PlotData<?> t : layout.data) {
            if (traceType.isInstance(t)) {
                apply.accept(traceType.cast(t));
            }

        }
        return this;
    }

    /**
     * Update the most recent (last) layout
     *
     * @param fn the function to apply to the layout
     * @return this figure
     */
    public Figure updateLayout(Consumer<PlotLayout> fn) {
        fn.accept(layout);
        return this;
    }

    Color getDefaultColor(final PlotData<?> data) {
        final Color cached = defaultColor.get(data);
        if (cached != null) {
            return cached;
        }
        if (((Map<?, ?>) defaultColor) == Collections.emptyMap()) {
            defaultColor = new HashMap<>();
        }
        defaultColor.put(data, qualitativeColormap.get(((float) defaultColor.size() % qualitativeColormap.size()) / (qualitativeColormap.size() - 1)));
        return defaultColor.get(data);
    }

    private PlotData<?> getTrace() {
        if (layout == null) {
            return null;
        }
        return layout.getTrace();
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

    /**
     * Update the figure locally
     */
    protected void update() {
        if (renderer == null) {
            return;
        }
        update(renderer.getCanvas());
    }

    /**
     * Update the figure on a "visitor" canvas
     *
     * @param canvas the "visitor" canvas
     */
    protected void update(final ChartCanvas<?> canvas) {
        if (renderer != null) {
            layout(renderer, 0, 0, width, height);
            draw(renderer, canvas);
            return;
        }
        System.err.println("No renderer");
    }

    static void layoutIfUsing(KeyArea<?> a, KeyArea<?> b, final Side side, Renderer<?> source, double minX, double minY, double maxX, double maxY) {
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
            KeyArea<?> tmp = a;
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
        this.posX = minX;
        this.posY = minY;
        this.sizeX = maxX - minX;
        this.sizeY = maxY - minY;
        minX += paddingLeft;
        maxX -= (paddingLeft + paddingRight);
        minY += paddingTop;
        maxY -= (paddingBottom + paddingTop);

        layoutIfUsing(legend, colorScales, Side.RIGHT, source, minX, minY, maxX, maxY);
        layoutIfUsing(legend, colorScales, Side.LEFT, source, minX, minY, maxX, maxY);
        if (!legend.isFloating) {
            switch (legend.side) {
                case RIGHT:
                    maxX -= legend.sizeX;
                    break;
                case LEFT:
                    minX += legend.sizeX;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        if (!colorScales.isFloating) {
            switch (colorScales.side) {
                case RIGHT:
                    maxX -= colorScales.sizeX;
                    break;
                case LEFT:
                    minX += colorScales.sizeX;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        if (title.isVisible()) {
            title.layout(source, minX, minY, maxX, maxY);
            minY = ((Component) title).sizeY + ((Component) title).posY;
        }
        layout.layout(source, minX, minY, maxX, maxY);
        layout.renderer = source;

    }

    @Override
    protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
        canvas.reset();
        layout.draw(source, canvas);

        legend.draw(source, canvas);
        colorScales.draw(source, canvas);
        if (title.isVisible()) {
            canvas.setFill(titleColor);
            title.draw(source, canvas);
        }
        canvas.done();
    }

    @Override
    protected void markLayoutAsOld() {
        title.markLayoutAsOld();
        legend.markLayoutAsOld();
        colorScales.markLayoutAsOld();
        if (layout != null) {
            layout.markLayoutAsOld();
        }
        super.markLayoutAsOld();
    }

    @Override
    protected void markDrawAsOld() {
        title.markDrawAsOld();
        legend.markDrawAsOld();
        colorScales.markDrawAsOld();
        if (layout != null) {
            layout.markDrawAsOld();
        }
        super.markDrawAsOld();
    }

    @Override
    public Figure getFigure() {
        return this;
    }
}
