package productcatalog.hooks;

import common.hooks.Hook;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;

import java.util.concurrent.CompletionStage;

public interface SingleProductVariantHook extends Hook {
    CompletionStage<?> onSingleProductVariantLoaded(final ProductProjection product, final ProductVariant variant);
}
