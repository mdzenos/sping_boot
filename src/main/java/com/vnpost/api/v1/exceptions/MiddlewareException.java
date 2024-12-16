package com.vnpost.api.v1.exceptions;

public class MiddlewareException extends RuntimeException {
    public MiddlewareException(String message) {
        super(message);
    }
}
