package net.mahdilamb.charts.series;

import net.mahdilamb.charts.layouts.Plot;
import net.mahdilamb.charts.utils.StringUtils;

import java.util.*;
import java.util.function.IntToDoubleFunction;

import static net.mahdilamb.charts.series.DatasetImpl.COLUMN_SEPARATOR;

/**
 * Implementations of the various different types of series
 *
 * @param <T> the type of the elements in the series
 */
abstract class DataSeriesImpl<T extends Comparable<T>> implements DataSeries<T> {
    static int MAX_VISIBLE_CELLS = 10;

    /**
     * Default implementation of a series backed by a double array
     */
    static final class OfDoubleArray extends DataSeriesImpl<Double> implements DoubleSeries {
        final double[] data;

        OfDoubleArray(final String name, final double... data) {
            super(name);
            this.data = data;
            this.end = data.length;
        }

        @Override
        public double getDouble(int index) {
            return data[index];
        }

    }

    /**
     * Default implementation of a series backed by a boolean array
     */
    static final class OfBooleanArray extends DataSeriesImpl<Boolean> implements BooleanSeries {
        final boolean[] data;

        OfBooleanArray(final String name, final boolean... data) {
            super(name);
            this.data = data;
            this.end = data.length;
        }

        @Override
        public boolean getBoolean(int index) {
            return data[index];
        }


    }

    /**
     * Default implementation of a series backed by a double array
     */
    static final class OfStringArray extends DataSeriesImpl<String> implements StringSeries {
        final String[] data;

        OfStringArray(final String name, final String... data) {
            super(name);
            this.data = data;
            this.end = data.length;
        }

        @Override
        public String get(int index) {
            return data[index];
        }


    }

    /**
     * Default implementation of a series backed by a string, which parses to double when required
     */
    static final class OfStringToDoubleArray extends DataSeriesImpl<Double> implements DoubleSeries {

        private final String[] data;

        OfStringToDoubleArray(final String name, final String... data) {
            super(name);
            this.data = data;
            this.end = data.length;
        }

        @Override
        public double getDouble(int index) {
            return DataType.toDouble(data[index]);
        }

    }

    /**
     * Default implementation of a series backed by a string, which parses to long when required
     */
    static final class OfStringToLongArray extends DataSeriesImpl<Long> implements LongSeries {

        private final String[] data;

        OfStringToLongArray(final String name, final String... data) {
            super(name);
            this.data = data;
            this.end = data.length;
        }

        @Override
        public long getLong(int index) {
            return DataType.toLong(data[index]);
        }

        @Override
        public boolean isNaN(int index) {
            return !DataType.LONG.matches(data[index]);
        }
    }

    static final class OfLongArray extends DataSeriesImpl<Long> implements LongSeries {
        final long[] data;
        int[] isNan;
        int nanCount;

        /**
         * Create an abstract named series
         *
         * @param name the name of the series
         */
        OfLongArray(String name, long[] data, int[] isNan, int nanCount) {
            super(name);
            this.data = data;
            this.isNan = isNan;
            this.nanCount = nanCount;
            this.end = data.length;
        }

        @Override
        public long getLong(int index) {
            return data[index];
        }

        @Override
        public boolean isNaN(int index) {
            int i = Arrays.binarySearch(isNan, 0, nanCount, index);
            return i != -1;
        }

    }

    /**
     * Default implementation of a series backed by a collection of objects
     */
    static final class OfFunctionalDouble extends DataSeriesImpl<Double> implements DoubleSeries {
        private final IntToDoubleFunction dataGetter;

        OfFunctionalDouble(final String name, int size, IntToDoubleFunction dataGetter) {
            super(name);
            this.end = size;
            this.dataGetter = dataGetter;
        }

        @Override
        public double getDouble(int index) {
            return dataGetter.applyAsDouble(index);
        }

    }

