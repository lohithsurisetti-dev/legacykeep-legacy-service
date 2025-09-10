package com.legacykeep.legacy.exception;

import java.util.UUID;

/**
 * Exception thrown when legacy category is not found.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public class CategoryNotFoundException extends LegacyException {
    
    public CategoryNotFoundException(UUID categoryId) {
        super("LEGACY_CATEGORY_NOT_FOUND", 
              "Legacy category not found with ID: " + categoryId, 
              categoryId);
    }
    
    public CategoryNotFoundException(String message) {
        super("LEGACY_CATEGORY_NOT_FOUND", message);
    }
}
