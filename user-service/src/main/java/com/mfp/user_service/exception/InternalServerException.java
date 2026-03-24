package com.mfp.user_service.exception;

public class InternalServerException extends BaseException {
    public InternalServerException(String message) {
        super(message, "INTERNAL_SERVER_ERROR");
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, "INTERNAL_SERVER_ERROR");
        initCause(cause);
    }
}
