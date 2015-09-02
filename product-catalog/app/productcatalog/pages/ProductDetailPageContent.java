package productcatalog.pages;

import common.pages.*;

import java.util.List;

public class ProductDetailPageContent extends PageContent {
    private final String additionalTitle;
    private final PdpStaticData staticData;
    private final List<LinkData> breadcrumb;
    private final ProductData productData;
    private final List<ShippingRateData> shippingRates;
    private final List<ProductData> suggestions;

    public ProductDetailPageContent(final String additionalTitle, final PdpStaticData staticData, final List<LinkData> breadcrumb,
                                    final ProductData productData, final List<ShippingRateData> shippingRates,
                                    final List<ProductData> suggestions) {
        this.additionalTitle = additionalTitle;
        this.staticData = staticData;
        this.breadcrumb = breadcrumb;
        this.productData = productData;
        this.shippingRates = shippingRates;
        this.suggestions = suggestions;
    }

    public PdpStaticData getStatic() {
        return staticData;
    }

    public String additionalTitle() {
        return additionalTitle;
    }

    public List<LinkData> getBreadcrumb() {
        return breadcrumb;
    }

    public ProductData getProduct() {
        return productData;
    }

    public List<ProductData> getSuggestions() {
        return suggestions;
    }

    public List<ShippingRateData> getShippingRates() {
        return shippingRates;
    }
}