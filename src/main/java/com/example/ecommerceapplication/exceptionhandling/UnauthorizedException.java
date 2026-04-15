package com.example.ecommerceapplication.exceptionhandling;



public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}