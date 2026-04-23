package com.example.springbackend.exception;

public class PasswordConfirmationMismatchException extends RuntimeException {

    public PasswordConfirmationMismatchException(String message) {
        super(message);
    }
}
