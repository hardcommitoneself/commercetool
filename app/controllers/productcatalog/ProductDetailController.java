package controllers.productcatalog;

import com.commercetools.sunrise.framework.components.controllers.PageHeaderControllerComponentSupplier;
import com.commercetools.sunrise.framework.controllers.cache.NoCache;
import com.commercetools.sunrise.framework.components.controllers.RegisteredComponents;
import com.commercetools.sunrise.framework.reverserouters.productcatalog.product.ProductReverseRouter;
import com.commercetools.sunrise.framework.template.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.framework.template.engine.TemplateRenderer;
import com.commercetools.sunrise.productcatalog.productdetail.ProductFinder;
import com.commercetools.sunrise.productcatalog.productdetail.ProductRecommendationsControllerComponent;
import com.commercetools.sunrise.productcatalog.productdetail.ProductVariantFinder;
import com.commercetools.sunrise.productcatalog.productdetail.SunriseProductDetailController;
import com.commercetools.sunrise.productcatalog.productdetail.viewmodels.ProductDetailPageContentFactory;
import io.sphere.sdk.products.ProductProjection;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.completedFuture;

@NoCache
@RegisteredComponents({
        TemplateControllerComponentsSupplier.class,
        PageHeaderControllerComponentSupplier.class,
        ProductRecommendationsControllerComponent.class
})
public final class ProductDetailController extends SunriseProductDetailController {

    private final ProductReverseRouter productReverseRouter;

    @Inject
    public ProductDetailController(final TemplateRenderer templateRenderer,
                                   final ProductFinder productFinder,
                                   final ProductVariantFinder productVariantFinder,
                                   final ProductDetailPageContentFactory pageContentFactory,
                                   final ProductReverseRouter productReverseRouter) {
        super(templateRenderer, productFinder, productVariantFinder, pageContentFactory);
        this.productReverseRouter = productReverseRouter;
    }

    @Override
    public String getTemplateName() {
        return "pdp";
    }

    @Override
    public CompletionStage<Result> handleNotFoundProduct() {
        return completedFuture(notFound());
    }

    @Override
    public CompletionStage<Result> handleNotFoundProductVariant(final ProductProjection product) {
        return productReverseRouter
                .productDetailPageCall(product, product.getMasterVariant())
                .map(this::redirectToCall)
                .orElseGet(() -> completedFuture(notFound()));
    }
}