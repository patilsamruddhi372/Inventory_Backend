package com.Inventory.Inventory_Backend.purchase.exception;

import com.Inventory.Inventory_Backend.sales.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.Inventory.Inventory_Backend.purchase")
@Slf4j
public class PurchaseModuleExceptionHandler {

    // =========================================================
    // PURCHASE NOT FOUND
    // =========================================================

    @ExceptionHandler(PurchaseInvoiceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(PurchaseInvoiceNotFoundException ex) {

        log.warn("Purchase invoice not found: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }

    // =========================================================
    // DUPLICATE BILL NUMBER
    // =========================================================

    @ExceptionHandler(DuplicateBillNumberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateBillNumberException ex) {

        log.warn("Duplicate bill number detected: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }

    // =========================================================
    // BAD REQUEST
    // =========================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {

        log.warn("Bad request in purchase module: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                null
        );
    }

    // =========================================================
    // VALIDATION ERRORS
    // =========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        log.warn("Validation error in purchase module: {}", fieldErrors);

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                fieldErrors
        );
    }

    // =========================================================
    // GENERIC ERROR
    // =========================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {

        log.error("Unexpected error in purchase module", ex);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                null
        );
    }

    // =========================================================
    // RESPONSE BUILDER
    // =========================================================

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            Map<String, String> fieldErrors) {

        ErrorResponse response = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}