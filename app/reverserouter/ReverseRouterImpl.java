package reverserouter;

import com.commercetools.sunrise.common.controllers.ReverseRouter;
import io.sphere.sdk.models.Base;
import play.mvc.Call;
import static demo.routes.*;
import static com.commercetools.sunrise.common.controllers.routes.*;
import static com.commercetools.sunrise.productcatalog.productdetail.routes.*;
import static com.commercetools.sunrise.productcatalog.productoverview.routes.*;
import static com.commercetools.sunrise.shoppingcart.cartdetail.routes.*;
import static com.commercetools.sunrise.shoppingcart.checkout.confirmation.routes.*;
import static com.commercetools.sunrise.shoppingcart.checkout.payment.routes.*;
import static com.commercetools.sunrise.shoppingcart.checkout.shipping.routes.*;
import static com.commercetools.sunrise.shoppingcart.checkout.thankyou.routes.*;

public class ReverseRouterImpl extends Base implements ReverseRouter {

    public ReverseRouterImpl() {
    }

    @Override
    public Call themeAssets(final String file) {
        return controllers.routes.WebJarAssets.at(file);
    }

    @Override
    public Call processChangeLanguageForm() {
        return LocalizationController.changeLanguage();
    }

    @Override
    public Call processChangeCountryForm(final String languageTag) {
        return LocalizationController.changeCountry(languageTag);
    }

    @Override
    public Call homePageCall(final String languageTag) {
        return HomePageController.show(languageTag);
    }

    @Override
    public Call productOverviewPageCall(final String languageTag, final String categorySlug) {
        return ProductOverviewPageController.searchProductsByCategorySlug(languageTag, categorySlug);
    }

    @Override
    public Call productDetailPageCall(final String languageTag, final String productSlug, final String sku) {
        return ProductDetailPageController.showProductBySlugAndSku(languageTag, productSlug, sku);
    }

    @Override
    public Call processSearchProductsForm(final String languageTag) {
        return ProductOverviewPageController.searchProductsBySearchTerm(languageTag);
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

    @Override
    public Call showMyPersonalDetailsForm(final String languageTag) {
        return MyPersonalDetailsPageController.show(languageTag);
    }

    @Override
    public Call processMyPersonalDetailsForm(final String languageTag) {
        return MyPersonalDetailsPageController.process(languageTag);
    }

    @Override
    public Call showMyOrders(final String languageTag) {
        return MyOrdersPageController.list(languageTag, 1);
    }

    @Override
    public Call showMyOrder(final String languageTag, final String orderNumber) {
        return MyOrdersPageController.show(languageTag, orderNumber);
    }
}
