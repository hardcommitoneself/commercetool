package controllers.shoppingcart;

import com.commercetools.sunrise.shoppingcart.CartFinder;
import com.commercetools.sunrise.shoppingcart.add.AddToCartControllerAction;
import com.commercetools.sunrise.shoppingcart.add.AddToCartFormData;
import com.commercetools.sunrise.shoppingcart.CartCreator;
import com.commercetools.sunrise.shoppingcart.add.SunriseAddToCartController;
import com.commercetools.sunrise.shoppingcart.content.viewmodels.CartPageContentFactory;
import com.commercetools.sunrise.framework.components.controllers.PageHeaderControllerComponentSupplier;
import com.commercetools.sunrise.framework.components.controllers.RegisteredComponents;
import com.commercetools.sunrise.framework.controllers.cache.NoCache;
import com.commercetools.sunrise.framework.reverserouters.shoppingcart.cart.CartReverseRouter;
import com.commercetools.sunrise.framework.template.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.framework.template.engine.ContentRenderer;
import com.commercetools.sunrise.sessions.cart.CartOperationsControllerComponentSupplier;
import io.sphere.sdk.carts.Cart;
import play.data.FormFactory;
import play.mvc.Result;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@NoCache
@RegisteredComponents({
        TemplateControllerComponentsSupplier.class,
        PageHeaderControllerComponentSupplier.class,
        CartOperationsControllerComponentSupplier.class
})
public final class AddToCartController extends SunriseAddToCartController {

    private final CartReverseRouter cartReverseRouter;

    @Inject
    public AddToCartController(final ContentRenderer contentRenderer,
                               final FormFactory formFactory,
                               final AddToCartFormData formData,
                               final CartFinder cartFinder,
                               final CartCreator cartCreator,
                               final AddToCartControllerAction controllerAction,
                               final CartPageContentFactory pageContentFactory,
                               final CartReverseRouter cartReverseRouter) {
        super(contentRenderer, formFactory, formData, cartFinder, cartCreator, controllerAction, pageContentFactory);
        this.cartReverseRouter = cartReverseRouter;
    }

    @Nullable
    @Override
    public String getTemplateName() {
        return "cart";
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final Cart updatedCart, final AddToCartFormData formData) {
        return redirectToCall(cartReverseRouter.cartDetailPageCall());
    }
}
