package productcatalog.productoverview;

import common.contexts.UserContext;
import common.controllers.ControllerDependency;
import common.controllers.SunrisePageData;
import common.models.ProductDataConfig;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.search.PagedSearchResult;
import play.libs.concurrent.HttpExecution;
import play.mvc.Result;
import play.twirl.api.Html;
import productcatalog.common.BreadcrumbData;
import productcatalog.common.ProductCatalogController;
import productcatalog.common.ProductListData;
import productcatalog.productoverview.search.FacetBeanList;
import productcatalog.productoverview.search.SearchConfig;
import productcatalog.productoverview.search.SearchCriteria;
import productcatalog.services.ProductService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class ProductOverviewPageController extends ProductCatalogController {
    private final int paginationDisplayedPages;

    @Inject
    public ProductOverviewPageController(final ControllerDependency controllerDependency, final ProductService productService,
                                         final ProductDataConfig productDataConfig, final SearchConfig searchConfig) {
        super(controllerDependency, productService, productDataConfig, searchConfig);
        this.paginationDisplayedPages = configuration().getInt("pop.pagination.displayedPages", 6);
    }

    /* Controller actions */

    public CompletionStage<Result> show(final String languageTag, final int page, final String categorySlug) {
        final UserContext userContext = userContext(languageTag);
        final Optional<Category> categoryOpt = categoryTree().findBySlug(userContext.locale(), categorySlug);
        if (categoryOpt.isPresent()) {
            final SearchCriteria searchCriteria = SearchCriteria.of(page, searchConfig(), request(), i18nResolver(), userContext, categoryTree(), categoryOpt.get());
            return productService().searchProducts(page, searchCriteria).thenApplyAsync(searchResult ->
                    renderCategoryPage(categoryOpt.get(), page, searchCriteria, searchResult, userContext), HttpExecution.defaultContext());
        } else {
            return CompletableFuture.completedFuture(notFound("Category not found: " + categorySlug));
        }
    }

    public CompletionStage<Result> search(final String languageTag, final int page) {
        final UserContext userContext = userContext(languageTag);
        final SearchCriteria searchCriteria = SearchCriteria.of(page, searchConfig(), request(), i18nResolver(), userContext);
        if (searchCriteria.getSearchTerm().isPresent()) {
            final CompletionStage<PagedSearchResult<ProductProjection>> searchResultStage = productService().searchProducts(page, searchCriteria);
            return searchResultStage.thenApplyAsync(searchResult ->
                    renderSearchPage(page, userContext, searchCriteria, searchResult), HttpExecution.defaultContext());
        } else {
            return CompletableFuture.completedFuture(badRequest("Search term missing"));
        }
    }

    private ProductOverviewPageContent createPageContent(final int page, final SearchCriteria searchCriteria,
                                                         final PagedSearchResult<ProductProjection> searchResult,
                                                         final UserContext userContext) {
        final ProductOverviewPageContent content = new ProductOverviewPageContent();
        content.setFilterProductsUrl(request().path());
        content.setProducts(new ProductListData(searchResult.getResults(), productDataConfig(), userContext, reverseRouter(), categoryTreeInNew()));
        content.setPagination(new PaginationData(requestContext(request()), searchResult, page, searchCriteria.getDisplayCriteria().getSelectedPageSize(), paginationDisplayedPages));
        content.setSortSelector(searchCriteria.getSortCriteria().boundSortSelector());
        content.setDisplaySelector(searchCriteria.getDisplayCriteria().boundDisplaySelector());
        content.setFacets(new FacetBeanList(searchCriteria.getFacetsCriteria().boundFacets(searchResult)));
        return content;
    }

    private Result renderCategoryPage(final Category category, final int page, final SearchCriteria searchCriteria,
                                      final PagedSearchResult<ProductProjection> searchResult, final UserContext userContext) {
        final ProductOverviewPageContent pageContent = createPageContent(page, searchCriteria, searchResult, userContext);
        pageContent.setAdditionalTitle(category.getName().find(userContext.locales()).orElse(""));
        pageContent.setBreadcrumb(new BreadcrumbData(category, categoryTree(), userContext, reverseRouter()));
        pageContent.setJumbotron(new JumbotronData(category, userContext, categoryTree()));
        pageContent.setBanner(createBanner(userContext, category));
        pageContent.setSeo(new SeoData(userContext, category));
        return ok(renderPage(userContext, pageContent));
    }

    private Result renderSearchPage(final int page, final UserContext userContext, final SearchCriteria searchCriteria,
                                    final PagedSearchResult<ProductProjection> searchResult) {
        final String searchTerm = searchCriteria.getSearchTerm().get().getValue();
        final ProductOverviewPageContent pageContent = createPageContent(page, searchCriteria, searchResult, userContext);
        pageContent.setAdditionalTitle(searchTerm);
        pageContent.setBreadcrumb(new BreadcrumbData(searchTerm));
        pageContent.setSearchTerm(searchTerm);
        return ok(renderPage(userContext, pageContent));
    }

    private Html renderPage(final UserContext userContext, final ProductOverviewPageContent content) {
        final SunrisePageData pageData = pageData(userContext, content, ctx(), session());
        return templateService().renderToHtml("pop", pageData, userContext.locales());
    }

    private static BannerData createBanner(final UserContext userContext, final Category category) {
        final BannerData bannerData = new BannerData(userContext, category);
        bannerData.setImageMobile("/assets/img/banner_mobile-0a9241da249091a023ecfadde951a53b.jpg"); // TODO obtain from category?
        bannerData.setImageDesktop("/assets/img/banner_desktop-9ffd148c48068ce2666d6533b4a87d11.jpg"); // TODO obtain from category?
        return bannerData;
    }
}