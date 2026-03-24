package com.mfp.user_service.exception;

public class InvalidEmailException extends BaseException {
    public InvalidEmailException(String email) {
        super("Email '" + email + "' is invalid", "INVALID_EMAIL");
    }
}
