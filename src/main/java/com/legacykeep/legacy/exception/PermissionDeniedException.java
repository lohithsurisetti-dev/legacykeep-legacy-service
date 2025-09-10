package com.legacykeep.legacy.exception;

/**
 * Exception thrown when user doesn't have permission to perform an action.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public class PermissionDeniedException extends LegacyException {
    
    public PermissionDeniedException(String message) {
        super("LEGACY_PERMISSION_DENIED", message);
    }
    
    public PermissionDeniedException(String resource, String action) {
        super("LEGACY_PERMISSION_DENIED", 
              "Permission denied: Cannot " + action + " " + resource);
    }
}
