package net.mahdilamb.dataviz.data;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.DataType;
import net.mahdilamb.dataframe.DoubleSeries;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.IntArrayList;
import net.mahdilamb.dataviz.PlotBounds;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotDataAttribute;
import net.mahdilamb.dataviz.PlotShape;
import net.mahdilamb.dataviz.graphics.FillMode;
import net.mahdilamb.dataviz.layouts.XAxis;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.layouts.YAxis;
import net.mahdilamb.dataviz.plots.ScatterMode;
import net.mahdilamb.stats.ArrayUtils;

import java.util.function.DoubleUnaryOperator;

/**
 * Data for XY series
 */
public abstract class RelationalData<PD extends RelationalData<PD>> extends PlotData<PD, XYLayout> {
    protected final DoubleSeries x, y;
    protected PlotBounds<PlotBounds.XY, XYLayout> bounds;

    protected FillMode fillMode = FillMode.NONE;

    protected ScatterMode markerMode = ScatterMode.MARKER_ONLY;

    protected RelationalData(final DataFrame dataFrame, final String xAxis, final String yAxis) {
        super(dataFrame);

        x = dataFrame.getDoubleSeries(xAxis);
        y = dataFrame.getDoubleSeries(yAxis);
        getLayout().getXAxis().setTitle(xAxis);
        getLayout().getYAxis().setTitle(yAxis);
        if (dataFrame.getType(xAxis) == DataType.LONG) {
            getLayout().getXAxis().setFormat("%.0f");
        }
        init();

    }

    protected RelationalData(double[] x, double[] y) {
        super();
        if (x.length != y.length) {
            throw new IllegalArgumentException("x and y are of different sizes");
        }
        this.x = Series.of(null, x);
        this.y = Series.of(null, y);
        init();

    }

    protected RelationalData(double[] x, DoubleUnaryOperator y) {
        super();
        this.x = Series.of(null, x);
        this.y = Series.of(null, ArrayUtils.map(x, y));
        init();
    }

    protected abstract void init();


    @SuppressWarnings("unchecked")
    public PD setXLabel(final String name) {
        getLayout().getXAxis().setTitle(name);
        return (PD) this;
    }

    @SuppressWarnings("unchecked")
    public PD setYLabel(final String name) {
        getLayout().getYAxis().setTitle(name);
        return (PD) this;
    }

    @Override
    protected final XYLayout createLayout() {
        //todo consider multiple axes
        return new XYLayout(new XAxis(), new YAxis(true));
    }

    /**
     * @param i the index
     * @return the x element of the ith element
     */
    public double getX(int i) {
        return x.get(i);
    }

    /**
     * @param i the index
     * @return the y element of the ith element
     */
    public double getY(int i) {
        return y.get(i);
    }

    @Override
    public int size() {
        return x.size();
    }

    @Override
    protected boolean supportsWheelZoom() {
        return true;
    }

    public ScatterMode getMarkerMode() {
        return markerMode;
    }

    protected PD setMarkerMode(final String markerMode) {
        return setMarkerMode(ScatterMode.from(markerMode));
    }

    protected PD setMarkerMode(final ScatterMode markerMode) {
        this.markerMode = markerMode;
        clear();
        init();
        return refresh();
    }

    @SuppressWarnings("unchecked")
    protected PlotShape<XYLayout>[] createLines() {
        final PlotDataAttribute group = getAttribute(PlotDataAttribute.Type.GROUP);
        final PlotDataAttribute colors = getAttribute(PlotDataAttribute.Type.COLOR);
        if (colors == null || group == null) {
            if (colors instanceof PlotDataAttribute.Categorical) {
                final PlotDataAttribute.Categorical c = ((PlotDataAttribute.Categorical) colors);
                @SuppressWarnings("unchecked") final PlotShape<XYLayout>[] lines = new PlotShape[c.numCategories()];
                IntArrayList[] idObjects = new IntArrayList[lines.length];
                for (int i = 0; i < lines.length; ++i) {
                    final IntArrayList ids = new IntArrayList();
                    lines[i] = createPolyLine(this, -1, ids);
                    idObjects[i] = ids;
                }
                for (int i = 0; i < x.size(); ++i) {
                    idObjects[getRaw(c, i)].add(i);
                    if (getIndex(lines[getRaw(c, i)]) == -1) {
                        setIndex(lines[getRaw(c, i)], i);
                    }
                }
                return lines;
            }
        } else {
            /*if (group instanceof PlotDataAttribute.Categorical) {
                final PlotDataAttribute.Categorical g = ((PlotDataAttribute.Categorical) group);
                final PlotDataAttribute.Categorical c = ((PlotDataAttribute.Categorical) colors);

                final StringPair[][] keyCombinations = new StringPair[g.numCategories()][c.numCategories()];

                for (int i = 0; i < g.numCategories(); ++i) {
                    for (int j = 0; j < c.numCategories(); ++j) {
                        keyCombinations[i][j] = new StringPair(c.get(j), g.get(i));
                        AbstractComponent.print(c.get(j), j);
                    }
                }

                //Create group by
                final StringPair[] keys = new StringPair[size()];
                for (int i = 0; i < size(); ++i) {
                    keys[i] = keyCombinations[getRaw(g, i)][getRaw(c, i)];

                }
                final GroupBy<StringPair> lines = new GroupBy<>(keys);
                //create lines
                final PlotShape<XYLayout>[] pLines = new PlotShape[lines.numGroups()];
                int i = 0;
                for (final GroupBy.Group<StringPair> h : lines) {

                    pLines[i++] = createPolyLine(this, h.getIndices().get(0), h.getIndices());
                }
                return pLines;
            }*/
        }


        return new PlotShape[]{createPolyLine(this, 0, new IntArrayList(ArrayUtils.intRange(x.size())))};
    }


}
