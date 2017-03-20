package com.commercetools.sunrise.framework.hooks;

import com.commercetools.sunrise.framework.components.SunriseComponent;
import com.commercetools.sunrise.framework.injection.RequestScoped;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.utils.CompletableFutureUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.concurrent.HttpExecution;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.sphere.sdk.utils.CompletableFutureUtils.successful;
import static java.util.stream.Collectors.toList;

@RequestScoped
final class HookContextImpl extends Base implements HookContext {

    private static final Logger hookRunnerLogger = LoggerFactory.getLogger(HookRunner.class);
    private static final Logger componentRegistryLogger = LoggerFactory.getLogger(ComponentRegistry.class);

    private final List<Object> controllerComponents = new LinkedList<>();
    private final List<CompletionStage<Object>> asyncHooksCompletionStages = new LinkedList<>();

    @Override
    public <T extends Hook> CompletionStage<?> runEventHook(final Class<T> hookClass, final Function<T, CompletionStage<?>> f) {
        hookRunnerLogger.debug("Running EventHook {}", hookClass.getSimpleName());
        //TODO throw a helpful NPE if component returns null instead of CompletionStage
        final List<CompletionStage<Void>> collect = controllerComponents.stream()
                .filter(x -> hookClass.isAssignableFrom(x.getClass()))
                .map(hook -> f.apply((T) hook))
                .map(stage -> (CompletionStage<Void>) stage)
                .collect(toList());
        final CompletionStage<?> listCompletionStage = CompletableFutureUtils.listOfFuturesToFutureOfList(collect);
        final CompletionStage<Object> result = listCompletionStage.thenApply(z -> null);
        asyncHooksCompletionStages.add(result);
        return result;
    }

    @Override
    public <T extends Hook, R> CompletionStage<R> runActionHook(final Class<T> hookClass, final BiFunction<T, R, CompletionStage<R>> f, final R param) {
        hookRunnerLogger.debug("Running ActionHook {}", hookClass.getSimpleName());
        CompletionStage<R> result = successful(param);
        final List<T> applicableHooks = controllerComponents.stream()
                .filter(x -> hookClass.isAssignableFrom(x.getClass()))
                .map(x -> (T) x)
                .collect(Collectors.toList());
        for (final T hook : applicableHooks) {
            result = result.thenComposeAsync(res -> f.apply(hook, res), HttpExecution.defaultContext());
        }
        return result;
    }

    @Override
    public <H extends Hook, R> R runUnaryOperatorHook(final Class<H> hookClass, final BiFunction<H, R, R> f, final R param) {
        hookRunnerLogger.debug("Running UnaryOperatorHook {}", hookClass.getSimpleName());
        R result = param;
        final List<H> applicableHooks = controllerComponents.stream()
                .filter(x -> hookClass.isAssignableFrom(x.getClass()))
                .map(x -> (H) x)
                .collect(Collectors.toList());
        for (final H hook : applicableHooks) {
            result = f.apply(hook, result);
        }
        return result;
    }

    @Override
    public <H extends Hook> void runConsumerHook(final Class<H> hookClass, final Consumer<H> consumer) {
        hookRunnerLogger.debug("Running ConsumerHook {}", hookClass.getSimpleName());
        controllerComponents.stream()
                .filter(x -> hookClass.isAssignableFrom(x.getClass()))
                .forEach(action -> consumer.accept((H) action));
    }

    @Override
    public CompletionStage<Object> waitForHookedComponentsToFinish() {
        return CompletableFutureUtils.listOfFuturesToFutureOfList(asyncHooksCompletionStages).thenApply(list -> null);
    }

    @Override
    public void add(final SunriseComponent component) {
        componentRegistryLogger.debug("Registered component {}", component.getClass().getCanonicalName());
        controllerComponents.add(component);
    }
}
