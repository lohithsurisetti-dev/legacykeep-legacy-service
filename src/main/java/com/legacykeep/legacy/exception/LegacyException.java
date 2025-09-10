package com.legacykeep.legacy.exception;

/**
 * Base exception class for all Legacy Service exceptions.
 * 
 * Provides a common structure for all custom exceptions in the Legacy Service.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public class LegacyException extends RuntimeException {
    
    private final String errorCode;
    private final Object[] args;
    
    public LegacyException(String message) {
        super(message);
        this.errorCode = "LEGACY_ERROR";
        this.args = new Object[0];
    }
    
    public LegacyException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "LEGACY_ERROR";
        this.args = new Object[0];
    }
    
    public LegacyException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
    
    public LegacyException(String errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }
    
    public LegacyException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object[] getArgs() {
        return args;
    }
}
