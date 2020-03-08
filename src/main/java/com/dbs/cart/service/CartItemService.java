package com.dbs.cart.service;

import com.dbs.cart.domain.CartItem;
import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.generated.CartItemTypes;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CartItemService {

    public void saveCartMessage(CartItemType cartItem);

    public void tryFailedItem(CartItemType cartItem);

    public CartItemTypes getCartItemsByUserId(String userId);

}
