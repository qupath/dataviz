package net.mahdilamb.charts.series;


import net.mahdilamb.charts.utils.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.function.IntFunction;

/**
 * Datasets are a series of named 1D series
 */
public interface Dataset extends Iterable<Series<?>> {
    char DEFAULT_QUOTE_CHARACTER = '"';
    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * @return the name of the dataset
     */
    String getName();

    /**
     * Get a series from an index
     *
     * @param series the index of interest
     * @return the series at the index
     */
    Series<?> getSeries(final int series);

    default Object val(int series, int index) {
        return getSeries(series).val(index);
    }

    /**
     * @param name name of the series
     * @return series by its name or {@code null} if series not found
     */
    default Series<?> getSeries(final String name) {
        for (int i = 0; i < numSeries(); ++i) {
            final Series<?> s = getSeries(i);
            if (s.getName().compareTo(name) == 0) {
                return s;
            }
        }
        return null;
    }

    /**
     * Print the name of the series to the console
     */
    default void printSeriesNames() {
        if (numSeries() == 0) {
            System.out.println("[]");
            return;
        }
        for (int i = 0; i < numSeries(); ++i) {
            System.out.print((i == 0 ? "[" : ", ") + getSeries(i).getName());
        }
        System.out.println("]");
    }

    /**
     * Check the type of a series
     *
     * @param index the index of a series
     * @param type  the type of check
     * @return if the series at the specified index is the type specified
     */
    default boolean isSeriesType(final int index, final SeriesType type) {
        return getSeries(index).getType() == type;
    }

    /**
     * Get the series at the specified index
     *
     * @param index the index of the series
     * @return the series
     * @throws SeriesCastException if the index contains a non-double series
     */
    default DoubleSeries getDoubleSeries(final int index) throws SeriesCastException {
        final Series<?> series = getSeries(index);
        if (series.getType() != SeriesType.DOUBLE) {
            throw new SeriesCastException();
        }
        return (DoubleSeries) series;
    }

    /**
     * Get the series at the specified index
     *
     * @param index the index of the series
     * @return the series
     * @throws SeriesCastException if the index contains a non-boolean series
     */
    default BooleanSeries getBooleanSeries(final int index) throws SeriesCastException {
        final Series<?> series = getSeries(index);
        if (series.getType() != SeriesType.BOOLEAN) {
            throw new SeriesCastException();
        }
        return (BooleanSeries) series;
    }

    /**
     * Get the series at the specified index
     *
     * @param index the index of the series
     * @return the series
     * @throws SeriesCastException if the index contains a non-long series
     */
    default LongSeries getLongSeries(final int index) throws SeriesCastException {
        final Series<?> series = getSeries(index);
        if (series.getType() != SeriesType.INTEGER) {
            throw new SeriesCastException();
        }
        return (LongSeries) series;
    }

    /**
     * Get a string a string from the specified index.
     *
     * @param index the index of index
     * @return a string series at the index
     * @throws SeriesCastException if the index contains a non-string series
     */
    default StringSeries getStringSeries(final int index) throws SeriesCastException {
        final Series<?> series = getSeries(index);
        if (series.getType() != SeriesType.STRING) {
            throw new SeriesCastException();
        }
        return (StringSeries) series;
    }

    /**
     * @return the number of series in this dataset
     */
    int numSeries();

    /**
     * @return an iterator over the names of the series
     */
    @Override
    default Iterator<Series<?>> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < numSeries();
            }

            @Override
            public Series<?> next() {
                return getSeries(i++);
            }
        };
    }

    /**
     * Factory method to create a dataset from an array of numeric series
     *
     * @param name   the name of the dataset
     * @param series the array of series
     * @return a dataset wrapping the series
     */
    static Dataset of(final String name, Series<?>... series) {
        return new DatasetImpl.OfArray(name, series);
    }

    /**
     * Factory method to create a dataset in a functional style
     *
     * @param name         the name of the data
     * @param size         the number of series in the dataset
     * @param seriesGetter the function used to get a series from a name
     * @return a dataset that using functional programming to retrieve series
     */
    static Dataset of(final String name, int size, IntFunction<Series<?>> seriesGetter) {
        return new DatasetImpl.OfFunctional(name, size, seriesGetter);
    }

    static DatasetImporter importer(final File source, char separator, char quoteCharacter, Charset charset) {
        return new DatasetImporter.FromFile(source, separator, quoteCharacter, charset);
    }

    static DatasetImporter importer(final File file) {
        final String ext = StringUtils.getLastCharactersToLowerCase(new char[4], file.getName());
        switch (ext) {
            case ".csv":
                return importer(file, ',', DEFAULT_QUOTE_CHARACTER, DEFAULT_CHARSET);
            case ".tsv":
                return importer(file, '\t', DEFAULT_QUOTE_CHARACTER, DEFAULT_CHARSET);
            default:
                throw new UnsupportedOperationException();
        }
    }

    static Dataset from(DatasetImporter importer) {
        return importer.build();
    }
}
