package com.legacykeep.legacy.service;

import com.legacykeep.legacy.dto.response.FamilyTreeResponse;

import java.util.UUID;

/**
 * Service for managing family tree data and visualization.
 * Provides hierarchical family structure with content statistics and permissions.
 */
public interface FamilyTreeService {

    /**
     * Get complete family tree structure for a family
     * 
     * @param familyId The family ID
     * @param requestingUserId The user requesting the tree (for permission calculations)
     * @return Complete family tree with relationships and content statistics
     */
    FamilyTreeResponse getFamilyTree(UUID familyId, UUID requestingUserId);

    /**
     * Get family tree for a specific user's perspective
     * 
     * @param userId The user ID to get tree perspective for
     * @param requestingUserId The user requesting the tree
     * @return Family tree centered around the specified user
     */
    FamilyTreeResponse getFamilyTreeForUser(UUID userId, UUID requestingUserId);

    /**
     * Get family tree with content filtering
     * 
     * @param familyId The family ID
     * @param requestingUserId The user requesting the tree
     * @param includePrivateContent Whether to include private content in statistics
     * @param includeInactiveContent Whether to include inactive content in statistics
     * @return Filtered family tree data
     */
    FamilyTreeResponse getFamilyTreeWithFilters(UUID familyId, UUID requestingUserId, 
                                              boolean includePrivateContent, boolean includeInactiveContent);

    /**
     * Get family tree statistics summary
     * 
     * @param familyId The family ID
     * @param requestingUserId The user requesting the statistics
     * @return Summary statistics for the family tree
     */
    FamilyTreeResponse.PermissionSummary getFamilyTreeStatistics(UUID familyId, UUID requestingUserId);

    /**
     * Get content distribution across family members
     * 
     * @param familyId The family ID
     * @param requestingUserId The user requesting the data
     * @return Content distribution statistics
     */
    FamilyTreeResponse getContentDistribution(UUID familyId, UUID requestingUserId);

    /**
     * Get family tree with relationship details
     * 
     * @param familyId The family ID
     * @param requestingUserId The user requesting the tree
     * @param includeRelationshipDetails Whether to include detailed relationship information
     * @return Family tree with relationship details
     */
    FamilyTreeResponse getFamilyTreeWithRelationships(UUID familyId, UUID requestingUserId, 
                                                    boolean includeRelationshipDetails);

    /**
     * Get family tree for a specific generation
     * 
     * @param familyId The family ID
     * @param generationLevel The generation level to focus on
     * @param requestingUserId The user requesting the tree
     * @return Family tree focused on the specified generation
     */
    FamilyTreeResponse getFamilyTreeByGeneration(UUID familyId, Integer generationLevel, UUID requestingUserId);

    /**
     * Get family tree with inheritance information
     * 
     * @param familyId The family ID
     * @param requestingUserId The user requesting the tree
     * @return Family tree with inheritance rules and status
     */
    FamilyTreeResponse getFamilyTreeWithInheritance(UUID familyId, UUID requestingUserId);

    /**
     * Get family tree with permission matrix
     * 
     * @param familyId The family ID
     * @param requestingUserId The user requesting the tree
     * @return Family tree with detailed permission information
     */
    FamilyTreeResponse getFamilyTreeWithPermissions(UUID familyId, UUID requestingUserId);

    /**
     * Get family tree export data (for external visualization tools)
     * 
     * @param familyId The family ID
     * @param requestingUserId The user requesting the export
     * @param format Export format (JSON, CSV, etc.)
     * @return Family tree data in the specified format
     */
    String exportFamilyTree(UUID familyId, UUID requestingUserId, String format);
}
