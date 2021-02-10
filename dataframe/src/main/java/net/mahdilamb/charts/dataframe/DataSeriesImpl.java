package net.mahdilamb.charts.dataframe;

import net.mahdilamb.charts.dataframe.utils.StringUtils;
import net.mahdilamb.charts.dataframe.utils.GroupBy;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.*;

/**
 * Implementations of the various different types of series
 *
 * @param <T> the type of the elements in the series
 */
abstract class DataSeriesImpl<T extends Comparable<T>> implements DataSeries<T>, SeriesWithFunctionalOperators<T> {
    static int MAX_VISIBLE_CELLS = 10;

    static final class AsTypeArray<S extends Comparable<S>, T extends Comparable<T>> extends DataSeriesImpl<T> {
        private final DataSeries<S> source;
        private final Function<S, T> converter;
        private final DataType dataType;

        AsTypeArray(DataSeries<S> source, DataType dataType, Function<S, T> converter) {
            super(source.getName());
            this.source = source;
            this.converter = converter;
            this.dataType = dataType;
            this.end = source.size();

        }

        @Override
        public T get(int index) {
            return converter.apply(source.get(index));
        }

        @Override
        public DataType getType() {
            return dataType;
        }
    }

    static final class AsNumericTypeArray<S extends Comparable<S>, T extends Number & Comparable<T>> extends DataSeriesImpl<T> implements NumericSeries<T> {
        private final DataSeries<S> source;
        private final Function<S, T> converter;
        private final DataType dataType;

        AsNumericTypeArray(DataSeries<S> source, DataType dataType, Function<S, T> converter) {
            super(source.getName());
            this.source = source;
            this.converter = converter;
            this.dataType = dataType;
            this.end = source.size();

        }

        @Override
        public T get(int index) {
            return converter.apply(source.get(index));
        }

        @Override
        public DataType getType() {
            return dataType;
        }

        @Override
        public boolean isNaN(int index) {
            return get(index) == null || (get(index).getClass() == Double.class && Double.isNaN((Double) get(index)));
        }
    }


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

        <S extends Number & Comparable<S>> OfDoubleArray(NumericSeries<S> source, ToDoubleFunction<S> converter) {
            super(source.getName());
            this.data = new double[source.size()];
            this.end = source.size();
            for (int i = 0; i < source.size(); ++i) {
                data[i] = source.isNaN(i) ? Double.NaN : converter.applyAsDouble(source.get(i));
            }
        }

