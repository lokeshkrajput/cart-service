package com.dbs.cart;

import com.dbs.cart.domain.CartItem;
import com.dbs.cart.exception.CouldNotRetrieveCartItemsException;
import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.generated.CartItemTypes;
import com.dbs.cart.repo.CartItemRepo;
import com.dbs.cart.service.CartItemService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import java.math.BigDecimal;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@Profile("all-tests")
class CartServiceTests {

    private static CartItemType cartItemDTO1;
    private static CartItemType cartItemDTO2;
    private static CartItemType cartItemDTO3;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("stageItemQ")
    private Queue stageItemQ;

    @Autowired
    @Qualifier("errorItemQ")
    private Queue errorItemQ;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeAll
    private static void setUp() {

        cartItemDTO1 = CartTestHelper.createCartItemDTO("lrajput", "Lokesh Kumar", "lokeshkrajput@gmail.com",
                "1", "item1", "Item Desc1", new BigDecimal("21.30"), "IN");

        cartItemDTO2 = CartTestHelper.createCartItemDTO("lokeshrajput", "Lokesh Rajput", "lokesh.persistent@gmail.com",
                "2", "item2", "Item Desc2", new BigDecimal("20.12"), "SG");

        cartItemDTO3 = CartTestHelper.createCartItemDTO("lokeshrajput", "Lokesh Rajput", "lokesh.persistent@gmail.com",
                "3", "item3", "Item Desc3", new BigDecimal("20.30"), "IN");
    }

    @Test
    void testGivenService_WhenAddSingleCartItem_ThenServiceShouldSaveIt() {

        cartItemService.saveCartMessage(cartItemDTO1);

        CartItemTypes cartItems = null;
        try {
            cartItems = cartItemService.getCartItemsByUserId("lrajput");
        } catch (CouldNotRetrieveCartItemsException e) {
            Assert.fail("Could not retrieve cart items");
        }

        Assert.assertEquals(1, cartItems.getItems().size());

        CartItemType cartItemType = cartItems.getItems().get(0);
        CartTestHelper.validateCartItem(cartItemDTO1, cartItemType);

    }

    @Test
    void testGivenService_WhenAddMultipleCartItems_ThenServiceShouldSaveIt() {

        cartItemService.saveCartMessage(cartItemDTO2);
        cartItemService.saveCartMessage(cartItemDTO3);

        CartItemTypes cartItems = null;
        try {
            cartItems = cartItemService.getCartItemsByUserId("lokeshrajput");
        } catch (CouldNotRetrieveCartItemsException e) {
            Assert.fail("Could not retrieve cart items");
        }

        Assert.assertEquals(2, cartItems.getItems().size());

        CartItemType cartItemType2 = cartItems.getItems().get(0);
        CartTestHelper.validateCartItem(cartItemDTO2, cartItemType2);

        CartItemType cartItemType3 = cartItems.getItems().get(1);
        CartTestHelper.validateCartItem(cartItemDTO3, cartItemType3);

    }

}
