package net.mahdilamb.dataviz.swing;

import net.mahdilamb.dataviz.graphics.ClipShape;
import net.mahdilamb.dataviz.graphics.Gradient;
import net.mahdilamb.dataviz.utils.Variant;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static net.mahdilamb.dataviz.swing.SwingUtils.computeArc;
import static net.mahdilamb.dataviz.swing.SwingUtils.convert;

/**
 * Extract all the methods of painting to a swing canvas into one object
 */
public final class SwingPainter {

    private final Variant<Color, Gradient> currentFill = Variant.ofA(Color.BLACK);
    private net.mahdilamb.dataviz.graphics.Stroke currentStroke = net.mahdilamb.dataviz.graphics.Stroke.SOLID;
    private Color currentStrokeColor = Color.BLACK;
    private final Path2D path = new Path2D.Double();
    private final AffineTransform affineTransform = new AffineTransform();
    private boolean usingFill = true;

    public SwingPainter() {

    }

    void strokeRect(final Graphics2D g, double x, double y, double width, double height) {
        switchToStroked(g);
        g.draw(new Rectangle2D.Double(x, y, width, height));
    }

    void fillRect(final Graphics2D g, double x, double y, double width, double height) {
        switchToFilled(g);
        g.fill(new Rectangle2D.Double(x, y, width, height));
    }

    void strokeRoundRect(final Graphics2D g, double x, double y, double width, double height, double arcWidth, double arcHeight) {
        switchToStroked(g);
        g.draw(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    void fillRoundRect(final Graphics2D g, double x, double y, double width, double height, double arcWidth, double arcHeight) {
        switchToFilled(g);
        g.fill(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    void strokeOval(final Graphics2D g, double x, double y, double width, double height) {
        switchToStroked(g);
        g.draw(new Ellipse2D.Double(x, y, width, height));
    }

    void fillOval(final Graphics2D g, double x, double y, double width, double height) {
        switchToFilled(g);
        g.fill(new Ellipse2D.Double(x, y, width, height));
    }

    void strokeLine(final Graphics2D g, double x0, double y0, double x1, double y1) {
        switchToStroked(g);
        g.draw(new Line2D.Double(x0, y0, x1, y1));
    }

    void setFill(final Graphics2D g, Color color) {
        this.currentFill.setToA(color);
        g.setColor(color);
        g.setPaint(g.getColor());
        usingFill = true;
    }

    void setFill(final Graphics2D g, Gradient gradient) {
        this.currentFill.setToB(gradient);
        g.setPaint(convert(gradient));
        usingFill = true;
    }

    void setStroke(final Graphics2D g, net.mahdilamb.dataviz.graphics.Stroke stroke) {
        this.currentStroke = stroke;
        g.setStroke(convert(currentStroke));
        usingFill = false;
    }

    void setStroke(final Graphics2D g, Color color) {
        if (color == null) {
            System.err.println(color);
        }
        this.currentStrokeColor = color;
        g.setColor(color);
        g.setPaint(g.getColor());
        usingFill = false;
    }

    void reset(final Graphics2D g, final AffineTransform transform, double width, double height) {
        final Composite before = g.getComposite();
        g.setComposite(AlphaComposite.Clear);
        g.setTransform(BufferedImageExtended.IDENTITY);
        g.fillRect(0,0, (int) Math.ceil(width), (int) Math.ceil(height));
        g.setComposite(before);
        switchToFilled(g);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setTransform(transform);

    }

    void beginPath() {
        path.reset();
    }

    void moveTo(double endX, double endY) {
        path.moveTo(endX, endY);
    }

    void lineTo(double endX, double endY) {
        path.lineTo(endX, endY);
    }

    void quadTo(double cpX, double cpY, double endX, double endY) {
        path.quadTo(cpX, cpY, endX, endY);
    }

    void curveTo(double cp1X, double cp1Y, double cp2X, double cp2Y, double endX, double endY) {
        path.curveTo(cp1X, cp1Y, cp2X, cp2Y, endX, endY);
    }

    void arcTo(double rx, double ry, double xAxisRotationDegrees, boolean largeArc, boolean sweepFlag, double endX, double endY) {
        final Point2D point = path.getCurrentPoint();
        final Arc2D arc = computeArc(point.getX(), point.getY(), rx, ry, xAxisRotationDegrees, largeArc, sweepFlag, endX, endY);
        affineTransform.setToRotation(Math.toRadians(xAxisRotationDegrees), arc.getCenterX(), arc.getCenterY());
        path.append(new Path2D.Double(arc, affineTransform), true);
        affineTransform.setToIdentity();
    }

    void closePath() {
        path.closePath();
    }

    void fill(final Graphics2D g) {
        g.fill(path);
    }

    void stroke(final Graphics2D g) {
        switchToStroked(g);
        g.draw(path);
    }

    void clip(final Graphics2D g) {
        g.setClip(path);
    }


    void fillText(final Graphics2D g, String text, double x, double y) {
        switchToFilled(g);
        SwingUtils.drawMultilineTextLeft(g, text, x, y, 1, SwingUtils.getTextWidth(g.getFontMetrics(), text));
    }

    void fillText(final Graphics2D g, String text, double x, double y, double rotationDegrees, double pivotX, double pivotY) {
        switchToFilled(g);
        final AffineTransform lastTransform = g.getTransform();
        affineTransform.setTransform(lastTransform);
        affineTransform.rotate(Math.toRadians(rotationDegrees), pivotX, pivotY);
        g.setTransform(affineTransform);
        g.drawString(text, convert(x), convert(y));
        affineTransform.setTransform(lastTransform);
        g.setTransform(affineTransform);

    }

    void setFont(final Graphics2D g, net.mahdilamb.dataviz.graphics.Font font) {
        g.setFont(SwingUtils.convert(font));
    }

    void setClip(final Graphics2D g, ClipShape shape, double x, double y, double width, double height) {
        switch (shape) {
            case ELLIPSE:
                g.setClip(new Ellipse2D.Double(x, y, width, height));
                break;
            case RECTANGLE:
                g.setClip(new Rectangle2D.Double(x, y, width, height));
                break;
            default:
                throw new UnsupportedOperationException();
        }

    }

    void clearClip(final Graphics2D g) {
        g.setClip(null);
    }

    void drawImage(final Graphics2D g, BufferedImage bufferedImage, double x, double y) {
        g.drawImage(bufferedImage, AffineTransform.getTranslateInstance(x, y), null);
    }

    void setGlobalAlpha(final Graphics2D g, double alpha) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
    }

    private void switchToFilled(final Graphics2D g) {
        if (!usingFill) {
            if (currentFill.isA()) {
                if (!Objects.equals(g.getColor(), currentFill.asA())) {
                    g.setColor(currentFill.asA());
                    g.setPaint(g.getColor());
                }
            } else {
                g.setPaint(convert(currentFill.asB()));
            }
            usingFill = true;
        }
    }

    private void switchToStroked(final Graphics2D g) {
        if (usingFill) {
            g.setStroke(convert(currentStroke));
            g.setColor(currentStrokeColor);
            g.setPaint(g.getColor());
            usingFill = false;
        }
    }
}
