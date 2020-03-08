package com.dbs.cart.service.impl;

import ch.qos.logback.classic.Logger;
import com.dbs.cart.domain.AppUser;
import com.dbs.cart.domain.CartItem;
import com.dbs.cart.generated.AppUserType;
import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.generated.CartItemTypes;
import com.dbs.cart.repo.AppUserRepo;
import com.dbs.cart.repo.CartItemRepo;
import com.dbs.cart.service.CartItemService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.stereotype.Service;

import javax.jms.Queue;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
public class CartItemServiceImpl implements CartItemService {

    public static final long WAIT_TIME = 10000L;
    private static Logger log = (Logger) LoggerFactory.getLogger(CartItemServiceImpl.class);

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private AppUserRepo userRepo;

    @Autowired
    @Qualifier("failedItemQ")
    private Queue failedItemQ;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    @JmsListener(destination = "cart.item.queue")
    @Transactional
    public void saveCartMessage(CartItemType cartItemType) {

        try {
            log.info("Received <" + cartItemType + ">");
            cartItemRepo.save(convertToCartItem(cartItemType));
            log.info("Item Saved into DB!");

        } catch (MessageConversionException e) {
            // programming error- eat it
        } catch (Throwable th) {
            // fault tolerance - save into error queue
            log.error("Exception occurred while saving into DB", th);
            jmsTemplate.convertAndSend(failedItemQ, cartItemType);

            // retry with failed item from the failed queue
        }
    }

    @Override
    @JmsListener(destination = "cart.item.queue.fail")
    @Transactional
    public void tryFailedItem(CartItemType cartItem) {

        try {
            Thread.sleep(WAIT_TIME);
            saveCartMessage(cartItem);
        } catch (InterruptedException e) {
            //eat it
        }
    }

    @Override
    public CartItemTypes getCartItemsByUserId(String userId) {
        List<CartItem> cartItems =  cartItemRepo.findCartItemByUserId(userId);

        CartItemTypes cartItemTypes = new CartItemTypes();
        for(CartItem item: cartItems) {
            cartItemTypes.getItems().add(convertToCartItemType(item));
        }
        return cartItemTypes;
    }


    private CartItem convertToCartItem(CartItemType cartItemType) {

        CartItem cartItem = new CartItem();

        try{
            BeanUtils.copyProperties(cartItemType, cartItem);

            if(cartItemType.getUser() != null) {

                AppUser user = userRepo.findAppUserByEmail(cartItemType.getUser().getEmail());
                if(user != null) {
                    cartItem.setUser(user);
                } else {
                    cartItem.setUser(new AppUser());
                    BeanUtils.copyProperties(cartItemType.getUser(), cartItem.getUser());
                }
            }

        } catch (Exception e) {
            log.error("Message conversion failed", e);
            throw new MessageConversionException(e.getMessage());
        }
        return cartItem;
    }

    private CartItemType convertToCartItemType(CartItem cartItem) {

        CartItemType cartItemType = new CartItemType();

        try{
            BeanUtils.copyProperties(cartItem, cartItemType);

            if(cartItem.getUser() != null) {
                cartItemType.setUser(new AppUserType());
                BeanUtils.copyProperties(cartItem.getUser(), cartItemType.getUser());
            }

        } catch (Exception e) {
            log.error("Message conversion failed", e);
            throw new MessageConversionException(e.getMessage());
        }
        return cartItemType;
    }
}
