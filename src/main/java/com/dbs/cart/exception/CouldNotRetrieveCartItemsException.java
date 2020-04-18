package com.dbs.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Could not retrieve items due to system failure")
public class CouldNotRetrieveCartItemsException extends Exception {

    public CouldNotRetrieveCartItemsException(Exception cause) {
        super(cause);
    }
}
