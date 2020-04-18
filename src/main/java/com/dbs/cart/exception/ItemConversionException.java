package com.dbs.cart.exception;

public class ItemConversionException extends SystemException {

    public ItemConversionException(Exception e) {
        super(e);
    }

    public ItemConversionException(String message) {
        super(message);
    }

    public ItemConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
