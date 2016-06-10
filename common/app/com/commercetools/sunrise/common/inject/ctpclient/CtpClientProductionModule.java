package com.commercetools.sunrise.common.inject.ctpclient;

import com.google.inject.AbstractModule;
import io.sphere.sdk.client.SphereClient;

import javax.inject.Singleton;

/**
 * Configuration for the Guice {@link com.google.inject.Injector} which shall be used in production.
 */
public class CtpClientProductionModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SphereClient.class).toProvider(SphereClientProvider.class).in(Singleton.class);
    }
}
