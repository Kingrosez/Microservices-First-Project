package com.mfp.user_service.exception;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String message) {
        super(message, "FORBIDDEN");
    }

    public ForbiddenException() {
        super("Access to this resource is forbidden", "FORBIDDEN");
    }
}
