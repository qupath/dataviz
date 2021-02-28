package net.mahdilamb.charts;

import net.mahdilamb.charts.rtree.RTree;
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.DataType;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.utils.tuples.GenericTuple;
import net.mahdilamb.utils.tuples.Tuple;

public final class Line extends PlotData.XYData<Line> {

    public Line(double[] x, double[] y) {
        super(x, y);
    }

    public Line(DataFrame dataFrame, String x, String y) {
        super(dataFrame, x, y);
    }

    public Line(String[] x, double[] y) {
        super(x, y);
    }

    @Override
    void init(PlotLayout plotLayout) {
        plotLayout.putLines(this, createLines(plotLayout));
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
        colors = addAttribute(PlotData.Attribute.COLOR, new Trace.Categorical(s));
        addToHoverText(colors, "%s=%{color:s}", () -> colors.name, "color", ((Trace.Categorical) colors)::get);
        return this;
    }

    public Line setGroups(String series) throws DataFrameOnlyOperationException {
        final Series<?> s = getSeries(series);
        if (s.getType() == DataType.DOUBLE) {
            throw new UnsupportedOperationException("Series must not be double");
        }
        clear();
        if (group != null) {
            hoverFormatter.remove(group.defaultSeg);
        }
        group = new Trace.Categorical(s);
        return this;
    }

    @Override
    protected RTree<Runnable> createLines(PlotLayout plotLayout) {
        if (group == null || colors == null) {
            return super.createLines(plotLayout);
        } else {
            //Create possible combinations
            @SuppressWarnings("unchecked") final GenericTuple<String>[][] keyCombinations = new GenericTuple[group.categories.length][((Trace.Categorical) colors).categories.length];
            for (int i = 0; i < group.categories.length; ++i) {
                for (int j = 0; j < ((Trace.Categorical) colors).categories.length; ++j) {
                    keyCombinations[i][j] = Tuple.of(((Trace.Categorical) colors).categories[j], group.categories[i]);
                }
            }
            //Create group by
            @SuppressWarnings("unchecked") final GenericTuple<String>[] keys = new GenericTuple[size()];
            for (int i = 0; i < size(); ++i) {
                keys[i] = keyCombinations[group.getRaw(i)][((Trace.Categorical) colors).getRaw(i)];
            }
            final GroupBy<GenericTuple<String>> lines = new GroupBy<>(keys);
            //create lines
            PlotData.PlotPolyLine[] pLines = new PlotData.PlotPolyLine[lines.numGroups()];
            int i = 0;
            for (final GroupBy.Group<GenericTuple<String>> group : lines) {
                pLines[i++] = new PlotData.PlotPolyLine(this, group.getIndices());
            }
            RTree<Runnable> l = new RTree<>();
            l.putAll(pLines);
            return l;

        }
    }
}
