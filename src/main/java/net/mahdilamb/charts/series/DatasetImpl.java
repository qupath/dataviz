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

import static net.mahdilamb.charts.series.SeriesType.INTEGER;
import static net.mahdilamb.charts.utils.StringUtils.iterateLine;
//TODO don't show all columns

/**
 * Default implementation of a dataset
 */
abstract class DatasetImpl implements Dataset {
    static String EMPTY_COLUMN_PREFIX = "Col.";

    static int MAX_DISPLAY_ROWS = 10;
    static int MIN_WIDTH = 4;
    static int MAX_WIDTH = 12;
    static String COLUMN_SEPARATOR = "  ";

    /**
     * Dataset from an array of series
     */
    static final class OfArray extends DatasetImpl {
        private final Series<?>[] series;

        /**
         * Create a dataset from an array of series
         *
         * @param name   the name of the dataset
         * @param series the array of series
         */
        OfArray(final String name, final Series<?>... series) {
            super(name);
            this.series = series;
        }


        @Override
        public Series<?> getSeries(int series) {
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
        private final IntFunction<Series<?>> seriesGetter;
        private final int size;

        /**
         * Create a Dataset without having to implement the interface
         *
         * @param name         the name of the dataset
         * @param seriesGetter the function to get a series from its name
         * @param size         the number of series in the dataset
         */
        OfFunctional(final String name, final int size, final IntFunction<Series<?>> seriesGetter) {
            super(name);
            this.seriesGetter = Objects.requireNonNull(seriesGetter);
            this.size = size;
        }

        @Override
        public Series<?> getSeries(int series) {
            return seriesGetter.apply(series);
        }

        @Override
        public int numSeries() {
            return size;
        }
    }

    private final String name;

    /**
     * Create an abstract named dataset
     *
     * @param name the name of the dataset
     */
    protected DatasetImpl(final String name) {
        this.name = name;
    }

    static final class FromFile extends DatasetImpl {
        private final Series<?>[] series;

        protected FromFile(DatasetImporter.FromFile importer) {
            this(importer.source, importer.separator, importer.quoteCharacter, importer.charset, importer.putativeHeader, importer.types, importer.hasColumnNames, importer.numColumns, (importer.hasColumnNames ? -1 : 0) + importer.numLines);
        }


