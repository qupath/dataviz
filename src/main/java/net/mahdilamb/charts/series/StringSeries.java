package net.mahdilamb.charts.series;

import java.util.regex.Pattern;

public interface StringSeries extends DataSeries<String> , SeriesWithFunctionalOperators<String>{
    @Override
    default DataType getType() {
        return DataType.STRING;
    }

    default BooleanSeries matches(Pattern pattern) {
        return asBoolean(el -> pattern.matcher(el).matches());
    }

}
