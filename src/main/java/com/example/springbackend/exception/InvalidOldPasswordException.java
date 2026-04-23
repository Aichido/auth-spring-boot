package com.example.springbackend.exception;

public class InvalidOldPasswordException extends RuntimeException {

    public InvalidOldPasswordException(String message) {
        super(message);
    }
}
