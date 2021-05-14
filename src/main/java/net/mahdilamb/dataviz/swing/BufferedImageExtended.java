package net.mahdilamb.dataviz.swing;

import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.graphics.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

/**
 * An extended version of buffered image which supports drawing floating precision geometries using a painter
 */
public final class BufferedImageExtended extends BufferedImage implements GraphicsBuffer {
    /**
     * An instance of the swing painter which is not thread-safe as it contains geometries that are reused
     */
    protected static final SwingPainter DEFAULT_PAINTER = new SwingPainter();

    static final AffineTransform IDENTITY = new AffineTransform();
    transient Graphics2D g;
    final AffineTransform transform;
    /**
     * Get the top and left overflow
     */
    public final int overflowTop, overflowLeft;
    /**
     * The width and height of the buffered image
     */
    public final double width, height;

    private final SwingPainter painter;

    /**
     * Create a simple buffered image
     *
     * @param width  the width of the image
     * @param height the height of the image
     */
    public BufferedImageExtended(double width, double height) {
        super((int) Math.ceil(width), (int) Math.ceil(height), BufferedImage.TYPE_INT_ARGB);
        painter = DEFAULT_PAINTER;
        this.width = width;
        this.height = height;

        transform = IDENTITY;
        this.overflowLeft = 0;
        this.overflowTop = 0;
    }

    /**
     * Create an extended buffered image with the given painter
     *
     * @param painter        the painter
     * @param width          the width of the image
     * @param height         the height of the image
     * @param x              the x position
     * @param y              the y position
     * @param overflowTop    the overflow at the top
     * @param overflowLeft   the overflow at the left
     * @param overflowBottom the overflow at the bottom
     * @param overflowRight  the overflow to the right
     */
    public BufferedImageExtended(final SwingPainter painter, double width, double height, double x, double y, int overflowTop, int overflowLeft, int overflowBottom, int overflowRight) {
        super((int) Math.ceil(width + overflowLeft + overflowRight), (int) Math.ceil(height + overflowTop + overflowBottom), BufferedImage.TYPE_INT_ARGB);
        this.width = width;
        this.height = height;
        this.painter = painter;
        final double translateX = -x + overflowLeft;
        final double translateY = -y + overflowTop;
        transform = AffineTransform.getTranslateInstance(translateX, translateY);
        this.overflowLeft = overflowLeft;
        this.overflowTop = overflowTop;
    }

    /**
     * Create an extended buffered image using the default swing painter
     *
     * @param width          the width of the image
     * @param height         the height of the image
     * @param x              the x position
     * @param y              the y position
     * @param overflowTop    the overflow at the top
     * @param overflowLeft   the overflow at the left
     * @param overflowBottom the overflow at the bottom
     * @param overflowRight  the overflow to the right
     */
    public BufferedImageExtended(double width, double height, double x, double y, int overflowTop, int overflowLeft, int overflowBottom, int overflowRight) {
        this(DEFAULT_PAINTER, width, height, x, y, overflowTop, overflowLeft, overflowBottom, overflowRight);
    }

    @Override
    public Graphics2D getGraphics() {
        if (g == null) {
            g = super.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setTransform(transform);
        }
        return g;
    }

    /**
     * @param font the font to get the metrics of
     * @return the font metrics of this image
     */
    public FontMetrics getFontMetrics(final java.awt.Font font) {
        return getGraphics().getFontMetrics(font);
    }

    @Override
    public void reset() {
        painter.reset(getGraphics(), transform, getWidth(), getHeight());
    }

    @Override
    public void done() {
        if (g == null) {
            return;
        }
        g.dispose();
        g = null;

    }

    @Override
    public void strokeRect(double x, double y, double width, double height) {
        painter.strokeRect(getGraphics(), x, y, width, height);

    }

    @Override
    public void fillRect(double x, double y, double width, double height) {
        painter.fillRect(getGraphics(), x, y, width, height);

    }

    @Override
    public void strokeRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        painter.strokeRoundRect(getGraphics(), x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void fillRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        painter.fillRoundRect(getGraphics(), x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void strokeOval(double x, double y, double width, double height) {
        painter.strokeOval(getGraphics(), x, y, width, height);
    }

    @Override
    public void fillOval(double x, double y, double width, double height) {
        painter.fillOval(getGraphics(), x, y, width, height);
    }

    @Override
    public void strokeLine(double x0, double y0, double x1, double y1) {
        painter.strokeLine(getGraphics(), x0, y0, x1, y1);
    }

    @Override
    public void setFill(Color color) {
        painter.setFill(getGraphics(), color);
    }

    @Override
    public void setFill(Gradient gradient) {
        painter.setFill(getGraphics(), gradient);
    }

    @Override
    public void setStroke(Stroke stroke) {
        painter.setStroke(getGraphics(), stroke);
    }

    @Override
    public void setStroke(Color color) {
        painter.setStroke(getGraphics(), color);
    }

    @Override
    public void beginPath() {
        painter.beginPath();
    }

    @Override
    public void moveTo(double endX, double endY) {
        painter.moveTo(endX, endY);
    }

    @Override
    public void lineTo(double endX, double endY) {
        painter.lineTo(endX, endY);
    }

    @Override
    public void quadTo(double cpX, double cpY, double endX, double endY) {
        painter.quadTo(cpX, cpY, endX, endY);
    }

    @Override
    public void curveTo(double cp1X, double cp1Y, double cp2X, double cp2Y, double endX, double endY) {
        painter.curveTo(cp1X, cp1Y, cp2X, cp2Y, endX, endY);
    }

    @Override
    public void arcTo(double rx, double ry, double xAxisRotationDegrees, boolean largeArc, boolean sweepFlag, double endX, double endY) {
        painter.arcTo(rx, ry, xAxisRotationDegrees, largeArc, sweepFlag, endX, endY);
    }

    @Override
    public void closePath() {
        painter.closePath();
    }

    @Override
    public void fill() {
        painter.fill(getGraphics());
    }

    @Override
    public void stroke() {
        painter.stroke(getGraphics());
    }

    @Override
    public void fillText(String text, double x, double y) {
        painter.fillText(getGraphics(), text, x, y);

    }

    @Override
    public void fillText(String text, double x, double y, double rotationDegrees, double pivotX, double pivotY) {
        painter.fillText(getGraphics(), text, x, y, rotationDegrees, pivotX, pivotY);
    }

    @Override
    public void setFont(Font font) {
        painter.setFont(getGraphics(), font);
    }

    @Override
    public void setClip(ClipShape shape, double x, double y, double width, double height) {
        painter.setClip(getGraphics(), shape, x, y, width, height);
    }

    @Override
    public void clearClip() {
        painter.clearClip(getGraphics());
    }

    @Override
    public void drawImage(BufferedImage bufferedImage, double x, double y) {
        painter.drawImage(getGraphics(), bufferedImage, x, y);
    }

    @Override
    public void setGlobalAlpha(double alpha) {
        painter.setGlobalAlpha(getGraphics(), alpha);
    }
}
