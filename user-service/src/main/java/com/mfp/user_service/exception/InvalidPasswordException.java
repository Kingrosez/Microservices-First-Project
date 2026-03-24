package com.mfp.user_service.exception;

public class InvalidPasswordException extends BaseException {
    public InvalidPasswordException() {
        super("Password must be at least 8 characters long and contain alphanumeric characters", "INVALID_PASSWORD");
    }

    public InvalidPasswordException(String message) {
        super(message, "INVALID_PASSWORD");
    }
}
