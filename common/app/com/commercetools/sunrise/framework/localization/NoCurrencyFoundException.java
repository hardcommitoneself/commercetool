package com.commercetools.sunrise.framework.localization;

public class NoCurrencyFoundException extends RuntimeException {

    public NoCurrencyFoundException(final String message) {
        super(message);
    }

    public NoCurrencyFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
