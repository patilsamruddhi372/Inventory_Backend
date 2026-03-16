package com.Inventory.Inventory_Backend.sales.common.exception;

import com.Inventory.Inventory_Backend.sales.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ==========================================================
    // RESOURCE NOT FOUND
    // ==========================================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {

        log.warn("Resource not found: {}", ex.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    // ==========================================================
    // STOCK ERROR
    // ==========================================================

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {

        log.warn("Insufficient stock: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // ==========================================================
    // ILLEGAL ARGUMENT
    // ==========================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {

        log.warn("Bad request: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // ==========================================================
    // MISSING HEADER
    // ==========================================================

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException ex) {

        log.warn("Missing header: {}", ex.getHeaderName());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Missing required header: " + ex.getHeaderName(),
                null
        );
    }

    // ==========================================================
    // VALIDATION ERRORS
    // ==========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        fieldErrors.put(error.getField(), error.getDefaultMessage())
                );

        log.warn("Validation failed: {}", fieldErrors);

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                fieldErrors
        );
    }

    // ==========================================================
    // INVALID JSON / BODY
    // ==========================================================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBody(HttpMessageNotReadableException ex) {

        log.warn("Invalid request body");

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body",
                null
        );
    }

    // ==========================================================
    // BUSINESS / RUNTIME EXCEPTIONS
    // ==========================================================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {

        log.warn("Runtime exception: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                null
        );
    }

    // ==========================================================
    // GENERIC EXCEPTION
    // ==========================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {

        log.error("Unhandled exception", ex);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                null
        );
    }

    // ==========================================================
    // HELPER
    // ==========================================================

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            Map<String, String> fieldErrors
    ) {

        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(error);
    }


}