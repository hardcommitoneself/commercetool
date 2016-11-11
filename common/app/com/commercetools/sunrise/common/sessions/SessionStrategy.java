package com.commercetools.sunrise.common.sessions;

import com.google.inject.ImplementedBy;

import java.util.Optional;

/**
 * Represents a strategy used to read and write information into the user's session.
 */
@ImplementedBy(SessionCookieStrategy.class)
public interface SessionStrategy {

    /**
     * Finds the value associated with the given key in session.
     * @param key the session key
     * @return the value found in session, or empty if not found
     */
    Optional<String> findValueByKey(final String key);

    /**
     * Saves the value associated with the given key in session, replacing it if it already existed.
     * @param key the session key
     * @param value the value to be set in session
     */
    void overwriteValueByKey(final String key, final String value);

    /**
     * Removes the value associated with the given key from session.
     * @param key the session key
     */
    void removeValueByKey(final String key);
}
