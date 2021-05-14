package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.figure.BufferingStrategy;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Side;
import net.mahdilamb.dataviz.plots.GlyphFactory;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class Legend extends KeyArea<Legend> {

    /**
     * A block of legend items
     */
    static class Group extends Component {
        final PlotDataAttribute styler;
        final Legend legend;
        final List<Item> items;

        Group(final Legend legend, PlotDataAttribute styler, List<Item> items) {
            super(BufferingStrategy.NO_BUFFERING);
            this.styler = styler;
            this.legend = legend;
            this.items = items;

        }

        void mirrorContexts() {
            if (getContext() == null) {
                mirrorContext(legend, this);
                for (final Item item : items) {
                    mirrorContext(this, item);
                }
            }
        }

        @Override
        protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
            if (items.isEmpty()) {
                return;
            }
            mirrorContexts();
            //TODO vertical or horizontal
            double height = styler.getName() == null ? 0 : (getTextLineHeight(renderer, legend.titleFont, styler.getName()) + legend.titleSpacing);
            for (final Item item : items) {
                item.layoutComponent(renderer, minX, minY + height, maxX, maxY);
                height += item.getHeight();
                height += legend.itemSpacing;
            }
            setBoundsFromExtent(minX, minY, maxX, minY + height - legend.itemSpacing);
        }

        @Override
        protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
            //TODO vertical or horizontal
            if (legend.showBorder) {
                canvas.setStroke(legend.borderColor);
                canvas.setStroke(legend.border);
                canvas.strokeRect(getX(), getY(), getWidth(), getHeight() + legend.paddingY + legend.paddingY);
            }
            final double x = getX() + legend.paddingX;
            double y = getY() + legend.paddingY + getTextBaselineOffset(renderer, legend.titleFont);
            canvas.setFill(legend.titleColor);
            canvas.setFont(legend.titleFont);
            canvas.fillText(styler.getName(), x, y);
            canvas.setFill(legend.itemColor);
            canvas.setFont(legend.itemFont);
            for (final Item item : items) {
                item.drawComponent(renderer, canvas);
            }
        }

        protected Item getItemAt(double x, double y) {
            for (final Item item : items) {
                if (item.containsPoint(x, y)) {
                    return item;
                }
            }
            return null;
        }

        @Override
        protected Component getComponentAt(double x, double y) {
            return super.getComponentAt(x, y);
        }
    }

    static class Item extends Component {
        private final GlyphFactory.Glyph glyph;
        final String label;
        final Legend legend;

        Item(final Legend legend, final GlyphFactory.Glyph glyph, final String label) {
            this.glyph = glyph;
            this.label = label;
            this.legend = legend;
        }

        @Override
        protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
            final double labelHeight = getTextLineHeight(renderer, legend.itemFont, label);
            if (labelHeight > glyph.getSize()) {
                layoutComponent(glyph, renderer, minX + legend.paddingX, minY + (.5 * (labelHeight - glyph.getSize())), maxX, maxY);
            } else {
                layoutComponent(glyph, renderer, minX + legend.paddingX, minY, maxX, maxY);

            }
            setBoundsFromRect(minX, minY, maxX - minX, Math.max(labelHeight, glyph.getSize()));

        }

        @Override
        protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
            drawComponent(glyph, renderer, canvas);
            canvas.setFill(legend.itemColor);
            canvas.setFont(legend.itemFont);
            final double labelHeight = getTextLineHeight(renderer, legend.itemFont, label);
            final double labelOffsetY;
            if (labelHeight < glyph.getHeight()) {
                labelOffsetY = .5 * (glyph.getHeight() - labelHeight);
            } else {
                labelOffsetY = 0;
            }
            canvas.fillText(label, getX() + ((glyph.getMaxSize() + glyph.getSize()) * .5) + legend.paddingX + 2, getY() + labelOffsetY + getTextBaselineOffset(renderer, legend.itemFont));
        }

        @Override
        public String toString() {
            return "LegendItem {'" + label + "'}";
        }
    }

    static final class TogglableItem extends Item {
        private boolean isVisible = true;

        TogglableItem(Legend legend, GlyphFactory.Glyph glyph, String label) {
            super(legend, glyph, label);
        }

        final boolean toggleVisibility() {
            isVisible = !isVisible;
            markDrawAsOldQuietly();
            return isVisible;
        }


        @Override
        protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
            canvas.setGlobalAlpha(isVisible ? 1 : 0.5);
            super.drawComponent(renderer, canvas);
            canvas.resetGlobalAlpha();
        }
    }

    Legend(Figure figure) {
        super(figure, false, 120);
    }

    @Override
    protected void layoutVertical(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        final double width = getItemSize();
        if (side == Side.RIGHT) {
            minX = maxX - width;
        } else {
            maxX = minX + width;
        }
        double height = 0;
        boolean visible = false;
        for (final PlotData<?, ?> data : figure.getLayout().data) {
            for (final Map.Entry<PlotDataAttribute.Type, PlotDataAttribute> attrEntry : data.attributes.entrySet()) {
                if (attrEntry.getValue().showInLegend) {
                    final Group group = attrEntry.getValue().getLegendGroup(this);
                    group.layoutComponent(renderer, minX, minY + height, maxX, maxY);
                    height += group.getHeight() + groupSpacing;
                    visible = true;
                }
            }
        }
        if (visible) {
            setBoundsFromExtent(minX, minY, maxX, maxY);
        } else {
            setBoundsFromRect(-1, -1, 0, 0);
        }
    }

    @Override
    protected void layoutHorizontal(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        final double height = getItemSize();
        if (side == Side.BOTTOM) {
            minY = maxY - height;
        } else {
            maxY = minY + height;
        }
        double width = 0;
        boolean visible = false;
        for (final PlotData<?, ?> data : figure.getLayout().data) {
            for (final Map.Entry<PlotDataAttribute.Type, PlotDataAttribute> styler : data.attributes.entrySet()) {
                if (styler.getValue().showInLegend) {
                    final Group group = styler.getValue().getLegendGroup(this);
                    group.layoutComponent(renderer, minX, minY, maxX + width, maxY);//TODO check for overflow
                    width += group.getWidth();
                    visible = true;
                }
            }
        }

        if (visible) {
            setBoundsFromExtent(minX, minY, maxX, maxY);
        } else {
            setBoundsFromRect(-1, -1, 0, 0);
        }

    }


    @Override
    protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
        for (final PlotData<?, ?> data : figure.getLayout().data) {
            for (final Map.Entry<PlotDataAttribute.Type, PlotDataAttribute> styler : data.attributes.entrySet()) {
                if (styler.getValue().showInLegend) {
                    styler.getValue().getLegendGroup(this).drawComponent(renderer, canvas);
                }
            }
        }
    }

    @Override
    protected void markLayoutAsOldQuietly() {
        final PlotLayout<?> layout;
        if ((layout = figure.getLayout()) == null) {
            return;
        }
        for (final PlotData<?, ?> data : layout.data) {
            for (final Map.Entry<PlotDataAttribute.Type, PlotDataAttribute> attributeEntry : data.attributes.entrySet()) {
                if (!attributeEntry.getValue().showInLegend) {
                    continue;
                }
                markLayoutAsOld(attributeEntry.getValue().getLegendGroup(this));
            }
        }
    }

    @Override
    protected Component getComponentAt(double x, double y) {
        for (final PlotData<?, ?> data : figure.getLayout().data) {
            for (final Map.Entry<PlotDataAttribute.Type, PlotDataAttribute> styler : data.attributes.entrySet()) {
                if (styler.getValue().showInLegend) {
                    final Legend.Group group;
                    if ((group = styler.getValue().getLegendGroup(this)).containsPoint(x, y)) {
                        return group.getComponentAt(x, y);
                    }
                }
            }
        }
        return super.getComponentAt(x, y);
    }


    @Override
    public Legend apply(Theme theme) {
        theme.legend.accept(this);
        return this;
    }

}
