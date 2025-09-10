package com.legacykeep.legacy.exception;

import com.legacykeep.legacy.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the Legacy Service.
 * 
 * Provides centralized exception handling with consistent error responses
 * and proper HTTP status codes.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle custom Legacy Service exceptions
     */
    @ExceptionHandler(LegacyException.class)
    public ResponseEntity<ApiResponse<Void>> handleLegacyException(LegacyException ex) {
        log.error("Legacy Service Exception: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);
        
        HttpStatus status = determineHttpStatus(ex);
        
        return ResponseEntity.status(status)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage(), ex.getArgs()));
    }

    /**
     * Handle content not found exceptions
     */
    @ExceptionHandler(ContentNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleContentNotFoundException(ContentNotFoundException ex) {
        log.warn("Content not found: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("LEGACY_CONTENT_NOT_FOUND", ex.getMessage()));
    }

    /**
     * Handle bucket not found exceptions
     */
    @ExceptionHandler(BucketNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleBucketNotFoundException(BucketNotFoundException ex) {
        log.warn("Bucket not found: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("LEGACY_BUCKET_NOT_FOUND", ex.getMessage()));
    }

    /**
     * Handle category not found exceptions
     */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        log.warn("Category not found: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("LEGACY_CATEGORY_NOT_FOUND", ex.getMessage()));
    }

    /**
     * Handle recipient not found exceptions
     */
    @ExceptionHandler(RecipientNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleRecipientNotFoundException(RecipientNotFoundException ex) {
        log.warn("Recipient not found: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("LEGACY_RECIPIENT_NOT_FOUND", ex.getMessage()));
    }

    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex) {
        log.warn("Validation failed: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("LEGACY_VALIDATION_ERROR", ex.getMessage(), ex.getValidationErrors()));
    }

    /**
     * Handle permission denied exceptions
     */
    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handlePermissionDeniedException(PermissionDeniedException ex) {
        log.warn("Permission denied: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("LEGACY_PERMISSION_DENIED", ex.getMessage()));
    }

    /**
     * Handle method argument validation exceptions
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Method argument validation failed: {}", ex.getMessage());
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("LEGACY_VALIDATION_ERROR", "Validation failed", errors));
    }

    /**
     * Handle binding exceptions
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException ex) {
        log.warn("Binding validation failed: {}", ex.getMessage());
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("LEGACY_VALIDATION_ERROR", "Validation failed", errors));
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("LEGACY_VALIDATION_ERROR", "Constraint violation", errors));
    }

    /**
     * Handle missing request parameter exceptions
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getMessage());
        
        String message = "Missing required parameter: " + ex.getParameterName();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("LEGACY_MISSING_PARAMETER", message));
    }

    /**
     * Handle method argument type mismatch exceptions
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        log.warn("Method argument type mismatch: {}", ex.getMessage());
        
        String message = "Invalid parameter type for '" + ex.getName() + "': expected " + 
                        ex.getRequiredType().getSimpleName();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("LEGACY_INVALID_PARAMETER_TYPE", message));
    }

    /**
     * Handle HTTP message not readable exceptions
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        log.warn("HTTP message not readable: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("LEGACY_INVALID_REQUEST_BODY", "Invalid request body format"));
    }

    /**
     * Handle HTTP request method not supported exceptions
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        log.warn("HTTP method not supported: {}", ex.getMessage());
        
        String message = "Method " + ex.getMethod() + " not supported for this endpoint";
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error("LEGACY_METHOD_NOT_ALLOWED", message));
    }

    /**
     * Handle no handler found exceptions
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("No handler found: {}", ex.getMessage());
        
        String message = "Endpoint not found: " + ex.getRequestURL();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("LEGACY_ENDPOINT_NOT_FOUND", message));
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("LEGACY_INVALID_ARGUMENT", ex.getMessage()));
    }

    /**
     * Handle illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("LEGACY_ILLEGAL_STATE", ex.getMessage()));
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("LEGACY_INTERNAL_ERROR", 
                        "An unexpected error occurred. Please try again later."));
    }

    /**
     * Determine HTTP status code based on exception type
     */
    private HttpStatus determineHttpStatus(LegacyException ex) {
        return switch (ex.getErrorCode()) {
            case "LEGACY_CONTENT_NOT_FOUND", "LEGACY_BUCKET_NOT_FOUND", 
                 "LEGACY_CATEGORY_NOT_FOUND", "LEGACY_RECIPIENT_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "LEGACY_VALIDATION_ERROR", "LEGACY_INVALID_ARGUMENT" -> HttpStatus.BAD_REQUEST;
            case "LEGACY_PERMISSION_DENIED" -> HttpStatus.FORBIDDEN;
            case "LEGACY_ILLEGAL_STATE" -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
