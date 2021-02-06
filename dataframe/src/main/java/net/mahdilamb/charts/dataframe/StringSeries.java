package net.mahdilamb.charts.dataframe;

import java.util.regex.Pattern;

public interface StringSeries extends DataSeries<String> , SeriesWithFunctionalOperators<String>{
    @Override
    default DataType getType() {
        return DataType.STRING;
    }

    default BooleanSeries matches(Pattern pattern) {
        return map(el -> pattern.matcher(el).matches());
    }

}
