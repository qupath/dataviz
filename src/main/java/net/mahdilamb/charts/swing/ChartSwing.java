package net.mahdilamb.charts.swing;

import net.mahdilamb.charts.Chart;
import net.mahdilamb.charts.Text;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.layouts.Plot;
import net.mahdilamb.charts.plots.PlotSeries;
import net.mahdilamb.colormap.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.Queue;
import java.util.function.Consumer;

import static net.mahdilamb.charts.swing.SwingUtils.convert;

public class ChartSwing<P extends Plot<S>, S extends PlotSeries<S>> extends Chart<P, S> {

    static final class ModifiableAWTColor extends Color {
        float r, g, b, a;

        @Override
        public int getRed() {
            return Colors.floatTo8Bit(r);
        }

        @Override
        public int getGreen() {
            return Colors.floatTo8Bit(g);
        }

        @Override
        public int getBlue() {
            return Colors.floatTo8Bit(b);
        }

        @Override
        public int getAlpha() {
            return Colors.floatTo8Bit(a);
        }

        @Override
        public int getRGB() {
            return Colors.RGBAToInteger(r, g, b, a);
        }

        @Override
        public float[] getRGBComponents(float[] compArray) {
            compArray = compArray == null ? new float[4] : compArray;
            compArray[0] = r;
            compArray[1] = g;
            compArray[2] = b;
            compArray[3] = a;
            return compArray;
        }

        @Override
        public float[] getRGBColorComponents(float[] compArray) {
            compArray = compArray == null ? new float[3] : compArray;
            compArray[0] = r;
            compArray[1] = g;
            compArray[2] = b;
            return compArray;
        }

        @Override
        public float[] getComponents(float[] compArray) {
            compArray = compArray == null ? new float[4] : compArray;
            compArray[0] = r;
            compArray[1] = g;
            compArray[2] = b;
            compArray[3] = a;
            return compArray;
        }

        @Override
        public float[] getColorComponents(float[] compArray) {
            compArray = compArray == null ? new float[3] : compArray;
            compArray[0] = r;
            compArray[1] = g;
            compArray[2] = b;
            return compArray;
        }

        ModifiableAWTColor(float r, float g, float b, float a) {
            super(r, g, b, a);
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }

    private static final class ChartPane extends JPanel implements ChartCanvas {
        private static final class ModifiableBasicStroke extends BasicStroke {
            float width = 1;

            public float getLineWidth() {
                return width;
            }
        }


        private final Queue<Consumer<Graphics2D>> queue = new ArrayDeque<>();
        private final Chart<?, ?> chart;
        private Fill currentFill = Fill.BLACK_FILL;
        private Stroke currentStroke = Stroke.BLACK_STROKE;
        private final Rectangle2D rect = new Rectangle2D.Double();
        private final RoundRectangle2D roundedRect = new RoundRectangle2D.Double();
        private final Ellipse2D ellipse = new Ellipse2D.Double();
        private final Line2D line = new Line2D.Double();
        private final ModifiableBasicStroke stroke = new ModifiableBasicStroke();
        private final ModifiableAWTColor strokeColor = new ModifiableAWTColor(0, 0, 0, 1);
        private final ModifiableAWTColor fillColor = new ModifiableAWTColor(0, 0, 0, 1);
        private final Path2D path = new Path2D.Double();
        double currentBaselineOffset = 0;
        boolean usingFill = true;

        ChartPane(Chart<?, ?> chart) {
            this.chart = chart;
        }

        @Override
        public synchronized void paint(Graphics g) {
            try {
                super.paint(g);
                for (final Consumer<Graphics2D> command : queue) {
                    command.accept((Graphics2D) g);
                }
            } catch (ConcurrentModificationException e) {
                //hide errors
            }

        }

        private Queue<Consumer<Graphics2D>> switchToFilled() {
            queue.add(g -> {
                if (!usingFill) {
                    if (currentFill.isGradient()) {
                        g.setPaint(convert(currentFill.getGradient()));
                    } else {
                        g.setColor(convert(fillColor, currentFill.getColor()));
                        g.setPaint(fillColor);
                    }
                    usingFill = true;
                }
            });
            return queue;
        }

        private Queue<Consumer<Graphics2D>> switchToStroked() {
            queue.add(g -> {
                if (usingFill) {
                    this.stroke.width = (float) currentStroke.getWidth();
                    g.setStroke(this.stroke);
                    g.setColor(convert(strokeColor, this.currentStroke.getColor()));
                    usingFill = false;
                }
            });
            return queue;

        }

        @Override
        public void strokeRect(double x, double y, double width, double height) {
            switchToStroked().add(g -> {
                rect.setRect(x, y, width, height);
                g.draw(rect);
            });
        }

        @Override
        public void fillRect(double x, double y, double width, double height) {
            switchToFilled().add(g -> {
                rect.setRect(x, y, width, height);
                g.fill(rect);
            });
        }

        @Override
        public void strokeRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
            switchToStroked().add(g -> {
                roundedRect.setRoundRect(x, y, width, height, arcWidth, arcHeight);
                g.draw(roundedRect);
            });
        }

