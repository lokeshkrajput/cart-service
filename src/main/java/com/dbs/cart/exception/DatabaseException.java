package com.dbs.cart.exception;

public class DatabaseException extends SystemException {

    public DatabaseException(Exception e) {
        super(e);
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
