package com.legacykeep.legacy.exception;

import java.util.UUID;

/**
 * Exception thrown when legacy content is not found.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public class ContentNotFoundException extends LegacyException {
    
    public ContentNotFoundException(UUID contentId) {
        super("LEGACY_CONTENT_NOT_FOUND", 
              "Legacy content not found with ID: " + contentId, 
              contentId);
    }
    
    public ContentNotFoundException(String message) {
        super("LEGACY_CONTENT_NOT_FOUND", message);
    }
}
