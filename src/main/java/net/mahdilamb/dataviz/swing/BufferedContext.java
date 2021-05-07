package net.mahdilamb.dataviz.swing;

import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.Stroke;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BufferedContext<R extends Renderer<BufferedImage>> implements GraphicsContext<BufferedImage> {
    final R renderer;
    private BufferedImageExtended buffer;

    BufferedContext(R renderer) {
        this.renderer = renderer;
    }

    BufferedImageExtended getBuffer() {
        if (buffer == null) {
            buffer = new BufferedImageExtended(getWidth(), getHeight());
        }
        //if dimensions have changed, then copy the buffer over to a bigger buffer
        if (buffer.getWidth() < getWidth() || buffer.getHeight() < getHeight()) {
            if (buffer.g != null) {
                //dispose old buffer
                buffer.dispose();
            }
            final BufferedImageExtended newBufferedImage = new BufferedImageExtended(getWidth(), getHeight());
            buffer.g = newBufferedImage.getGraphics();
            buffer.g.drawImage(buffer, 0, 0, null);
            buffer = newBufferedImage;
        }
        return buffer;
    }

    final int getWidth() {
        return (int) Math.ceil(renderer.getFigure().getWidth());
    }


    final int getHeight() {
        return (int) Math.ceil(renderer.getFigure().getHeight());
    }

    @Override
    public void reset() {
        final Graphics2D g = getBuffer().getGraphics();
        getBuffer().reset();
        if (Renderer.isFigureContext(renderer, this) && renderer.getFigure().getBackgroundColor() != null) {
            g.setColor(renderer.getFigure().getBackgroundColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        g.setColor(Color.BLACK);
    }

    @Override
    public void done() {
        buffer.done();
    }

    @Override
    public void strokeRect(double x, double y, double width, double height) {
        buffer.strokeRect(x, y, width, height);
    }

    @Override
    public void fillRect(double x, double y, double width, double height) {
        buffer.fillRect(x, y, width, height);
    }

    @Override
    public void strokeRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        buffer.strokeRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void fillRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        buffer.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void strokeOval(double x, double y, double width, double height) {
        buffer.strokeOval(x, y, width, height);
    }

    @Override
    public void fillOval(double x, double y, double width, double height) {
        buffer.fillOval(x, y, width, height);
    }

    @Override
    public void strokeLine(double x0, double y0, double x1, double y1) {
        buffer.strokeLine(x0, y0, x1, y1);
    }

    @Override
    public void setFill(Color color) {
        buffer.setFill(color);
    }

    @Override
    public void setFill(Gradient gradient) {
        buffer.setFill(gradient);
    }

    @Override
    public void setStroke(Stroke stroke) {
        buffer.setStroke(stroke);
    }

    @Override
    public void setStroke(Color color) {
        buffer.setStroke(color);
    }

    @Override
    public void beginPath() {
        buffer.beginPath();
    }

    @Override
    public void moveTo(double endX, double endY) {
        buffer.moveTo(endX, endY);
    }

    @Override
    public void lineTo(double endX, double endY) {
        buffer.lineTo(endX, endY);
    }

    @Override
    public void quadTo(double cpX, double cpY, double endX, double endY) {
        buffer.quadTo(cpX, cpY, endX, endY);
    }

    @Override
    public void curveTo(double cp1X, double cp1Y, double cp2X, double cp2Y, double endX, double endY) {
        buffer.curveTo(cp1X, cp1Y, cp2X, cp2Y, endX, endY);
    }

    @Override
    public void arcTo(double rx, double ry, double xAxisRotationDegrees, boolean largeArc, boolean sweepFlag, double endX, double endY) {
        buffer.arcTo(rx, ry, xAxisRotationDegrees, largeArc, sweepFlag, endX, endY);
    }

    @Override
    public void closePath() {
        buffer.closePath();
    }

    @Override
    public void fill() {
        buffer.fill();
    }

    @Override
    public void stroke() {
        buffer.stroke();
    }

    @Override
    public void fillText(String text, double x, double y) {
        buffer.fillText(text, x, y);
    }

    @Override
    public void fillText(String text, double x, double y, double rotationDegrees, double pivotX, double pivotY) {
        buffer.fillText(text, x, y, rotationDegrees, pivotX, pivotY);
    }

    @Override
    public void setFont(Font font) {
        buffer.setFont(font);
    }

    @Override
    public void setClip(ClipShape shape, double x, double y, double width, double height) {
        buffer.setClip(shape, x, y, width, height);
    }

    @Override
    public void clearClip() {
        buffer.clearClip();
    }

    @Override
    public void drawImage(BufferedImage bufferedImage, double x, double y) {
        buffer.drawImage(bufferedImage, x, y);
    }

    @Override
    public void setGlobalAlpha(double alpha) {
        buffer.setGlobalAlpha(alpha);
    }

    @Override
    public R getRenderer() {
        return renderer;
    }
}
