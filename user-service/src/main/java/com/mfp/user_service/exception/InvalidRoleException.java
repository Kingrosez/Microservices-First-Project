package com.mfp.user_service.exception;

public class InvalidRoleException extends BaseException {
    public InvalidRoleException(String role) {
        super("Invalid role '" + role + "'. Allowed roles are: USER, ADMIN", "INVALID_ROLE");
    }
}
