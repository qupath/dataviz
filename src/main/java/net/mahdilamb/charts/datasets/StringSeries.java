package net.mahdilamb.charts.datasets;

/**
 * A series of strings
 */
public interface StringSeries extends DataSeries<String> {

    @Override
    default DataType getType() {
        return DataType.STRING;
    }

}
