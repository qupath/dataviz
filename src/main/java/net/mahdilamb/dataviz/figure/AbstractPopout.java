package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.utils.ColorUtils;
import net.mahdilamb.dataviz.utils.Numbers;

import java.awt.*;

abstract class AbstractPopout<T> extends Component {

    static final double triWidth = 10;

    static final double DEFAULT_TOOLTIP_RADIUS = 5;
    static final double DEFAULT_RELATIVE_POSITION = 0.5;
    static final VAlign DEFAULT_VALIGN = VAlign.BOTTOM;
    static final HAlign DEFAULT_HALIGN = HAlign.CENTER;
    static final Side DEFAULT_SIDE = Side.BOTTOM;
    static final Color DEFAULT_OUTLINE = null;
    static final boolean DEFAULT_SHOW_ARROW = false;

    protected VAlign vAlign;
    protected HAlign hAlign;
    protected double radius = 3;
    protected Color background, outline;
    protected T content;
    AbstractComponent component;

    static final double paddingX = 6, paddingY = 4;
    private double x, y, width, height;
    private final boolean drawRelative;
    private final double relativePosition;
    private double contentX = Double.NaN, contentY = Double.NaN;
    private final Side side;

    boolean showArrow;

    protected AbstractPopout(VAlign vAlign, HAlign hAlign, double relativePosition, Color background, Color outline, Side side, T content, boolean showArrow) {
        if (relativePosition < 0 || relativePosition > 1.) {
            throw new IllegalArgumentException("relative position needs to be between 0-1");
        }
        this.vAlign = vAlign;
        this.hAlign = hAlign;
        this.relativePosition = relativePosition;
        this.background = background;
        this.outline = outline;
        this.side = side;
        this.content = content;
        this.showArrow = showArrow;
        drawRelative = true;
    }

    protected AbstractPopout(double x, double y, Color background, Color outline, Side side, T content, boolean showArrow) {
        relativePosition = .5;
        drawRelative = false;
        this.x = x;
        this.y = y;
        this.background = background;
        this.outline = outline;
        this.side = side;
        this.content = content;
        this.showArrow = showArrow;
    }

    /**
     * @return the background color
     */
    public Color getBackground() {
        return background;
    }

    /**
     * @return the foreground color
     */
    protected Color getForeground() {
        return ColorUtils.getForegroundFromBackground(background);
    }

    protected abstract void layoutContent(Renderer renderer, double minX, double minY, double maxX, double maxY);

    @Override
    protected final void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        width = getContentWidth(renderer) + paddingX + paddingX;
        height = getContentHeight(renderer) + paddingY + paddingY;
        double triWidth = 10;
        double triHeight = triWidth * .5;
        if (drawRelative) {
            x = component.getX();
            y = component.getY();
            if (vAlign == VAlign.MIDDLE) {
                y += component.getHeight() * .5;
            } else if (vAlign == VAlign.BOTTOM) {
                y += component.getHeight();
            }
            if (hAlign == HAlign.CENTER) {
                x += component.getWidth() * .5;
            } else if (hAlign == HAlign.RIGHT) {
                x += component.getWidth();
            }
        }
        if (side.isHorizontal()) {
            contentX = x - triHeight - radius - (width - radius * 2 - triWidth) * (relativePosition);
            if (side == Side.TOP) {
                contentY = y - height - triHeight;
            } else {
                contentY = y + triHeight;
            }
        } else {
            contentY = y - triHeight - radius - (height - radius * 2 - triWidth) * (relativePosition);
            if (side == Side.LEFT) {
                contentX = x - triHeight - width;
            } else {
                contentX = x + triHeight;
            }
        }

        contentX = clipX(renderer, contentX, width + triHeight);
        contentY = clipY(renderer, contentY, height + triHeight);
        layoutContent(renderer, contentX + paddingX, contentY + paddingY, contentX + getContentWidth(renderer), contentY + getContentHeight(renderer));
        setBoundsFromExtent(contentX - triHeight, contentY - triHeight, contentX + width + (side == Side.RIGHT ? triHeight : 0), contentY + height + (side == Side.BOTTOM ? triHeight : 0));
    }

    @Override
    protected final void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
        if (content == null) {
            return;
        }
        double right = contentX + width;
        double bottom = contentY + height;
        double triHeight = triWidth / 2;
        canvas.beginPath();
        canvas.moveTo(contentX + radius, contentY);
        if (showArrow && side == Side.BOTTOM) {
            canvas.lineTo(x - triHeight, contentY);
            canvas.lineTo(x, y);
            canvas.lineTo(x + triHeight, contentY);
        }
        canvas.lineTo(right - radius, contentY);
        canvas.curveTo(right + radius * (Numbers.MORTENSEN_CONSTANT - 1), contentY, right, contentY + radius * (1 - Numbers.MORTENSEN_CONSTANT), right, contentY + radius);
        if (showArrow && side == Side.LEFT && x > right) {
            canvas.lineTo(right, y - triHeight);
            canvas.lineTo(x, y);
            canvas.lineTo(right, y + triHeight);
        }
        canvas.lineTo(right, bottom - radius);
        canvas.curveTo(right, bottom + radius * (Numbers.MORTENSEN_CONSTANT - 1), right + radius * (Numbers.MORTENSEN_CONSTANT - 1), bottom, right - radius, bottom);
        if (showArrow && side == Side.TOP) {
            canvas.lineTo(x + triHeight, bottom);
            canvas.lineTo(x, y);
            canvas.lineTo(x - triHeight, bottom);
        }
        canvas.lineTo(contentX + radius, bottom);
        canvas.curveTo(contentX + radius * (1 - Numbers.MORTENSEN_CONSTANT), bottom, contentX, bottom + radius * (Numbers.MORTENSEN_CONSTANT - 1), contentX, bottom - radius);
        if (showArrow && side == Side.RIGHT && x < contentX) {
            canvas.lineTo(contentX, y + triHeight);
            canvas.lineTo(x, y);
            canvas.lineTo(contentX, y - triHeight);
        }
        canvas.lineTo(contentX, contentY + radius);
        canvas.curveTo(contentX, contentY + radius * (1 - Numbers.MORTENSEN_CONSTANT), contentX + radius * (1 - Numbers.MORTENSEN_CONSTANT), contentY, contentX + radius, contentY);
        canvas.closePath();
        canvas.setFill(getBackground());
        canvas.fill();
        if (outline != null) {
            canvas.setStroke(outline);
            canvas.setStroke(Stroke.SOLID_THIN);
            canvas.stroke();
        }
        drawContent(renderer, canvas, contentX + paddingX, contentY + paddingY);

    }

    /**
     * @param renderer the renderer
     * @return the width of the content
     */
    protected abstract double getContentWidth(Renderer renderer);

    /**
     * @param renderer the renderer
     * @return the height of the content
     */
    protected abstract double getContentHeight(Renderer renderer);

    /**
     * Draw the content
     *
     * @param renderer the renderer
     * @param canvas   the canvas to draw to
     * @param x        the min x position
     * @param y        the min y position
     */
    protected abstract void drawContent(Renderer renderer, GraphicsBuffer canvas, double x, double y);

}
