package reverserouter;

import common.controllers.ReverseRouter;
import io.sphere.sdk.models.Base;
import play.mvc.Call;

import static productcatalog.home.routes.*;
import static productcatalog.productdetail.routes.*;
import static productcatalog.productoverview.routes.*;
import static shoppingcart.cartdetail.routes.*;
import static shoppingcart.checkout.address.routes.*;
import static shoppingcart.checkout.confirmation.routes.*;
import static shoppingcart.checkout.payment.routes.*;
import static shoppingcart.checkout.shipping.routes.*;
import static shoppingcart.checkout.thankyou.routes.*;
import static myaccount.login.routes.*;

public class ReverseRouterImpl extends Base implements ReverseRouter {

    public ReverseRouterImpl() {
    }

    @Override
    public Call themeAssets(final String file) {
        return controllers.routes.WebJarAssets.at(file);
    }

    @Override
    public Call processChangeLanguageForm() {
        return common.routes.ApplicationController.changeLanguage();
    }

    @Override
    public Call processChangeCountryForm(final String languageTag) {
        return common.routes.ApplicationController.changeCountry(languageTag);
    }

    @Override
    public Call showHome(final String languageTag) {
        return HomeController.show(languageTag);
    }

    @Override
    public Call showCategory(final String languageTag, final String categorySlug) {
        return ProductOverviewPageController.show(languageTag, 1, categorySlug);
    }

    @Override
    public Call showProduct(final String languageTag, final String productSlug, final String sku) {
        return ProductDetailPageController.show(languageTag, productSlug, sku);
    }

    @Override
    public Call processSearchProductsForm(final String languageTag) {
        return ProductOverviewPageController.search(languageTag, 1);
    }

    @Override
    public Call showCart(final String languageTag) {
        return CartDetailPageController.show(languageTag);
    }

    @Override
    public Call processAddProductToCartForm(final String languageTag) {
        return CartDetailPageController.addProductToCart(languageTag);
    }

    @Override
    public Call processChangeLineItemQuantityForm(final String languageTag) {
        return CartDetailPageController.changeLineItemQuantity(languageTag);
    }

    @Override
    public Call processDeleteLineItemForm(final String languageTag) {
        return CartDetailPageController.removeLineItem(languageTag);
    }

    @Override
    public Call showCheckoutAddressesForm(final String languageTag) {
        return CheckoutAddressPageController.show(languageTag);
    }

    @Override
    public Call processCheckoutAddressesForm(final String languageTag) {
        return CheckoutAddressPageController.process(languageTag);
    }

    @Override
    public Call showCheckoutShippingForm(final String languageTag) {
        return CheckoutShippingPageController.show(languageTag);
    }

    @Override
    public Call processCheckoutShippingForm(final String languageTag) {
        return CheckoutShippingPageController.process(languageTag);
    }

    @Override
    public Call showCheckoutPaymentForm(final String languageTag) {
        return CheckoutPaymentPageController.show(languageTag);
    }

    @Override
    public Call processCheckoutPaymentForm(final String languageTag) {
        return CheckoutPaymentPageController.process(languageTag);
    }

    @Override
    public Call showCheckoutConfirmationForm(final String languageTag) {
        return CheckoutConfirmationPageController.show(languageTag);
    }

    @Override
    public Call processCheckoutConfirmationForm(final String languageTag) {
        return CheckoutConfirmationPageController.process(languageTag);
    }

    @Override
    public Call showCheckoutThankYou(final String languageTag) {
        return CheckoutThankYouPageController.show(languageTag);
    }

    @Override
    public Call showLogInForm(final String languageTag) {
        return LogInPageController.show(languageTag);
    }

    @Override
    public Call processLogInForm(final String languageTag) {
        return LogInPageController.processLogIn(languageTag);
    }

    @Override
    public Call processSignUpForm(final String languageTag) {
        return LogInPageController.processSignUp(languageTag);
    }

    @Override
    public Call processLogOut(final String languageTag) {
        return LogInPageController.processLogOut(languageTag);
    }
}
