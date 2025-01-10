package com.kevin.order_producer_service.exception;

public class KafkaCustomException extends RuntimeException{
    public KafkaCustomException(String message) {
        super(message);
    }

    public KafkaCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}
