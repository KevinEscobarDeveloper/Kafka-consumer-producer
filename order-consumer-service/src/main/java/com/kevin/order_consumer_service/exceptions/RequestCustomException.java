package com.kevin.order_consumer_service.exceptions;

public class RequestCustomException extends RuntimeException {

    private final int statusCode;

    public RequestCustomException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
