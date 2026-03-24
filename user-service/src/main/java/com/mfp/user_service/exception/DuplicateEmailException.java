package com.mfp.user_service.exception;

public class DuplicateEmailException extends BaseException {
    public DuplicateEmailException(String email) {
        super("User with email '" + email + "' already exists", "DUPLICATE_EMAIL");
    }
}
