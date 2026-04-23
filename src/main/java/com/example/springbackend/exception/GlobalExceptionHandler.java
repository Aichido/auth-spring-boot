package com.example.springbackend.exception;

import com.example.springbackend.dto.ApiMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiMessageResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiMessageResponse(ex.getMessage()));
    }

    @ExceptionHandler({
        InvalidOldPasswordException.class,
        PasswordConfirmationMismatchException.class,
        WeakPasswordException.class
    })
    public ResponseEntity<ApiMessageResponse> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ApiMessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedPasswordChangeException.class)
    public ResponseEntity<ApiMessageResponse> handleForbidden(UnauthorizedPasswordChangeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiMessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiMessageResponse> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(new ApiMessageResponse("Invalid request payload"));
    }
}
