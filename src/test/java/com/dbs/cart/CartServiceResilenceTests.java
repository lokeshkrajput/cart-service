package com.dbs.cart;

import com.dbs.cart.domain.CartItem;
import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.repo.CartItemRepo;
import com.dbs.cart.service.CartItemService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import java.math.BigDecimal;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@Profile("all-tests")
class CartServiceResilenceTests {

    private static CartItemType cartItemDTO1;

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

    @MockBean
    private CartItemRepo cartItemRepo;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeAll
    private static void setUp() {

        cartItemDTO1 = CartTestHelper.createCartItemDTO("lrajput", "Lokesh Kumar", "lokeshkrajput@gmail.com",
                "1", "item1", "Item Desc1", new BigDecimal("21.30"), "IN");

    }


    @Test
    void testGivenService_WhenAddSingleCartItem_AndDBSaveFailedReachThreshHoldLimit_ThenServiceShouldPutItemInStageQueue() {

        Mockito.when(cartItemRepo.save(Mockito.any(CartItem.class))).thenThrow(RuntimeException.class);
        cartItemService.saveCartMessage(cartItemDTO1);

        // Verify number of times cart-item  tried to be saved into DB
        Mockito.verify(cartItemRepo, Mockito.times(4)).save(Mockito.any(CartItem.class));

        doWait(10);
        jmsTemplate.setReceiveTimeout(3000L);
        ObjectMessage msg = (ObjectMessage) jmsTemplate.receive(stageItemQ);

        try {
            if(msg != null && msg.getObject() != null && msg.getObject() instanceof CartItem) {
                CartTestHelper.validateCartItem(cartItemDTO1, (CartItem)msg.getObject());
            } else {
                Assert.fail("Items did not go into the error queue");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testGivenService_WhenAddSingleCartItem_AndDBSaveFailedOneTime_ThenItemShouldNotGoIntoStagingQueue() {


        Mockito.when(cartItemRepo.save(Mockito.any(CartItem.class))).then(new Answer<CartItem>() {
            int counter = 0;
            @Override
            public CartItem answer(InvocationOnMock invocation) throws Throwable {
                if (counter++ == 0) {
                    throw new RuntimeException();
                }
                return CartTestHelper.createCartItem(cartItemDTO1);
            }
        });
        cartItemService.saveCartMessage(cartItemDTO1);

        // Verify number of times cart-item tried to be saved into DB
        Mockito.verify(cartItemRepo, Mockito.times(2)).save(Mockito.any(CartItem.class));

        doWait(10);
        jmsTemplate.setReceiveTimeout(3000L);
        ObjectMessage msg = (ObjectMessage) jmsTemplate.receive(stageItemQ);

        try {
            if(msg != null && msg.getObject() != null && msg.getObject() instanceof CartItem) {
                Assert.fail("Items did not go into the error queue");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    private void doWait(int timeInSec) {
        try {
           Thread.sleep(timeInSec * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
