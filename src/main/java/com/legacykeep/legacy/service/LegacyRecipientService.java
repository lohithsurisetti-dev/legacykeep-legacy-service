package com.legacykeep.legacy.service;

import com.legacykeep.legacy.dto.request.CreateRecipientRequest;
import com.legacykeep.legacy.dto.response.RecipientResponse;
import com.legacykeep.legacy.entity.LegacyRecipient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing legacy recipients
 */
public interface LegacyRecipientService {

    /**
     * Get recipients with filters and pagination
     */
    Page<RecipientResponse> getRecipientsWithFilters(
            Pageable pageable,
            UUID contentId,
            UUID recipientId,
            LegacyRecipient.RecipientType recipientType,
            LegacyRecipient.RecipientStatus status,
            LegacyRecipient.AccessLevel accessLevel,
            String sortBy,
            String sortDir);

    /**
     * Create a new recipient
     */
    RecipientResponse createRecipient(CreateRecipientRequest request);

    /**
     * Get recipient by ID
     */
    RecipientResponse getRecipientById(UUID id);

    /**
     * Update recipient
     */
    RecipientResponse updateRecipient(UUID id, CreateRecipientRequest request);

    /**
     * Delete recipient (soft delete)
     */
    void deleteRecipient(UUID id);

    /**
     * Get recipients for specific content
     */
    List<RecipientResponse> getRecipientsByContentId(UUID contentId);

    /**
     * Get recipients for specific user
     */
    Page<RecipientResponse> getRecipientsByUserId(UUID userId, Pageable pageable);

    /**
     * Update recipient status (accept/reject invitation)
     */
    RecipientResponse updateRecipientStatus(UUID id, LegacyRecipient.RecipientStatus status);

    /**
     * Get pending recipients for a user
     */
    List<RecipientResponse> getPendingRecipientsForUser(UUID userId);

    /**
     * Search recipients
     */
    Page<RecipientResponse> searchRecipients(String keyword, Pageable pageable, UUID contentId, UUID recipientId);

    /**
     * Get accessible recipients for a user in a family
     */
    Page<RecipientResponse> getAccessibleRecipients(UUID userId, UUID familyId, Pageable pageable);
}
