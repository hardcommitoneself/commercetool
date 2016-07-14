package com.commercetools.sunrise.shoppingcart.checkout.confirmation;

import com.commercetools.sunrise.common.contexts.UserContext;
import com.commercetools.sunrise.common.template.i18n.I18nIdentifier;
import com.commercetools.sunrise.common.template.i18n.I18nResolver;
import com.commercetools.sunrise.shoppingcart.CartLikeBeanFactory;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.models.Base;
import play.data.Form;

import javax.inject.Inject;

public class CheckoutConfirmationPageContentFactory extends Base {

    @Inject
    private CartLikeBeanFactory cartLikeBeanFactory;
    @Inject
    private UserContext userContext;
    @Inject
    private I18nResolver i18nResolver;

    public CheckoutConfirmationPageContent create(final Form<?> form, final Cart cart) {
        return billBean(new CheckoutConfirmationPageContent(), form, cart);
    }

    protected <T extends CheckoutConfirmationPageContent> T billBean(final T pageContent, final Form<?> form, final Cart cart) {
        fillCart(pageContent, cart);
        fillTitle(pageContent, cart);
        fillForm(pageContent, form);
        return pageContent;
    }

    public void fillForm(final CheckoutConfirmationPageContent pageContent, final Form<?> form) {
        pageContent.setCheckoutForm(form);
    }

    public void fillTitle(final CheckoutConfirmationPageContent pageContent, final Cart cart) {
        pageContent.setTitle(i18nResolver.getOrEmpty(userContext.locales(), I18nIdentifier.of("checkout:confirmationPage.title")));
    }

    public void fillCart(final CheckoutConfirmationPageContent pageContent, final Cart cart) {
        pageContent.setCart(cartLikeBeanFactory.create(cart));
    }
}
