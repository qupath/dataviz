package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.ui.Label;
import net.mahdilamb.dataviz.utils.Numbers;

import java.awt.*;

import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;

public abstract class PlotAxis<PL extends PlotLayout<PL>> extends Component {



    public static final class ColumnHeadings extends PlotAxis<XYLayout> {

        @Override
        protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {

        }

        @Override
        protected void drawGrid(XYLayout layout, Renderer renderer, GraphicsBuffer canvas) {
            //todo
        }

        @Override
        protected String getLabel(double value) {
            return Double.toString(value);

        }
    }

    public static final class RowNames extends PlotAxis<XYLayout> {

        @Override
        protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {

        }

        @Override
        protected void drawGrid(XYLayout layout, Renderer renderer, GraphicsBuffer canvas) {
            //todo
        }

        @Override
        protected String getLabel(double value) {
            return Double.toString(value);

        }
    }

    @Override
    protected abstract void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY);

    protected double scale;

    protected final Label title = new Label(EMPTY_STRING, Font.DEFAULT_TITLE_FONT);
    protected PL layout;

    protected Stroke majorGridStroke = new Stroke(1.5),
            minorGridStroke = new Stroke(.75),
            zeroGridStroke = new Stroke(2.5);

    protected Stroke majorLineStroke = majorGridStroke,
            minorLineStroke = minorGridStroke,
            axisStroke = majorGridStroke;
    protected Color majorLineColor = Color.white,
            minorLineColor = Color.white,
            zeroLineColor = Color.white,
            axisColor = Color.black;
    protected Font labelFont = Font.DEFAULT_FONT;
    protected Color labelColor = Color.BLACK;

    protected abstract void drawGrid(PL layout, Renderer renderer, GraphicsBuffer canvas);


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
    protected void onMouseScroll(boolean controlDown, boolean shiftDown, double x, double y, double rotation) {
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
