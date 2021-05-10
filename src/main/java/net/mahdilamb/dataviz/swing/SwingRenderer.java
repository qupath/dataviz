package net.mahdilamb.dataviz.swing;

import net.mahdilamb.dataviz.figure.FigureBase;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.utils.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static net.mahdilamb.dataviz.swing.SwingUtils.convert;

/**
 * Default Swing renderer
 */
public final class SwingRenderer extends Renderer<BufferedImage> {
    private static final JFileChooser fileChooser = new JFileChooser();

    FigurePanel panel;
    private final BufferedContext canvas = new BufferedContext(this);
    private final BufferedContext overlay = new BufferedContext(this);

    private static final class FigurePanel extends JPanel {

        SwingRenderer renderer;

        FigurePanel(SwingRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(renderer.canvas.getBuffer(), 0, 0, null);
            g.drawImage(renderer.overlay.getBuffer(), 0, 0, null);
        }
    }

    /**
     * Render and show a figure
     *
     * @param figure the figure to show
     */
    public SwingRenderer(FigureBase<?> figure) {
        this(figure, false);
    }

    /**
     * Render and show a figure (if not headless)
     *
     * @param figure   the figure to show
     * @param headless whether to create the renderer as headless or not
     */
    public SwingRenderer(FigureBase<?> figure, boolean headless) {
        super();
        init(figure);
        if (!headless) {
            try {
                SwingUtilities.invokeAndWait(this::show);
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected BufferedContext getFigureContext() {
        return canvas;
    }

    @Override
    protected BufferedContext getOverlayContext() {
        return overlay;
    }

    @Override
    protected void done() {
        if (panel != null) {
            panel.repaint();
        }
    }

    @Override
    protected BufferedImage loadImage(InputStream stream) {
        try {
            return ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected BufferedImageExtended createBuffer(double width, double height, double translateX, double translateY, int overflowTop, int overflowLeft, int overflowBottom, int overflowRight) {
        return new BufferedImageExtended(width, height, translateX, translateY, overflowTop, overflowLeft, overflowBottom, overflowRight);
    }

    @Override
    protected void drawBuffer(GraphicsBuffer<BufferedImage> context, GraphicsBuffer<BufferedImage> buffer, double x, double y) {
        final BufferedImageExtended bufferedImageExtended = (BufferedImageExtended) buffer;
        context.drawImage(bufferedImageExtended, x - bufferedImageExtended.overflowLeft, y - bufferedImageExtended.overflowTop);
    }

    @Override
    protected BufferedImage cropImage(BufferedImage source, int x, int y, int width, int height) {
        return source.getSubimage(x, y, width, height);
    }

    @Override
    protected double getTextBaselineOffset(Font font) {
        final FontMetrics fontMetrics = canvas.getBuffer().getFontMetrics(SwingUtils.convert(font));
        return fontMetrics.getAscent();
    }

    @Override
    protected double getTextWidth(Font font, String text) {
        return SwingUtils.getTextWidth(canvas.getBuffer().getFontMetrics(SwingUtils.convert(font)), text);
    }

    @Override
    protected double getCharWidth(Font font, char character) {
        return canvas.getBuffer().getFontMetrics(SwingUtils.convert(font)).charWidth(character);
    }

    @Override
    protected double getTextLineHeight(Font font, final String text) {
        if (text == null) {
            return canvas.getBuffer().getFontMetrics(SwingUtils.convert(font)).getHeight();
        }
        return SwingUtils.getLineHeight(canvas.getBuffer().getFontMetrics(SwingUtils.convert(font)), text, 1);
    }

    @Override
    protected double getImageWidth(BufferedImage image) {
        return image.getWidth();
    }

    @Override
    protected double getImageHeight(BufferedImage image) {
        return image.getHeight();
    }

    @Override
    protected byte[] bytesFromImage(BufferedImage image) {
        return SwingUtils.convertToByteArray(image);
    }

    @Override
    protected int argbFromImage(BufferedImage image, int x, int y) {
        return image.getRGB(x, y);
    }

    @Override
    protected File getOutputPath(List<String> fileTypes, String defaultExtension) {
        if (fileTypes == null) {
            fileChooser.setFileFilter(null);
        } else {
            fileChooser.setFileFilter(new FileNameExtensionFilter(String.format("Image types %s", fileTypes), fileTypes.toArray(new String[0])));
        }
        if (fileChooser.showSaveDialog(getPanel()) == JFileChooser.APPROVE_OPTION) {
            if (!StringUtils.hasFileExtension(fileChooser.getSelectedFile().toString())) {
                return new File(fileChooser.getSelectedFile().toString() + defaultExtension);
            }
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    @Override
    protected boolean bufferSizeChanged(GraphicsBuffer<BufferedImage> buffer, double x, double y, double width, double height) {
        final BufferedImageExtended graphicsBuffer = (BufferedImageExtended) buffer;//TODO full size changed
        return (graphicsBuffer.width) != width || (graphicsBuffer.height) != height;
    }

    @Override
    protected String getFromClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void addToClipboard(String text) {
        final StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
    }

    /**
     * @return the JPanel containing the figure
     */
    public JPanel getPanel() {
        if (panel == null) {
            panel = new FigurePanel(this);

            int width = (int) Math.ceil(getFigure().getWidth()),
                    height = (int) Math.ceil(getFigure().getHeight());
            final Dimension dims = new Dimension(width, height);

            panel.setPreferredSize(dims);
            panel.setSize(dims);
            panel.setMinimumSize(dims);

            panel.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    SwingRenderer.this.mouseMoved(e.isControlDown(), e.isShiftDown(), e.getX(), e.getY());
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    SwingRenderer.this.mouseMoved(e.isControlDown(), e.isShiftDown(), e.getX(), e.getY());
                }
            });
            panel.addMouseWheelListener(e -> SwingRenderer.this.mouseScrolled(e.isControlDown(), e.isShiftDown(), e.getX(), e.getY(), calculateScrollRotation(e)));
            panel.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    SwingRenderer.this.keyTyped(e.isControlDown(), e.isShiftDown(), e.getKeyCode());
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    SwingRenderer.this.keyPressed(e.isControlDown(), e.isShiftDown(), e.getKeyCode());
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    SwingRenderer.this.keyReleased(e.isControlDown(), e.isShiftDown(), e.getKeyCode());

                }
            });
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    SwingRenderer.this.mouseExited();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() != MouseEvent.BUTTON1) {
                        return;
                    }
                    SwingRenderer.this.mousePressed(e.isControlDown(), e.isShiftDown(), e.getX(), e.getY());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    SwingRenderer.this.mouseReleased(e.isControlDown(), e.isShiftDown(), e.getX(), e.getY());
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (e.getClickCount() == 2) {
                            SwingRenderer.this.mouseDoubleClicked(e.isControlDown(), e.isShiftDown(), e.getX(), e.getY());
                        } else {
                            SwingRenderer.this.mouseClicked(e.isControlDown(), e.isShiftDown(), e.getX(), e.getY());
                        }
                    }
                    super.mouseClicked(e);
                }
            });
        }
        return panel;
    }

    private static double calculateScrollRotation(MouseWheelEvent e) {
        double v = e.getPreciseWheelRotation() * e.getScrollAmount();
        v *= .1;
        if (Math.abs(v) > 1) {
            return 1 / v;
        }
        return v;
    }

    private void show() {
        final JFrame frame = new JFrame(getFigure().getTitle());
        final JComponent panel = getPanel();
        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        panel.requestFocus();
        frame.setResizable(false);
    }

}
