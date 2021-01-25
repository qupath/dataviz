package net.mahdilamb.charts.series;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static net.mahdilamb.charts.series.DatasetImpl.EMPTY_COLUMN_PREFIX;
import static net.mahdilamb.charts.utils.StringUtils.LINE_PATTERN;
import static net.mahdilamb.charts.utils.StringUtils.iterateLine;

/**
 * Builder for importing data from a file or strong.
 */
public abstract class DatasetImporter {

    /**
     * @return a temporary dataset with the top n rows.
     */
    public final Dataset preview() {
        preparePreview();
        return currentPreview;
    }

    /**
     * @return the final dataset. If this is reused, it will create a new dataset with the same backing data
     */
    public abstract Dataset build();

    /**
     * Set a column type
     *
     * @param index the index of the column
     * @param type  the type of the column
     * @return this importer
     */
    public DatasetImporter setType(int index, SeriesType type) {
        types[index] = Objects.requireNonNull(type);
        return this;
    }

    /**
     * Fields that should be calculated in the initial read
     */
    protected int numLines;
    protected int numColumns;
    protected SeriesType[] types;
    protected Dataset currentPreview;

    /**
     * Fields that should be populated with user input
     */
    protected boolean hasColumnNames;

    private DatasetImporter() {

    }

    //TODO allow creating from string or clipboard
    static final class FromString extends DatasetImporter {
        private final String source;

        FromString(final String source) {
            this.source = source;
        }

        @Override
        protected void preparePreview() {
            //TODO
        }

        @Override
        public Dataset build() {
            //TODO
            return null;
        }
    }

    //TODO allow changing whether it has header or not
    //TODO skiprows, skipcols, last row, lastcol
    static final class FromFile extends DatasetImporter {
        static int TEST_LINES = 20;
        final File source;
        final char separator;
        final Charset charset;
        final char quoteCharacter;
        String[] putativeHeader;
        private String[] previewLines;

