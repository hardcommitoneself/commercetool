package purchase;

import common.contexts.UserContext;
import common.controllers.ControllerDependency;
import common.models.ProductDataConfig;
import common.controllers.SunrisePageData;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import play.data.Form;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Shows the contents of the cart.
 */
public final class CartDetailPageController extends CartController {
    private final ProductDataConfig productDataConfig;

    @Inject
    public CartDetailPageController(final ControllerDependency controllerDependency, final ProductDataConfig productDataConfig) {
        super(controllerDependency);
        this.productDataConfig = productDataConfig;
    }

    public F.Promise<Result> setItemsToCart(final String languageTag) {
        final UserContext userContext = userContext(languageTag);
        final Http.Session session = session();
        final F.Promise<Cart> cartPromise = getOrCreateCart(userContext, session);

        final Form<AddToCartFormData> filledForm = obtainFilledForm();
        if (filledForm.hasErrors()) {
            return F.Promise.pure(badRequest());
        } else {
            final AddLineItem updateAction = AddLineItem.of(filledForm.get().getProductId(), filledForm.get().getVariantId(), filledForm.get().getQuantity());
            return cartPromise.flatMap(cart -> {
                return sphere().execute(CartUpdateCommand.of(cart, updateAction)).map(updatedCart -> {
                    CartSessionUtils.overwriteCartSessionData(updatedCart, session);
                    return ok();
                });
            });
        }
    }

    public F.Promise<Result> show(final String languageTag) {
        final UserContext userContext = userContext(languageTag);
        final F.Promise<Cart> cartPromise = getOrCreateCart(userContext, session());
        return cartPromise.map(cart -> {
            final CartDetailPageContent content = new CartDetailPageContent(cart, productDataConfig, userContext, reverseRouter());
            final SunrisePageData pageData = pageData(userContext, content, ctx());
            return ok(templateService().renderToHtml("cart", pageData, userContext.locales()));
        });
    }

    private Form<AddToCartFormData> obtainFilledForm() {
        return Form.form(AddToCartFormData.class, AddToCartFormData.Validation.class).bindFromRequest(request());
    }
}
