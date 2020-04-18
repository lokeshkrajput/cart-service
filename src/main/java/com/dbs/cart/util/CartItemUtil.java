package com.dbs.cart.util;

import ch.qos.logback.classic.Logger;
import com.dbs.cart.domain.AppUser;
import com.dbs.cart.domain.CartItem;
import com.dbs.cart.exception.ItemConversionException;
import com.dbs.cart.generated.AppUserType;
import com.dbs.cart.generated.CartItemType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

public class CartItemUtil {

    private static Logger log = (Logger) LoggerFactory.getLogger(CartItemUtil.class);

    public static CartItem convertToCartItemBean(CartItemType cartItemType, AppUser user) {

        CartItem cartItem = new CartItem();

        try {
            BeanUtils.copyProperties(cartItemType, cartItem);

           if (user != null) {
               cartItem.setUser(user);
            } else {
                cartItem.setUser(new AppUser());
                BeanUtils.copyProperties(cartItemType.getUser(), cartItem.getUser());
            }

        } catch (BeansException e) {
            log.error("Message conversion failed", e);
            throw new ItemConversionException(e);
        }
        return cartItem;
    }

    public static CartItemType convertToCartItemDTO(CartItem cartItem) {

        CartItemType cartItemType = new CartItemType();

        try {
            BeanUtils.copyProperties(cartItem, cartItemType);

            if(cartItem.getUser() != null) {
                cartItemType.setUser(new AppUserType());
                BeanUtils.copyProperties(cartItem.getUser(), cartItemType.getUser());
            }
        } catch (BeansException e) {
            log.error("Message conversion failed", e);
            throw new ItemConversionException(e);
        }

        return cartItemType;
    }
}
