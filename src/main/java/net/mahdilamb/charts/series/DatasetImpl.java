package net.mahdilamb.charts.series;


import net.mahdilamb.charts.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.IntFunction;

import static net.mahdilamb.charts.series.DataType.*;
import static net.mahdilamb.charts.utils.StringUtils.iterateLine;

/**
 * Default implementation of datasets
 */
abstract class DatasetImpl implements Dataset {
    /**
     * The prefix to use for unnamed columns
     */
    static String EMPTY_COLUMN_PREFIX = "Col.";
    /**
     * The maximum number of rows to display in the {@link #toString()} method
     */
    static int MAX_DISPLAY_ROWS = 10;
    /**
     * The maximum number of columns to display in the {@link #toString()} method
     */
    static int MAX_DISPLAY_COLUMNS = 10;
    /**
     * The minimum width of a row when printing
     */
    static int MIN_WIDTH = 4;
    /**
     * The maximum width of a row when printing
     */
    static int MAX_WIDTH = 12;
    /**
     * The string used to separate columns
     */
    static String COLUMN_SEPARATOR = "  ";

    static String SKIP_ROWS = "...";
    static String SKIP_COLUMNS = "...";

    /**
     * Dataset from an array of series
     */
    static final class OfArray extends DatasetImpl {
        private final DataSeries<?>[] series;

        /**
         * Create a dataset from an array of series
         *
         * @param name   the name of the dataset
         * @param series the array of series
         */
        OfArray(final String name, final DataSeries<?>... series) {
            super(name);
            this.series = series;
        }

        @Override
        public DataSeries<?> get(int series) {
            return this.series[series];
        }

        @Override
        public int numSeries() {
            return series.length;
        }

    }

    /**
     * Functional implementation of a dataset
     */
    static final class OfFunctional extends DatasetImpl {
        private final IntFunction<DataSeries<?>> seriesGetter;
        private final int size;

        /**
         * Create a Dataset without having to implement the interface
         *
         * @param name         the name of the dataset
         * @param seriesGetter the function to get a series from its name
         * @param size         the number of series in the dataset
         */
        OfFunctional(final String name, final int size, final IntFunction<DataSeries<?>> seriesGetter) {
            super(name);
            this.seriesGetter = Objects.requireNonNull(seriesGetter);
            this.size = size;
        }

        @Override
        public DataSeries<?> get(int series) {
            return seriesGetter.apply(series);
        }

        @Override
        public int numSeries() {
            return size;
        }
    }

    /**
     * The name of the dataset
     */
    private final String name;

    /**
     * Create an abstract named dataset
     *
     * @param name the name of the dataset
     */
    protected DatasetImpl(final String name) {
        this.name = name;
    }

    /**
     * Implementation of a dataset created from a file
     */
    static final class FromFile extends DatasetImpl {
        private final DataSeries<?>[] series;

        FromFile(DatasetImporter.FromFile importer) {
            this(importer.source, importer.separator, importer.quoteCharacter, importer.charset, importer.putativeHeader, importer.types, importer.hasColumnNames, importer.numColumns, (importer.hasColumnNames ? -1 : 0) + importer.numLines);
        }

