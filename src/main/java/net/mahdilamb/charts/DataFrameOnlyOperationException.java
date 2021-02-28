package net.mahdilamb.charts;

/**
 * Exception that is thrown when a method that only applies to a dataframe is used without a dataframe attached
 */
public class DataFrameOnlyOperationException extends UnsupportedOperationException {
    /**
     * Create an exception with the given message
     *
     * @param message the message that is outputted
     */
    public DataFrameOnlyOperationException(String message) {
        super(message);
    }

    public DataFrameOnlyOperationException() {
        this("This method should only be used when using a dataframe.");
    }
}
