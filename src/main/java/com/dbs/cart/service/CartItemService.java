package com.dbs.cart.service;

import com.dbs.cart.domain.CartItem;
import com.dbs.cart.exception.CouldNotCreateCartItemException;
import com.dbs.cart.exception.CouldNotRetrieveCartItemsException;
import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.generated.CartItemTypes;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


public interface CartItemService {

    public void saveCartMessage(CartItemType cartItem);

    public void retrySavingCartMessage(CartItem cartItem);

    public CartItemTypes getCartItemsByUserId(String userId) throws CouldNotRetrieveCartItemsException;

}
