package com.legacykeep.legacy.exception;

import java.util.UUID;

/**
 * Exception thrown when legacy recipient is not found.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public class RecipientNotFoundException extends LegacyException {
    
    public RecipientNotFoundException(UUID recipientId) {
        super("LEGACY_RECIPIENT_NOT_FOUND", 
              "Legacy recipient not found with ID: " + recipientId, 
              recipientId);
    }
    
    public RecipientNotFoundException(String message) {
        super("LEGACY_RECIPIENT_NOT_FOUND", message);
    }
}
