package net.mahdilamb.dataviz;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provide easy access to figure methods from components of the figure
 *
 * @param <O> the concrete type of the implementing class
 */
interface FigureComponent<O> {
    /**
     * @return the figure from the implementing class
     */
    Figure getFigure();

    /**
     * Apply a function to the figure
     *
     * @param fn the function to apply
     * @return the implementing object
     */
    @SuppressWarnings("unchecked")
    default O updateFigure(final Consumer<Figure> fn) {
        getFigure().updateFigure(fn);
        return (O) this;
    }

    /**
     * Save the figure
     *
     * @param output the output path
     */
    default void saveAs(final File output) {
        getFigure().saveAs(output);
    }

    /**
     * Add the plot to a figure and show
     */
    default void show() {
        getFigure().show();
    }

    /**
     * Add the plot to a figure and show in the given figure renderer
     *
     * @param creator the renderer
     */
    default void show(Function<Figure, ? extends Renderer<?>> creator) {
        getFigure().show(creator);
    }
}
