package net.mahdilamb.charts.dataframe;


import net.mahdilamb.charts.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static net.mahdilamb.charts.dataframe.DataType.*;
import static net.mahdilamb.charts.utils.StringUtils.iterateLine;

/**
 * Default implementation of datasets
 */
//TODO use correct id column
//todo check formatting when number of rows equals MAX_ROWS
abstract class DataFrameImpl implements DataFrame {
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

    static final class DataFrameView extends DataFrameImpl {

        private final DataFrame dataFrame;
        int numSeries;
        int[] seriesIDs;
        DataSeries<?>[] series;
        int rows[];
        int numRows = -1;

        public DataFrameView(DataFrame dataFrame, int[] ids) {
            //TODO what if dataframe is dataframe view?
            super(dataFrame.getName());
            this.dataFrame = dataFrame;
            this.numSeries = ids.length;
            seriesIDs = ids;
        }

        public DataFrameView(DataFrame dataFrame, IntPredicate test) {
            //TODO what if dataframe is dataframe view?
            super(dataFrame.getName());
            this.dataFrame = dataFrame;
            this.seriesIDs = new int[dataFrame.numSeries()];
            int j = 0;
            int i = 0;
            while (i < dataFrame.numSeries()) {
                if (test.test(i)) {
                    seriesIDs[j++] = i;
                }
                ++i;
            }
            numSeries = j;
        }

        public DataFrameView(DataFrame dataFrame, Predicate<String> test) {
            //TODO what if dataframe is dataframe view?
            super(dataFrame.getName());
            this.dataFrame = dataFrame;
            this.seriesIDs = new int[dataFrame.numSeries()];
            int j = 0;
            int i = 0;
            while (i < dataFrame.numSeries()) {
                if (test.test(dataFrame.get(i).getName())) {
                    seriesIDs[j++] = i;
                }
                ++i;
            }
            numSeries = j;
        }

        public DataFrameView(DataFrame dataFrame, int start, int end) {
            this(dataFrame, range(start, end));
        }

        @Override
        public DataSeries<?> get(int series) {
            if (this.series == null) {
                return dataFrame.get(series);
            }
            if (this.series[series] == null) {
                this.series[series] = new DataSeriesImpl.DataSeriesView<>(dataFrame.get(series), rows);
            }
            //todo bounds checking
            return this.series[series];
        }


        @Override
        public int numSeries() {
            return numSeries;
        }
    }


    /**
     * Dataset from an array of series
     */
    static final class OfArray extends DataFrameImpl {
        private final DataSeries<?>[] series;

        /**
         * Create a dataset from an array of series
         *
         * @param name   the name of the dataset
         * @param series the array of series
         */
        OfArray(final String name, final DataSeries<?>... series) {
            super(name);
            this.series = new DataSeries[series.length];
            int size = -1;
            int i = 0;
            for (DataSeries<?> s : series) {
                this.series[i++] = (s.getClass() == DataSeriesImpl.DataSeriesView.class) ? ((DataSeriesImpl.DataSeriesView<?>) s).dataSeries : s;
                if (size == -1) {
                    size = s.size();
                    continue;
                }
                if (s.size() != size) {
                    throw new IllegalArgumentException("series must be of equal length");
                }
            }
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
     * The name of the dataset
     */
    private final String name;

    /**
     * Create an abstract named dataset
     *
     * @param name the name of the dataset
     */
    protected DataFrameImpl(final String name) {
        this.name = name;
    }

    /**
     * Implementation of a dataset created from a file
     */
    static final class FromFile extends DataFrameImpl {
        private final DataSeries<?>[] series;

        FromFile(DataFrameImporter.FromFile importer) {
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
        int[] width = new int[numSeries()];
        int rows = get(0).size();
        int halfRow = MAX_DISPLAY_ROWS >>> 1;
        int halfCols = MAX_DISPLAY_COLUMNS >>> 1;

        int rowStop = rows < MAX_DISPLAY_ROWS ? (rows >>> 1) : halfRow;
        int rowStart = rows < MAX_DISPLAY_ROWS ? rowStop : (rows - halfRow);
        int idColWidth = String.valueOf(get(0).size()).length();
        final StringBuilder stringBuilder = new StringBuilder(StringUtils.repeatCharacter(' ', idColWidth)).append(COLUMN_SEPARATOR);

        int colStop = numSeries() < MAX_DISPLAY_COLUMNS ? (numSeries() >>> 1) : halfCols;
        int colStart = numSeries() < MAX_DISPLAY_COLUMNS ? colStop : (numSeries() - halfCols);
        for (int i = 0; i < numSeries(); ++i) {
            if (i == colStop && colStart != colStop) {
                stringBuilder.append(SKIP_ROWS).append(COLUMN_SEPARATOR);
                i = colStart;
            }
            final String th = get(i).getName() == null ? (EMPTY_COLUMN_PREFIX + i) : get(i).getName();
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
                alignRight(stringBuilder, SKIP_ROWS, idColWidth).append(COLUMN_SEPARATOR);
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
            alignRight(stringBuilder, String.valueOf(j), idColWidth).append(COLUMN_SEPARATOR);

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

    @Override
    public DataFrame subset(int start, int end) {
        return new DataFrameView(this, start, end);
    }

    @Override
    public DataFrame subset(Predicate<String> test) {
        return new DataFrameView(this, test);
    }

    static int[] range(int[] out, int start, int end) {
        for (int i = start, j = 0; i < end; ++i) {
            out[j++] = i;
        }
        return out;
    }

    static int[] range(int start, int end) {
        return range(new int[end - start], start, end);
    }
}
