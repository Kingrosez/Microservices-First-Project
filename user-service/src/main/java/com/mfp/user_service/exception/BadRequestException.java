package com.mfp.user_service.exception;

public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(message, "BAD_REQUEST");
    }
}
