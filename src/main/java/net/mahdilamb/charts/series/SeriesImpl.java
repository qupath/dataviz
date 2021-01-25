package net.mahdilamb.charts.series;

import net.mahdilamb.charts.plots.Plot;

import java.util.*;
import java.util.function.IntToDoubleFunction;

abstract class SeriesImpl<T> implements Series<T> {
    static final class CompatibleSeries {
        private final SeriesType[] plotType;

        public CompatibleSeries(SeriesType[] plotType) {
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

    static final Map<CompatibleSeries, Set<Plot>> seriesToPlotMap = new HashMap<>();

    static {
        //TODO go through plots and add to the map
    }

    /**
     * Default implementation of a series backed by a double array
     */
    static final class OfDoubleArray extends SeriesImpl<Double> implements DoubleSeries {
        final double[] data;

        OfDoubleArray(final String name, final double... data) {
            super(name);
            this.data = data;
        }

        @Override
        public double get(int index) {
            return data[index];
        }

        @Override
        public int size() {
            return data.length;
        }

    }
    /**
     * Default implementation of a series backed by a boolean array
     */
    static final class OfBooleanArray extends SeriesImpl<Boolean> implements BooleanSeries {
        final boolean[] data;

        OfBooleanArray(final String name, final boolean... data) {
            super(name);
            this.data = data;
        }

        @Override
        public boolean get(int index) {
            return data[index];
        }

        @Override
        public int size() {
            return data.length;
        }

    }

    /**
     * Default implementation of a series backed by a double array
     */
    static final class OfStringArray extends SeriesImpl<String> implements StringSeries {
        final String[] data;

        OfStringArray(final String name, final String... data) {
            super(name);
            this.data = data;
        }

        @Override
        public String get(int index) {
            return data[index];
        }

        @Override
        public int size() {
            return data.length;
        }

    }

    /**
     * Default implementation of a series backed by a string, which parses to double when required
     */
    static final class OfStringToDoubleArray extends SeriesImpl<Double> implements DoubleSeries {

        private final String[] data;

        OfStringToDoubleArray(final String name, final String... data) {
            super(name);
            this.data = data;
        }

        @Override
        public double get(int index) {
            return SeriesType.toDouble(data[index]);
        }

        @Override
        public int size() {
            return data.length;
        }

    }

    /**
     * Default implementation of a series backed by a string, which parses to long when required
     */
    static final class OfStringToLongArray extends SeriesImpl<Long> implements LongSeries {

        private final String[] data;

        OfStringToLongArray(final String name, final String... data) {
            super(name);
            this.data = data;
        }

        @Override
        public long get(int index) {
            return SeriesType.toLong(data[index]);
        }

        @Override
        public int size() {
            return data.length;
        }

        @Override
        public boolean isNaN(int index) {
            return !SeriesType.INTEGER.matches(data[index]);
        }
    }

    static final class OfLongArray extends SeriesImpl<Long> implements LongSeries {
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
        }

        @Override
        public long get(int index) {
            return data[index];
        }

        @Override
        public boolean isNaN(int index) {
            int i = Arrays.binarySearch(isNan, 0, nanCount, index);
            return i != -1;
        }

        @Override
        public int size() {
            return data.length;
        }
    }

    /**
     * Default implementation of a series backed by a collection of objects
     */
    static final class OfFunctionalDouble extends SeriesImpl<Double> implements DoubleSeries {
        private final int size;
        private final IntToDoubleFunction dataGetter;

        OfFunctionalDouble(final String name, int size, IntToDoubleFunction dataGetter) {
            super(name);
            this.size = size;
            this.dataGetter = dataGetter;
        }

        @Override
        public double get(int index) {
            return dataGetter.applyAsDouble(index);
        }

        @Override
        public int size() {
            return size;
        }
    }

    /**
     * Default implementation of a series backed by a string, which parses to boolean when accessed
     */
    static final class OfStringToBooleanArray extends SeriesImpl<Boolean> implements BooleanSeries {

        private final String[] data;

        OfStringToBooleanArray(final String name, final String... data) {
            super(name);
            this.data = data;
        }

        @Override
        public boolean get(int index) {
            return SeriesType.toBoolean(data[index]);
        }

        @Override
        public int size() {
            return data.length;
        }

    }

    static final class RepeatedString extends SeriesImpl<String> implements StringSeries {

        private final int size;
        private final StringRepetition[] repeats;

        /**
         * Create an abstract named series
         *
         * @param name the name of the series
         */
        protected RepeatedString(String name, StringRepetition... repeats) {
            super(name);
            if (repeats.length <=0){
                throw new IllegalArgumentException("must be at least one repeat");
            }
            this.repeats = repeats;
            int size = 0;
            for (final StringRepetition r : repeats) {
                size += r.num;
            }
            this.size = size;
        }

        @Override
        public int size() {
            return size;
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
                    return i < size;
                }

                @Override
                public String next() {
                    ++i;
                    if (j < currentRepetition.num) {
                        ++j;
                    }else {
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
            while (i < size) {
                j += repeats[i++].num;
                if (j > index) {
                    return repeats[i - 1].data;
                }
            }
            throw new IndexOutOfBoundsException();
        }
    }

    private final String name;

    /**
     * Create an abstract named series
     *
     * @param name the name of the series
     */
    protected SeriesImpl(final String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("{Series: \"%s\", size: %d}", getName(), size());
    }

}
