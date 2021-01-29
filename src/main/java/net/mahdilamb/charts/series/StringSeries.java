package net.mahdilamb.charts.series;

/**
 * A series of strings
 */
public interface StringSeries extends DataSeries<String>, SeriesWithFunctionalOperators<String> {

    @Override
    default DataType getType() {
        return DataType.STRING;
    }

}
