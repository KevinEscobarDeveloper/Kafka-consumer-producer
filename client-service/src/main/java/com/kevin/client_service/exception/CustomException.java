package com.kevin.client_service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomException extends Exception {

    static final long serialVersionUID = -3387516993124229948L;


    private final int httpCode;
    private final int internalCode;

    public CustomException(final String message, final Throwable cause, final int httpCode, final int internalCode) {
        super(message,cause);
        this.httpCode = httpCode;
        this.internalCode = internalCode;
    }

    public CustomException(final String message, final int httpCode, final int internalCode) {
        super(message);
        this.httpCode = httpCode;
        this.internalCode = internalCode;
    }

}