        <S extends Comparable<S>> OfDoubleArray(DataSeries<S> source, ToDoubleFunction<S> converter) {
            super(source.getName());
            this.data = new double[source.size()];
            this.end = source.size();
            for (int i = 0; i < source.size(); ++i) {
                data[i] = converter.applyAsDouble(source.get(i));
            }
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

        <S extends Comparable<S>> OfBooleanArray(DataSeries<S> source, Predicate<S> converter) {
            super(source.getName());
            this.end = source.size();
            data = new boolean[source.size()];
            for (int i = 0; i < source.size(); ++i) {
                data[i] = converter.test(source.get(i));
            }

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

        <S extends Comparable<S>> OfStringArray(DataSeries<S> source, Function<S, String> converter) {
            super(source.getName());
            this.end = source.size();
            data = new String[source.size()];
            for (int i = 0; i < source.size(); ++i) {
                data[i] = converter.apply(source.get(i));
            }
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
        int[] isNaN;
        int nanCount;

        /**
         * Create an abstract named series
         *
         * @param name the name of the series
         */
        OfLongArray(String name, long[] data, int[] isNaN, int nanCount) {
            super(name);
            this.data = data;
            this.isNaN = isNaN;
            this.nanCount = nanCount;
            this.end = data.length;
        }

        @Override
        public long getLong(int index) {
            return data[index];
        }

        @Override
        public boolean isNaN(int index) {
            int i = Arrays.binarySearch(isNaN, 0, nanCount, index);
            return i != -1;
        }

        static int[] ensureCapacity(int[] array, int newCapacity) {
            if (newCapacity > array.length) {
                return Arrays.copyOf(array, newCapacity);
            }
            return array;
        }

        <S extends Comparable<S>> OfLongArray(DataSeries<S> source, ToLongFunction<S> converter, Predicate<S> notNanTest) {
            super(source.getName());
            data = new long[source.size()];
            nanCount = 0;
            isNaN = new int[4];
            this.end = source.size();

            for (int i = 0; i < source.size(); ++i) {
                if (notNanTest.test(source.get(i))) {
                    data[i] = converter.applyAsLong(source.get(i));
                } else {
                    data[i] = 0;
                    isNaN = ensureCapacity(isNaN, ++nanCount);
                    isNaN[nanCount - 1] = i;
                }
            }
        }

    }

    static final class OfNonNaNLongArray extends DataSeriesImpl<Long> implements LongSeries {
        final long[] data;


        /**
         * Create an abstract named series
         *
         * @param name the name of the series
         */
        OfNonNaNLongArray(String name, long[] data) {
            super(name);
            this.data = data;
            this.end = data.length;
        }

        <S extends Comparable<S>> OfNonNaNLongArray(DataSeries<S> source, ToLongFunction<S> converter) {
            super(source.getName());
            data = new long[source.size()];
            this.end = source.size();

            for (int i = 0; i < source.size(); ++i) {
                data[i] = converter.applyAsLong(source.get(i));
            }

        }

        @Override
        public long getLong(int index) {
            return data[index];
        }

        @Override
        public boolean isNaN(int index) {
            return false;
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

    private GroupBy<T> group;
    private final String name;
    int start = 0;
    int end;

    @Override
    public GroupBy<T> groups() {
        if (group == null) {
            group = new GroupBy<>(this);
        }
        return group;
    }

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
    public int size() {
        return end - start;
    }

    @SuppressWarnings("unchecked")
    static String formatCell(DataSeries<?> series, int index) {
        return series.getType() == DataType.LONG && ((NumericSeries<Long>) series).isNaN(index) ? "NaN" : String.valueOf(series.get(index));
    }

    int getId(int i) {
        return i;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        if (size() > 1) {
            int width = Math.max(1, String.valueOf(getId(size() - 1)).length());
            if (size() <= MAX_VISIBLE_CELLS) {
                for (int i = 0; i < size(); ++i) {
                    DataFrameImpl.alignRight(stringBuilder, String.valueOf(getId(i)), width).append(DataFrameImpl.COLUMN_SEPARATOR).append(formatCell(this, i)).append('\n');
                }
            } else {
                int halfRows = MAX_VISIBLE_CELLS >>> 1;
                for (int i = 0; i < halfRows; ++i) {
                    DataFrameImpl.alignRight(stringBuilder, String.valueOf(getId(i)), width).append(DataFrameImpl.COLUMN_SEPARATOR).append(formatCell(this, i)).append('\n');
                }
                //ellipses
                DataFrameImpl.alignRight(stringBuilder, StringUtils.repeatCharacter('.', Math.min(3, Math.max(width, 1))), width).append(DataFrameImpl.COLUMN_SEPARATOR).append("...\n");
                for (int i = size() - halfRows; i < size(); ++i) {
                    DataFrameImpl.alignRight(stringBuilder, String.valueOf(getId(i)), width).append(DataFrameImpl.COLUMN_SEPARATOR).append(formatCell(this, i)).append('\n');
                }
            }


        }
        return stringBuilder.append(String.format("{Series: \"%s\", size: %d, type: %s}", getName(), size(), StringUtils.toTitleCase(getType().name()))).toString();
    }

    @Override
    public DataSeries<T> subset(int start, int end) {
        return new DataSeriesView<>(this, start, end);
    }

    @Override
    public DataSeries<T> subset(IntPredicate test) {
        return new DataSeriesView<>(this, test);
    }

    static final class DataSeriesView<T extends Comparable<T>> extends DataSeriesImpl<T> {

        final DataSeries<T> dataSeries;
        int[] rows;
        int numRows;

        public DataSeriesView(DataSeries<T> dataSeries, int[] ids, int numRows) {
            super(dataSeries.getName());
            if (dataSeries.getClass() == DataSeriesView.class) {
                this.dataSeries = ((DataSeriesView<T>) dataSeries).dataSeries;
                rows = new int[ids.length];
                for (int i = 0; i < ids.length; ++i) {
                    rows[i] = ((DataSeriesView<T>) dataSeries).getId(ids[i]);
                }
            } else {
                this.dataSeries = dataSeries;
                rows = ids;
            }
            this.numRows = numRows;

        }

        public DataSeriesView(DataSeries<T> dataSeries, int[] ids) {
            this(dataSeries, ids, ids.length);
        }

        public DataSeriesView(DataSeries<T> dataSeries, IntPredicate test) {
            super(dataSeries.getName());
            int j = 0;
            int i = 0;
            if (dataSeries.getClass() == DataSeriesView.class) {
                this.dataSeries = ((DataSeriesView<T>) dataSeries).dataSeries;
                this.rows = new int[dataSeries.size()];

                while (i < dataSeries.size()) {
                    int k = ((DataSeriesView<T>) dataSeries).getId(i);
                    if (test.test(k)) {
                        rows[j++] = k;
                    }
                    ++i;
                }
            } else {
                this.dataSeries = dataSeries;
                this.rows = new int[dataSeries.size()];
                while (i < dataSeries.size()) {
                    if (test.test(i)) {
                        rows[j++] = i;
                    }
                    ++i;
                }
            }
            numRows = j;

        }

        protected DataSeriesView(DataSeries<T> dataSeries, int start, int end) {
            this(dataSeries, DataFrameImpl.range(start, end));
        }

        @Override
        int getId(int i) {
            return rows[i];
        }

        @Override
        public T get(int index) {
            return dataSeries.get(rows[index]);
        }

        @Override
        public int size() {
            return numRows;
        }

        @Override
        public DataType getType() {
            return dataSeries.getType();
        }
    }

}