    /**
     * Default implementation of a series backed by a string, which parses to boolean when accessed
     */
    static final class OfStringToBooleanArray extends DataSeriesImpl<Boolean> implements BooleanSeries {

        private final String[] data;

        OfStringToBooleanArray(final String name, final String... data) {
            super(name);
            this.data = data;
            this.end = data.length;
        }

        @Override
        public boolean getBoolean(int index) {
            return DataType.toBoolean(data[index]);
        }

    }

    static final class RepeatedString extends DataSeriesImpl<String> implements StringSeries {

        private final StringRepetition[] repeats;

        /**
         * Create an abstract named series
         *
         * @param name the name of the series
         */
        protected RepeatedString(String name, StringRepetition... repeats) {
            super(name);
            if (repeats.length <= 0) {
                throw new IllegalArgumentException("must be at least one repeat");
            }
            this.repeats = repeats;
            int size = 0;
            for (final StringRepetition r : repeats) {
                size += r.num;
            }
            this.end = size;
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<>() {
                private int i = 0;
                private int j = 0;
                private int k = 0;
                private StringRepetition currentRepetition = repeats[0];

                @Override
                public boolean hasNext() {
                    return i < end;
                }

                @Override
                public String next() {
                    ++i;
                    if (j < currentRepetition.num) {
                        ++j;
                    } else {
                        currentRepetition = repeats[++k];
                        j = 1;
                    }
                    return currentRepetition.data;
                }
            };
        }

        @Override
        public String get(int index) {

            int i = 0, j = 0;
            while (i < end) {
                j += repeats[i++].num;
                if (j > index) {
                    return repeats[i - 1].data;
                }
            }
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Key used to find compatible series
     */
    static final class CompatibleSeries {
        private final DataType[] plotType;

        public CompatibleSeries(DataType[] plotType) {
            this.plotType = plotType;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(plotType);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != CompatibleSeries.class) {
                return false;
            }
            return Arrays.equals(plotType, ((CompatibleSeries) obj).plotType);
        }
    }

    /**
     * Map of the plot to compatible series
     */
    static final Map<CompatibleSeries, Set<Plot<?>>> seriesToPlotMap = new HashMap<>();

    static {
        //TODO go through plots and add to the map
    }

    private final String name;
    int start = 0;
    int end;

    /**
     * Create an abstract named series
     *
     * @param name the name of the series
     */
    protected DataSeriesImpl(final String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final int size() {
        return end - start;
    }

    static String formatCell(DataSeries<?> series, int index) {
        return series.getType() == DataType.LONG && ((LongSeries) series).isNaN(index) ? "NaN" : String.valueOf(series.get(index));
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        if (size() > 1) {
            int width = Math.max(1, String.valueOf(size()).length());
            if (size() < MAX_VISIBLE_CELLS) {
                for (int i = 0; i < size(); ++i) {
                    DatasetImpl.alignRight(stringBuilder, String.valueOf(i), width).append(COLUMN_SEPARATOR).append(formatCell(this, i)).append('\n');

                }
            } else {
                int halfRows = MAX_VISIBLE_CELLS >>> 1;
                for (int i = 0; i < halfRows; ++i) {
                    DatasetImpl.alignRight(stringBuilder, String.valueOf(i), width).append(COLUMN_SEPARATOR).append(formatCell(this, i)).append('\n');
                }
                //ellipses
                DatasetImpl.alignRight(stringBuilder, StringUtils.repeatCharacter('.', Math.min(3, Math.max(width, 1))), width).append(COLUMN_SEPARATOR).append("...\n");
                for (int i = size() - halfRows; i < size(); ++i) {
                    DatasetImpl.alignRight(stringBuilder, String.valueOf(i), width).append(COLUMN_SEPARATOR).append(formatCell(this, i)).append('\n');
                }
            }


        }
        return stringBuilder.append(String.format("{Series: \"%s\", size: %d, type: %s}", getName(), size(), StringUtils.toTitleCase(getType().name()))).toString();
    }

}
