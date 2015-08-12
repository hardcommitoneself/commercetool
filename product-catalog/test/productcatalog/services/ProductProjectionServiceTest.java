package productcatalog.services;

import common.categories.CategoryUtils;
import common.products.ProductUtils;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.client.SphereRequest;
import io.sphere.sdk.json.JsonUtils;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.queries.PagedQueryResult;
import org.junit.Test;
import play.libs.F;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductProjectionServiceTest {

    private static final int DEFAULT_TIMEOUT = 1000;

    @Test
    public void testFindVariantBySku() throws Exception {
        final ProductProjectionService service = new ProductProjectionService(null);
        final ProductProjection product = JsonUtils.readObjectFromResource("product.json", ProductProjection.typeReference());
        final String masterSku = "M0E20000000DSB9";
        final String otherSku = "M0E20000000DSBA";

        assertThat(service.findVariantBySku(product, masterSku).get().getSku()).contains(masterSku);
        assertThat(service.findVariantBySku(product, otherSku).get().getSku()).contains(otherSku);
        assertThat(service.findVariantBySku(product, "")).isEmpty();
    }

    @Test
    public void testGetSuggestions() {
        final PlayJavaSphereClient sphereClient = new PlayJavaSphereClient() {
            @Override
            public void close() {

            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> F.Promise<T> execute(SphereRequest<T> sphereRequest) {
                final PagedQueryResult<ProductProjection> queryResult = ProductUtils.getQueryResult("productProjectionQueryResult.json");
                return (F.Promise<T>) F.Promise.pure(queryResult);
            }
        };
        final ProductProjectionService service = new ProductProjectionService(sphereClient);
        final List<Category> categories = CategoryUtils.getQueryResult("categoryQueryResult.json").getResults();

        assertThat(service.getSuggestions(categories, 4).get(DEFAULT_TIMEOUT)).hasSize(4).doesNotHaveDuplicates();
        assertThat(service.getSuggestions(categories, 5).get(DEFAULT_TIMEOUT)).hasSize(5).doesNotHaveDuplicates();
    }
}
