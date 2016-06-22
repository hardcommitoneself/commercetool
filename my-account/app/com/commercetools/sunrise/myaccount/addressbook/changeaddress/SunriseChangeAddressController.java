package com.commercetools.sunrise.myaccount.addressbook.changeaddress;


import com.commercetools.sunrise.common.contexts.RequestScoped;
import com.commercetools.sunrise.common.controllers.WithOverwriteableTemplateName;
import com.commercetools.sunrise.common.errors.UserFeedback;
import com.commercetools.sunrise.common.reverserouter.AddressBookReverseRouter;
import com.commercetools.sunrise.myaccount.addressbook.AddressFormData;
import com.commercetools.sunrise.myaccount.addressbook.DefaultAddressFormData;
import com.commercetools.sunrise.myaccount.common.MyAccountController;
import com.google.inject.Injector;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.commands.CustomerUpdateCommand;
import io.sphere.sdk.customers.commands.updateactions.ChangeAddress;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.SphereException;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.concurrent.HttpExecution;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.commercetools.sunrise.common.utils.FormUtils.extractUserFeedback;
import static io.sphere.sdk.utils.FutureUtils.exceptionallyCompletedFuture;
import static io.sphere.sdk.utils.FutureUtils.recoverWithAsync;
import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.completedFuture;

@RequestScoped
public abstract class SunriseChangeAddressController extends MyAccountController implements WithOverwriteableTemplateName {

    protected static final Logger logger = LoggerFactory.getLogger(SunriseChangeAddressController.class);

    @Inject
    private Injector injector;
    @Inject
    private FormFactory formFactory;
    @Inject
    private AddressBookReverseRouter addressBookReverseRouter;

    @Override
    public Set<String> getFrameworkTags() {
        final Set<String> frameworkTags = super.getFrameworkTags();
        frameworkTags.addAll(asList("address-book", "change-address", "address"));
        return frameworkTags;
    }

    @Override
    public String getTemplateName() {
        return "my-account-edit-address";
    }

    @AddCSRFToken
    public CompletionStage<Result> show(final String languageTag, final String addressId) {
        return doRequest(() -> injector.getInstance(ChangeAddressActionDataDefaultProvider.class).getActionData(session(), addressId, null)
                .thenComposeAsync(this::showChangeAddress, HttpExecution.defaultContext()));
    }

    @RequireCSRFCheck
    public CompletionStage<Result> process(final String languageTag, final String addressId) {
        return doRequest(() -> {
            Form<DefaultAddressFormData> form = formFactory.form(DefaultAddressFormData.class).bindFromRequest();
            return injector.getInstance(ChangeAddressActionDataDefaultProvider.class).getActionData(session(), addressId, form)
                    .thenComposeAsync(this::processChangeAddress, HttpExecution.defaultContext());
        });
    }

    protected <T extends AddressFormData> CompletionStage<Result> showChangeAddress(final ChangeAddressActionData<T> data) {
        return ifNotNullCustomer(data.getCustomer().orElse(null), notNullCustomer -> data.getOldAddress()
                .map(oldAddress -> showFormWithOriginalAddress(notNullCustomer, oldAddress))
                .orElseGet(() -> handleNotFoundOriginalAddress(notNullCustomer)));
    }

    protected <T extends AddressFormData> CompletionStage<Result> processChangeAddress(final ChangeAddressActionData<T> data) {
        return ifNotNullCustomer(data.getCustomer().orElse(null), customer -> data.getOldAddress()
                .map(oldAddress -> data.getForm()
                        .map(form -> {
                            if (!form.hasErrors()) {
                                return applySubmittedAddress(customer, oldAddress, form.get());
                            } else {
                                return handleInvalidSubmittedAddress(customer, oldAddress, form);
                            }
                        }).orElseGet(() -> showFormWithOriginalAddress(customer, oldAddress)))
                .orElseGet(() -> handleNotFoundOriginalAddress(customer)));
    }

    protected CompletionStage<Result> showFormWithOriginalAddress(final Customer customer, final Address oldAddress) {
        final Form<?> form = obtainFilledForm(oldAddress);
        return completedFuture(ok(renderPage(customer, form)));
    }

    protected CompletionStage<Result> handleNotFoundOriginalAddress(final Customer customer) {  // TODO move up to common controller
        return redirectToAddressBook();
    }

    protected <T extends AddressFormData> CompletionStage<Result> applySubmittedAddress(final Customer customer, final Address oldAddress, final T formData) {
        final CompletionStage<Result> resultStage = changeAddressFromCustomer(customer, oldAddress, formData.extractAddress())
                .thenComposeAsync(updatedCustomer -> displaySuccessfulCustomerUpdate(updatedCustomer, oldAddress, formData), HttpExecution.defaultContext());
        return recoverWithAsync(resultStage, HttpExecution.defaultContext(), throwable ->
                handleFailedCustomerUpdate(customer, oldAddress, formData, throwable));
    }

    protected <T extends AddressFormData> CompletionStage<Result> displaySuccessfulCustomerUpdate(final Customer customer, final Address oldAddress, final T formData) {
        return redirectToAddressBook();
    }

    protected <T extends AddressFormData> CompletionStage<Result> handleFailedCustomerUpdate(final Customer customer, final Address oldAddress,
                                                                                             final T formData, final Throwable throwable) {
        if (throwable.getCause() instanceof SphereException) {
            saveError((SphereException) throwable.getCause());
            final Form<?> form = obtainFilledForm(formData.extractAddress());
            return completedFuture(badRequest(renderPage(customer, form)));
        }
        return exceptionallyCompletedFuture(throwable);
    }

    protected <T extends AddressFormData> CompletionStage<Result> handleInvalidSubmittedAddress(final Customer customer, final Address oldAddress, final Form<T> form) {
        saveFormErrors(form);
        return completedFuture(badRequest(renderPage(customer, form)));
    }

    protected CompletionStage<Customer> changeAddressFromCustomer(final Customer customer, final Address oldAddress, final Address newAddress) {
        final ChangeAddress updateAction = ChangeAddress.ofOldAddressToNewAddress(oldAddress, newAddress);
        return sphere().execute(CustomerUpdateCommand.of(customer, updateAction));
    }

    protected Html renderPage(final Customer customer, final Form<?> form) {
        throw new NotImplementedException("Change address controller page");
        //return renderPage(pageContent, getTemplateName();
    }

    protected Form<?> obtainFilledForm(@Nullable final Address address) {
        final DefaultAddressFormData formData = new DefaultAddressFormData();
        formData.apply(address);
        return formFactory.form(DefaultAddressFormData.class).fill(formData);
    }

    protected final void saveFormErrors(final Form<?> form) {
        final Http.Context context = injector.getInstance(Http.Context.class);
        context.flash().putAll(extractUserFeedback(form));
    }

    private CompletableFuture<Result> redirectToAddressBook() {
        final Call call = addressBookReverseRouter.showMyAddressBook(userContext().languageTag());
        return completedFuture(redirect(call));
    }

    private void saveError(final SphereException sphereException) {
        logger.error("The request to change address to a customer raised an exception", sphereException);
        final Http.Context context = injector.getInstance(Http.Context.class);
        context.flash().put(UserFeedback.ERROR, "Something went wrong, please try again"); // TODO get from i18n
    }
}