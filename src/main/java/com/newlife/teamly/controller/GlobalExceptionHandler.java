package com.newlife.teamly.controller;

import com.newlife.teamly.dto.MessageResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            message.append(error.getDefaultMessage()).append(". ");
        });
        return new ResponseEntity<>(new MessageResponse(message.toString().trim()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Database error: Possible duplicate entry or constraint violation.";
        String exceptionMessage = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        if (exceptionMessage.contains("username") || exceptionMessage.contains("users_username_key")) {
            message = "Username already exists. Please choose another one.";
        } else if (exceptionMessage.contains("email") || exceptionMessage.contains("users_email_key")) {
            message = "Email already exists. Please use a different email.";
        }
        return new ResponseEntity<>(new MessageResponse(message), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponse> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(new MessageResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGenericException(Exception ex) {
        return new ResponseEntity<>(new MessageResponse("An unexpected error occurred: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
