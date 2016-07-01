package com.commercetools.sunrise.shoppingcart.checkout.address;

import com.commercetools.sunrise.common.contexts.RequestScoped;
import com.commercetools.sunrise.common.controllers.SimpleFormBindingControllerTrait;
import com.commercetools.sunrise.common.controllers.WithOverwriteableTemplateName;
import com.commercetools.sunrise.common.reverserouter.CheckoutReverseRouter;
import com.commercetools.sunrise.hooks.CartUpdateCommandFilterHook;
import com.commercetools.sunrise.hooks.PrimaryCartUpdatedHook;
import com.commercetools.sunrise.shoppingcart.common.SunriseFrameworkCartController;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.SetBillingAddress;
import io.sphere.sdk.carts.commands.updateactions.SetCountry;
import io.sphere.sdk.carts.commands.updateactions.SetCustomerEmail;
import io.sphere.sdk.carts.commands.updateactions.SetShippingAddress;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.models.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.concurrent.HttpExecution;
import play.mvc.Call;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static io.sphere.sdk.utils.FutureUtils.exceptionallyCompletedFuture;
import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.completedFuture;

@RequestScoped
public abstract class SunriseCheckoutAddressController extends SunriseFrameworkCartController implements WithOverwriteableTemplateName, SimpleFormBindingControllerTrait<CheckoutAddressFormData, Cart, Cart> {
    protected static final Logger logger = LoggerFactory.getLogger(SunriseCheckoutAddressController.class);

    @Inject
    protected CheckoutAddressPageContentFactory pageContentFactory;

    @Override
    public Class<? extends CheckoutAddressFormData> getFormDataClass() {
        return DefaultCheckoutAddressFormData.class;
    }

    @AddCSRFToken
    public CompletionStage<Result> show(final String languageTag) {
        return doRequest(() -> getOrCreateCart().thenComposeAsync(this::showForm, HttpExecution.defaultContext()));
    }

    @RequireCSRFCheck
    @SuppressWarnings("unused")
    public CompletionStage<Result> process(final String languageTag) {
        return doRequest(() -> getOrCreateCart().thenComposeAsync(this::validateForm, HttpExecution.defaultContext()));
    }

    @Override
    public CompletionStage<Result> showForm(final Cart cart) {
        final Form<? extends CheckoutAddressFormData> filledForm = createFilledForm(cart);
        return asyncOk(renderPage(filledForm, cart));
    }

    @Override
    public CompletionStage<Result> handleInvalidForm(final Form<? extends CheckoutAddressFormData> form, final Cart cart) {
        saveFormErrors(form);
        return asyncBadRequest(renderPage(form, cart));
    }

    @Override
    public CompletionStage<? extends Cart> doAction(final CheckoutAddressFormData formData, final Cart cart) {
        return setAddressToCart(cart, formData.toShippingAddress(), formData.toBillingAddress());
    }

    @Override
    public CompletionStage<Result> handleFailedAction(final Form<? extends CheckoutAddressFormData> form, final Cart cart, final Throwable throwable) {
        return exceptionallyCompletedFuture(throwable);
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final CheckoutAddressFormData formData, final Cart oldCart, final Cart updatedCart) {
        final Call call = injector().getInstance(CheckoutReverseRouter.class).checkoutShippingPageCall(userContext().languageTag());
        return completedFuture(redirect(call));
    }

    @Override
    public Form<? extends CheckoutAddressFormData> createForm() {
        return isBillingDifferent()
                ? formFactory().form(getFormDataClass(), BillingAddressDifferentToShippingAddressGroup.class)
                : formFactory().form(getFormDataClass());
    }

    protected Form<? extends CheckoutAddressFormData> createFilledForm(final Cart cart) {
        final DefaultCheckoutAddressFormData formData = new DefaultCheckoutAddressFormData();
        if (cart.getShippingAddress() != null) {
            formData.applyShippingAddress(cart.getShippingAddress());
        }
        if (cart.getBillingAddress() != null) {
            formData.applyBillingAddress(cart.getBillingAddress());
            formData.setBillingAddressDifferentToBillingAddress(true);
        }
        return formFactory().form(DefaultCheckoutAddressFormData.class).fill(formData);
    }

    @Override
    public String getTemplateName() {
        return "checkout-address";
    }

    @Override
    public Set<String> getFrameworkTags() {
        return new HashSet<>(asList("checkout", "checkout-address"));
    }

    protected CompletionStage<Html> renderPage(final Form<? extends CheckoutAddressFormData> form, final Cart cart) {
        final CheckoutAddressPageContent pageContent = injector().getInstance(CheckoutAddressPageContentFactory.class).create(form, cart);
        return renderPage(pageContent, getTemplateName());
    }

    private CompletionStage<Cart> setAddressToCart(final Cart cart,
                                                   final Address shippingAddress,
                                                   @Nullable final Address billingAddress) {
        final List<UpdateAction<Cart>> updateActions = new ArrayList<>();
        updateActions.add(SetCountry.of(shippingAddress.getCountry()));
        updateActions.add(SetShippingAddress.of(shippingAddress));
        updateActions.add(SetBillingAddress.of(billingAddress));
        Optional.ofNullable(shippingAddress.getEmail())
                .ifPresent(email -> updateActions.add(SetCustomerEmail.of(email)));
        return executeSphereRequestWithHooks(CartUpdateCommand.of(cart, updateActions),
                CartUpdateCommandFilterHook.class, CartUpdateCommandFilterHook::filterCartUpdateCommand,
                PrimaryCartUpdatedHook.class, PrimaryCartUpdatedHook::onPrimaryCartUpdated);
    }

    private boolean isBillingDifferent() {
        final String flagFieldName = "billingAddressDifferentToBillingAddress";
        final String fieldValue = formFactory().form().bindFromRequest().get(flagFieldName);
        return "true".equals(fieldValue);
    }
}