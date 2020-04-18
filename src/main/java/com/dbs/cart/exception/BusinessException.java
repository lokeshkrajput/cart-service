package com.dbs.cart.exception;

/**
 * Class caters the exceptions related to a business functionality
 */
public class BusinessException extends RuntimeException {

    public BusinessException(Exception e) {
        super(e);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
