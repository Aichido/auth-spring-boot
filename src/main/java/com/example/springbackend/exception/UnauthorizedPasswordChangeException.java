package com.example.springbackend.exception;

public class UnauthorizedPasswordChangeException extends RuntimeException {

    public UnauthorizedPasswordChangeException(String message) {
        super(message);
    }
}
