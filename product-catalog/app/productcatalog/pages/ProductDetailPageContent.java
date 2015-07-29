package productcatalog.pages;

import common.cms.CmsPage;
import common.contexts.AppContext;
import common.pages.*;
import common.prices.PriceFinder;
import common.utils.PriceFormatterImpl;
import io.sphere.sdk.attributes.Attribute;
import io.sphere.sdk.attributes.AttributeAccess;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.models.LocalizedEnumValue;
import io.sphere.sdk.models.LocalizedStrings;
import io.sphere.sdk.productdiscounts.DiscountedPrice;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.shippingmethods.ShippingMethod;

import javax.money.MonetaryAmount;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static common.utils.Languages.translate;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Arrays.toString;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

public class ProductDetailPageContent extends PageContent {

    private static final AttributeAccess<String> TEXT_ATTR_ACCESS = AttributeAccess.ofText();
    private static final AttributeAccess<LocalizedEnumValue> LENUM_ATTR_ACCESS = AttributeAccess.ofLocalizedEnumValue();
    private static final int SHORT_DESCRIPTION_MAX_CHARACTERS = 170;

    private final AppContext context;
    private final ProductProjection product;
    private final ProductVariant variant;
    private final List<ProductProjection> suggestionList;
    private final List<ShippingMethod> shippingMethods;
    private final List<Category> breadcrubs;

    private final Optional<Price> priceOpt;

    public ProductDetailPageContent(final CmsPage cms, final AppContext context, final PriceFinder priceFinder, final ProductProjection product, final ProductVariant variant, List<ProductProjection> suggestionList, final List<ShippingMethod> shippingMethods, final List<Category> breadcrumbs) {
        super(cms);
        this.context = context;
        this.product = product;
        this.variant = variant;
        this.suggestionList = suggestionList;
        this.shippingMethods = shippingMethods;
        this.breadcrubs = breadcrumbs;
        this.priceOpt = priceFinder.findPrice(product.getMasterVariant().getPrices());
    }

    @Override
    public String additionalTitle() {
        return translate(product.getName(), context);
    }

    public String getText() {
        return cms.getOrEmpty("content.text");
    }

    private <T> List<T> listWith(final List<T> old, final T elem) {
        final List<T> merged = new ArrayList<>(old);
        merged.add(elem);
        return merged;
    }

    public List<LinkData> getBreadcrumb() {
        return breadcrubs.stream()
                .map(this::categoryToLinkData)
                .collect(toList());
    }

    private LinkData categoryToLinkData(final Category category) {
      return new LinkData(translate(category.getName(), context), "");
    }

    public List<ImageData> getGallery() {
        return variant.getImages().stream()
                .map(ImageData::of)
                .collect(toList());
    }

    public ProductData getProduct() {
        return ProductDataBuilder.of()
                .withText(translate(product.getName(), context))
                .withSku(variant.getSku().orElse(""))
                .withRatingList(buildRating())
                .withDescription(product.getDescription().map(description -> shorten(translate(description, context))).orElse(""))
                .withAdditionalDescription(product.getDescription().map(description -> translate(description, context)).orElse(""))
                .withViewDetailsText(cms.getOrEmpty("product.viewDetails"))
                .withPrice(getFormattedPrice())
                .withPriceOld(getFormattedPriceOld())
                .withColorList(buildColorList())
                .withSizeList(buildSizeList())
                .withSizeGuideText(cms.getOrEmpty("product.sizeGuide"))
                .withBagItemList(buildBagItemList())
                .withAddToBagText(cms.getOrEmpty("product.addToBag"))
                .withAddToWishlistText(cms.getOrEmpty("product.addToWishlist"))
                .withAvailableText(cms.getOrEmpty("product.available"))
                .withProductDetails(buildProductDetails())
                .withDeliveryAndReturn(buildDeliveryAndReturn())
                .build();
    }

    public ProductListData getSuggestions() {
        return new ProductListData(suggestionList, context, PriceFormatterImpl.of(),
                cms.getOrEmpty("suggestions.text"), cms.getOrEmpty("suggestions.sale"),
                cms.getOrEmpty("suggestions.new"), cms.getOrEmpty("suggestions.quickView"),
                cms.getOrEmpty("suggestions.wishlist"), cms.getOrEmpty("suggestions.moreColors"));
    }

    public CollectionData getReviews() {
        return new CollectionData(cms.getOrEmpty("reviews.text"), Collections.<SelectableData>emptyList());
    }

