package net.mahdilamb.dataviz.inprogress;

import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.FigureBase;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.ui.InputComponent;
import net.mahdilamb.stats.utils.DistanceMetrics;

import java.awt.*;

public class SliderSandbox {
    static final class Slider extends Component implements InputComponent<Double> {
        double position = 0;
        boolean mouseDown = false;
        boolean mouseOver = false;

        Slider() {
            super(16, 16, 16, 16);
        }

        @Override
        protected final void onMouseDown(boolean ctrlDown, boolean shiftDown, double x, double y) {
            mouseDown = true;
            super.onMouseDown(ctrlDown, shiftDown, x, y);
        }

        @Override
        protected final void onMouseUp(boolean ctrlDown, boolean shiftDown, double x, double y) {
            mouseDown = false;
            super.onMouseUp(ctrlDown, shiftDown, x, y);
            redraw();
        }

        @Override
        protected final void onMouseMove(boolean ctrlDown, boolean shiftDown, double x, double y) {
            if (mouseDown) {
                position = x - getX();
            }
            mouseOver = DistanceMetrics.euclidean(x, y, getX() + position, getY() + getHeight() * .5) <= 16;
            redraw();
        }

        protected final void onMouseClick(boolean ctrlDown, boolean shiftDown, double x, double y) {
            position = x - getX();
            redraw();
        }

        @Override
        protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
            setBoundsFromRect(minX, minY, 200, 32);
        }

        @Override
        protected void onMouseScroll(boolean ctrlDown, boolean shiftDown, double x, double y, double rotation) {
            position += rotation * 2;
            redraw();
            super.onMouseScroll(ctrlDown, shiftDown, x, y, rotation);
        }

        void drawControl(GraphicsBuffer canvas, double size) {
            canvas.fillOval(getX() + position - size / 2, getY() + (16 - size / 2) - 2, size, size);

        }

        @Override
        protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
            canvas.setFill(Colors.lightsteelblue);
            canvas.fillRoundRect(getX(), getY() + 12, getWidth(), 4, 4, 4);
            canvas.setFill(Colors.cornflowerblue);
            canvas.fillRoundRect(getX(), getY() + 12, getX() + position, 4, 4, 4);

            if (mouseDown) {
                canvas.setFill(new Color(100, 149, 237, 128));
                drawControl(canvas, 32);
            } else if (mouseOver) {
                canvas.setFill(new Color(100, 149, 237, 128));
                drawControl(canvas, 24);
            }
            canvas.setFill(Colors.cornflowerblue);

            drawControl(canvas, 12);

        }

        @Override
        public void setValue(Double value) {
            position = value;
        }

        @Override
        public Double getValue() {
            return position;
        }

        @Override
        protected void onBlur() {
            mouseOver = false;
            mouseDown = false;
            super.onBlur();
        }
    }

    private static final class Figure extends FigureBase<Figure> {
        Figure() {
            add(new Slider());
        }
    }

    public static void main(String[] args) {
        new Figure().show();
    }

}
