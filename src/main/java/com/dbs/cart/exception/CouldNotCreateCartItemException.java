package com.dbs.cart.exception;

import org.springframework.jms.support.converter.MessageConversionException;

public class CouldNotCreateCartItemException extends Exception {

    public CouldNotCreateCartItemException(String message, Throwable cause) {
        super(message, cause);
    }
}
