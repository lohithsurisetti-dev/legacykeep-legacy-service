package com.legacykeep.legacy.exception;

import java.util.UUID;

/**
 * Exception thrown when legacy bucket is not found.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public class BucketNotFoundException extends LegacyException {
    
    public BucketNotFoundException(UUID bucketId) {
        super("LEGACY_BUCKET_NOT_FOUND", 
              "Legacy bucket not found with ID: " + bucketId, 
              bucketId);
    }
    
    public BucketNotFoundException(String message) {
        super("LEGACY_BUCKET_NOT_FOUND", message);
    }
}
