package net.mahdilamb.dataviz;

import java.io.File;
import java.util.function.Consumer;

interface FigureComponent<T extends FigureComponent<T>> {
    Figure getFigure();

    default void show() {
        getFigure().show();
    }

    default void saveAs(final File file) {
        getFigure().saveAs(file);
    }

    @SuppressWarnings("unchecked")
    default T setTitle(final String title) {
        getFigure().setTitle(title);
        return (T) this;
    }

    /**
     * Apply a function to the figure
     *
     * @param fn the function to apply
     * @return the implementing object
     */
    @SuppressWarnings("unchecked")
    default T updateFigure(final Consumer<Figure> fn) {
        getFigure().updateFigure(fn);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T updateLegend(final Consumer<Legend> fn) {
        getFigure().updateLegend(fn);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T updateLayout(final Consumer<PlotLayout<?>> fn) {
        getFigure().updateLayout(fn);
        return (T) this;
    }
}
