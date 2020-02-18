package com.dbs.cart.service;

import com.dbs.cart.domain.CartItem;
import com.dbs.cart.generated.CartItemType;
import org.springframework.stereotype.Service;


public interface CartItemService {

    public void saveMessage(CartItemType cartItem);

}
