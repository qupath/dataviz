package net.mahdilamb.charts.series;


import net.mahdilamb.charts.utils.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

/**
 * Datasets are a series of named series. They can be thought of as a table.
 */
public interface Dataset extends Iterable<DataSeries<?>> {
    /**
     * The default quote character to use when reading a text file
     */
    char DEFAULT_QUOTE_CHARACTER = '"';
    /**
     * The default charset to use when reading a text file
     */
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
    DataSeries<?> get(final int series);

    /**
     * @return the number of series in this dataset
     */
    int numSeries();

    /**
     * @param name name of the series
     * @return series by its name or {@code null} if series not found
     */
    default DataSeries<? extends Comparable<?>> get(final String name) {
        for (int i = 0; i < numSeries(); ++i) {
            final DataSeries<?> s = get(i);
            if (s.getName().compareTo(name) == 0) {
                return s;
            }
        }
        System.err.println("No series could be found with the name " + name);
        return null;
    }

    /**
     * Get a value from a series
     *
     * @param series the index of the series
     * @param index  the index in the series
     * @return the value in the given series
     * @apiNote the return type is intentionally an object. For primitive return types, get a series of known
     * type using the relevant methods
     */
    default Object get(int series, int index) {
        return get(series).get(index);
    }

    /**
     * Get an iterable over the names of the series
     */
    default Iterable<String> seriesNames() {
        return () -> new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < numSeries();
            }

