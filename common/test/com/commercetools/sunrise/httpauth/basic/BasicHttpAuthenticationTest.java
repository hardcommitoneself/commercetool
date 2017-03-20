package com.commercetools.sunrise.httpauth.basic;

import org.junit.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicHttpAuthenticationTest {

    private static final String MY_REALM = "My Realm";
    private static final String CREDENTIALS = "username:password";
    private static final BasicHttpAuthentication BASIC_AUTH = new BasicHttpAuthentication(MY_REALM, CREDENTIALS);

    @Test
    public void isAuthorizedWithCorrectCredentials() throws Exception {
        assertThat(BASIC_AUTH.isAuthorized("Basic " + encode(CREDENTIALS))).isTrue();
    }

    @Test
    public void isNotAuthorizedWithDecodedCredentials() throws Exception {
        assertThat(BASIC_AUTH.isAuthorized("Basic " + CREDENTIALS)).isFalse();
    }

    @Test
    public void isNotAuthorizedWithInvalidCredentials() throws Exception {
        assertThat(BASIC_AUTH.isAuthorized("Basic " + encode("username:wrong"))).isFalse();
    }

    @Test
    public void isNotAuthorizedWithoutBasicHeader() throws Exception {
        assertThat(BASIC_AUTH.isAuthorized(encode(CREDENTIALS))).isFalse();
    }

    private static String encode(final String credentials) {
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
