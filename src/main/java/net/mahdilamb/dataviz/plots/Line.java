package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.DataType;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;
import net.mahdilamb.dataviz.PlotTrace;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.utils.tuples.GenericTuple;
import net.mahdilamb.utils.tuples.Tuple;

import java.util.function.DoubleUnaryOperator;

public final class Line extends PlotData.XYData<Line> {

    public Line(double[] x, double[] y) {
        super(x, y);
        markerMode = ScatterMode.LINE_ONLY;
    }

    public Line(double[] x, DoubleUnaryOperator func) {
        super(x, func);
        markerMode = ScatterMode.LINE_ONLY;
    }

    public Line(DataFrame dataFrame, String x, String y) {
        super(dataFrame, x, y);
        markerMode = ScatterMode.LINE_ONLY;
    }

    public Line(String[] x, double[] y) {
        super(x, y);
        markerMode = ScatterMode.LINE_ONLY;

    }

    @Override
    protected void init(PlotLayout plotLayout) {
        if (lineStroke == null) {
            lineStroke = new Stroke(lineWidth, lineDashes);
            lineDashes = null;
        }
        putLines(plotLayout, this, createLines(plotLayout));
        updateXYBounds(plotLayout, xMin, xMax, yMin, yMax);
        plotLayout.getXAxis().setRange(xMin, xMax);

       /* for (final Node2D<Runnable> l : createLines().search(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)) {
            PlotPolyLine line = (PlotPolyLine) l;
            System.out.printf("start=%s,%s end=%s,%s%s%s, hover=%s%n",
                    line.getStartX(),
                    line.getStartY(),
                    line.getEndX(),
                    line.getEndY(),
                    colors == null ? EMPTY_STRING : (String.format(" color=%s (%s)", ((Trace.Categorical) colors).get(line.i), colors.get(qualitativeColormap, line.i).toHex())),
                    group == null ? EMPTY_STRING : (String.format(" group=%s", group.get(line.i))),
                    getHoverText(line.i)
            );
        }*/
    }

    public Line setColors(String series) throws DataFrameOnlyOperationException {
        final Series<?> s = getSeries(series);
        if (s.getType() == DataType.DOUBLE) {
            throw new UnsupportedOperationException("Series must not be double");
        }
        clear();
        colors = addAttribute(PlotData.Attribute.COLOR, new PlotTrace.Categorical(this, Attribute.COLOR, s));
        addToHoverText(colors, "%s=%{color:s}", () -> colors.getName(), "color", ((PlotTrace.Categorical) colors)::get);
        return this;
    }

    public Line setGroups(String series) throws DataFrameOnlyOperationException {
        final Series<?> s = getSeries(series);
        if (s.getType() == DataType.DOUBLE) {
            throw new UnsupportedOperationException("Series must not be double");
        }
        clear();
        if (group != null) {
            hoverFormatter.remove(group);
        }
        group = new PlotTrace.Categorical(this, Attribute.GROUP, s);
        return this;
    }

    @Override
    protected RTree<Runnable> createLines(PlotLayout plotLayout) {
        if (group == null || colors == null) {
            return super.createLines(plotLayout);
        } else {
            //Create possible combinations
            @SuppressWarnings("unchecked") final GenericTuple<String>[][] keyCombinations = new GenericTuple[getCategories(group).length][getCategories((PlotTrace.Categorical) colors).length];
            for (int i = 0; i < getCategories(group).length; ++i) {
                for (int j = 0; j < getCategories((PlotTrace.Categorical) colors).length; ++j) {
                    keyCombinations[i][j] = Tuple.of(getCategories((PlotTrace.Categorical) colors)[j], getCategories(group)[i]);
                }
            }
            //Create group by
            @SuppressWarnings("unchecked") final GenericTuple<String>[] keys = new GenericTuple[size()];
            for (int i = 0; i < size(); ++i) {
                keys[i] = keyCombinations[getRaw(group, i)][getRaw(((PlotTrace.Categorical) colors), i)];
            }
            final GroupBy<GenericTuple<String>> lines = new GroupBy<>(keys);
            //create lines
            PlotData.PlotPolyLine[] pLines = new PlotData.PlotPolyLine[lines.numGroups()];
            int i = 0;
            for (final GroupBy.Group<GenericTuple<String>> group : lines) {
                pLines[i++] = new PlotData.PlotPolyLine(this, i, group.getIndices());
            }
            RTree<Runnable> l = new RTree<>();
            l.putAll(pLines);
            return l;

        }
    }
}