        @Override
        public void fillRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
            switchToFilled().add(g -> {
                roundedRect.setRoundRect(x, y, width, height, arcWidth, arcHeight);
                g.fill(roundedRect);
            });
        }

        @Override
        public void strokeOval(double x, double y, double width, double height) {
            switchToStroked().add(g -> {
                ellipse.setFrame(x, y, width, height);
                g.draw(ellipse);
            });
        }

        @Override
        public void fillOval(double x, double y, double width, double height) {
            switchToFilled().add(g -> {
                ellipse.setFrame(x, y, width, height);
                g.fill(ellipse);
            });
        }

        @Override
        public void strokeLine(double x0, double y0, double x1, double y1) {
            switchToStroked().add(g -> {
                line.setLine(x0, y0, x1, y1);
                g.draw(line);
            });
        }

        @Override
        public void setFill(Fill fill) {
            queue.add(g -> {
                this.currentFill = fill;
                if (currentFill.isGradient()) {
                    g.setPaint(convert(currentFill.getGradient()));
                } else {
                    g.setColor(convert(fillColor, currentFill.getColor()));
                    g.setPaint(fillColor);
                }
                usingFill = true;
            });
        }

        @Override
        public void setStroke(Stroke stroke) {
            queue.add(g -> {
                this.currentStroke = stroke;
                this.stroke.width = (float) currentStroke.getWidth();
                g.setStroke(this.stroke);
                g.setColor(convert(strokeColor, this.currentStroke.getColor()));
                usingFill = false;
            });
        }

        @Override
        public void done() {
            repaint();
        }

        @Override
        public void reset() {
            queue.clear();
            queue.add(g -> {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (chart.getBackgroundColor() != null) {
                    g.setColor(convert(fillColor, chart.getBackgroundColor()));
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);

            });

        }

        @Override
        public void beginPath() {
            queue.add(g -> path.reset());
        }

        @Override
        public void moveTo(double endX, double endY) {
            queue.add(g -> path.moveTo(endX, endY));
        }

        @Override
        public void lineTo(double endX, double endY) {
            queue.add(g -> path.lineTo(endX, endY));
        }

        @Override
        public void quadTo(double cpX, double cpY, double endX, double endY) {
            queue.add(g -> path.quadTo(cpX, cpY, endX, endY));
        }

        @Override
        public void curveTo(double cp1X, double cp1Y, double cp2X, double cp2Y, double endX, double endY) {
            queue.add(g -> path.curveTo(cp1X, cp1Y, cp2X, cp2Y, endX, endY));
        }

        @Override
        public void closePath() {
            queue.add(g -> path.closePath());
        }

        @Override
        public void fill() {
            switchToFilled().add(g -> g.fill(path));
        }

        @Override
        public void stroke() {
            switchToStroked().add(g -> g.draw(path));

        }

        @Override
        public void fillText(String text, double x, double y) {
            switchToFilled().add(g -> g.drawString(text, SwingUtils.convert(x), SwingUtils.convert(y)));
        }

        @Override
        public void setFont(Font font) {
            queue.add(g -> {
                g.setFont(SwingUtils.convert(font));
                currentBaselineOffset = g.getFontMetrics().getAscent();
            });

        }

        @Override
        public void fillText(Text text, double x, double y) {
            if (!isSet(text)) {
                final FontMetrics fontMetrics = getFontMetrics(SwingUtils.convert(text.getFont()));
                setFont(SwingUtils.convert(text.getFont()));
                setMetrics(text, fontMetrics.stringWidth(text.getText()), fontMetrics.getHeight(), fontMetrics.getAscent());
            }
            switchToFilled().add(g -> g.drawString(text.getText(), SwingUtils.convert(getAdjustedX(text, x)), SwingUtils.convert(getAdjustedY(text, y))));

        }

        @Override
        public void setClip(ClipShape shape, double x, double y, double width, double height) {
            queue.add(g -> {
                switch (shape) {
                    case ELLIPSE:
                        ellipse.setFrame(x, y, width, height);
                        g.setClip(ellipse);
                        break;
                    case RECTANGLE:
                        rect.setRect(x, y, width, height);
                        g.setClip(rect);
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }

            });
        }

        @Override
        public void clearClip() {
            queue.add(g->g.setClip(null));
        }

    }

    private final ChartPane canvas = new ChartPane(this);

    public ChartSwing(String title, double width, double height, P plot) {
        super(title, width, height, plot);
        canvas.setSize((int) Math.ceil(width), (int) Math.ceil(height));
    }

    @Override
    protected ChartCanvas getCanvas() {
        return canvas;
    }

    @Override
    public void saveAsSVG(File file) {
        if (SwingUtilities.isEventDispatchThread()) {
            super.saveAsSVG(file);
        } else {
            SwingUtilities.invokeLater(() -> super.saveAsSVG(file));
        }
    }

    public void addTo(Container parent, String position) {
        parent.add(canvas, position);
        layout();
    }
}
