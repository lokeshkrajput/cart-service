package com.dbs.cart;

import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.generated.CartItemTypes;
import com.dbs.cart.service.CartItemService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;


@RunWith(SpringRunner.class)
@SpringBootTest
class CartServiceTests {

    private static CartItemType cartItem1;
    private static CartItemType cartItem2;
    private static CartItemType cartItem3;


    @Autowired
    private CartItemService cartItemService;

    @BeforeAll
    private static void setUp() {

        cartItem1 = CartTestUtils.createCartItem("lrajput", "Lokesh Kumar", "lokeshkrajput@gmail.com",
                "1", "item1", "Item Desc1", new BigDecimal("21.30"), "IN");

        cartItem2 = CartTestUtils.createCartItem("lokeshrajput", "Lokesh Rajput", "lokesh.persistent@gmail.com",
                "2", "item2", "Item Desc2", new BigDecimal("20.12"), "SG");

        cartItem3 = CartTestUtils.createCartItem("lokeshrajput", "Lokesh Rajput", "lokesh.persistent@gmail.com",
                "3", "item3", "Item Desc3", new BigDecimal("20.30"), "IN");


    }

    @Test
    void testAddCartSingleItem() {

        cartItemService.saveCartMessage(cartItem1);

        CartItemTypes cartItems = cartItemService.getCartItemsByUserId("lrajput");

        Assert.assertEquals(1, cartItems.getItems().size());

        CartItemType cartItemType = cartItems.getItems().get(0);
        CartTestUtils.validateAddedItem(cartItem1, cartItemType);

    }

    @Test
    void testAddCartMultipleItems() {

        cartItemService.saveCartMessage(cartItem2);
        cartItemService.saveCartMessage(cartItem3);

        CartItemTypes cartItems = cartItemService.getCartItemsByUserId("lokeshrajput");

        Assert.assertEquals(2, cartItems.getItems().size());

        CartItemType cartItemType2 = cartItems.getItems().get(0);
        CartTestUtils.validateAddedItem(cartItem2, cartItemType2);

        CartItemType cartItemType3 = cartItems.getItems().get(1);
        CartTestUtils.validateAddedItem(cartItem3, cartItemType3);

    }

}