        FromFile(File name, char separator, char quoteCharacter, Charset charset, String[] columnNames, DataType[] types, boolean hasColumnNames, int numColumns, int numRows) {
            super(name.getName());
            this.series = new DataSeries[numColumns];
            boolean getColumnNames = columnNames == null && hasColumnNames;
            for (int i = 0; i < numColumns; ++i) {
                final String columnName = columnNames == null ? (hasColumnNames ? (EMPTY_COLUMN_PREFIX + i) : null) : columnNames[i] == null ? EMPTY_COLUMN_PREFIX + i : columnNames[i];
                switch (types[i]) {
                    case LONG:
                        series[i] = new DataSeriesImpl.OfLongArray(columnName, new long[numRows], new int[4], 0);
                        break;
                    case DOUBLE:
                        series[i] = new DataSeriesImpl.OfDoubleArray(columnName, new double[numRows]);
                        break;
                    case BOOLEAN:
                        series[i] = new DataSeriesImpl.OfBooleanArray(columnName, new boolean[numRows]);
                        break;
                    case STRING:
                        series[i] = new DataSeriesImpl.OfStringArray(columnName, new String[numRows]);
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }

            }

            try (FileInputStream inputStream = new FileInputStream(name); Scanner scanner = new Scanner(inputStream, charset.name())) {
                if (hasColumnNames) {
                    final String colNames = scanner.nextLine();
                    if (getColumnNames) {
                        //TODO get column names

                    }
                }
                int rowCount = 0;
                while (scanner.hasNextLine()) {
                    final String line = scanner.nextLine();
                    int h = 0, o = 0;
                    while (h < line.length()) {
                        int currentO = o;
                        int row = rowCount;
                        h = iterateLine(line, h, separator, quoteCharacter, str -> {
                            switch (types[currentO]) {
                                case LONG:
                                    final DataSeriesImpl.OfLongArray s = (DataSeriesImpl.OfLongArray) series[currentO];
                                    if (LONG.matches(str)) {
                                        s.data[row] = Long.parseLong(str);
                                    } else {
                                        s.data[row] = 0;
                                        if (s.nanCount + 1 > s.isNaN.length) {
                                            s.isNaN = Arrays.copyOf(s.isNaN, s.isNaN.length + 8);
                                        }
                                        s.isNaN[s.nanCount++] = row;
                                    }
                                    break;
                                case DOUBLE:
                                    ((DataSeriesImpl.OfDoubleArray) series[currentO]).data[row] = toDouble(str);
                                    break;
                                case BOOLEAN:
                                    ((DataSeriesImpl.OfBooleanArray) series[currentO]).data[row] = toBoolean(str);
                                    break;
                                case STRING:
                                    ((DataSeriesImpl.OfStringArray) series[currentO]).data[row] = str;
                                    break;
                                default:
                                    throw new UnsupportedOperationException();

                            }
                        });
                        ++o;
                    }
                    ++rowCount;
                }
                //update the size
                for (int i = 0; i < numColumns; ++i) {
                    switch (types[i]) {
                        case LONG:
                            ((DataSeriesImpl.OfLongArray) series[i]).end = rowCount;
                            break;
                        case DOUBLE:
                            ((DataSeriesImpl.OfDoubleArray) series[i]).end = rowCount;
                            break;
                        case BOOLEAN:
                            ((DataSeriesImpl.OfBooleanArray) series[i]).end = rowCount;
                            break;
                        case STRING:
                            ((DataSeriesImpl.OfStringArray) series[i]).end = rowCount;
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public DataSeries<?> get(int series) {
            return this.series[series];
        }

        @Override
        public int numSeries() {
            return this.series.length;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        int[] width = new int[numSeries()];
        int rows = get(0).size();
        int halfRow = MAX_DISPLAY_ROWS >>> 1;
        int halfCols = MAX_DISPLAY_COLUMNS >>> 1;

        int rowStop = rows < MAX_DISPLAY_ROWS ? (rows >>> 1) : halfRow;
        int rowStart = rows < MAX_DISPLAY_ROWS ? rowStop : (rows - halfRow);

        int colStop = numSeries() < MAX_DISPLAY_COLUMNS ? (numSeries() >>> 1) : halfCols;
        int colStart = numSeries() < MAX_DISPLAY_COLUMNS ? colStop : (numSeries() - halfCols);
        for (int i = 0; i < numSeries(); ++i) {
            if (i == colStop && colStart != colStop) {
                stringBuilder.append(SKIP_ROWS).append(COLUMN_SEPARATOR);
                i = colStart;
            }
            final String th = get(i).getName();
            if (th.length() < MIN_WIDTH) {
                stringBuilder.append(StringUtils.repeatCharacter(' ', MIN_WIDTH - th.length())).append(th);
                width[i] = MIN_WIDTH;
            } else if (th.length() < MAX_WIDTH) {
                stringBuilder.append(th);
                width[i] = th.length();
            } else {
                if (th.length() == MAX_WIDTH) {
                    stringBuilder.append(th);
                } else {
                    stringBuilder.append(th, 0, MAX_WIDTH - 3).append(SKIP_ROWS);
                }
                width[i] = MAX_WIDTH;
            }
            stringBuilder.append(COLUMN_SEPARATOR);
        }
        stringBuilder.delete(stringBuilder.length() - COLUMN_SEPARATOR.length(), stringBuilder.length()).append('\n');

        for (int j = 0; j < rows; ++j) {
            if (j == rowStop && rowStart != rowStop) {
                for (int i = 0; i < numSeries(); ++i) {
                    if (i == colStop && colStop != colStart) {
                        stringBuilder.append(SKIP_ROWS).append(COLUMN_SEPARATOR);
                        i = colStart;
                    }
                    alignRight(stringBuilder, SKIP_COLUMNS, width[i]).append(COLUMN_SEPARATOR);
                }
                stringBuilder.delete(stringBuilder.length() - COLUMN_SEPARATOR.length(), stringBuilder.length()).append('\n');
                j = rowStart;
            }
            for (int i = 0; i < colStop; ++i) {
                alignRight(stringBuilder, i, j, width[i]).append(COLUMN_SEPARATOR);
            }
            if (colStart != colStop) {

                stringBuilder.append(SKIP_ROWS).append(COLUMN_SEPARATOR);
            }

            for (int i = colStart; i < numSeries(); ++i) {
                alignRight(stringBuilder, i, j, width[i]).append(COLUMN_SEPARATOR);
            }
            stringBuilder.delete(stringBuilder.length() - COLUMN_SEPARATOR.length(), stringBuilder.length()).append('\n');
        }

        return stringBuilder.append(String.format("Dataset {name: \"%s\", cols: %d, rows: %d}\n", getName(), numSeries(), get(0).size())).toString();
    }

    static StringBuilder alignRight(StringBuilder stringBuilder, final String td, int width) {
        if (td.length() < width) {
            return stringBuilder.append(StringUtils.repeatCharacter(' ', width - td.length())).append(td);
        } else {
            return stringBuilder.append(td, 0, width);
        }
    }

    @SuppressWarnings("unchecked")
    private StringBuilder alignRight(StringBuilder stringBuilder, int col, int row, int width) {
        final String td;
        switch (get(col).getType()) {
            case LONG:
                td = String.valueOf(((NumericSeries<Long>) get(col)).isNaN(row) ? "NaN" : ((NumericSeries<Long>) get(col)).get(row));
                break;
            case DOUBLE:
                td = String.valueOf(((NumericSeries<Double>) get(col)).get(row));
                break;
            case BOOLEAN:
                td = String.valueOf(((DataSeries<Boolean>) get(col)).get(row));
                break;
            case STRING:
                td = ((DataSeries<String>) get(col)).get(row);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return alignRight(stringBuilder, td, width);
    }


}
