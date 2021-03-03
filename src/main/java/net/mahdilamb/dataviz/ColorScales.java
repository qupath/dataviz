package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.dataviz.graphics.ChartCanvas;
import net.mahdilamb.dataviz.graphics.Orientation;
import net.mahdilamb.dataviz.graphics.Paint;

/**
 * The color scales area of a figure
 */
public final class ColorScales extends KeyArea<ColorScales> {
    protected static final class ModifiableGradient extends Paint.Gradient {

        protected ModifiableGradient(Colormap colorMap, double startX, double startY, double endX, double endY) {
            super(Paint.GradientType.LINEAR, colorMap, startX, startY, endX, endY);
        }

        void update(ColorScales scales, ColorBar colorBar) {
            endX = scales.padding + colorBar.posX;
            endY = colorBar.posY;
            startX = endX + colorBar.sizeX;
            startY = colorBar.sizeY + endY;

        }
    }

    private static final class ModifiablePaint extends Paint {
        ModifiableGradient gradient;

        public ModifiablePaint(ModifiableGradient gradient) {
            super(gradient);
            this.gradient = gradient;
        }


    }

    protected static final class ColorBar extends Component {
        private final PlotTrace.Numeric trace;
        ModifiablePaint gradient;

        public ColorBar(PlotTrace.Numeric trace) {
            this.trace = trace;
        }

        @Override
        protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
            //ignored
        }

        @Override
        protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
            //ignored
        }

        void computeSize(Renderer<?> source, ColorScales scales) {
            //TODO deal with tick marks and labels + title
            if (scales.orientation == Orientation.VERTICAL) {
                sizeX = scales.thickness;
                sizeY = scales.size * scales.sizeY;
            } else {
                sizeY = scales.thickness;
                sizeX = scales.size;
            }
        }

        void drawColorBar(Renderer<?> source, ChartCanvas<?> canvas, ColorScales scales) {
            canvas.setFill(getGradient());
            canvas.fillRect(scales.padding+posX, posY, sizeX, sizeY);
        }

        private ModifiablePaint getGradient() {
            if (gradient == null) {
                gradient = new ModifiablePaint(new ModifiableGradient(trace.data.getColormap(), sizeX + posX, sizeY + posY, posX, posY));
            }
            return gradient;
        }

    }

    double size = .9;
    double thickness = 30;
    double padding = 10;

    ColorScales(Figure figure) {
        super(figure);
    }

    @Override
    protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        if (orientation == Orientation.VERTICAL) {
            sizeX = 0;
            posY = minY;
            sizeY = maxY - minY;
            for (final PlotData<?> trace : figure.layout.data) {
                if (!trace.showsColorBar()) {
                    continue;
                }
                trace.layoutColorBar(source, this);
                sizeX += ((PlotTrace.Numeric) trace.colors).getColorBar().sizeX;
                ((PlotTrace.Numeric) trace.colors).getColorBar().posY = posY + (.5 * (1 - size)) * sizeY;
            }
            posX = maxX - sizeX-padding;
            if (sizeX == 0) {
                sizeY = 0;
            } else {
                double x = posX ;
                sizeX += padding;
                for (final PlotData<?> trace : figure.layout.data) {
                    if (!trace.showsColorBar()) {
                        continue;
                    }
                    ((PlotTrace.Numeric) trace.colors).getColorBar().posX = x;
                    ((PlotTrace.Numeric) trace.colors).getColorBar().getGradient().gradient.update(this, ((PlotTrace.Numeric) trace.colors).getColorBar());
                    x -= ((PlotTrace.Numeric) trace.colors).getColorBar().sizeX;

                }
            }

        } else {
            throw new UnsupportedOperationException();//TODO
        }
    }

    @Override
    protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
            for (final PlotData<?> trace : figure.layout.data) {
                if (!trace.showsColorBar()) {
                    continue;
                }
                trace.drawColorBar(source, canvas, this);
            }

    }
}
