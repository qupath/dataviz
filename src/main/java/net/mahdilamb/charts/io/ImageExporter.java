package net.mahdilamb.charts.io;

import net.mahdilamb.charts.ChartExporter;
import net.mahdilamb.charts.Renderer;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Paint;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.swing.SwingUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static net.mahdilamb.charts.swing.SwingUtils.convert;

/**
 * Main class for exporting to bitmap types via AWT
 */
public class ImageExporter extends ChartExporter {


    /**
     * Create a PNG version of a chart
     *
     * @param file  the output file
     * @param chart the chart
     */
    public static void toPNG(File file, Renderer<?> chart) {
        try {
            ImageIO.write(toBufferedImage(BufferedImage.TYPE_INT_ARGB, chart), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create a TIFF version of a chart
     *
     * @param file  the output file
     * @param chart the chart
     */
    public static void toTIFF(File file, Renderer<?> chart) {
        try {
            ImageIO.write(toBufferedImage(BufferedImage.TYPE_INT_ARGB, chart), "tiff", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create a JPEG version of a chart
     *
     * @param file  the output file
     * @param chart the chart
     */
    public static void toJPEG(File file, Renderer<?> chart) {
        try {
            ImageIO.write(toBufferedImage(BufferedImage.TYPE_INT_RGB, chart), "jpeg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a BMP version of a chart
     *
     * @param file  the output file
     * @param chart the chart
     */
    public static void toBMP(File file, Renderer<?> chart) {
        try {
            ImageIO.write(toBufferedImage(BufferedImage.TYPE_INT_RGB, chart), "bmp", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final class ImageExporterCanvas extends Component implements ChartCanvas<java.awt.image.BufferedImage> {

        private final Graphics2D g;
        private final Renderer<?> chart;
        private final Path2D path = new Path2D.Double();
        private final Rectangle2D rect = new Rectangle2D.Double();
        private final RoundRectangle2D roundedRect = new RoundRectangle2D.Double();
        private final Ellipse2D ellipse = new Ellipse2D.Double();
        private final Line2D line = new Line2D.Double();
        private final boolean fillWhite;
        private Paint currentFill = Paint.BLACK_FILL;
        private Stroke currentStroke = Stroke.SOLID;
        private net.mahdilamb.colormap.Color currentStrokeColor = net.mahdilamb.colormap.Color.BLACK;
        boolean usingFill = true;
        private final AffineTransform affineTransform = new AffineTransform();

        public ImageExporterCanvas(boolean fillWhite, Renderer<?> chart, Graphics2D graphics) {
            this.g = graphics;
            this.chart = chart;
            this.fillWhite = fillWhite;
        }

        @Override
        public void resetRect(double x, double y, double width, double height) {
            //ignored
        }

        @Override
        public void reset() {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getFigure(chart).getBackgroundColor() != null) {
                g.setColor(convert(getFigure(chart).getBackgroundColor()));
            } else if (fillWhite) {
                g.setColor(Color.white);
            }

            g.fillRect(0, 0, convert(getFigure(chart).getWidth()), convert(getFigure(chart).getHeight()));
            g.setColor(Color.BLACK);
            g.setPaint(g.getColor());
        }

        @Override
        public void done() {

        }

        private void switchToFilled() {
            if (!usingFill) {
                if (currentFill.isGradient()) {
                    g.setPaint(convert(currentFill.getGradient()));
                } else {
                    g.setColor(convert(currentFill.getColor()));
                    g.setPaint(g.getColor());
                }
                usingFill = true;
            }

        }

        private void switchToStroked() {
            if (usingFill) {
                g.setStroke(convert(currentStroke));
                g.setColor(convert(currentStrokeColor));
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
        public void setFill(Paint fill) {
            this.currentFill = fill;
            if (currentFill.isGradient()) {
                g.setPaint(convert(currentFill.getGradient()));
            } else {
                g.setColor(convert(currentFill.getColor()));
                g.setPaint(g.getColor());
            }
            usingFill = true;
        }

        @Override
        public void setStroke(Stroke stroke) {
            this.currentStroke = stroke;
            g.setStroke(convert(stroke));
            usingFill = false;
        }

        @Override
        public void setStroke(net.mahdilamb.colormap.Color color) {
            usingFill = false;
            currentStrokeColor = color;
            g.setColor(convert(color));
            g.setPaint(convert(color));
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
        public void drawImage(java.awt.image.BufferedImage bufferedImage, double x, double y) {
            g.drawImage(bufferedImage, convert(x), convert(y), null);
        }

    }

    private static BufferedImage toBufferedImage(int encoding, final Renderer<?> chart) {
        final BufferedImage image = new BufferedImage((int) getFigure(chart).getWidth(), (int) getFigure(chart).getHeight(), encoding);
        layoutChart(new ImageExporterCanvas(encoding != BufferedImage.TYPE_INT_ARGB, chart, (Graphics2D) image.getGraphics()), chart);
        return image;
    }

}
