package com.commercetools.sunrise.common.sessions;

import com.commercetools.sunrise.common.contexts.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Http;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Uses a session cookie to store information about the user.
 */
@RequestScoped
public class SessionCookieStrategy implements SessionStrategy {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final Http.Session session;

    @Inject
    public SessionCookieStrategy(final Http.Session session) {
        this.session = session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> findValueByKey(final String key) {
        final Optional<String> value = Optional.ofNullable(session.get(key));
        if (value.isPresent()) {
            logger.debug("Loaded from session \"{}\" = {}", key, value.get());
        } else {
            logger.debug("Not found in session \"{}\"", key);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void overwriteValueByKey(final String key, final String value) {
        session.put(key, value);
        logger.debug("Saved in session \"{}\" = {}", key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeValueByKey(final String key) {
        final String oldValue = session.remove(key);
        logger.debug("Removed from session \"{}\" = {}", key, oldValue);
    }
}