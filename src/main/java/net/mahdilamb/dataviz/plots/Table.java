package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;

public final class Table extends PlotData.TabularData<Table> {

    public Table(final DataFrame dataFrame){
        super(dataFrame);
    }

    @Override
    protected void init(PlotLayout plotLayout) {

    }
}
