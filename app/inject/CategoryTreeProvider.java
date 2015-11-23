package inject;

import com.google.inject.Provider;
import controllers.RefreshableCategoryTree;
import io.sphere.sdk.categories.CategoryTree;
import io.sphere.sdk.client.SphereClient;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class CategoryTreeProvider implements Provider<CategoryTree> {
    private final SphereClient client;

    @Inject
    private CategoryTreeProvider(final SphereClient client) {
        this.client = client;
    }

    @Override
    public CategoryTree get() {
        try {
            final RefreshableCategoryTree categoryTree = RefreshableCategoryTree.of(client);
            Logger.debug("Provide RefreshableCategoryTree with " + categoryTree.getAllAsFlatList().size() + " categories");
            return categoryTree;
        } catch (RuntimeException e) {
            throw new SunriseInitializationException("Could not fetch categories", e);
        }
    }
}
