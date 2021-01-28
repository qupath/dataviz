package net.mahdilamb.charts.datasets;

import java.util.Iterator;

/**
 * Utility class for creating simple groups of repeated strings
 */
public final class StringRepetition implements Iterable<String> {

    /**
     * Create a repeated string
     *
     * @param string the string to repeat
     * @param num    the number of times to repeat it
     * @return an iterable repeated string
     */
    public static StringRepetition repeat(String string, int num) {
        return new StringRepetition(string, num);
    }

    /**
     * Create a group from repeats
     *
     * @param name       the name of the group
     * @param repetition the repeats
     * @return a string series from the repeats
     */
    public static StringSeries group(String name, StringRepetition... repetition) {
        return new DataSeriesImpl.RepeatedString(name, repetition);
    }

    final String data;
    final int num;

    /**
     * Create a repeated string
     *
     * @param string the string to repeat
     * @param num    the number of times to repeat it
     */
    public StringRepetition(String string, int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("num must be >0");
        }
        this.data = string;
        this.num = num;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < num;
            }

            @Override
            public String next() {
                i++;
                return data;
            }
        };
    }

}