        FromFile(final File source, char separator, char quoteCharacter, Charset charset) {
            this.source = source;
            this.separator = separator;
            this.charset = charset;
            this.quoteCharacter = quoteCharacter;
            numLines = 1;
            try (FileInputStream inputStream = new FileInputStream(source); Scanner scanner = new Scanner(inputStream, charset.name())) {
                String line = scanner.nextLine();
                int[] headerNameLength = new int[20];
                char quote = 0;
                int g = 0, k = 1;
                for (int j = 0; g < line.length(); ++g, ++k) {
                    char c = line.charAt(g);
                    if (isSeparator(c) && quote == 0) {
                        if (j >= headerNameLength.length) {
                            headerNameLength = Arrays.copyOf(headerNameLength, headerNameLength.length + 20);
                        }
                        headerNameLength[j++] = k - 1;
                        k = 0;
                        numColumns++;
                        quote = 0;
                    } else {
                        if (isQuote(c)) {
                            quote = quote != c ? c : 0;
                        }
                    }
                }
                if (g == line.length()) {
                    if (g >= headerNameLength.length) {
                        headerNameLength = Arrays.copyOf(headerNameLength, headerNameLength.length + 1);
                    }
                    headerNameLength[numColumns] = k - 1;
                    numColumns++;
                }
                putativeHeader = new String[numColumns];
                types = new SeriesType[numColumns];
                if (numColumns > 1) {
                    for (int i = 0, l = 0; i < numColumns; l += headerNameLength[i] + 1, ++i) {
                        if (headerNameLength[i] == 0) {
                            putativeHeader[i] = EMPTY_COLUMN_PREFIX + i;
                        } else {
                            int s = l, e = l + headerNameLength[i];
                            if (line.charAt(s) == line.charAt(e - 1) && isQuote(line.charAt(s))) {
                                ++s;
                                --e;
                            }
                            putativeHeader[i] = line.substring(s, e);
                        }
                    }
                } else {
                    if (line.length() == 0) {
                        putativeHeader[0] = EMPTY_COLUMN_PREFIX + "0";
                    } else {
                        if (isQuote(line.charAt(0)) && line.charAt(0) == line.charAt(line.length() - 1)) {
                            putativeHeader[0] = line.substring(1, line.length() - 1);

                        } else {
                            putativeHeader[0] = line;

                        }
                    }
                }

                int scanCount = 0;
                SeriesType[] seriesTypes = SeriesType.values();

                int[] typeCounts = new int[seriesTypes.length * numColumns];
                previewLines = new String[TEST_LINES];
                while (scanCount < TEST_LINES && scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    previewLines[scanCount] = line;
                    int h = 0, o = 0;
                    while (h < line.length()) {
                        int currentO = o;
                        h = iterateLine(line, h, separator, quoteCharacter, str -> {
                            int b = 0;
                            for (final SeriesType t : seriesTypes) {
                                if (t.matches(str)) {
                                    typeCounts[currentO * seriesTypes.length + b] += 1;
                                }
                                ++b;
                            }
                        });
                        ++o;
                    }
                    scanCount++;
                    numLines++;
                }
                for (int i = 0; i < numColumns; ++i) {
                    SeriesType favored = null;
                    int favoredScored = 0;
                    for (int j = 0; j < seriesTypes.length; ++j) {
                        int count = typeCounts[i * seriesTypes.length + j];
                        SeriesType t = seriesTypes[j];
                        if (favored == null || count >= favoredScored) {
                            if (favored == null || count > favoredScored) {
                                favored = t;
                                favoredScored = count;
                            } else {
                                if (favored.weight < t.weight) {
                                    favored = t;
                                }
                            }
                        }
                    }
                    types[i] = favored;
                }
                guessHasColumnNames();
                while (scanner.findWithinHorizon(LINE_PATTERN, 0) != null) {
                    numLines++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void guessHasColumnNames() {
            hasColumnNames = false;
            for (int i = 0; i < numColumns; ++i) {
                if (types[i] != SeriesType.STRING && !types[i].matches(putativeHeader[i])) {
                    hasColumnNames = true;
                    break;
                }
            }
        }

        private boolean isQuote(char c) {
            return c == quoteCharacter;
        }

        private boolean isSeparator(char c) {
            return c == separator;
        }

        @Override
        public String toString() {
            return String.format("DatasetImporter {file: '%s'}", source);
        }

        @Override
        protected void preparePreview() {
            //TODO move header down to series if no column names
            int rows = Math.min(TEST_LINES, numLines - 1);
            final String[][] data = new String[numColumns][rows];

            for (int i = 0; i < numColumns; ++i) {
                data[i] = new String[rows];
            }
            for (int i = 0; i < rows; ++i) {
                final String line = previewLines[i];
                int h = 0, o = 0;
                while (h < line.length()) {
                    int col = o;
                    int row = i;
                    h = iterateLine(line, h, separator, quoteCharacter, str -> data[col][row] = str);
                    ++o;
                }
            }

            final Series<?>[] series = new Series[numColumns];

            for (int i = 0; i < numColumns; ++i) {
                switch (types[i]) {
                    case DOUBLE:
                        series[i] = new SeriesImpl.OfStringToDoubleArray(putativeHeader[i], data[i]);
                        break;
                    case INTEGER:
                        series[i] = new SeriesImpl.OfStringToLongArray(putativeHeader[i], data[i]);
                        break;
                    case STRING:
                        series[i] = new SeriesImpl.OfStringArray(putativeHeader[i], data[i]);
                        break;
                    case BOOLEAN:
                        series[i] = new SeriesImpl.OfStringToBooleanArray(putativeHeader[i], data[i]);
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
            currentPreview = new DatasetImpl.OfArray(source.getName(), series);

        }

        @Override
        public Dataset build() {
            return new DatasetImpl.FromFile(this);
        }
    }

    protected abstract void preparePreview();


}
