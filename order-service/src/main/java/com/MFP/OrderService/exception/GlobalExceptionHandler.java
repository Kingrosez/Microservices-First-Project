package com.MFP.OrderService.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request){
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err->err.getField()+": "+err.getDefaultMessage())
                .findFirst()
                .orElse("Validation Failed");
        log.warn("Validation Error: [{}]", message);

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400,
                        "Validation Error",
                        message,
                        request.getRequestURI()
                )
        );
    }
    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ErrorResponse> handelInvalidOrderException(InvalidOrderException ex, HttpServletRequest request){
        log.warn("Invalid Order Exception: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400,
                        "Invalid_Order",
                        ex.getMessage(),
                        request.getRequestURI()
                        )
        );
    }

    @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleOrderAlreadyExists(OrderAlreadyExistsException ex, HttpServletRequest request){
        log.warn("Order Already Exists Exception: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        409,
                        "Order_Already_Exists",
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handelOrderNotFoundException(OrderNotFoundException ex, HttpServletRequest request){
        log.warn("Order Not Found Exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(
                        404,
                        "Order_NotFound",
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request){
        log.warn("Unhandled Exception: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        500,
                        "Internal Server Error",
                        "something went wrong please try again later",
                        request.getRequestURI()
                )
        );
    }
}
