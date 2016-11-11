package com.commercetools.sunrise.myaccount.authentication.signup;

import com.commercetools.sunrise.common.contexts.RequestScoped;
import com.commercetools.sunrise.common.controllers.SunriseFrameworkController;
import com.commercetools.sunrise.common.controllers.WithFormFlow;
import com.commercetools.sunrise.common.controllers.WithTemplateName;
import com.commercetools.sunrise.common.reverserouter.MyPersonalDetailsReverseRouter;
import com.commercetools.sunrise.framework.annotations.IntroducingMultiControllerComponents;
import com.commercetools.sunrise.framework.annotations.SunriseRoute;
import com.commercetools.sunrise.hooks.events.CustomerSignInResultLoadedHook;
import com.commercetools.sunrise.myaccount.authentication.AuthenticationPageContent;
import com.commercetools.sunrise.myaccount.authentication.AuthenticationPageContentFactory;
import com.commercetools.sunrise.shoppingcart.CartInSession;
import io.sphere.sdk.client.ClientErrorException;
import io.sphere.sdk.client.ErrorResponseException;
import io.sphere.sdk.customers.CustomerDraft;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.customers.commands.CustomerCreateCommand;
import io.sphere.sdk.models.errors.DuplicateFieldError;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.completedFuture;

@RequestScoped
@IntroducingMultiControllerComponents(SunriseSignUpHeroldComponent.class)
public abstract class SunriseSignUpController extends SunriseFrameworkController implements WithTemplateName, WithFormFlow<SignUpFormData, Void, CustomerSignInResult> {

    private static final Logger logger = LoggerFactory.getLogger(SunriseSignUpController.class);

    @Override
    public Set<String> getFrameworkTags() {
        return new HashSet<>(asList("my-account", "sign-up", "authentication", "customer", "user"));
    }

    @Override
    public String getTemplateName() {
        return "my-account-login";
    }

    @Override
    public Class<? extends SignUpFormData> getFormDataClass() {
        return DefaultSignUpFormData.class;
    }

    public CompletionStage<Result> show(final String languageTag) {
        return doRequest(() -> {
            logger.debug("show sign up form in locale={}", languageTag);
            return showForm(null);
        });
    }

    @SunriseRoute("processSignUpForm")
    public CompletionStage<Result> process(final String languageTag) {
        return doRequest(() -> {
            logger.debug("process sign up form in locale={}", languageTag);
            return validateForm(null);
        });
    }

    @Override
    public CompletionStage<? extends CustomerSignInResult> doAction(final SignUpFormData formData, final Void context) {
        final String cartId = injector().getInstance(CartInSession.class).findCartId().orElse(null);
        final CustomerDraft customerDraft = formData.toCustomerDraftBuilder()
                .customerNumber(generateCustomerNumber())
                .anonymousCartId(cartId)
                .build();
        final CustomerCreateCommand customerCreateCommand = CustomerCreateCommand.of(customerDraft);
        return sphere().execute(customerCreateCommand);
    }

    @Override
    public CompletionStage<Result> handleClientErrorFailedAction(final Form<? extends SignUpFormData> form, final Void context, final ClientErrorException clientErrorException) {
        if (isDuplicatedEmailFieldError(clientErrorException)) {
            saveFormError(form, "A user with this email already exists"); // TODO i18n
        } else {
            saveUnexpectedFormError(form, clientErrorException, logger);
        }
        return asyncBadRequest(renderPage(form, context, null));
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final SignUpFormData formData, final Void context, final CustomerSignInResult result) {
        CustomerSignInResultLoadedHook.runHook(hooks(), result);
        return redirectToMyAccount();
    }

    @Override
    public CompletionStage<Html> renderPage(final Form<? extends SignUpFormData> form, final Void context, @Nullable final CustomerSignInResult result) {
        final AuthenticationPageContent pageContent = injector().getInstance(AuthenticationPageContentFactory.class).createWithSignUpForm(form);
        return renderPageWithTemplate(pageContent, getTemplateName());
    }

    @Override
    public void fillFormData(final SignUpFormData formData, final Void context) {
        // Do nothing
    }

    protected String generateCustomerNumber() {
        return RandomStringUtils.randomNumeric(6);
    }

    protected final boolean isDuplicatedEmailFieldError(final ClientErrorException clientErrorException) {
        return clientErrorException instanceof ErrorResponseException
                && ((ErrorResponseException) clientErrorException).getErrors().stream()
                    .filter(error -> error.getCode().equals(DuplicateFieldError.CODE))
                    .map(error -> error.as(DuplicateFieldError.class).getField())
                    .filter(duplicatedField -> duplicatedField != null && duplicatedField.equals("email"))
                    .findAny()
                    .isPresent();
    }

    protected CompletionStage<Result> redirectToMyAccount() {
        final Call call = injector().getInstance(MyPersonalDetailsReverseRouter.class).myPersonalDetailsPageCall(userContext().languageTag());
        return completedFuture(redirect(call));
    }
}
