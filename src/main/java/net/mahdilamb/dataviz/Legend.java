package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;

public class Legend extends KeyArea<Legend> {
    static final class LegendItem extends Component {

        @Override
        protected <T>void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {

        }
    }
}
