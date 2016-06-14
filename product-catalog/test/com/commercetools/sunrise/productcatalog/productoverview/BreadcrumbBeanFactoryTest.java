package com.commercetools.sunrise.productcatalog.productoverview;

import com.commercetools.sunrise.common.contexts.UserContext;
import com.commercetools.sunrise.common.contexts.UserContextImpl;
import com.commercetools.sunrise.common.controllers.TestableReverseRouter;
import com.commercetools.sunrise.common.models.LinkBean;
import com.commercetools.sunrise.common.reverserouter.ProductReverseRouter;
import com.commercetools.sunrise.productcatalog.common.BreadcrumbBean;
import com.commercetools.sunrise.productcatalog.common.BreadcrumbBeanFactory;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.CategoryTree;
import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;

import static com.commercetools.sunrise.common.utils.JsonUtils.readCtpObject;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class BreadcrumbBeanFactoryTest {

    private static final CategoryTree CATEGORY_TREE = CategoryTree.of(readCtpObject("breadcrumb/breadcrumbCategories.json", CategoryQuery.resultTypeReference()).getResults());
    private static final ProductProjection PRODUCT = readCtpObject("breadcrumb/breadcrumbProduct.json", ProductProjection.typeReference());
    private static final UserContext USER_CONTEXT = UserContextImpl.of(singletonList(ENGLISH), CountryCode.UK, null);
    private static final ProductReverseRouter REVERSE_ROUTER = reverseRouter();

    @Test
    public void createCategoryBreadcrumbOfOneLevel() {
        testCategoryBreadcrumb("1stLevel",
                texts -> assertThat(texts).containsExactly("1st Level"),
                urls -> assertThat(urls).containsExactly("category-1st-level"));
    }

    @Test
    public void createCategoryBreadcrumbOfManyLevels() {
        testCategoryBreadcrumb("3rdLevel",
                texts -> assertThat(texts).containsExactly("1st Level", "2nd Level", "3rd Level"),
                urls -> assertThat(urls).containsExactly("category-1st-level", "category-2nd-level", "category-3rd-level"));
    }

    @Test
    public void createProductBreadcrumb() throws Exception {
        testProductBreadcrumb("some-sku", PRODUCT,
                texts -> assertThat(texts).containsExactly("1st Level", "2nd Level", "Some product"),
                urls -> assertThat(urls).containsExactly("category-1st-level", "category-2nd-level", "product-some-product-some-sku"));
    }

    private static BreadcrumbBeanFactory createBreadcrumbBeanFactory() {
        final Injector injector = Guice.createInjector(new Module() {
            @Override
            public void configure(final Binder binder) {
                binder.bind(UserContext.class).toInstance(USER_CONTEXT);
                binder.bind(CategoryTree.class).toInstance(CATEGORY_TREE);
                binder.bind(ProductReverseRouter.class).toInstance(REVERSE_ROUTER);
            }
        });
        return injector.getInstance(BreadcrumbBeanFactory.class);
    }

    private void testCategoryBreadcrumb(final String extId, final Consumer<List<String>> texts, final Consumer<List<String>> urls) {
        final BreadcrumbBeanFactory breadcrumbBeanFactory = createBreadcrumbBeanFactory();
        final Category category = CATEGORY_TREE.findByExternalId(extId).get();
        final BreadcrumbBean breadcrumb = breadcrumbBeanFactory.create(category);
        testBreadcrumb(breadcrumb, texts, urls);
    }

    private void testProductBreadcrumb(final String sku, final ProductProjection product, final Consumer<List<String>> texts, final Consumer<List<String>> urls) {
        final ProductVariant variant = product.findVariantBySku(sku).get();
        final BreadcrumbBean breadcrumb = createBreadcrumbBeanFactory().create(product, variant);
        testBreadcrumb(breadcrumb, texts, urls);
    }

    private void testBreadcrumb(final BreadcrumbBean breadcrumb, final Consumer<List<String>> texts, final Consumer<List<String>> urls) {
        texts.accept(breadcrumb.getLinks().stream().map(LinkBean::getText).collect(toList()));
        urls.accept(breadcrumb.getLinks().stream().map(LinkBean::getUrl).collect(toList()));
    }

    private static ProductReverseRouter reverseRouter() {
        final TestableReverseRouter reverseRouter = new TestableReverseRouter();
        reverseRouter.setShowCategoryUrl("category-");
        reverseRouter.setShowProductUrl("product-");
        reverseRouter.setProcessSearchProductsFormUrl("search-");
        return reverseRouter;
    }
}
