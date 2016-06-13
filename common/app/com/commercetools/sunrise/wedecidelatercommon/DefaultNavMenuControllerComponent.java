package com.commercetools.sunrise.wedecidelatercommon;

import com.commercetools.sunrise.common.pages.SunrisePageData;
import com.commercetools.sunrise.common.hooks.SunrisePageDataHook;
import com.commercetools.sunrise.common.models.NavMenuDataFactory;
import com.commercetools.sunrise.framework.ControllerComponent;

import javax.inject.Inject;

public class DefaultNavMenuControllerComponent implements ControllerComponent, SunrisePageDataHook {
    @Inject
    private NavMenuDataFactory navMenuDataFactory;

    @Override
    public void acceptSunrisePageData(final SunrisePageData sunrisePageData) {
        sunrisePageData.getHeader().setNavMenu(navMenuDataFactory.create());
    }
}
