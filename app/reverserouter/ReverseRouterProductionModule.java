package reverserouter;

import com.google.inject.AbstractModule;
import com.commercetools.sunrise.common.controllers.ReverseRouter;
import wedecidelatercommon.CheckoutReverseRouter;
import wedecidelatercommon.HomeReverseRouter;
import wedecidelatercommon.ProductReverseRouter;

public class ReverseRouterProductionModule extends AbstractModule {

    @Override
    protected void configure() {
        final ReverseRouterImpl reverseRouter = new ReverseRouterImpl();
        bind(ReverseRouter.class).toInstance(reverseRouter);
        bind(ProductReverseRouter.class).toInstance(reverseRouter);
        bind(CheckoutReverseRouter.class).toInstance(reverseRouter);
        bind(HomeReverseRouter.class).toInstance(reverseRouter);
    }
}
