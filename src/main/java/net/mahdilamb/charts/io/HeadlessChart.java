package net.mahdilamb.charts.io;

import net.mahdilamb.charts.Chart;
import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.Title;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.swing.SwingChart;
import net.mahdilamb.charts.swing.SwingUtils;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import static net.mahdilamb.charts.swing.SwingUtils.convert;
import static net.mahdilamb.charts.swing.SwingUtils.convertToByteArray;

//TODO come back to this
public abstract class HeadlessChart< S extends PlotSeries<S>> extends Chart<S> {


    protected HeadlessChart(String title, double width, double height, PlotLayout<S> plot, HeadlessChartCanvas<S> canvas) {
        super(title, width, height, plot);
        this.canvas = canvas;
    }

    private static final class HeadlessChartCanvas<S extends PlotSeries<S>> extends Component implements ChartCanvas<BufferedImage> {

        private Graphics2D g;
        private Chart<S> chart;
        private final Path2D path = new Path2D.Double();
        private final Rectangle2D rect = new Rectangle2D.Double();
        private final RoundRectangle2D roundedRect = new RoundRectangle2D.Double();
        private final Ellipse2D ellipse = new Ellipse2D.Double();
        private final Line2D line = new Line2D.Double();
        private Fill currentFill = Fill.BLACK_FILL;
        private Stroke currentStroke = Stroke.BLACK_STROKE;
        boolean usingFill = true;
        private BufferedImage image;
        private boolean supportTransparency;
        private final AffineTransform affineTransform = new AffineTransform();

        HeadlessChartCanvas() {

        }

        void setChart(Chart<S> chart, boolean supportTransparency) {
            this.chart = chart;
            this.image = new BufferedImage((int) chart.getWidth(), (int) chart.getHeight(), supportTransparency ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
            this.g = (Graphics2D) this.image.getGraphics();
            this.supportTransparency = supportTransparency;

        }

        @Override
        public void reset() {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (chart.getBackgroundColor() != null) {
                g.setColor(convert(chart.getBackgroundColor()));
            } else if (!supportTransparency) {
                g.setColor(Color.white);
            }

            g.fillRect(0, 0, convert(chart.getWidth()), convert(chart.getHeight()));
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
                g.setStroke(new BasicStroke((float) currentStroke.getWidth()));
                g.setColor(convert(this.currentStroke.getColor()));
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
        public void setFill(Fill fill) {
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
            g.setStroke(new BasicStroke((float) stroke.getWidth()));
            g.setColor(convert(this.currentStroke.getColor()));
            usingFill = false;
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

        private double getTextBaselineOffset(Font font) {
            final FontMetrics fontMetrics = getFontMetrics(SwingUtils.convert(font));
            return fontMetrics.getAscent();
        }

        private double getTextWidth(Font font, String text) {
            return SwingUtils.getTextWidth(getFontMetrics(SwingUtils.convert(font)), text);

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
        public void drawImage(BufferedImage bufferedImage, double x, double y) {
            g.drawImage(bufferedImage, convert(x), convert(y), null);
        }

        private double getImageWidth(BufferedImage bufferedImage) {
            return bufferedImage.getHeight();
        }


        private double getImageHeight(BufferedImage bufferedImage) {
            return bufferedImage.getHeight();
        }

        private byte[] bytesFromImage(BufferedImage bufferedImage) {
            return convertToByteArray(bufferedImage);
        }

        public double getTextLineHeight(Font font) {
            return SwingUtils.getLineHeight(getFontMetrics(SwingUtils.convert(font)));

        }
    }

    private final HeadlessChartCanvas<S> canvas;
/*
    @SuppressWarnings("unchecked")
    protected HeadlessChart(String title, double width, double height, P plot) {
        super(title, width, height, (PlotLayoutImpl<S>) plot);
        this.canvas = new HeadlessChartCanvas<>();
    }
*/
    @Override
    protected ChartCanvas<?> getCanvas() {
        return canvas;
    }

    @Override
    protected double getTextLineHeight(Title title, double maxWidth, double lineSpacing) {
        if (title.getText() == null || title.getText().length() == 0) {
            return 0;
        }
        return SwingChart.getTextLineHeight(canvas.getFontMetrics(SwingUtils.convert(title.getFont())), title.getText(), maxWidth, lineSpacing);
    }

    @Override
    protected double getTextBaselineOffset(Font font) {
        return canvas.getTextBaselineOffset(font);
    }

    @Override
    protected double getTextWidth(Font font, String text) {
        return canvas.getTextWidth(font, text);
    }

    @Override
    protected double getTextLineHeight(Font font) {
        return canvas.getTextLineHeight(font);
    }

    @Override
    protected double getImageWidth(Object image) throws ClassCastException {
        return canvas.getImageWidth((BufferedImage) image);
    }

    @Override
    protected double getImageHeight(Object image) throws ClassCastException {
        return canvas.getImageHeight((BufferedImage) image);
    }

    @Override
    protected byte[] bytesFromImage(Object image) throws ClassCastException {
        return canvas.bytesFromImage((BufferedImage) image);
    }

    @Override
    protected int argbFromImage(Object image, int x, int y) {
        return ((BufferedImage) image).getRGB(x, y);
    }

    @Override
    protected void backgroundChanged() {
        //Not required
    }

    @Override
    protected double getTextLineHeight(Title title) {
        return canvas.getFontMetrics(SwingUtils.convert(title.getFont())).getHeight();

    }


}
