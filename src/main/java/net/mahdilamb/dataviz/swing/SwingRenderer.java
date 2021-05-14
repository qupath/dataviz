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

/**
 * Default Swing renderer
 */
public final class SwingRenderer extends Renderer {
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
    protected synchronized void done() {
        if (panel != null) {
            panel.repaint();
        }
    }


    @Override
    protected double getTextBaselineOffset(Font font) {
        final FontMetrics fontMetrics = canvas.getBuffer().getFontMetrics(FontUtils.convert(font));
        return fontMetrics.getAscent();
    }

    @Override
    protected double getTextWidth(Font font, String text) {
        return FontUtils.getTextWidth(canvas.getBuffer().getFontMetrics(FontUtils.convert(font)), text);
    }

    @Override
    protected double getCharWidth(Font font, char character) {
        return canvas.getBuffer().getFontMetrics(FontUtils.convert(font)).charWidth(character);
    }

    @Override
    protected double getTextLineHeight(Font font, final String text) {
        if (text == null) {
            return canvas.getBuffer().getFontMetrics(FontUtils.convert(font)).getHeight();
        }
        return FontUtils.getLineHeight(canvas.getBuffer().getFontMetrics(FontUtils.convert(font)), text, 1);
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
    protected String getFromClipboard() {
        try {
            if (Toolkit.getDefaultToolkit()
                    .getSystemClipboard().isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                return (String) Toolkit.getDefaultToolkit()
                        .getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException ignored) {
        }
        return null;
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
                    SwingRenderer.this.keyTyped(e.isControlDown(), e.isShiftDown(), e.getKeyCode(), e.getKeyChar());
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    SwingRenderer.this.keyPressed(e.isControlDown(), e.isShiftDown(), e.getKeyCode(), e.getKeyChar());
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    SwingRenderer.this.keyReleased(e.isControlDown(), e.isShiftDown(), e.getKeyCode(), e.getKeyChar());

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
