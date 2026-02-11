package com.MFP.OrderService.exception;

public abstract class BusinessException extends RuntimeException{
    public BusinessException(String message){
        super(message);
    }
}
