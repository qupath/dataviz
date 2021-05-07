package net.mahdilamb.dataviz.ui;

import net.mahdilamb.dataframe.utils.StringUtils;

import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

final class ValidationImpl {
    private ValidationImpl() {

    }

    static final class StringToDoubleValidation implements Validation<String, Double> {
        static Validation<String, Double> INSTANCE = new StringToDoubleValidation();

        private StringToDoubleValidation() {

        }

        @Override
        public Double validate(String value) {
            if (value != null && isValid(value) >= 0) {
                return Double.parseDouble(value);
            }
            return null;
        }

        @Override
        public int isValid(String value) {
            return StringUtils.FLOATING_POINT_PATTERN_WITHOUT_HEX.matcher(value).matches() ? 0 : -1;
        }
    }

    static final class CustomStringToDoubleValidation implements Validation<String, Double> {

        private final DoublePredicate acceptable;
        private final String errorMessage;

        CustomStringToDoubleValidation(final String errorMessage, final DoublePredicate acceptable) {
            Objects.requireNonNull(this.acceptable = acceptable);
            Objects.requireNonNull(this.errorMessage = errorMessage);
        }

        @Override
        public Double validate(String value) {
            if (value != null && isValid(value) >= 0) {
                return Double.parseDouble(value);
            }
            return null;
        }

        @Override
        public int isValid(String value) {
            if (!StringUtils.FLOATING_POINT_PATTERN_WITHOUT_HEX.matcher(value).matches()) {
                return -1;
            }
            if (!acceptable.test(Double.parseDouble(value))) {
                return -2;
            }
            return 0;
        }

        @Override
        public String getErrorMessage(int errorCode) {
            if (errorCode == -1){
                return "Input is not valid double";
            }
            if (errorCode == -2){
                return errorMessage;
            }
            return null;
        }
    }

    static final class CustomStringValidation implements Validation<String, String> {

        private final Predicate<String> acceptable;

        CustomStringValidation(final Predicate<String> acceptable) {
            this.acceptable = acceptable;
        }

        @Override
        public String validate(String value) {
            if (value != null && isValid(value) >= 0) {
                return value;
            }
            return null;
        }

        @Override
        public int isValid(String value) {
            return acceptable.test(value) ? 0 : -1;
        }
    }

    static final class StringToIntegerValidation implements Validation<String, Integer> {
        static Validation<String, Integer> INSTANCE = new StringToIntegerValidation();

        private StringToIntegerValidation() {

        }

        @Override
        public Integer validate(String value) {
            if (value != null && isValid(value) >= 0) {
                return Integer.parseInt(value);
            }
            return null;
        }

        @Override
        public int isValid(String value) {
            return StringUtils.canInt(value) ? 0 : -1;
        }
    }

    static final class StringToLongValidation implements Validation<String, Long> {
        static Validation<String, Long> INSTANCE = new StringToLongValidation();

        private StringToLongValidation() {

        }

        @Override
        public Long validate(String value) {
            if (value != null && isValid(value) >= 0) {
                return Long.parseLong(value);
            }
            return null;
        }

        @Override
        public int isValid(String value) {
            return StringUtils.LONG_PATTERN.matcher(value).matches() ? 0 : -1;
        }
    }
}