        protected FromFile(File name, char separator, char quoteCharacter, Charset charset, String[] columnNames, SeriesType[] types, boolean hasColumnNames, int numColumns, int numRows) {
            super(name.getName());
            this.series = new Series[numColumns];
            boolean getColumnNames = columnNames == null && hasColumnNames;
            for (int i = 0; i < numColumns; ++i) {
                final String columnName = columnNames == null ? (hasColumnNames ? (EMPTY_COLUMN_PREFIX + i) : null) : columnNames[i] == null ? EMPTY_COLUMN_PREFIX + i : columnNames[i];

                switch (types[i]) {
                    case DOUBLE:
                        series[i] = new SeriesImpl.OfDoubleArray(columnName, new double[numRows]);
                        break;
                    case INTEGER:
                        series[i] = new SeriesImpl.OfLongArray(columnName, new long[numRows], new int[4], 0);
                        break;
                    case STRING:
                        series[i] = new SeriesImpl.OfStringArray(columnName, new String[numRows]);
                        break;
                    case BOOLEAN:
                        series[i] = new SeriesImpl.OfBooleanArray(columnName, new boolean[numRows]);
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
                                case DOUBLE:
                                    ((SeriesImpl.OfDoubleArray) series[currentO]).data[row] = SeriesType.toDouble(str);
                                    break;
                                case INTEGER:
                                    final SeriesImpl.OfLongArray s = (SeriesImpl.OfLongArray) series[currentO];
                                    if (INTEGER.matches(str)) {
                                        s.data[row] = Long.parseLong(str);
                                    } else {
                                        s.data[row] = 0;
                                        if (s.nanCount + 1 > s.isNan.length) {
                                            s.isNan = Arrays.copyOf(s.isNan, s.isNan.length + 8);
                                        }
                                        s.isNan[s.nanCount++] = row;
                                    }
                                    break;
                                case STRING:
                                    ((SeriesImpl.OfStringArray) series[currentO]).data[row] = str;
                                    break;
                                case BOOLEAN:
                                    ((SeriesImpl.OfBooleanArray) series[currentO]).data[row] = SeriesType.toBoolean(str);
                                    break;
                                default:
                                    throw new UnsupportedOperationException();
                            }
                        });
                        ++o;
                    }
                    ++rowCount;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Series<?> getSeries(int series) {
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
        for (int i = 0; i < numSeries(); ++i) {
            final String th = getSeries(i).getName();
            if (th.length() < MIN_WIDTH) {
                stringBuilder.append(StringUtils.repeatCharacter(' ', MIN_WIDTH - th.length())).append(th);
                width[i] = MIN_WIDTH;

            } else if (th.length() < MAX_WIDTH) {
                stringBuilder.append(th);
                width[i] = th.length();
            } else {
                stringBuilder.append(th, 0, MAX_WIDTH - 3).append("...");
                width[i] = MAX_WIDTH;
            }
            stringBuilder.append(COLUMN_SEPARATOR);
        }
        stringBuilder.delete(stringBuilder.length() - COLUMN_SEPARATOR.length(), stringBuilder.length()).append('\n');

        int rows = getSeries(0).size();
        if (rows < MAX_DISPLAY_ROWS) {
            for (int j = 0; j < rows; ++j) {
                for (int i = 0; i < numSeries(); ++i) {
                    formatCell(stringBuilder, i, j, width[i]).append(COLUMN_SEPARATOR);
                }
                stringBuilder.delete(stringBuilder.length() - COLUMN_SEPARATOR.length(), stringBuilder.length()).append('\n');
            }
        } else {
            int halfRow = MAX_DISPLAY_ROWS >> 1;
            for (int j = 0; j < halfRow; ++j) {
                for (int i = 0; i < numSeries(); ++i) {
                    formatCell(stringBuilder, i, j, width[i]).append(COLUMN_SEPARATOR);
                }
                stringBuilder.delete(stringBuilder.length() - COLUMN_SEPARATOR.length(), stringBuilder.length()).append('\n');
            }
            for (int i = 0; i < numSeries(); ++i) {
                formatCell(stringBuilder, "...", width[i]).append(COLUMN_SEPARATOR);
            }
            stringBuilder.delete(stringBuilder.length() - COLUMN_SEPARATOR.length(), stringBuilder.length()).append('\n');

            for (int j = rows - halfRow; j < rows; ++j) {
                for (int i = 0; i < numSeries(); ++i) {
                    formatCell(stringBuilder, i, j, width[i]).append(COLUMN_SEPARATOR);
                }
                stringBuilder.delete(stringBuilder.length() - COLUMN_SEPARATOR.length(), stringBuilder.length()).append('\n');
            }
        }
        return stringBuilder.append(String.format("Dataset {name: \"%s\", cols: %d, rows: %d}\n", getName(), numSeries(), getSeries(0).size())).toString();
    }

    private StringBuilder formatCell(StringBuilder stringBuilder, final String td, int width) {
        if (td.length() < width) {
            return stringBuilder.append(StringUtils.repeatCharacter(' ', width - td.length())).append(td);
        } else {
            return stringBuilder.append(td, 0, width);
        }
    }

    private StringBuilder formatCell(StringBuilder stringBuilder, int col, int row, int width) {
        final String td;
        switch (getSeries(col).getType()) {
            case STRING:
                td = ((StringSeries) getSeries(col)).get(row);
                break;
            case INTEGER:
                td = String.valueOf(((LongSeries) getSeries(col)).isNaN(row) ? "NaN" : ((LongSeries) getSeries(col)).get(row));
                break;
            case DOUBLE:
                td = String.valueOf(((DoubleSeries) getSeries(col)).get(row));
                break;
            case BOOLEAN:
                td = String.valueOf(((BooleanSeries) getSeries(col)).get(row));
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return formatCell(stringBuilder, td, width);
    }


}
