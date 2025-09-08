package com.legacykeep.legacy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for interaction summary (from Interaction Service)
 * This will be populated by calling the Interaction Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionSummaryResponse {

    private UUID contentId;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long shareCount;
    private Long bookmarkCount;
    private Double averageRating;
    private Long ratingCount;

    // User's specific interactions
    private UserInteractionResponse userInteraction;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInteractionResponse {
        private UUID userId;
        private Boolean hasLiked;
        private Boolean hasShared;
        private Boolean hasBookmarked;
        private Boolean hasCommented;
        private Integer userRating;
        private List<String> userReactions; // like, love, heart, etc.
    }

    // Recent comments (preview)
    private List<CommentPreviewResponse> recentComments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentPreviewResponse {
        private UUID commentId;
        private UUID userId;
        private String userName;
        private String commentText;
        private String createdAt;
        private Integer replyCount;
        private Boolean isEdited;
    }
}
