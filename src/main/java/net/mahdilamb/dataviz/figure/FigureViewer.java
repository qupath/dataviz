package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.swing.SwingRenderer;

import java.util.ServiceLoader;

/**
 * Service provider of figure viewer
 */
public interface FigureViewer {
    /**
     * A GUI swing viewer
     */
    class SwingViewer implements FigureViewer {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public void show(FigureBase<?> figure) {
            new SwingRenderer(figure);
        }
    }


    /**
     * @return the priority of this viewer
     */
    int priority();

    /**
     * Show the figure
     *
     * @param figure the figure
     */
    void show(final FigureBase<?> figure);

    /**
     * Short a figure with the default viewer
     *
     * @return the default viewer
     * @throws RuntimeException if no default viewer
     */
    static FigureViewer getDefault() throws RuntimeException {
        FigureViewer found = null;
        int priority = Integer.MIN_VALUE;

        for (final FigureViewer figureViewer : ServiceLoader.load(FigureViewer.class)) {
            if (figureViewer.priority() > priority) {
                found = figureViewer;
                priority = figureViewer.priority();
            }
        }
        if (found == null) {
            throw new RuntimeException("Could not find a viewer");
        }
        return found;
    }
}
