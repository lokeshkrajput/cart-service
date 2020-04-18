package com.dbs.cart;

import com.dbs.cart.domain.AppUser;
import com.dbs.cart.domain.CartItem;
import com.dbs.cart.generated.AppUserType;
import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.util.CartItemUtil;
import org.junit.Assert;

import java.math.BigDecimal;

public class CartTestHelper {

    static CartItemType createCartItemDTO(String userId, String name, String email, String itemCode, String itemName,
                                          String itemDesc, BigDecimal itemPrice, String itemLoc) {
        AppUserType user = new AppUserType();
        user.setUserId(userId);
        user.setEmail(email);
        user.setName(name);

        CartItemType cartItem = new CartItemType();
        cartItem.setUser(user);
        cartItem.setItemCode(itemCode);
        cartItem.setItemName(itemName);
        cartItem.setItemDesc(itemDesc);
        cartItem.setItemPrice(itemPrice);
        cartItem.setItemLocation(itemLoc);
        return cartItem;
    }

    public static CartItem createCartItem(CartItemType cartItemDTO) {
        return CartItemUtil.convertToCartItemBean(cartItemDTO, null);
    }


    static void validateCartItem(CartItemType cartItem, CartItemType cartItemType) {

        AppUserType cartItemUserPersisted = cartItemType.getUser();

        Assert.assertEquals(cartItem.getItemCode(), cartItemType.getItemCode());
        Assert.assertEquals(cartItem.getItemDesc(), cartItemType.getItemDesc());
        Assert.assertEquals(cartItem.getItemName(), cartItemType.getItemName());
        Assert.assertEquals(cartItem.getItemPrice(), cartItemType.getItemPrice());
        Assert.assertEquals(cartItemUserPersisted.getName(), cartItemType.getUser().getName());
        Assert.assertEquals(cartItemUserPersisted.getEmail(), cartItemType.getUser().getEmail());
    }

    static void validateCartItem(CartItemType cartItem, CartItem cartItemType) {

        AppUser cartItemUserPersisted = cartItemType.getUser();

        Assert.assertEquals(cartItem.getItemCode(), cartItemType.getItemCode());
        Assert.assertEquals(cartItem.getItemDesc(), cartItemType.getItemDesc());
        Assert.assertEquals(cartItem.getItemName(), cartItemType.getItemName());
        Assert.assertEquals(cartItem.getItemPrice(), cartItemType.getItemPrice());
        Assert.assertEquals(cartItemUserPersisted.getName(), cartItemType.getUser().getName());
        Assert.assertEquals(cartItemUserPersisted.getEmail(), cartItemType.getUser().getEmail());
    }


}
