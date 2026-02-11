package com.MFP.OrderService.exception;

public class OrderAlreadyExistsException extends RuntimeException{
    public OrderAlreadyExistsException(String message) {
        super(message);
    }
}
