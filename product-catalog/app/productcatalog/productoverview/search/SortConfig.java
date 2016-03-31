package productcatalog.productoverview.search;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.ProductProjection;

import java.util.List;

public class SortConfig extends Base {

    private final String key;
    private final List<SortOption<ProductProjection>> options;
    private final List<String> defaultValue;

    public SortConfig(final String key, final List<SortOption<ProductProjection>> options,
                      final List<String> defaultValue) {
        this.key = key;
        this.options = options;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public List<SortOption<ProductProjection>> getOptions() {
        return options;
    }

    public List<String> getDefaultValue() {
        return defaultValue;
    }
}
