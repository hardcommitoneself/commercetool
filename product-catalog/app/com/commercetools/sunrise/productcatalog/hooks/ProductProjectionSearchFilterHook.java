package com.commercetools.sunrise.productcatalog.hooks;

import com.commercetools.sunrise.hooks.Hook;
import io.sphere.sdk.products.search.ProductProjectionSearch;

public interface ProductProjectionSearchFilterHook extends Hook {
    ProductProjectionSearch filterProductProjectionSearch(ProductProjectionSearch search);
}
