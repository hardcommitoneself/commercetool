package com.commercetools.sunrise.play.configuration;

import play.Configuration;

public class SunriseConfigurationException extends RuntimeException {

    public SunriseConfigurationException(final String message) {
        super(message);
    }

    public SunriseConfigurationException(final String message, final String configurationKey) {
        super(generateMessage(message, configurationKey));
    }

    public SunriseConfigurationException(final String message, final String configurationKey, final Throwable throwable) {
        super(generateMessage(message, configurationKey), throwable);
    }

    public SunriseConfigurationException(final String message, final String configurationKey, final String configurationPath) {
        super(generateMessage(message, "\"" + configurationKey + "\" inside " + configurationPath));
    }

    public SunriseConfigurationException(final String message, final String configurationKey, final Configuration configuration) {
        super(generateMessage(message, "\"" + configurationKey + "\" inside " + configuration.asMap()));
    }

    public SunriseConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    private static String generateMessage(final String additionalMessage, final String configurationKey) {
        String message = "Missing or invalid configuration " + configurationKey + " in application.conf";
        if (!additionalMessage.isEmpty()) {
            message += ": " + additionalMessage;
        }
        return message;
    }
}
