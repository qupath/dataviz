package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.figure.BufferingStrategy;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.ui.Label;
import net.mahdilamb.dataviz.utils.Numbers;

import static net.mahdilamb.dataviz.PlotAxis.UnmodifiableAxis.INSTANCE;
import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;

public abstract class PlotAxis<PL extends PlotLayout<PL>> extends Component {

    public static final class ColumnHeadings extends PlotAxis<XYLayout> {

        @Override
        protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {

        }

        @Override
        protected <T> void drawGrid(XYLayout layout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {
            //todo
        }

        @Override
        protected String getLabel(double value) {
            return Double.toString(value);

        }
    }

    public static final class RowNames extends PlotAxis<XYLayout> {

        @Override
        protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {

        }

        @Override
        protected <T> void drawGrid(XYLayout layout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {
            //todo
        }

        @Override
        protected String getLabel(double value) {
            return Double.toString(value);

        }
    }

    static final class UnmodifiableAxis<PL extends PlotLayout<PL>> extends PlotAxis<PL> {
        static final PlotAxis<?> INSTANCE = new UnmodifiableAxis<>();

        @Override
        protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {

        }

        @Override
        protected <T> void drawGrid(PL layout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {

        }

        @Override
        protected String getLabel(double value) {
            return Double.toString(value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <PL extends PlotLayout<PL>> PlotAxis<PL> emptyAxis() {
        return (PlotAxis<PL>) INSTANCE;
    }

    @Override
    protected abstract <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY);
    protected   double scale;

    protected final Label title = new Label(EMPTY_STRING, Font.DEFAULT_TITLE_FONT);
    protected PL layout;

    protected abstract <T> void drawGrid(PL layout, Renderer<T> renderer, GraphicsBuffer<T> canvas);


    public String getTitle() {
        return title.getText();
    }

    public PlotAxis<PL> setTitle(final String title) {
        this.title.setText(title);
        redraw();
        return this;
    }

    protected abstract String getLabel(double value);

    @Override
    protected void onMouseScrolled(boolean controlDown, boolean shiftDown, double x, double y, double rotation) {
        //todo check if allowed
        layout.zoomPlotArea(x, y, rotation);
    }

    //from https://stackoverflow.com/questions/8506881/nice-label-algorithm-for-charts-with-minimum-ticks#:~:text=For%20example%2C%20if%20the%20data,a%20tick%20spacing%20of%200.05.
    protected static double niceNum(double range, boolean round) {
        double exponent; /* exponent of range */
        double fraction; /* fractional part of range */
        double niceFraction; /* nice, rounded fraction */

        exponent = Math.floor(Math.log10(range));
        fraction = range / Math.pow(10, exponent);

        if (round) {
            if (fraction < 1.5) {
                niceFraction = 1;
            } else if (fraction < 3) {
                niceFraction = 2;
            } else if (fraction < 7) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (fraction <= 1) {
                niceFraction = 1;
            } else if (fraction <= 2) {
                niceFraction = 2;
            } else if (fraction <= 5) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }
        return niceFraction * Math.pow(10, exponent);
    }

    protected static double getIterStart(double v, double spacing) {
        double remainder = Numbers.mod(v, spacing);
        if (remainder == 0) {
            return v;
        }
        return v - remainder + spacing;
    }

    protected static double getIterEnd(double v, double spacing) {
        double remainder = Numbers.mod(v, spacing);
        if (remainder == 0) {
            return v;
        }
        return v - remainder;
    }

}
