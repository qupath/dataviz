package net.mahdilamb.dataviz.tests;

import net.mahdilamb.dataviz.figure.BufferingStrategy;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.FigureBase;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.ui.Toolbar;

import java.awt.*;

public class BufferTest {
    private static final class Line extends Component {
        private final double x0, y0, x1, y1;
        private final Color color;

        private Line(BufferingStrategy<? extends Component, ?> bufferingStrategy, final Color color, double x0, double y0, double x1, double y1) {
            super(bufferingStrategy);
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            this.color = color;
        }


        @Override
        protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
            setBoundsFromExtent(Math.min(x0, x1), Math.min(y0, y1), Math.max(x0, x1), Math.max(y0, y1));
        }

        @Override
        protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {
            canvas.setStroke(color);
            canvas.strokeLine(x0, y0, x1, y1);
        }

        @Override
        protected void onMouseClick(boolean ctrlDown, boolean shiftDown, double x, double y) {
            redraw();
        }
    }

    private static final class Figure extends FigureBase<Figure> {
        Figure() {
            double x0 = 100.5;
            double x1 = 500.5;
            double y0 = 100.5;
            double y1 = 500.5;
            getChildren().add(new Line(BufferingStrategy.BASIC_BUFFERING, Color.RED, x0, y0, x0, y1));
            getChildren().add(new Line(BufferingStrategy.BASIC_BUFFERING, Color.RED, x0, y0, x1, y0));

            //    getChildren().add(new Line(BufferingStrategy.NO_BUFFERING, Color.BLACK, x0, y0, x1, y1));

        }

        @Override
        protected Toolbar createToolbar() {
            return null;
        }
    }

    public static void main(String[] args) {
        new Figure().show();
    }
}
