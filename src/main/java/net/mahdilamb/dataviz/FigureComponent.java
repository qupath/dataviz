package net.mahdilamb.dataviz;

import java.io.File;
import java.util.function.Consumer;

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
    default void show(Consumer<Figure> creator) {
        getFigure().show(creator);
    }

    @SuppressWarnings("unchecked")
    default O setTitle(final String title) {
        getFigure().setTitle(title);
        return (O) this;
    }

    @SuppressWarnings("unchecked")
    default O apply(final Theme theme) {
        getFigure().apply(theme);
        return (O) this;
    }

    @SuppressWarnings("unchecked")
    default O apply(final String theme) {
        try {
            return apply(Theme.get(theme));
        } catch (IllegalAccessException e) {
            System.err.println("Could not find theme called '" + theme + "'");
        }
        return (O) this;
    }
}
