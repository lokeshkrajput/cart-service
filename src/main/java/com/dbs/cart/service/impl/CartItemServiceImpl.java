package com.dbs.cart.service.impl;

import com.dbs.cart.domain.AppUser;
import com.dbs.cart.domain.CartItem;
import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.repo.CartItemRepo;
import com.dbs.cart.service.CartItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepo cartItemRepo;

    @Override
    @JmsListener(destination = "cart.item.queue")
    public void saveMessage(CartItemType cartItemType) {

        System.out.println("Received <" + cartItemType + ">");
        cartItemRepo.save(convertToCartItem(cartItemType));
        System.out.println("Item Saved into DB!");

    }

    private CartItem convertToCartItem(CartItemType cartItemType) {

        CartItem cartItem = new CartItem();
        cartItem.setUser(new AppUser());

        BeanUtils.copyProperties(cartItemType, cartItem);
        BeanUtils.copyProperties(cartItemType.getUser(), cartItem.getUser());

        return cartItem;
    }
}
