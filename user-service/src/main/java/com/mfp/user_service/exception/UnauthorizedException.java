package com.mfp.user_service.exception;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }

    public UnauthorizedException() {
        super("User is not authorized to perform this action", "UNAUTHORIZED");
    }
}
