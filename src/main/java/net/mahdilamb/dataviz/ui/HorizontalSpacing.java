package net.mahdilamb.dataviz.ui;

import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;

public final class HorizontalSpacing extends Component {
    public static final HorizontalSpacing HORIZONTAL_SPACING_5 = new HorizontalSpacing();

    double size;

    public HorizontalSpacing(double size) {
        this.size = size;
    }

    public HorizontalSpacing() {
        this.size = 5;
    }

    @Override
    protected <T>void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
        setBoundsFromRect(minX, minY, size, 1e-13);
    }

    @Override
    protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {

    }
}
