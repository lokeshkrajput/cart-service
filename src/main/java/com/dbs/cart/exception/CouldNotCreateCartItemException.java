package com.dbs.cart.exception;

import org.springframework.jms.support.converter.MessageConversionException;

public class CouldNotCreateCartItemException extends Exception {

    public CouldNotCreateCartItemException(Exception e) {
        super(e);
    }

    public CouldNotCreateCartItemException(String message) {
        super(message);
    }

    public CouldNotCreateCartItemException(String message, Throwable cause) {
        super(message, cause);
    }
}
