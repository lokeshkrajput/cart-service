package com.dbs.cart;

import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.generated.CartItemTypes;
import com.dbs.cart.repo.CartItemRepo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.math.BigDecimal;


/**
 * Integration / End point tests
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Profile("end-point")
class CartServiceIntegrationTests {

    private static CartItemType cartItem1;
    private static CartItemType cartItem2;
    private static CartItemType cartItem3;
    private static CartItemType cartItem4;

    @LocalServerPort
    private int port;

    @Autowired
    private EntityManager em;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @BeforeAll
    private static void setUp() {

        cartItem1 = CartTestHelper.createCartItemDTO("lrajput1", "Lokesh Kumar", "lokeshkrajput@gmail.com",
                "11", "item11", "Item Desc11", new BigDecimal("21.30"), "IN");

        cartItem2 = CartTestHelper.createCartItemDTO("lokeshrajput1", "Lokesh Rajput", "lokesh.persistent@gmail.com",
                "21", "item21", "Item Desc21", new BigDecimal("20.12"), "SG");

        cartItem3 = CartTestHelper.createCartItemDTO("lokeshrajput1", "Lokesh Rajput", "lokesh.persistent@gmail.com",
                "31", "item31", "Item Desc31", new BigDecimal("20.30"), "IN");

        cartItem4 = CartTestHelper.createCartItemDTO("lokeshr1", "Lokesh Kumar", "lokesh.persistent2@gmail.com",
                "41", "item41", "Item Desc41", new BigDecimal("10.30"), "IN");
    }

    @Test
    void testGivenService_WhenAddSingleCartItem_ThenServiceShouldSaveIt() {

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
				.getForEntity(getURL("/getItemByUserId/lrajput1"), CartItemTypes.class);
		Assert.assertEquals(200, cartItemsResp.getStatusCodeValue());

        Assert.assertEquals(1, cartItemsResp.getBody().getItems().size());
        CartItemType cartItemType = cartItemsResp.getBody().getItems().get(0);

        CartTestHelper.validateCartItem(cartItem1, cartItemType);
    }


    @Test
    void testGivenService_WhenAddMultipleCartItems_ThenServiceShouldSaveThese() {

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
                .getForEntity(getURL("/getItemByUserId/lokeshrajput1"), CartItemTypes.class);
        Assert.assertEquals(200, cartItemsResp.getStatusCodeValue());

        Assert.assertEquals(2, cartItemsResp.getBody().getItems().size());
        CartItemType cartItemType2 = cartItemsResp.getBody().getItems().get(0);
        CartItemType cartItemType3 = cartItemsResp.getBody().getItems().get(1);

        CartTestHelper.validateCartItem(cartItem2, cartItemType2);
        CartTestHelper.validateCartItem(cartItem3, cartItemType3);
    }

    String getURL(String url) {
        return "http://localhost:" + port  + "/cart" + url;
    }

}
