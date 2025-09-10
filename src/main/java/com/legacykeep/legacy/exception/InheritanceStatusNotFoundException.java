package com.legacykeep.legacy.exception;

import org.springframework.http.HttpStatus;
import java.util.UUID;

/**
 * Exception thrown when inheritance status is not found.
 */
public class InheritanceStatusNotFoundException extends LegacyException {
    
    public InheritanceStatusNotFoundException(UUID recipientId, UUID contentId, UUID ruleId) {
        super("INHERITANCE_STATUS_NOT_FOUND", 
              "Inheritance status not found for recipient: " + recipientId + 
              ", content: " + contentId + ", rule: " + ruleId, 
              HttpStatus.NOT_FOUND);
    }
    
    public InheritanceStatusNotFoundException(String message) {
        super("INHERITANCE_STATUS_NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
