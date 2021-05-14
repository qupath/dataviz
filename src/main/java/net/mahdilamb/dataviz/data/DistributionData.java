package net.mahdilamb.dataviz.data;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.DoubleSeries;
import net.mahdilamb.dataviz.GlyphFactory;
import net.mahdilamb.dataviz.PlotBounds;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotDataAttribute;
import net.mahdilamb.dataviz.layouts.XAxis;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.layouts.YAxis;

public class DistributionData<PD extends DistributionData<PD>> extends PlotData<PD, XYLayout> {
    protected DoubleSeries value;
    protected PlotBounds<PlotBounds.XY, XYLayout> bounds;

    protected DistributionData(final DataFrame dataFrame, final String y) {
        super(dataFrame);
        this.value = dataFrame.getDoubleSeries(y);
        getLayout().getXAxis().setTitle(y);

    }


    @Override
    protected XYLayout createLayout() {
        return new XYLayout(new XAxis(), new YAxis());
    }

    @Override
    protected PlotBounds<? extends PlotBounds.Bounds<XYLayout>, XYLayout> getBoundPreferences() {
        return bounds;
    }

    @Override
    protected GlyphFactory.Glyph getGlyph(PlotDataAttribute.Categorical attribute, int category) {
        return null;
    }

    @Override
    protected GlyphFactory.Glyph getGlyph(PlotDataAttribute.Numeric attribute, double value) {
        return null;
    }

    @Override
    public int size() {
        return value.size();
    }

    @Override
    public GlyphFactory.Glyph getGlyph(PlotDataAttribute.UncategorizedTrace uncategorizedTrace, int i) {
        return null;
    }
}
