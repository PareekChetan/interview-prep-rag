package com.chetan.interviewprep.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

// @RestControllerAdvice makes this a global safety net - any exception thrown
// from any @RestController in the app gets caught here instead of leaking a
// raw stack trace to whoever called the API.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Triggered when @Valid on a request body fails (e.g. missing "company" field)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ApiError(400, message, LocalDateTime.now()));
    }

    // Catch-all for anything else (DB connection issues, null pointers, etc.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.internalServerError()
                .body(new ApiError(500, ex.getMessage(), LocalDateTime.now()));
    }
}
