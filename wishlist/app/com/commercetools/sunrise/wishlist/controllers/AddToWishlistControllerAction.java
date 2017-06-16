package com.commercetools.sunrise.wishlist.controllers;

import com.commercetools.sunrise.framework.controllers.ControllerAction;
import com.commercetools.sunrise.wishlist.viewmodels.AddWishlistLineItemFormData;
import com.google.inject.ImplementedBy;
import io.sphere.sdk.shoppinglists.ShoppingList;

import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;

@FunctionalInterface
@ImplementedBy(DefaultAddToWishlistControllerAction.class)
public interface AddToWishlistControllerAction extends ControllerAction, BiFunction<ShoppingList, AddWishlistLineItemFormData, CompletionStage<ShoppingList>> {
    /**
     * Adds a line item to the given wishlist.
     *
     * @param wishlist                    the {@link ShoppingList} to add the line item to
     * @param addWishlistLineItemFormData specifies the line item to add
     *
     * @return the completion stage for the updated wishlist with the added line item
     */
    @Override
    CompletionStage<ShoppingList> apply(ShoppingList wishlist, AddWishlistLineItemFormData addWishlistLineItemFormData);
}
