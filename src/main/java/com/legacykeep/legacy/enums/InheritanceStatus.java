package com.legacykeep.legacy.enums;

/**
 * Enum representing the status of inheritance rules.
 * Defines the current state of an inheritance rule.
 */
public enum InheritanceStatus {
    
    /**
     * Rule is active and processing
     */
    ACTIVE("Active", "Rule is active and processing"),
    
    /**
     * Rule is paused temporarily
     */
    PAUSED("Paused", "Rule is paused temporarily"),
    
    /**
     * Rule has completed all inheritance
     */
    COMPLETED("Completed", "Rule has completed all inheritance"),
    
    /**
     * Rule has been cancelled
     */
    CANCELLED("Cancelled", "Rule has been cancelled");

    private final String displayName;
    private final String description;

    InheritanceStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get inheritance status by name (case-insensitive)
     */
    public static InheritanceStatus fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (InheritanceStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Unknown inheritance status: " + name);
    }

    /**
     * Check if this status allows processing
     */
    public boolean allowsProcessing() {
        return this == ACTIVE;
    }

    /**
     * Check if this status is paused
     */
    public boolean isPaused() {
        return this == PAUSED;
    }

    /**
     * Check if this status is completed
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * Check if this status is cancelled
     */
    public boolean isCancelled() {
        return this == CANCELLED;
    }
}
