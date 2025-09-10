package com.legacykeep.legacy.enums;

/**
 * Enum representing the triggers for inheritance rules.
 * Defines when inheritance should occur.
 */
public enum InheritanceTrigger {
    
    /**
     * Inherit immediately for existing relationships
     */
    IMMEDIATE("Immediate", "Inherit immediately for existing relationships"),
    
    /**
     * Inherit on specific events (e.g., new relationship, birthday)
     */
    EVENT_BASED("Event Based", "Inherit on specific events"),
    
    /**
     * Inherit on time-based conditions (e.g., age milestones, anniversaries)
     */
    TIME_BASED("Time Based", "Inherit on time-based conditions"),
    
    /**
     * Manual inheritance approval required
     */
    MANUAL("Manual", "Manual inheritance approval required");

    private final String displayName;
    private final String description;

    InheritanceTrigger(String displayName, String description) {
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
     * Get inheritance trigger by name (case-insensitive)
     */
    public static InheritanceTrigger fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (InheritanceTrigger trigger : values()) {
            if (trigger.name().equalsIgnoreCase(name)) {
                return trigger;
            }
        }
        
        throw new IllegalArgumentException("Unknown inheritance trigger: " + name);
    }

    /**
     * Check if this trigger requires immediate action
     */
    public boolean requiresImmediateAction() {
        return this == IMMEDIATE;
    }

    /**
     * Check if this trigger is event-driven
     */
    public boolean isEventDriven() {
        return this == EVENT_BASED;
    }

    /**
     * Check if this trigger is time-driven
     */
    public boolean isTimeDriven() {
        return this == TIME_BASED;
    }

    /**
     * Check if this trigger requires manual approval
     */
    public boolean requiresManualApproval() {
        return this == MANUAL;
    }
}
