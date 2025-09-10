package com.legacykeep.legacy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for family tree visualization data.
 * Contains hierarchical family structure with content statistics and permissions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyTreeResponse {

    /**
     * Root family member (usually the oldest generation)
     */
    private FamilyMemberNode rootMember;

    /**
     * Total number of family members in the tree
     */
    private Integer totalMembers;

    /**
     * Total number of generations in the family
     */
    private Integer totalGenerations;

    /**
     * Total content count across all family members
     */
    private Long totalContent;

    /**
     * Content count by generation
     */
    private Map<Integer, Long> contentByGeneration;

    /**
     * Content count by privacy level
     */
    private Map<String, Long> contentByPrivacyLevel;

    /**
     * Permission summary for the requesting user
     */
    private PermissionSummary permissionSummary;

    /**
     * Individual family member node with relationships and content stats
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FamilyMemberNode {
        private UUID userId;
        private String name;
        private String email;
        private Integer generationLevel;
        private String profileImageUrl;
        
        // Content statistics
        private ContentStats contentStats;
        
        // Relationships
        private List<RelationshipNode> relationships;
        
        // Children (for tree structure)
        private List<FamilyMemberNode> children;
        
        // Permission info for current user
        private MemberPermissionInfo permissionInfo;
    }

    /**
     * Relationship information between family members
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelationshipNode {
        private UUID relatedUserId;
        private String relatedUserName;
        private String relationshipType;
        private String relationshipCategory;
        private Boolean isBidirectional;
        private String status; // ACTIVE, INACTIVE, etc.
    }

    /**
     * Content statistics for a family member
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentStats {
        private Long totalContent;
        private Long textContent;
        private Long imageContent;
        private Long videoContent;
        private Long audioContent;
        private Long documentContent;
        
        // Privacy breakdown
        private Long privateContent;
        private Long familyContent;
        private Long extendedFamilyContent;
        private Long publicContent;
        
        // Status breakdown
        private Long activeContent;
        private Long inactiveContent;
        private Long archivedContent;
        
        // Recent activity
        private Long contentCreatedLast30Days;
        private Long contentModifiedLast30Days;
    }

    /**
     * Permission information for a family member from current user's perspective
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberPermissionInfo {
        private Boolean canViewProfile;
        private Boolean canViewContent;
        private String effectiveAccessLevel; // READ, COMMENT, EDIT
        private Long accessibleContentCount;
        private List<String> accessibleContentTypes;
        private Boolean isDirectFamilyMember;
        private Boolean isExtendedFamilyMember;
    }

    /**
     * Overall permission summary for the requesting user
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionSummary {
        private UUID requestingUserId;
        private Integer userGenerationLevel;
        private Long totalAccessibleContent;
        private Long familyAccessibleContent;
        private Long extendedFamilyAccessibleContent;
        private Long publicAccessibleContent;
        private Map<String, Long> accessibleContentByType;
        private List<String> accessibleFamilyMembers;
        private Boolean canCreateContent;
        private Boolean canManageFamilyTree;
    }
}
