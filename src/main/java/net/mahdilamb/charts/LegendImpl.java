package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Fill;
import net.mahdilamb.charts.graphics.Orientation;
import net.mahdilamb.charts.plots.Scatter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

final class LegendImpl<S extends PlotSeries<S>> extends KeyAreaImpl<Legend> implements Legend {

    static final class LegendGroup extends ChartComponent {

        public String name;
        public LegendImpl<?> legend;
        List<LegendItem> legendItems = new ArrayList<>();


        public LegendGroup(PlotSeries.TraceGroup<?> attribute, LegendImpl<?> legend) {
            name = attribute.name;
            this.legend = legend;
            for (final PlotSeries.Trace trace : attribute.traces) {
                legendItems.add(new LegendItem(trace));
            }
        }

        @Override
        public String toString() {
            return String.format("LegendGroup {%s, %s}", name, legendItems);
        }


        @Override
        protected void layoutComponent(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
            if (name != null && name.length() != 0) {
                sizeX = source.getTextWidth(legend.titleFont, name);
                sizeY = source.getTextLineHeight(legend.titleFont);
            } else {
                sizeX = 0;
                sizeY = 0;
            }
            if (legend.orientation == Orientation.VERTICAL) {
                for (final LegendItem li : legendItems) {
                    if (li.name != null && li.name.length() != 0) {
                        li.sizeY = Math.max(source.getTextLineHeight(legend.itemFont), li.trace.glyphSize);
                        li.sizeX = li.trace.glyphSize + source.getTextWidth(legend.itemFont, li.name);
                    }

                    sizeY += li.sizeY;
                    sizeX = Math.max(sizeX, li.sizeX);
                }
            } else {
                double width = maxX - minX;
                for (final LegendItem li : legendItems) {

                    double lineHeight = li.trace.glyphSize;
                    double lineWidth = li.trace.glyphSize + 5;
                    if (li.name != null && li.name.length() != 0) {
                        li.sizeY = Math.max(source.getTextLineHeight(legend.itemFont), li.trace.glyphSize);
                        li.sizeX = lineWidth + source.getTextWidth(legend.itemFont, li.name);
                        lineHeight = Math.max(lineHeight, li.sizeY);
                        lineWidth = Math.max(lineWidth, li.sizeX);
                    }
                    //todo check
                    if ((lineWidth + sizeX > width) || sizeX == 0) {
                        sizeY += lineHeight;
                        sizeX = lineWidth;
                    } else {
                        sizeX += lineWidth;
                    }
                }
            }
            posX = minX;
            posY = minY;
        }

        public void alignChildren(Figure<?, ?> source) {
            double curY = posY;
            if (name != null && name.length() != 0) {
                curY += source.getTextLineHeight(legend.titleFont);
            }
            for (final LegendItem li : legendItems) {
                li.posY = curY;
                li.posX = posX;
                curY += li.sizeY;
            }

        }

        @Override
        protected void drawComponent(Figure<?, ?> source, ChartCanvas<?> canvas) {
            double minY = posY;
            if (legend.orientation == Orientation.VERTICAL) {
                if (name != null && name.length() != 0) {
                    canvas.setFont(legend.titleFont);
                    canvas.setFill(legend.titleColor);
                    double xOffset = (0) ;
                    canvas.fillText(name, xOffset + posX, minY + source.getTextBaselineOffset(legend.titleFont));
                    minY += source.getTextLineHeight(legend.titleFont);
                }

                canvas.setFont(legend.itemFont);
                double yOffset = source.getTextLineHeight(legend.itemFont) - source.getTextBaselineOffset(legend.itemFont);
                double labOffset = Scatter.DEFAULT_MARKER_SIZE;
                for (final LegendItem li : legendItems) {
                    labOffset = Math.max(labOffset, li.trace.glyphSize);
                    if (li.name != null && li.name.length() != 0) {
                        canvas.setFill(Fill.BLACK_FILL);
                        if (li.trace.glyph != null) {
                            li.trace.glyph.accept(canvas, li.trace, li.posX + labOffset * .5, minY + (li.sizeY * .5));
                        }

                        canvas.setFill(legend.valueColor);
                        canvas.fillText(li.name, posX + labOffset + 5, yOffset + minY + (li.sizeY * .5));

                        minY += Math.max(li.trace.glyphSize, source.getTextLineHeight(legend.itemFont));
                    }
                }
            } else {
                // todo throw new UnsupportedOperationException();

            }
        }


    }

    static final class LegendItem {
        final String name;
        private final PlotSeries.Trace trace;
        public double posX, posY, sizeX, sizeY;

        public LegendItem(PlotSeries.Trace trace) {
            this.name = trace.name;
            this.trace = trace;

        }

        @Override
        public String toString() {
            return String.format("LegendItem {%s}", name);
        }


    }

    final List<LegendGroup> legendGroups = new LinkedList<>();

    public LegendImpl(Figure.PlotImpl<S> layout) {
        super();
        for (final S series : layout.series) {
            for (final PlotSeries.TraceGroup<?> attribute : series.attributes()) {
                //todo check for linked
                legendGroups.add(new LegendGroup(attribute, this));

            }
        }
    }

    @Override
    protected void layoutComponent(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
        sizeY = 0;
        sizeX = 0;

        for (final LegendGroup group : legendGroups) {
            group.layoutComponent(source, minX + sizeX, minY + sizeY, maxX, maxY);
            sizeY += group.sizeY;
            sizeY += groupSpacing;
            sizeX = Math.max(sizeX, group.sizeX);
        }

    }

    @Override
    protected void align(Figure<?, ?> source, double height, double width) {
        super.align(source, height, width);
        if (orientation == Orientation.VERTICAL) {
            double curY = posY;
            for (final LegendGroup group : legendGroups) {
                group.posY = curY;
                group.posX = posX;
                group.sizeX = sizeX;
                group.alignChildren(source);
                curY += group.sizeY;
            }
        } else {
            //TODO check
            double curX = posX;
            for (final LegendGroup group : legendGroups) {
                group.posX = curX;
                group.posY = posY;
                group.alignChildren(source);
                curX += group.sizeX;
            }
        }

    }

    @Override
    protected void drawComponent(Figure<?, ?> source, ChartCanvas<?> canvas) {
        for (final LegendGroup group : legendGroups) {
            group.drawComponent(source, canvas);
        }
    }
}