            @Override
            public String next() {
                return get(i++).getName();
            }
        };
    }

    /**
     * Get an iterable over the datatypes of the series
     */
    default Iterable<DataType> dataTypes() {
        return () -> new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < numSeries();
            }

            @Override
            public DataType next() {
                return get(i++).getType();
            }
        };
    }

    /**
     * @return a list of the series names
     */
    default List<String> listSeriesNames() {
        final List<String> result = new ArrayList<>(numSeries());
        seriesNames().forEach(result::add);
        return result;
    }

    /**
     * @return a list of the series names
     */
    default List<DataType> listDataTypes() {
        final List<DataType> result = new ArrayList<>(numSeries());
        dataTypes().forEach(result::add);
        return result;
    }

    /**
     * @return an iterable over the indices of the items in the dataset
     */
    default Iterable<Integer> indices() {
        return () -> new PrimitiveIterator.OfInt() {
            private int i = 0;

            @Override
            public int nextInt() {
                return i++;
            }

            @Override
            public boolean hasNext() {
                return i < get(0).size();
            }
        };
    }

    /**
     * Check the type of a series
     *
     * @param index the index of a series
     * @param type  the type of check
     * @return if the series at the specified index is the type specified
     */
    default boolean isSeriesType(final int index, final DataType type) {
        return get(index).getType() == type;
    }

    /**
     * Get the series at the specified index
     *
     * @param index the index of the series
     * @return the series
     * @throws DataSeriesCastException if the series cannot be cast to a double series
     */
    default NumericSeries<Double> getDoubleSeries(final int index) throws DataSeriesCastException {
        return get(index).asDouble();
    }

    default NumericSeries<Double> getDoubleSeries(final String seriesName) throws DataSeriesCastException {
        return get(seriesName).asDouble();
    }

    default StringSeries getStringSeries(final String seriesName) throws DataSeriesCastException {
        return get(seriesName).asString();
    }

    default NumericSeries<Long> getLongSeries(final String seriesName) throws DataSeriesCastException {
        return get(seriesName).asLong();
    }

    /**
     * Get the series at the specified index
     *
     * @param index the index of the series
     * @return the series
     * @throws DataSeriesCastException  if the series cannot be cast to a boolean series
     */
    default BooleanSeries getBooleanSeries(final int index) throws DataSeriesCastException {
        return get(index).asBoolean();
    }

    /**
     * Get the series at the specified index
     *
     * @param index the index of the series
     * @return the series
     * @throws DataSeriesCastException if the series cannot be cast to a long series
     */
    default NumericSeries<Long> getLongSeries(final int index) throws DataSeriesCastException {
        return get(index).asLong();
    }

    /**
     * Get a string a string from the specified index.
     *
     * @param index the index of index
     * @return a string series at the index
     * @throws DataSeriesCastException if the series cannot be cast to a string series
     */
    default StringSeries getStringSeries(final int index) throws DataSeriesCastException {
        return get(index).asString();
    }

    /**
     * @return an iterator over the names of the series
     */
    @Override
    default Iterator<DataSeries<?>> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < numSeries();
            }

            @Override
            public DataSeries<?> next() {
                return get(i++);
            }
        };
    }

    /**
     * Factory method to create a series from double data
     *
     * @param name the name of the series
     * @param data the data to use in the series
     * @return the default series wrapping the data
     */
    static NumericSeries<Double> of(final String name, double... data) {
        return new DataSeriesImpl.OfDoubleArray(name, data);
    }

    /**
     * Factory method to create a series from a "collection" of objects.
     * <p>
     * This enables the generation of a series from a collection of objects without having to implement the interface
     *
     * @param name       the name of the series
     * @param size       the size of the series
     * @param dataGetter a function which gets a double element at a position in the series
     * @return a series from a collection of objects
     */
    static NumericSeries<Double> of(final String name, int size, IntToDoubleFunction dataGetter) {
        return new DataSeriesImpl.OfFunctionalDouble(name, size, dataGetter);
    }

    /**
     * Factory method to create a dataset from an array of numeric series
     *
     * @param name   the name of the dataset
     * @param series the array of series
     * @return a dataset wrapping the series
     */
    static Dataset from(final String name, DataSeries<?>... series) {
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
    static Dataset from(final String name, int size, IntFunction<DataSeries<?>> seriesGetter) {
        return new DatasetImpl.OfFunctional(name, size, seriesGetter);
    }

    /**
     * Get an importer to use while importing a text file
     *
     * @param source         the source of the file
     * @param separator      the separator (e.g. comma or tab)
     * @param quoteCharacter the quote character
     * @param charset        the character set used to read the file
     * @return a dataset importer.
     * @see DatasetImporter
     */
    static DatasetImporter importer(final File source, char separator, char quoteCharacter, Charset charset) {
        return new DatasetImporter.FromFile(source, separator, quoteCharacter, charset, true);
    }

    /**
     * Get an text file importer which guesses the separator based on the file extension and using the defaults
     * {@link #DEFAULT_CHARSET} and {@link #DEFAULT_QUOTE_CHARACTER} to read the text file
     *
     * @param file the file
     * @return a dataset importer
     */
    static DatasetImporter importer(final File file) {
        final String ext = StringUtils.getLastCharactersToLowerCase(new char[4], file.getName());
        switch (ext) {
            case ".csv":
                return importer(file, ',', DEFAULT_QUOTE_CHARACTER, DEFAULT_CHARSET);
            case ".tsv":
                return importer(file, '\t', DEFAULT_QUOTE_CHARACTER, DEFAULT_CHARSET);
            default:
                throw new UnsupportedOperationException("Reading " + file + " is not currently supported");
        }
    }

    /**
     * Create a dataset from a file, skipping the import checking phase
     *
     * @param source         the file to import
     * @param separator      the character separator
     * @param quoteCharacter the quote character used in the file
     * @param charset        the character set used by the file
     * @return a dataset from the file
     */
    static Dataset from(final File source, char separator, char quoteCharacter, Charset charset) {
        return new DatasetImporter.FromFile(source, separator, quoteCharacter, charset, false).build();
    }

    /**
     * Create a dataset from a file, skipping the import phase. Uses the defaults as described in
     * {@link #importer(File, char, char, Charset)}
     *
     * @param file the file to import
     * @return a dataset from the file
     */
    static Dataset from(File file) {
        final String ext = StringUtils.getLastCharactersToLowerCase(new char[4], file.getName());
        switch (ext) {
            case ".csv":
                return from(file, ',', DEFAULT_QUOTE_CHARACTER, DEFAULT_CHARSET);
            case ".tsv":
                return from(file, '\t', DEFAULT_QUOTE_CHARACTER, DEFAULT_CHARSET);
            default:
                throw new UnsupportedOperationException("Reading " + file + " is not currently supported");
        }
    }

}
