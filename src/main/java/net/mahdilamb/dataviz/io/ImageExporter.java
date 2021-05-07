package net.mahdilamb.dataviz.io;

import net.mahdilamb.dataviz.figure.FigureExporter;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.swing.SwingUtils;
import net.mahdilamb.dataviz.utils.Variant;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import static net.mahdilamb.dataviz.swing.SwingUtils.computeArc;
import static net.mahdilamb.dataviz.swing.SwingUtils.convert;

/**
 * Main class for exporting to bitmap types via AWT
 */
class ImageExporter extends FigureExporter {

    private static final class ImageExporterCanvas<T> extends Component implements GraphicsContext<T> {

        private final Graphics2D g;
        private final Renderer<T> chart;
        private final Path2D path = new Path2D.Double();
        private final Rectangle2D rect = new Rectangle2D.Double();
        private final RoundRectangle2D roundedRect = new RoundRectangle2D.Double();
        private final Ellipse2D ellipse = new Ellipse2D.Double();
        private final Line2D line = new Line2D.Double();
        private final Arc2D.Double arc = new Arc2D.Double();
        private final boolean fillWhite;
        private final Variant<Color, Gradient> currentFill = Variant.ofA(Color.BLACK);
        private Stroke currentStroke = Stroke.SOLID;
        private Color currentStrokeColor = Color.BLACK;
        boolean usingFill = true;
        private final AffineTransform affineTransform = new AffineTransform();

        ImageExporterCanvas(boolean fillWhite, Renderer<T> chart, Graphics2D graphics) {
            this.g = graphics;
            this.chart = chart;
            this.fillWhite = fillWhite;
        }


        @Override
        public Renderer<T> getRenderer() {
            return chart;
        }

        @Override
        public void reset() {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (chart.getFigure().getBackgroundColor() != null) {
                g.setColor(chart.getFigure().getBackgroundColor());
            } else if (fillWhite) {
                g.setColor(Color.white);
            }

            g.fillRect(0, 0, convert(chart.getFigure().getWidth()), convert(chart.getFigure().getHeight()));
            g.setColor(Color.BLACK);
            g.setPaint(g.getColor());
        }

        @Override
        public void done() {
            //ignored
        }

        private void switchToFilled() {
            if (!usingFill) {
                if (currentFill.isA()) {
                    g.setColor(currentFill.asA());
                    g.setPaint(g.getColor());
                } else {
                    g.setPaint(convert(currentFill.asB()));
                }
                usingFill = true;
            }

        }

        private void switchToStroked() {
            if (usingFill) {
                g.setStroke(convert(currentStroke));
                g.setColor(currentStrokeColor);
                g.setPaint(g.getColor());
                usingFill = false;
            }
        }

        @Override
        public void strokeRect(double x, double y, double width, double height) {
            switchToStroked();
            rect.setRect(x, y, width, height);
            g.draw(rect);
        }

        @Override
        public void fillRect(double x, double y, double width, double height) {
            switchToFilled();
            rect.setRect(x, y, width, height);
            g.fill(rect);
        }

        @Override
        public void strokeRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
            switchToStroked();
            roundedRect.setRoundRect(x, y, width, height, arcWidth, arcHeight);
            g.draw(rect);
        }

        @Override
        public void fillRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
            switchToFilled();
            roundedRect.setRoundRect(x, y, width, height, arcWidth, arcHeight);
            g.fill(rect);
        }

        @Override
        public void strokeOval(double x, double y, double width, double height) {
            switchToStroked();
            ellipse.setFrame(x, y, width, height);
            g.draw(ellipse);
        }

        @Override
        public void fillOval(double x, double y, double width, double height) {
            switchToFilled();
            ellipse.setFrame(x, y, width, height);
            g.fill(ellipse);
        }

        @Override
        public void strokeLine(double x0, double y0, double x1, double y1) {
            switchToStroked();
            line.setLine(x0, y0, x1, y1);
            g.draw(line);
        }

        @Override
        public void setFill(Color color) {
            this.currentFill.setToA(color);
            g.setColor(color);
            g.setPaint(g.getColor());
            usingFill = true;
        }

        @Override
        public void setFill(Gradient gradient) {
            currentFill.setToB(gradient);
            g.setPaint(convert(gradient));
            usingFill = true;
        }


        @Override
        public void setStroke(Stroke stroke) {
            this.currentStroke = stroke;
            g.setStroke(convert(stroke));
            usingFill = false;
        }

        @Override
        public void setStroke(Color color) {
            usingFill = false;
            currentStrokeColor = color;
            g.setColor(color);
            g.setPaint(color);
        }

        @Override
        public void beginPath() {
            path.reset();
        }

        @Override
        public void moveTo(double endX, double endY) {
            path.moveTo(endX, endY);
        }

        @Override
        public void lineTo(double endX, double endY) {
            path.lineTo(endX, endY);
        }

        @Override
        public void quadTo(double cpX, double cpY, double endX, double endY) {
            path.quadTo(cpX, cpY, endX, endY);
        }

        @Override
        public void curveTo(double cp1X, double cp1Y, double cp2X, double cp2Y, double endX, double endY) {
            path.curveTo(cp1X, cp1Y, cp2X, cp2Y, endX, endY);
        }

        @Override
        public void arcTo(double rx, double ry, double xAxisRotationDegrees, boolean largeArc, boolean sweepFlag, double endX, double endY) {
            final Point2D point = path.getCurrentPoint();
            final Arc2D arc = computeArc( point.getX(), point.getY(), rx, ry, xAxisRotationDegrees, largeArc, sweepFlag, endX, endY);
            affineTransform.setToRotation(Math.toRadians(xAxisRotationDegrees), arc.getCenterX(), arc.getCenterY());
            path.append(new Path2D.Double(arc, affineTransform), true);
            affineTransform.setToIdentity();
        }

        @Override
        public void closePath() {
            path.closePath();
        }

        @Override
        public void fill() {
            switchToFilled();
            g.fill(path);
        }

        @Override
        public void stroke() {
            switchToStroked();
            g.draw(path);
        }

        @Override
        public void fillText(String text, double x, double y) {
            switchToFilled();
            SwingUtils.drawMultilineTextLeft(g, text, x, y, 1, SwingUtils.getTextWidth(g.getFontMetrics(), text));
        }

        @Override
        public void fillText(String text, double x, double y, double rotationDegrees, double pivotX, double pivotY) {
            affineTransform.setToIdentity();
            affineTransform.rotate(Math.toRadians(rotationDegrees), pivotX, pivotY);
            g.setTransform(affineTransform);
            g.drawString(text, convert(x), convert(y));
            affineTransform.setToIdentity();
            g.setTransform(affineTransform);
        }

        @Override
        public void setFont(Font font) {
            g.setFont(SwingUtils.convert(font));
        }


        @Override
        public void setClip(ClipShape shape, double x, double y, double width, double height) {
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
        }

        @Override
        public void clearClip() {
            g.setClip(null);
        }

        @Override
        public void drawImage(T bufferedImage, double x, double y) {
            g.drawImage((Image) bufferedImage, convert(x), convert(y), null);
        }

        @Override
        public void setGlobalAlpha(double alpha) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
        }

    }

    static <T> BufferedImage toBufferedImage(int encoding, final Renderer<T> renderer) {
        final BufferedImage image = new BufferedImage((int) renderer.getFigure().getWidth(), (int) renderer.getFigure().getHeight(), encoding);
        drawContent(new ImageExporterCanvas<>(encoding != BufferedImage.TYPE_INT_ARGB, renderer, (Graphics2D) image.getGraphics()), renderer);
        return image;
    }

}
