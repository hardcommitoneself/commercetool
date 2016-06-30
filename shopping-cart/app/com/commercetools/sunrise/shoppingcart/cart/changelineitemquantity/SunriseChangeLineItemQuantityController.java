package com.commercetools.sunrise.shoppingcart.cart.changelineitemquantity;

import com.commercetools.sunrise.common.controllers.FormBindingTrait;
import com.commercetools.sunrise.common.controllers.ReverseRouter;
import com.commercetools.sunrise.common.forms.UserFeedback;
import com.commercetools.sunrise.shoppingcart.common.SunriseFrameworkCartController;
import com.google.inject.Injector;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.ChangeLineItemQuantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import static io.sphere.sdk.utils.FutureUtils.recoverWithAsync;
import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static play.libs.concurrent.HttpExecution.defaultContext;

public abstract class SunriseChangeLineItemQuantityController extends SunriseFrameworkCartController implements FormBindingTrait<ChangeLineItemQuantityFormData> {
    private static final Logger logger = LoggerFactory.getLogger(SunriseChangeLineItemQuantityController.class);

    @Inject
    private ReverseRouter reverseRouter;
    @Inject
    private Injector injector;

    @RequireCSRFCheck
    public CompletionStage<Result> changeLineItemQuantity(final String languageTag) {
        return doRequest(() -> {
            return bindForm().thenComposeAsync(filledForm -> {
                return filledForm.hasErrors() ? handleInvalidForm(filledForm) : handleValidForm(filledForm);
            }, defaultContext());
        });
    }

    @Override
    public Class<? extends ChangeLineItemQuantityFormData> getFormDataClass() {
        return DefaultChangeLineItemQuantityFormData.class;
    }

    private CompletionStage<Result> handleValidForm(final Form<? extends ChangeLineItemQuantityFormData> form) {
        return getOrCreateCart()
                .thenComposeAsync(cart -> {
                    final ChangeLineItemQuantityFormData formData = form.get();
                    final String lineItemId = formData.getLineItemId();
                    final Long quantity = formData.getQuantity();
                    final CompletionStage<Result> resultStage = changeLineItemQuantity(cart, lineItemId, quantity)
                            .thenComposeAsync(this::handleSuccessfulCartChange, defaultContext());
                    return recoverWithAsync(resultStage, defaultContext(), throwable ->
                            handleChangeLineItemQuantityError(throwable, form, cart));
                }, defaultContext());
    }

    protected CompletionStage<Result> handleInvalidForm(final Form<? extends ChangeLineItemQuantityFormData> form) {
        injector.getInstance(UserFeedback.class).addErrors(form);
        return completedFuture(redirect(reverseRouter.showCart(userContext().languageTag())));
    }

    protected CompletionStage<Cart> changeLineItemQuantity(final Cart cart, final String lineItemId, final long quantity) {
        final ChangeLineItemQuantity changeLineItemQuantity = ChangeLineItemQuantity.of(lineItemId, quantity);
        return sphere().execute(CartUpdateCommand.of(cart, changeLineItemQuantity));
    }

    protected CompletionStage<Result> handleSuccessfulCartChange(final Cart cart) {
        overrideCartSessionData(cart);
        return completedFuture(redirect(reverseRouter.showCart(userContext().languageTag())));
    }

    protected CompletionStage<Result> handleChangeLineItemQuantityError(final Throwable throwable,
                                                                        final Form<? extends ChangeLineItemQuantityFormData> form,
                                                                        final Cart cart) {
        injector.getInstance(UserFeedback.class).addErrors("The request to change line item quantity raised an exception");// TODO get from i18n
        return completedFuture(redirect(reverseRouter.showCart(userContext().languageTag())));
    }

    @Override
    public Set<String> getFrameworkTags() {
        return new HashSet<>(asList("cart"));
    }
}
