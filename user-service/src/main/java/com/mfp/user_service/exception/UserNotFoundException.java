package com.mfp.user_service.exception;

public class UserNotFoundException extends BaseException{
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found","USER_NOT_FOUND");
    }
}