    private CollectionData buildRating() {
        return new CollectionData("", asList(
                new SelectableData("5 Stars", "5", cms.getOrEmpty("product.ratingList.five.text"), "", false),
                new SelectableData("4 Stars", "4", cms.getOrEmpty("product.ratingList.four.text"), "", false),
                new SelectableData("3 Stars", "3", cms.getOrEmpty("product.ratingList.three.text"), "", false),
                new SelectableData("2 Stars", "2", cms.getOrEmpty("product.ratingList.two.text"), "", false),
                new SelectableData("1 Stars", "1", cms.getOrEmpty("product.ratingList.one.text"), "", false)
        ));
    }

    private CollectionData buildColorList() {
        final SelectableData defaultItem =
                new SelectableData(cms.getOrEmpty("product.colorList.choose.text"), "none", "", "", true);

        final List<SelectableData> colors =
                concat(Stream.of(defaultItem), getColorsinAllVariants().stream().map(this::selectableColor))
                        .collect(toList());

        return new CollectionData(cms.getOrEmpty("product.colorList.text"), colors);
    }

    private SelectableData selectableColor(final Attribute color) {
        final String colorLabel = translate(color.getValue(LENUM_ATTR_ACCESS).getLabel(), context);
        return new SelectableData(colorLabel, color.getName(), "", "", false);
    }

    private List<Attribute> getColorsinAllVariants() {
        return getAttributeInAllVariants("color");
    }

    private List<Attribute> getAttributeInAllVariants(final String attributeName) {
        return product.getAllVariants().stream()
                .map(variant -> variant.getAttribute(attributeName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(toList());
    }

    private CollectionData buildSizeList() {
        final SelectableData defaultItem =
                new SelectableData(cms.getOrEmpty("product.sizeList.choose.text"), "none", "", "", true);

        final List<SelectableData> sizes =
                concat(Stream.of(defaultItem), getSizesinAllVariants().stream().map(this::selectableSize))
                        .collect(toList());

        return new CollectionData(cms.getOrEmpty("product.sizeList.text"), sizes);
    }

    private SelectableData selectableSize(final Attribute size) {
        return new SelectableData(size.getValue(TEXT_ATTR_ACCESS), size.getValue(TEXT_ATTR_ACCESS), "", "", false);
    }

    private List<Attribute> getSizesinAllVariants() {
        return getAttributeInAllVariants("size");
    }

    private CollectionData buildBagItemList() {
        final SelectableData defaultItem = new SelectableData("1", "", "", "", true);

        final List<SelectableData> bagItems =
                concat(Stream.of(defaultItem), IntStream.range(2, 100).mapToObj(this::selectableBagItem))
                        .collect(toList());

        return new CollectionData("", bagItems);
    }

    private SelectableData selectableBagItem(final int number) {
        return new SelectableData(Integer.toString(number), "", "", "", false);
    }

    private DetailData buildProductDetails() {
        final Set<LocalizedStrings> details =
                variant.getAttribute("details", AttributeAccess.ofLocalizedStringsSet()).orElse(Collections.emptySet());
        final String joined = join(", ", details.stream().map(elem -> translate(elem, context)).collect(toSet()));

        return new DetailData(cms.getOrEmpty("product.productDetails.text"), joined);
    }

    private DetailData buildDeliveryAndReturn() {
        final String joined = join(", ", shippingMethods.stream().map(ShippingMethod::getName).collect(toList()));

        return new DetailData(cms.getOrEmpty("product.deliveryAndReturn.text"), joined);
    }

    private String getFormattedPrice() {
        return priceOpt
                .map(this::getPriceOrDiscounted)
                .map(price -> PriceFormatterImpl.of().format(price, context))
                .orElse("");
    }

    private String getFormattedPriceOld() {
        final Optional<MonetaryAmount> priceOld = priceOpt.flatMap(this::getOldPrice);

        return priceOld
                .map(price -> PriceFormatterImpl.of().format(price, context))
                .orElse("");

    }

    private Optional<MonetaryAmount> getOldPrice(final Price price) {
        if (priceOpt.flatMap(Price::getDiscounted).isPresent())
            return priceOpt.map(Price::getValue);
        else
            return  Optional.empty();
    }

    private  MonetaryAmount getPriceOrDiscounted(final Price price) {
        return price.getDiscounted()
                .map(DiscountedPrice::getValue)
                .orElse(price.getValue());
    }

    private String shorten(final String text) {
        return text.substring(0, SHORT_DESCRIPTION_MAX_CHARACTERS);
    }
}