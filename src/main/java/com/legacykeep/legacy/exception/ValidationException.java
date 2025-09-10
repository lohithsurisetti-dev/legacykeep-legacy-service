package com.legacykeep.legacy.exception;

import java.util.List;

/**
 * Exception thrown when validation fails.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public class ValidationException extends LegacyException {
    
    private final List<String> validationErrors;
    
    public ValidationException(String message) {
        super("LEGACY_VALIDATION_ERROR", message);
        this.validationErrors = List.of(message);
    }
    
    public ValidationException(List<String> validationErrors) {
        super("LEGACY_VALIDATION_ERROR", "Validation failed: " + String.join(", ", validationErrors));
        this.validationErrors = validationErrors;
    }
    
    public ValidationException(String message, List<String> validationErrors) {
        super("LEGACY_VALIDATION_ERROR", message);
        this.validationErrors = validationErrors;
    }
    
    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
