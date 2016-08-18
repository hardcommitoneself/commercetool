package com.commercetools.sunrise.productcatalog.productdetail;

import com.commercetools.sunrise.common.contexts.UserContext;
import com.commercetools.sunrise.hooks.RequestHookContext;
import com.commercetools.sunrise.hooks.requests.ProductProjectionSearchHook;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.search.PriceSelection;
import io.sphere.sdk.products.search.ProductProjectionSearch;
import io.sphere.sdk.search.PagedSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.concurrent.HttpExecution;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static com.commercetools.sunrise.common.utils.PriceUtils.createPriceSelection;

public final class ProductFinderBySlugAndSku implements ProductFinder<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductFinderBySlugAndSku.class);

    @Inject
    private SphereClient sphereClient;
    @Inject
    private UserContext userContext;
    @Inject
    private RequestHookContext hookContext;

    @Override
    public CompletionStage<ProductFinderResult> findProduct(final String productIdentifier,
                                                            final String variantIdentifier) {
        return findProduct(productIdentifier)
                .thenApplyAsync(productOpt -> productOpt
                .map(product -> findVariant(variantIdentifier, product)
                        .map(variant -> ProductFinderResult.of(product, variant))
                        .orElseGet(() -> ProductFinderResult.ofNotFoundVariant(product)))
                .orElseGet(ProductFinderResult::ofNotFoundProduct),
                HttpExecution.defaultContext());
    }

    private CompletionStage<Optional<ProductProjection>> findProduct(final String productIdentifier) {
        return findProductBySlug(productIdentifier, userContext.locale());
    }

    private Optional<ProductVariant> findVariant(final String variantIdentifier, final ProductProjection product) {
        return product.findVariantBySku(variantIdentifier);
    }

    /**
     * Gets a product, uniquely identified by a slug for a given locale.
     * @param slug the product slug
     * @param locale the locale in which you provide the slug
     * @return A CompletionStage of an optionally found ProductProjection
     */
    private CompletionStage<Optional<ProductProjection>> findProductBySlug(final String slug, final Locale locale) {
        final PriceSelection priceSelection = createPriceSelection(userContext);
        final ProductProjectionSearch request = ProductProjectionSearch.ofCurrent()
                .withQueryFilters(m -> m.slug().locale(locale).is(slug))
                .withPriceSelection(priceSelection);
        return sphereClient.execute(ProductProjectionSearchHook.runHook(hookContext, request))
                .thenApplyAsync(PagedSearchResult::head, HttpExecution.defaultContext())
                .whenCompleteAsync((productOpt, t) -> {
                    if (productOpt.isPresent()) {
                        final String productId = productOpt.get().getId();
                        LOGGER.trace("Found product for slug {} in locale {} with ID {}.", slug, locale, productId);
                    } else {
                        LOGGER.trace("No product found for slug {} in locale {}.", slug, locale);
                    }
                }, HttpExecution.defaultContext());
    }
}
