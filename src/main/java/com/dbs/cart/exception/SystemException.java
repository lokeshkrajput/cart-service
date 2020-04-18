package com.dbs.cart.exception;

/**
 * Class caters the programming exceptions
 */
public class SystemException extends RuntimeException {

    public SystemException(Exception e) {
        super(e);
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
