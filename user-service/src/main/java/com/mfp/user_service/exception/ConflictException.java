package com.mfp.user_service.exception;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(message, "CONFLICT");
    }
}
