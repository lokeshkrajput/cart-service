package com.dbs.cart;

import com.dbs.cart.generated.AppUserType;
import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.generated.CartItemTypes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartServiceApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private EntityManager em;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private static CartItemType cartItem1;
    private static CartItemType cartItem2;
    private static CartItemType cartItem3;
    private static CartItemType cartItem4;

    @BeforeAll
    private static void setUp() {
        cartItem1 = CartTestUtils.createCartItem("lrajput", "Lokesh Kumar", "lokeshkrajput@gmail.com",
                "1", "item1", "Item Desc1", new BigDecimal("21.30"), "IN");

        cartItem2 = CartTestUtils.createCartItem("lokeshrajput", "Lokesh Rajput", "lokesh.persistent@gmail.com",
                "2", "item2", "Item Desc2", new BigDecimal("20.12"), "SG");

        cartItem3 = CartTestUtils.createCartItem("lokeshrajput", "Lokesh Rajput", "lokesh.persistent@gmail.com",
                "3", "item3", "Item Desc3", new BigDecimal("20.30"), "IN");

        cartItem4 = CartTestUtils.createCartItem("lokeshr", "Lokesh Kumar", "lokesh.persistent2@gmail.com",
                "4", "item4", "Item Desc4", new BigDecimal("10.30"), "IN");
    }

    @Test
    void testAddCartSingleItem() {

        ResponseEntity<String> responseEntity = this.restTemplate
                .postForEntity(getURL("/addItem"), cartItem1, String.class);
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
		Assert.assertEquals("SUCCESS", responseEntity.getBody());

		// put the thread on sleep for 5 seconds - wait for the message to be processed
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
		}

		ResponseEntity<CartItemTypes> cartItemsResp = this.restTemplate
				.getForEntity(getURL("/getItemByUserId/lrajput"), CartItemTypes.class);
		Assert.assertEquals(200, cartItemsResp.getStatusCodeValue());

        Assert.assertEquals(1, cartItemsResp.getBody().getItems().size());
        CartItemType cartItemType = cartItemsResp.getBody().getItems().get(0);

        CartTestUtils.validateAddedItem(cartItem1, cartItemType);
    }


    @Test
    void testAddCartMultipleItems() {

        ResponseEntity<String> responseEntity = this.restTemplate
                .postForEntity(getURL("/addItem"), cartItem2, String.class);
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        Assert.assertEquals("SUCCESS", responseEntity.getBody());

        responseEntity = this.restTemplate
                .postForEntity(getURL("/addItem"), cartItem3, String.class);
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        Assert.assertEquals("SUCCESS", responseEntity.getBody());

        // put the thread on sleep for 5 seconds - wait for the message to be processed
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
        }

        ResponseEntity<CartItemTypes> cartItemsResp = this.restTemplate
                .getForEntity(getURL("/getItemByUserId/lokeshrajput"), CartItemTypes.class);
        Assert.assertEquals(200, cartItemsResp.getStatusCodeValue());

        Assert.assertEquals(2, cartItemsResp.getBody().getItems().size());
        CartItemType cartItemType2 = cartItemsResp.getBody().getItems().get(0);
        CartItemType cartItemType3 = cartItemsResp.getBody().getItems().get(1);

        CartTestUtils.validateAddedItem(cartItem2, cartItemType2);
        CartTestUtils.validateAddedItem(cartItem3, cartItemType3);
    }

    String getURL(String url) {
        return "http://localhost:" + port  + "/cart" + url;
    }

}
