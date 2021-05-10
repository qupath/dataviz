package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;

public class ColorScales extends KeyArea<ColorScales> {


    static final class ColorBar extends Component {

        @Override
        protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {

        }
    }

    ColorScales(Figure figure) {
        super(figure, false, 40);
    }


    @Override
    protected <IMG> void layoutVertical(Renderer<IMG> renderer, double minX, double minY, double maxX, double maxY) {

    }

    @Override
    protected <IMG> void layoutHorizontal(Renderer<IMG> renderer, double minX, double minY, double maxX, double maxY) {

    }


    @Override
    protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {

    }
}
