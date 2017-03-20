package controllers.myaccount;

import com.commercetools.sunrise.framework.controllers.cache.NoCache;
import com.commercetools.sunrise.framework.components.controllers.RegisteredComponents;
import com.commercetools.sunrise.framework.reverserouters.myaccount.addressbook.AddressBookReverseRouter;
import com.commercetools.sunrise.framework.reverserouters.myaccount.authentication.AuthenticationReverseRouter;
import com.commercetools.sunrise.framework.template.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.framework.template.engine.TemplateRenderer;
import com.commercetools.sunrise.myaccount.CustomerFinder;
import com.commercetools.sunrise.myaccount.addressbook.AddressFinder;
import com.commercetools.sunrise.myaccount.addressbook.addressbookdetail.viewmodels.AddressBookPageContentFactory;
import com.commercetools.sunrise.myaccount.addressbook.removeaddress.RemoveAddressControllerAction;
import com.commercetools.sunrise.myaccount.addressbook.removeaddress.RemoveAddressFormData;
import com.commercetools.sunrise.myaccount.addressbook.removeaddress.SunriseRemoveAddressController;
import com.commercetools.sunrise.sessions.customer.CustomerOperationsControllerComponentSupplier;
import com.commercetools.sunrise.framework.components.controllers.PageHeaderControllerComponentSupplier;
import io.sphere.sdk.customers.Customer;
import play.data.FormFactory;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@NoCache
@RegisteredComponents({
        TemplateControllerComponentsSupplier.class,
        PageHeaderControllerComponentSupplier.class,
        CustomerOperationsControllerComponentSupplier.class
})
public final class RemoveAddressController extends SunriseRemoveAddressController {

    private final AuthenticationReverseRouter authenticationReverseRouter;
    private final AddressBookReverseRouter addressBookReverseRouter;

    @Inject
    public RemoveAddressController(final TemplateRenderer templateRenderer,
                                   final FormFactory formFactory,
                                   final RemoveAddressFormData formData,
                                   final CustomerFinder customerFinder,
                                   final AddressFinder addressFinder,
                                   final RemoveAddressControllerAction controllerAction,
                                   final AddressBookPageContentFactory pageContentFactory,
                                   final AuthenticationReverseRouter authenticationReverseRouter,
                                   final AddressBookReverseRouter addressBookReverseRouter) {
        super(templateRenderer, formFactory, formData, customerFinder, addressFinder, controllerAction, pageContentFactory);
        this.authenticationReverseRouter = authenticationReverseRouter;
        this.addressBookReverseRouter = addressBookReverseRouter;
    }

    @Override
    public String getTemplateName() {
        return "my-account-address-book";
    }

    @Override
    public CompletionStage<Result> handleNotFoundCustomer() {
        return redirectToCall(authenticationReverseRouter.logInPageCall());
    }

    @Override
    public CompletionStage<Result> handleNotFoundAddress() {
        return redirectToCall(addressBookReverseRouter.addressBookDetailPageCall());
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final Customer updatedCustomer, final RemoveAddressFormData formData) {
        return redirectToCall(addressBookReverseRouter.addressBookDetailPageCall());
    }
}
