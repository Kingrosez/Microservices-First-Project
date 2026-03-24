package com.mfp.user_service.exception;

public abstract class BaseException extends RuntimeException {
    private final String errorCode;
    protected BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
