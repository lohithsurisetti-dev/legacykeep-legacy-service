package com.legacykeep.legacy.enums;

/**
 * Enum representing the types of targeting for inheritance rules.
 * Defines how content should be targeted for inheritance.
 */
public enum TargetType {
    
    /**
     * Target specific relationship types (e.g., "Son", "Daughter", "Friend")
     */
    RELATIONSHIP_TYPE("Relationship Type", "Target specific relationship types"),
    
    /**
     * Target by relationship category (e.g., "FAMILY", "SOCIAL", "PROFESSIONAL", "CUSTOM")
     */
    RELATIONSHIP_CATEGORY("Relationship Category", "Target by relationship category"),
    
    /**
     * Target by generation level (e.g., "CHILDREN", "GRANDCHILDREN")
     */
    GENERATION("Generation", "Target by generation level"),
    
    /**
     * Target by relationship context (e.g., "FAMILY", "SOCIAL", "PROFESSIONAL")
     */
    CONTEXT("Context", "Target by relationship context"),
    
    /**
     * Custom targeting with user-defined logic
     */
    CUSTOM("Custom", "Custom targeting with user-defined logic");

    private final String displayName;
    private final String description;

    TargetType(String displayName, String description) {
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
     * Get target type by name (case-insensitive)
     */
    public static TargetType fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (TargetType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unknown target type: " + name);
    }

    /**
     * Check if this is a predefined target type
     */
    public boolean isPredefined() {
        return this != CUSTOM;
    }

    /**
     * Check if this target type allows custom logic
     */
    public boolean allowsCustomLogic() {
        return this == CUSTOM;
    }
}
