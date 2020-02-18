package com.dbs.cart.resource;

import ch.qos.logback.classic.Logger;
import com.dbs.cart.generated.CartItemType;
import com.dbs.cart.generated.ResultType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.Queue;

@RestController
@RequestMapping("/cart")
public class CartItemProducer {

    private static Logger LOG = (Logger) LoggerFactory.getLogger(CartItemProducer.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Queue queue;

    @PostMapping("/addItem")
    @ResponseBody
    public ResultType addItem(@RequestBody CartItemType cartItem) {
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

        return ResultType.SUCCESS;
    }
}
