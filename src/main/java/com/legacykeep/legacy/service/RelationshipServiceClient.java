package com.legacykeep.legacy.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Client interface for communicating with the Relationship Service.
 * Provides methods to query relationships for inheritance targeting.
 */
public interface RelationshipServiceClient {

    /**
     * Get all active relationships for a user.
     * 
     * @param userId The user ID
     * @return List of active relationships
     */
    List<RelationshipInfo> getActiveUserRelationships(UUID userId);

    /**
     * Get relationships by relationship type name.
     * 
     * @param userId The user ID
     * @param relationshipTypeName The relationship type name (e.g., "Son", "Daughter", "Father", "Mother")
     * @return List of relationships matching the type
     */
    List<RelationshipInfo> getRelationshipsByType(UUID userId, String relationshipTypeName);

    /**
     * Get relationships by relationship category.
     * 
     * @param userId The user ID
     * @param category The relationship category (FAMILY, SOCIAL, PROFESSIONAL, CUSTOM)
     * @return List of relationships in the category
     */
    List<RelationshipInfo> getRelationshipsByCategory(UUID userId, String category);

    /**
     * Get all users who have a specific relationship type with the given user.
     * 
     * @param userId The user ID
     * @param relationshipTypeName The relationship type name
     * @return List of user IDs who have the specified relationship
     */
    List<UUID> getUsersByRelationshipType(UUID userId, String relationshipTypeName);

    /**
     * Get all users in a specific relationship category with the given user.
     * 
     * @param userId The user ID
     * @param category The relationship category
     * @return List of user IDs in the category
     */
    List<UUID> getUsersByRelationshipCategory(UUID userId, String category);

    /**
     * Check if a relationship exists between two users.
     * 
     * @param user1Id First user ID
     * @param user2Id Second user ID
     * @return True if relationship exists, false otherwise
     */
    boolean relationshipExists(UUID user1Id, UUID user2Id);

    /**
     * Check if an active relationship exists between two users.
     * 
     * @param user1Id First user ID
     * @param user2Id Second user ID
     * @return True if active relationship exists, false otherwise
     */
    boolean activeRelationshipExists(UUID user1Id, UUID user2Id);

    /**
     * Get relationship type information by name.
     * 
     * @param relationshipTypeName The relationship type name
     * @return Relationship type information or null if not found
     */
    RelationshipTypeInfo getRelationshipTypeByName(String relationshipTypeName);

    /**
     * Get all relationship types in a category.
     * 
     * @param category The relationship category
     * @return List of relationship type information
     */
    List<RelationshipTypeInfo> getRelationshipTypesByCategory(String category);

    /**
     * Get all family members for a family.
     * 
     * @param familyId The family ID
     * @return List of family member information
     */
    List<Map<String, Object>> getFamilyMembers(UUID familyId);

    /**
     * Get user information by user ID.
     * 
     * @param userId The user ID
     * @return User information map
     */
    Map<String, Object> getUserInfo(UUID userId);

    /**
     * Data class for relationship information.
     */
    class RelationshipInfo {
        private final UUID relationshipId;
        private final UUID user1Id;
        private final UUID user2Id;
        private final String relationshipTypeName;
        private final String relationshipCategory;
        private final String status;
        private final String metadata;

        public RelationshipInfo(UUID relationshipId, UUID user1Id, UUID user2Id, 
                              String relationshipTypeName, String relationshipCategory, 
                              String status, String metadata) {
            this.relationshipId = relationshipId;
            this.user1Id = user1Id;
            this.user2Id = user2Id;
            this.relationshipTypeName = relationshipTypeName;
            this.relationshipCategory = relationshipCategory;
            this.status = status;
            this.metadata = metadata;
        }

        // Getters
        public UUID getRelationshipId() { return relationshipId; }
        public UUID getUser1Id() { return user1Id; }
        public UUID getUser2Id() { return user2Id; }
        public String getRelationshipTypeName() { return relationshipTypeName; }
        public String getRelationshipCategory() { return relationshipCategory; }
        public String getStatus() { return status; }
        public String getMetadata() { return metadata; }

        /**
         * Get the other user ID in the relationship.
         * 
         * @param currentUserId The current user ID
         * @return The other user ID
         */
        public UUID getOtherUserId(UUID currentUserId) {
            return user1Id.equals(currentUserId) ? user2Id : user1Id;
        }

        /**
         * Check if the relationship is active.
         * 
         * @return True if active, false otherwise
         */
        public boolean isActive() {
            return "ACTIVE".equals(status);
        }
    }

    /**
     * Data class for relationship type information.
     */
    class RelationshipTypeInfo {
        private final Long id;
        private final String name;
        private final String category;
        private final Boolean bidirectional;
        private final String metadata;

        public RelationshipTypeInfo(Long id, String name, String category, 
                                  Boolean bidirectional, String metadata) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.bidirectional = bidirectional;
            this.metadata = metadata;
        }

        // Getters
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public Boolean getBidirectional() { return bidirectional; }
        public String getMetadata() { return metadata; }

        /**
         * Check if this relationship type is bidirectional.
         * 
         * @return True if bidirectional, false otherwise
         */
        public boolean isBidirectional() {
            return Boolean.TRUE.equals(bidirectional);
        }
    }
}
