package net.mahdilamb.dataviz.tests;

import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.FigureBase;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Stroke;

import java.awt.*;

public class TextInputTest {
    private static final class TextInput extends Component {
        boolean blinkOn = false;
        final Runnable blink = () -> {
            while (true) {
                blinkOn = !blinkOn;
                redraw();
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        TextInput() {
            new Thread(blink).start();
        }

        @Override
        protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
            setBoundsFromRect(minX, minY, 200, 32);
        }

        @Override
        protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {
            canvas.setStroke(Color.black);
            canvas.strokeRect(getX(), getY(), getWidth(), getHeight());
            if (blinkOn) {
                canvas.setStroke(Stroke.SOLID);
                canvas.strokeLine(getX() + 5, getY() + 2, getX() + 5, getY() + getHeight() - 4);
            }
        }
    }

    private final static class Figure extends FigureBase<Figure> {
        Figure() {
            add(new TextInput());
        }

    }

    public static void main(String[] args) {
        new Figure().show();
    }
}
