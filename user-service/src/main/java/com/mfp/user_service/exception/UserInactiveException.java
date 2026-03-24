package com.mfp.user_service.exception;

public class UserInactiveException extends BaseException{
    public UserInactiveException(Long id) {
        super("User with id " + id + " inactive","USER_INACTIVE");
    }
}
