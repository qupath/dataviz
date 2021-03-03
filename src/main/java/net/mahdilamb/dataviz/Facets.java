package net.mahdilamb.dataviz;

import net.mahdilamb.dataframe.utils.GroupBy;
import net.mahdilamb.dataframe.utils.IntArrayList;

import java.util.Collections;
import java.util.Map;

import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;

//TODO colwrap
final class Facets {
    private static final String DEFAULT_TITLE_FORMAT = "%s = %s";
    private static final String DEFAULT_JOIN = " | ";
    PlotLayout[][] plots;
    public Map<PlotLayout, IntArrayList> key = Collections.emptyMap();
    GroupBy<?> cols, rows;
    String colName, rowName;
    String formatTitle = DEFAULT_TITLE_FORMAT;
    String divider = DEFAULT_JOIN;

    public Facets() {

    }

    void setCols(final String name, GroupBy<?> group) {
        plots = null;
        key = Collections.emptyMap();
        this.colName = name;
        this.cols = group;
    }

    void setRows(final String name, GroupBy<?> group) {
        plots = null;
        key = Collections.emptyMap();
        this.rowName = name;
        this.rows = group;
    }

    @Override
    public String toString() {
        return String.format(
                "Facets {%s%s}",
                cols == null ? EMPTY_STRING : String.format("cols=%s", colName),
                rows == null ? EMPTY_STRING : String.format(", rows=%s", rowName));
    }
}
