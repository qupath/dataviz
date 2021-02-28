package net.mahdilamb.charts.swing;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Renderer;
import net.mahdilamb.charts.Title;
import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.ClipShape;
import net.mahdilamb.charts.graphics.Font;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.Queue;
import java.util.function.Consumer;

import static net.mahdilamb.charts.swing.SwingUtils.convert;

public class SwingRenderer extends Renderer<BufferedImage> {

    final Panel panel;

    public SwingRenderer(Figure figure) {
        super(figure);
        panel = new Panel(this);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JFrame frame = new JFrame();
                int width = (int) Math.ceil(figure.getWidth()),
                        height = (int) Math.ceil(figure.getHeight());
                panel.setSize(width, height);
                panel.setPreferredSize(panel.getSize());
                frame.getContentPane().add(panel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLayout(null);
                frame.setVisible(true);
                refresh();
            }
        });
    }

    @Override
    protected Panel getCanvas() {
        return panel;
    }

    /**
     * Calculate the height of wrapped text
     *
     * @param fontMetrics the font metrics to use
     * @param text        the text to display
     * @param maxWidth    the maximum width of the text
     * @param lineSpacing the line spacing
     * @return the height of wrapped text
     */
    public static double getTextLineHeight(final FontMetrics fontMetrics, final String text, double maxWidth, double lineSpacing) {
        int lineCount = 0;
        int i = 0;
        int wordStart = 0;
        double currentWidth = 0;
        while (i < text.length()) {
            char c = text.charAt(i++);
            if (Character.isWhitespace(c) && i != 0) {
                final String word = text.substring(wordStart, i);
                currentWidth += fontMetrics.stringWidth(word);
                if (currentWidth > maxWidth) {
                    ++lineCount;
                    currentWidth = 0;
                }
                wordStart = i;
            }
        }
        if (currentWidth > 0) {
            ++lineCount;
        }
        return lineSpacing * lineCount * fontMetrics.getHeight();
    }

    @Override
    protected double getTextLineHeight(Title title, double maxWidth, double lineSpacing) {
        if (title.getText() == null || title.getText().length() == 0) {
            return 0;
        }
        return getTextLineHeight(panel.getFontMetrics(SwingUtils.convert(title.getFont())), title.getText(), maxWidth, lineSpacing);
    }

    @Override
    protected double getTextBaselineOffset(Font font) {
        return panel.getTextBaselineOffset(font);
    }


    @Override
    protected double getTextWidth(Font font, String text) {
        return panel.getTextWidth(font, text);
    }

    @Override
    protected double getCharWidth(Font font, char character) {
        return panel.getFontMetrics(SwingUtils.convert(font)).charWidth(character);
    }

    @Override
    protected double getTextLineHeight(Font font) {
        return panel.getTextHeight(font);
    }

    @Override
    protected double getImageWidth(BufferedImage image) {
        return panel.getImageWidth(image);
    }

    @Override
    protected double getImageHeight(BufferedImage image) {
        return panel.getImageHeight(image);
    }

    @Override
    protected byte[] bytesFromImage(BufferedImage image) {
        return panel.bytesFromImage(image);
    }

    @Override
    protected int argbFromImage(BufferedImage image, int x, int y) {
        return image.getRGB(x, y);
    }

    @Override
    protected double getTextLineHeight(Title title) {
        return panel.getFontMetrics(SwingUtils.convert(title.getFont())).getHeight();
    }
    private static final class Panel extends JPanel implements ChartCanvas<BufferedImage> {

        private final Queue<Consumer<Graphics2D>> queue = new ArrayDeque<>();
        private final SwingRenderer renderer;
        private net.mahdilamb.charts.graphics.Paint currentFill = net.mahdilamb.charts.graphics.Paint.BLACK_FILL;
        private net.mahdilamb.charts.graphics.Stroke currentStroke = net.mahdilamb.charts.graphics.Stroke.SOLID;
        private net.mahdilamb.colormap.Color currentStrokeColor = net.mahdilamb.colormap.Color.BLACK;

        private final Rectangle2D rect = new Rectangle2D.Double();
        private final RoundRectangle2D roundedRect = new RoundRectangle2D.Double();
        private final Ellipse2D ellipse = new Ellipse2D.Double();
        private final Line2D line = new Line2D.Double();
        private final Path2D path = new Path2D.Double();
        private final AffineTransform affineTransform = new AffineTransform();
        boolean usingFill = true;

        Panel(SwingRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public void paint(Graphics g) {
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
                        g.setColor(convert(currentFill.getColor()));
                        g.setPaint(g.getColor());
                    }

                    usingFill = true;
                }
            });
            return queue;
        }

        private Queue<Consumer<Graphics2D>> switchToStroked() {
            queue.add(g -> {
                if (usingFill) {
                    g.setStroke(convert(currentStroke));
                    g.setColor(convert(currentStrokeColor));
                    g.setPaint(g.getColor());
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
        public void setFill(net.mahdilamb.charts.graphics.Paint fill) {
            queue.add(g -> {
                this.currentFill = fill;
                if (currentFill.isGradient()) {
                    g.setPaint(convert(currentFill.getGradient()));
                } else {
                    g.setColor(convert(currentFill.getColor()));
                    g.setPaint(g.getColor());
                }
                usingFill = true;
            });
        }

        @Override
        public void setStroke(net.mahdilamb.charts.graphics.Stroke stroke) {
            queue.add(g -> {
                this.currentStroke = stroke;
                g.setStroke(convert(currentStroke));
                usingFill = false;
            });
        }

        @Override
        public void setStroke(net.mahdilamb.colormap.Color color) {
            queue.add(g -> {
                this.currentStrokeColor = color;
                g.setColor(convert(color));
                g.setPaint(g.getColor());
                usingFill = false;
            });
        }

        @Override
        public void done() {
            repaint();
        }

        @Override
        public void resetRect(double x, double y, double width, double height) {
            queue.add(g -> {
                if (renderer.figure.getBackgroundColor() != null) {
                    g.setColor(convert(renderer.figure.getBackgroundColor()));
                } else {
                    g.setColor(Color.WHITE);
                }
                g.clearRect(convert(x), convert(y), convert(width), convert(height));
            });
        }

        @Override
        public void reset() {
            queue.clear();
            queue.add(g -> {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (renderer.figure.getBackgroundColor() != null) {
                    g.setColor(convert(renderer.figure.getBackgroundColor()));
                } else {
                    g.setColor(Color.WHITE);

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
            switchToFilled().add(g -> SwingUtils.drawMultilineTextLeft(g, text, x, y, 1, SwingUtils.getTextWidth(g.getFontMetrics(), text)));
        }

        @Override
        public void fillText(String text, double x, double y, double rotationDegrees, double pivotX, double pivotY) {
            switchToFilled().add(g -> {
                affineTransform.setToIdentity();
                affineTransform.rotate(Math.toRadians(rotationDegrees), pivotX, pivotY);
                g.setTransform(affineTransform);
                g.drawString(text, convert(x), convert(y));

                affineTransform.setToIdentity();
                g.setTransform(affineTransform);

            });

        }

        @Override
        public void setFont(net.mahdilamb.charts.graphics.Font font) {
            queue.add(g -> g.setFont(SwingUtils.convert(font)));

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
                        //use the rect so we can keep double precision
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
            queue.add(g -> g.setClip(null));
        }

        @Override
        public void drawImage(BufferedImage bufferedImage, double x, double y) {
            queue.add(g -> g.drawImage(bufferedImage, SwingUtils.convert(x), SwingUtils.convert(y), null));
        }

        private double getImageWidth(BufferedImage bufferedImage) {
            return bufferedImage.getWidth();
        }

        private double getImageHeight(BufferedImage bufferedImage) {
            return bufferedImage.getHeight();
        }

        private byte[] bytesFromImage(BufferedImage bufferedImage) {
            return SwingUtils.convertToByteArray(bufferedImage);
        }

        private double getTextBaselineOffset(net.mahdilamb.charts.graphics.Font font) {
            final FontMetrics fontMetrics = getFontMetrics(SwingUtils.convert(font));
            return fontMetrics.getAscent();
        }

        private double getTextWidth(net.mahdilamb.charts.graphics.Font font, String text) {
            return SwingUtils.getTextWidth(getFontMetrics(SwingUtils.convert(font)), text);

        }

        double getTextHeight(Font font) {
            return SwingUtils.getLineHeight(getFontMetrics(SwingUtils.convert(font)));

        }
    }

}
