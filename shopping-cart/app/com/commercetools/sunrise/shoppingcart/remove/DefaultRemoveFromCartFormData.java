package com.commercetools.sunrise.shoppingcart.remove;

import io.sphere.sdk.models.Base;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

public class DefaultRemoveFromCartFormData extends Base implements RemoveFromCartFormData {

    @Required
    @MinLength(1)
    private String lineItemId;

    @Override
    public String lineItemId() {
        return lineItemId;
    }


    // Getters & setters

    public String getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(final String lineItemId) {
        this.lineItemId = lineItemId;
    }
}
