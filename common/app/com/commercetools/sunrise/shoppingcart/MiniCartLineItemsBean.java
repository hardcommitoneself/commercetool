package com.commercetools.sunrise.shoppingcart;

import com.commercetools.sunrise.common.contexts.UserContext;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.models.Base;
import com.commercetools.sunrise.wedecidelatercommon.ProductReverseRouter;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class MiniCartLineItemsBean extends Base {
    private List<MiniCartLineItemBean> list;

    public MiniCartLineItemsBean() {
        this.list = emptyList();
    }

    public MiniCartLineItemsBean(final Cart cart, final UserContext userContext, final ProductReverseRouter reverseRouter) {
        this.list = cart.getLineItems().stream()
                .map(lineItem -> new MiniCartLineItemBean(lineItem, userContext, reverseRouter))
                .collect(toList());
    }

    public List<MiniCartLineItemBean> getList() {
        return list;
    }

    public void setList(final List<MiniCartLineItemBean> list) {
        this.list = list;
    }
}
