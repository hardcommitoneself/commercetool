package wedecidelater;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import framework.MultiControllerSunriseComponentResolver;
import framework.MultiControllerSunriseComponentResolverBuilder;
import shoppingcart.checkout.CheckoutCommonComponent;
import wedecidelatercommon.MiniCartControllerComponent;

public class ComponentsModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    public MultiControllerSunriseComponentResolver foo() {
        return new MultiControllerSunriseComponentResolverBuilder()
                .add(CheckoutCommonComponent.class, controller -> controller.getFrameworkTags().contains("checkout"))
                .add(MiniCartControllerComponent .class, controller -> !controller.getFrameworkTags().contains("checkout"))
                .build();
    }
}
