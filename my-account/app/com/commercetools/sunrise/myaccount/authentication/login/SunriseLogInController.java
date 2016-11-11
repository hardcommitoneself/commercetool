package com.commercetools.sunrise.myaccount.authentication.login;

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
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.customers.commands.CustomerSignInCommand;
import io.sphere.sdk.customers.errors.CustomerInvalidCredentials;
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
@IntroducingMultiControllerComponents(SunriseLogInHeroldComponent.class)
public abstract class SunriseLogInController extends SunriseFrameworkController implements WithTemplateName, WithFormFlow<LogInFormData, Void, CustomerSignInResult> {

    private static final Logger logger = LoggerFactory.getLogger(SunriseLogInController.class);

    @Override
    public Set<String> getFrameworkTags() {
        return new HashSet<>(asList("my-account", "log-in", "authentication", "customer", "user"));
    }

    @Override
    public String getTemplateName() {
        return "my-account-login";
    }

    @Override
    public Class<? extends LogInFormData> getFormDataClass() {
        return DefaultLogInFormData.class;
    }

    @SunriseRoute("showLogInForm")
    public CompletionStage<Result> show(final String languageTag) {
        return doRequest(() -> {
            logger.debug("show sign up form in locale={}", languageTag);
            return showForm(null);
        });
    }

    @SunriseRoute("processLogInForm")
    public CompletionStage<Result> process(final String languageTag) {
        return doRequest(() -> {
            logger.debug("process sign up form in locale={}", languageTag);
            return validateForm(null);
        });
    }

    @Override
    public CompletionStage<? extends CustomerSignInResult> doAction(final LogInFormData formData, final Void context) {
        final String cartId = injector().getInstance(CartInSession.class).findCartId().orElse(null);
        final CustomerSignInCommand signInCommand = CustomerSignInCommand.of(formData.getUsername(), formData.getPassword(), cartId);
        return sphere().execute(signInCommand);
    }

    @Override
    public CompletionStage<Result> handleClientErrorFailedAction(final Form<? extends LogInFormData> form, final Void context, final ClientErrorException clientErrorException) {
        if (isInvalidCredentialsError(clientErrorException)) {
            saveFormError(form, "Invalid credentials"); // TODO i18n
        } else {
            saveUnexpectedFormError(form, clientErrorException, logger);
        }
        return asyncBadRequest(renderPage(form, context, null));
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final LogInFormData formData, final Void context, final CustomerSignInResult result) {
        CustomerSignInResultLoadedHook.runHook(hooks(), result);
        return redirectToMyPersonalDetails();
    }

    @Override
    public CompletionStage<Html> renderPage(final Form<? extends LogInFormData> form, final Void context, @Nullable final CustomerSignInResult result) {
        final AuthenticationPageContent pageContent = injector().getInstance(AuthenticationPageContentFactory.class).createWithLogInForm(form);
        return renderPageWithTemplate(pageContent, getTemplateName());
    }

    @Override
    public void fillFormData(final LogInFormData formData, final Void context) {
        // Do nothing
    }

    protected final CompletionStage<Result> redirectToMyPersonalDetails() {
        final Call call = injector().getInstance(MyPersonalDetailsReverseRouter.class).myPersonalDetailsPageCall(userContext().languageTag());
        return completedFuture(redirect(call));
    }

    protected final boolean isInvalidCredentialsError(final ClientErrorException clientErrorException) {
        return clientErrorException instanceof ErrorResponseException
                && ((ErrorResponseException) clientErrorException).hasErrorCode(CustomerInvalidCredentials.CODE);
    }
}
