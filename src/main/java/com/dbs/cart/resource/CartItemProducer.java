package com.dbs.cart.resource;

import com.dbs.cart.domain.CartItem;
import com.dbs.cart.generated.CartItemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.Queue;

@RestController
@RequestMapping("/cart")
public class CartItemProducer {

        @Autowired
        private JmsTemplate jmsTemplate;

        @Autowired
        private Queue queue;

        @PostMapping("/addItem")
        @ResponseBody
        public CartItemType addItem(CartItemType cartItem) {
            jmsTemplate.convertAndSend(queue, cartItem);
            return cartItem;
        }
}
