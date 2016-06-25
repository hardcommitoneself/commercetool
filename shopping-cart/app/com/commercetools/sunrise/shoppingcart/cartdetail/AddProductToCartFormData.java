package com.commercetools.sunrise.shoppingcart.cartdetail;

import io.sphere.sdk.models.Base;
import play.data.validation.Constraints;

public class AddProductToCartFormData extends Base {
    @Constraints.Required
    private String productId;
    @Constraints.Required
    private int variantId;
    @Constraints.Required
    private long quantity;

    public AddProductToCartFormData() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(final String productId) {
        this.productId = productId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(final int variantId) {
        this.variantId = variantId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }
}
