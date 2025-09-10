package com.legacykeep.legacy.service;

import com.legacykeep.legacy.entity.LegacyContent;
import com.legacykeep.legacy.entity.LegacyRecipient;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing content permissions and access control.
 * Handles generation-based access control, privacy settings, and content sharing controls.
 */
public interface PermissionService {

    /**
     * Check if a user has access to content based on privacy level and relationships
     * 
     * @param userId The user ID requesting access
     * @param content The content to check access for
     * @param userGenerationLevel The user's generation level
     * @param isFamilyMember Whether the user is a family member
     * @param isExtendedFamilyMember Whether the user is an extended family member
     * @return True if user has access, false otherwise
     */
    boolean hasContentAccess(UUID userId, LegacyContent content, Integer userGenerationLevel, 
                           boolean isFamilyMember, boolean isExtendedFamilyMember);

    /**
     * Check if a user has specific access level to content
     * 
     * @param userId The user ID requesting access
     * @param content The content to check access for
     * @param requiredAccessLevel The required access level
     * @param userGenerationLevel The user's generation level
     * @param isFamilyMember Whether the user is a family member
     * @param isExtendedFamilyMember Whether the user is an extended family member
     * @return True if user has the required access level, false otherwise
     */
    boolean hasAccessLevel(UUID userId, LegacyContent content, LegacyRecipient.AccessLevel requiredAccessLevel,
                          Integer userGenerationLevel, boolean isFamilyMember, boolean isExtendedFamilyMember);

    /**
     * Get the effective access level for a user on content
     * 
     * @param userId The user ID
     * @param content The content
     * @param userGenerationLevel The user's generation level
     * @param isFamilyMember Whether the user is a family member
     * @param isExtendedFamilyMember Whether the user is an extended family member
     * @return The effective access level for the user
     */
    LegacyRecipient.AccessLevel getEffectiveAccessLevel(UUID userId, LegacyContent content, Integer userGenerationLevel,
                                      boolean isFamilyMember, boolean isExtendedFamilyMember);

    /**
     * Check if content can be inherited by a specific generation
     * 
     * @param content The content to check
     * @param targetGenerationLevel The target generation level
     * @return True if content can be inherited by the target generation
     */
    boolean canInheritContent(LegacyContent content, Integer targetGenerationLevel);

    /**
     * Get all users who have access to content based on privacy settings
     * 
     * @param content The content
     * @param familyMembers List of family member IDs
     * @param extendedFamilyMembers List of extended family member IDs
     * @return List of user IDs who have access
     */
    List<UUID> getAccessibleUsers(LegacyContent content, List<UUID> familyMembers, 
                                 List<UUID> extendedFamilyMembers);

    /**
     * Validate privacy level inheritance rules
     * 
     * @param parentPrivacyLevel The parent content's privacy level
     * @param childPrivacyLevel The child content's privacy level
     * @return True if the privacy level inheritance is valid
     */
    boolean validatePrivacyInheritance(LegacyContent.PrivacyLevel parentPrivacyLevel, LegacyContent.PrivacyLevel childPrivacyLevel);

    /**
     * Get the default privacy level for content based on generation level
     * 
     * @param generationLevel The generation level
     * @return The default privacy level for the generation
     */
    LegacyContent.PrivacyLevel getDefaultPrivacyLevelForGeneration(Integer generationLevel);

    /**
     * Check if a user can modify content privacy settings
     * 
     * @param userId The user ID
     * @param content The content
     * @param newPrivacyLevel The new privacy level
     * @return True if user can modify privacy settings
     */
    boolean canModifyPrivacyLevel(UUID userId, LegacyContent content, LegacyContent.PrivacyLevel newPrivacyLevel);

    /**
     * Get content visibility rules based on generation level
     * 
     * @param contentGenerationLevel The content's generation level
     * @param userGenerationLevel The user's generation level
     * @return True if content should be visible to the user's generation
     */
    boolean isContentVisibleToGeneration(Integer contentGenerationLevel, Integer userGenerationLevel);

    /**
     * Check if content sharing is allowed between generations
     * 
     * @param fromGeneration The source generation level
     * @param toGeneration The target generation level
     * @return True if sharing is allowed
     */
    boolean isSharingAllowedBetweenGenerations(Integer fromGeneration, Integer toGeneration);
}
