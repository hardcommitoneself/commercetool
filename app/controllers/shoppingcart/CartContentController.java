package controllers.shoppingcart;

import com.commercetools.sunrise.shoppingcart.content.SunriseCartContentController;
import com.commercetools.sunrise.framework.controllers.cache.NoCache;
import com.commercetools.sunrise.framework.components.controllers.RegisteredComponents;
import com.commercetools.sunrise.framework.template.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.framework.template.engine.ContentRenderer;
import com.commercetools.sunrise.sessions.cart.CartOperationsControllerComponentSupplier;
import com.commercetools.sunrise.shoppingcart.CartFinder;
import com.commercetools.sunrise.shoppingcart.content.viewmodels.CartPageContentFactory;
import com.commercetools.sunrise.framework.components.controllers.PageHeaderControllerComponentSupplier;

import javax.annotation.Nullable;
import javax.inject.Inject;

@NoCache
@RegisteredComponents({
        TemplateControllerComponentsSupplier.class,
        PageHeaderControllerComponentSupplier.class,
        CartOperationsControllerComponentSupplier.class
})
public final class CartContentController extends SunriseCartContentController {

    @Inject
    public CartContentController(final ContentRenderer contentRenderer,
                                 final CartFinder cartFinder,
                                 final CartPageContentFactory pageContentFactory) {
        super(contentRenderer, cartFinder, pageContentFactory);
    }

    @Nullable
    @Override
    public String getTemplateName() {
        return "cart";
    }
}
