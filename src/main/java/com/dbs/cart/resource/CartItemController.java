package com.dbs.cart.resource;

import ch.qos.logback.classic.Logger;
import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.generated.CartItemTypes;
import com.dbs.cart.generated.ResultType;
import com.dbs.cart.service.CartItemService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.Queue;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartItemController {

    private static Logger LOG = (Logger) LoggerFactory.getLogger(CartItemController.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Queue queue;

    @Autowired
    private CartItemService cartService;

    @PostMapping(value="/addItem", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String addItem(@RequestBody CartItemType cartItem) {
            /*
            Example Input::
            {
              "itemCode": "1",
              "itemName": "item1",
              "itemDesc": "item Desc",
              "itemPrice": 20.00,
              "itemLocation": "SG",
              "timeAdded": null,
              "user": {
                "userId": "user1",
                "name": "Lokesh",
                "email": "lokesh.persistent@gmail.com"
              }
            }
         */

        LOG.info("Received Item: " + cartItem);
        jmsTemplate.convertAndSend(queue, cartItem);
        LOG.info("Item Sent to persist...");

        return ResultType.SUCCESS.name();
    }

    @GetMapping(value="/getItemByUserId/{userId}", produces = "application/json")
    @ResponseBody
    public CartItemTypes getItem(@PathVariable String userId) {

        LOG.info("Received request to get cart Items for User: " + userId);
        return cartService.getCartItemsByUserId(userId);

    }

}
