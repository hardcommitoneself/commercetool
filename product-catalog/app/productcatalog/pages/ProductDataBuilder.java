package productcatalog.pages;

import common.pages.DetailData;
import common.pages.SelectableData;
import common.prices.PriceFinder;
import common.utils.PriceFormatter;
import common.utils.Translator;
import io.sphere.sdk.attributes.Attribute;
import io.sphere.sdk.attributes.AttributeAccess;
import io.sphere.sdk.models.LocalizedEnumValue;
import io.sphere.sdk.models.LocalizedStrings;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;

public class ProductDataBuilder {

    private static final AttributeAccess<String> TEXT_ATTR_ACCESS = AttributeAccess.ofText();
    private static final AttributeAccess<LocalizedEnumValue> LENUM_ATTR_ACCESS = AttributeAccess.ofLocalizedEnumValue();
    private static final AttributeAccess<Set<LocalizedStrings>> LENUM_SET_ATTR_ACCESS = AttributeAccess.ofLocalizedStringsSet();

    private final Translator translator;
    private final PriceFinder priceFinder;
    private final PriceFormatter priceFormatter;

    private ProductDataBuilder(final Translator translator, final PriceFinder priceFinder, final PriceFormatter priceFormatter) {
        this.translator = translator;
        this.priceFinder = priceFinder;
        this.priceFormatter = priceFormatter;
    }

    public static ProductDataBuilder of(final Translator translator, final PriceFinder priceFinder, final PriceFormatter priceFormatter) {
        return new ProductDataBuilder(translator, priceFinder, priceFormatter);
    }

    public ProductData build(final ProductProjection product, final ProductVariant variant) {
        final Optional<Price> priceOpt = priceFinder.findPrice(variant.getPrices());

        return new ProductData(
                translator.translate(product.getName()),
                variant.getSku().orElse(""),
                product.getDescription().map(translator::translate).orElse(""),
                priceOpt.map(price -> priceFormatter.format(price.getValue())).orElse(""),
                getPriceOld(priceOpt).map(price -> priceFormatter.format(price.getValue())).orElse(""),
                getColors(product),
                getSizes(product),
                getProductDetails(variant)
        );
    }

    private List<SelectableData> getColors(final ProductProjection product) {
        return getColorInAllVariants(product).stream()
                .map(this::colorToSelectableItem)
                .collect(toList());
    }

    private List<SelectableData> getSizes(final ProductProjection product) {
        return getSizeInAllVariants(product).stream()
                .map(this::sizeToSelectableItem)
                .collect(toList());
    }

    private List<DetailData> getProductDetails(final ProductVariant variant) {
        return variant.getAttribute("details", LENUM_SET_ATTR_ACCESS).orElse(emptySet()).stream()
                .map(this::localizedStringsToDetailData)
                .collect(toList());
    }

    private List<Attribute> getColorInAllVariants(final ProductProjection product) {
        return getAttributeInAllVariants(product, "color");
    }

    private List<Attribute> getSizeInAllVariants(final ProductProjection product) {
        return getAttributeInAllVariants(product, "size");
    }

    private List<Attribute> getAttributeInAllVariants(final ProductProjection product, final String attributeName) {
        return product.getAllVariants().stream()
                .map(variant -> variant.getAttribute(attributeName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(toList());
    }

    private SelectableData colorToSelectableItem(final Attribute color) {
        final String colorLabel = translator.translate(color.getValue(LENUM_ATTR_ACCESS).getLabel());
        return new SelectableData(colorLabel, color.getName(), "", "", false);
    }

    private SelectableData sizeToSelectableItem(final Attribute size) {
        final String sizeLabel = size.getValue(TEXT_ATTR_ACCESS);
        return new SelectableData(sizeLabel, sizeLabel, "", "", false);
    }

    private DetailData localizedStringsToDetailData(final LocalizedStrings localizedStrings) {
        final String label = translator.translate(localizedStrings);
        return new DetailData(label, "");
    }

    private Optional<Price> getPriceOld(final Optional<Price> priceOpt) {
        return priceOpt.flatMap(price -> price.getDiscounted().map(discountedPrice -> price));
    }
}
