package net.mahdilamb.dataviz.ui;

import java.util.function.DoublePredicate;
import java.util.function.Predicate;

public interface Validation<S, T> {
    Validation<String, Double> STRING_TO_DOUBLE = ValidationImpl.StringToDoubleValidation.INSTANCE;
    Validation<String, Integer> STRING_TO_INTEGER = ValidationImpl.StringToIntegerValidation.INSTANCE;
    Validation<String, Long> STRING_TO_LONG = ValidationImpl.StringToLongValidation.INSTANCE;

    T validate(S value);

    /**
     * @param value the value to check the validation of
     * @return 0 if the input is valid or a negative number otherwise
     */
    int isValid(S value);

    default String getErrorMessage(int errorCode) {
        if (errorCode < 0){
            return "Invalid input";
        }
        return null;
    }


    static Validation<String, Double> createDoubleValidation(final String errorMessage, DoublePredicate acceptable) {
        return new ValidationImpl.CustomStringToDoubleValidation(errorMessage, acceptable);
    }

    static Validation<String, String> createStringValidation(Predicate<String> acceptable) {
        return new ValidationImpl.CustomStringValidation(acceptable);
    }

}
