package com.dbs.cart.service.impl;

import ch.qos.logback.classic.Logger;
import com.dbs.cart.domain.AppUser;
import com.dbs.cart.domain.CartItem;
import com.dbs.cart.exception.*;
import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.generated.CartItemTypes;
import com.dbs.cart.repo.AppUserRepo;
import com.dbs.cart.repo.CartItemRepo;
import com.dbs.cart.service.CartItemService;
import com.dbs.cart.util.CartItemUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Queue;
import javax.transaction.Transactional;
import java.util.List;


@Service
public class CartItemServiceImpl implements CartItemService {

    public static final long WAIT_TIME_MULTIPLIER = 1000L;

    private static Logger log = (Logger) LoggerFactory.getLogger(CartItemServiceImpl.class);

    @Value("${cart.failure.retry-seq}")
    private String retrySeq;

    @Value("${cart.failure.max-retry}")
    private String maxRetry;


    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private AppUserRepo userRepo;

    @Autowired
    @Qualifier("stageItemQ")
    private Queue stageItemQ;

    @Autowired
    @Qualifier("errorItemQ")
    private Queue errorItemQ;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    @JmsListener(destination = "cart.item.queue")
    @Transactional
    public void saveCartMessage(CartItemType cartItemType)  {

        CartItem item = null;
        try {

            log.info("Received a cart item with item code: " + cartItemType.getItemCode()
                    + ", added by user: " + cartItemType.getUser().getUserId());

            item = getEntity(cartItemType);

            log.info("Going to persist cart item: " + item);

            // save the cart item into the database
            saveItem(item);

            log.info("Cart item saved into database.");

        } catch (ItemConversionException e) {

            // programming error - put the item into error-queue should not be re-tried
            log.error("Message conversion failed for Cart Item", e);

            if(item != null) {
                jmsTemplate.convertAndSend(errorItemQ, item);
            }

        }  catch (DatabaseException e) {

            // retry with failed item from the failed queue
            if(item != null) {

                try {
                    tryFailedItem(item, 0);

                } catch (CouldNotCreateCartItemException e1) {

                    // Save the cart item into stage-queue, message can be re-triggered later from this queue by
                    // moving message to the retry queue "cart.item.queue.retry"
                    jmsTemplate.convertAndSend(stageItemQ, item);

                }
            }
        }
    }

    private CartItem getEntity(CartItemType cartItemType) {

        AppUser user = null;
        if (cartItemType.getUser() != null) {
            user = userRepo.findAppUserByEmail(cartItemType.getUser().getEmail());
        }

        return CartItemUtil.convertToCartItemBean(cartItemType, user);
    }

    @Override
    @JmsListener(destination = "retry.cart.item.queue")
    @Transactional
    public void retrySavingCartMessage(CartItem cartItem) {

        try {
            tryFailedItem(cartItem, 0);

        } catch (CouldNotCreateCartItemException e1) {

            // Save the cart item into stage-queue, message can be re-triggered later from this queue by
            // moving message to the retry queue "cart.item.queue.retry"
            jmsTemplate.convertAndSend(stageItemQ, cartItem);

        }
    }

    @Override
    public CartItemTypes getCartItemsByUserId(String userId) throws CouldNotRetrieveCartItemsException {

        try {
            List<CartItem> cartItems =  cartItemRepo.findCartItemByUserId(userId);

            CartItemTypes cartItemTypes = new CartItemTypes();
            for(CartItem item: cartItems) {
                cartItemTypes.getItems().add(CartItemUtil.convertToCartItemDTO(item));
            }

            return cartItemTypes;

        } catch (ItemConversionException e) {
            throw new CouldNotRetrieveCartItemsException(e);
        }

    }


    private void saveItem(CartItem item) throws DatabaseException {

        try {
            cartItemRepo.save(item);

        } catch (Exception e) { // DB related exception

            log.error("Exception occurred while saving cart item: " + item, e);
            throw new DatabaseException("Exception occurred while saving cart item: " + item, e);
        }
    }

    private void tryFailedItem(CartItem item, int tryCount) throws CouldNotCreateCartItemException {

        try {

            // Wait for some time
            try {
                // get the wait time from configuration
                long waitTime = Long.parseLong(retrySeq.split(",")[tryCount++]) * WAIT_TIME_MULTIPLIER;
                Thread.sleep(waitTime);

            } catch (InterruptedException e) {
                log.error("Thread interrupted ", e);
            }

            // try saving the item again
            saveItem(item);

        } catch (DatabaseException e) {

            // Check if maximum try count reached - if not retry again OR put the message into error-queue
            if(tryCount < Integer.parseInt(maxRetry)) {

                tryFailedItem(item, tryCount);

            } else {
                throw new CouldNotCreateCartItemException("Could not save cart item after maximum try count: " +  maxRetry, e);
            }
        }
    }
}
