package productcatalog.hooks;

import common.hooks.Hook;
import io.sphere.sdk.categories.Category;

import java.util.concurrent.CompletionStage;

public interface SingleCategoryHook extends Hook {
    CompletionStage<?> onSingleCategoryLoaded(final Category category);
}

