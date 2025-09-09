package com.legacykeep.legacy.service.impl;

import com.legacykeep.legacy.dto.request.CreateRecipientRequest;
import com.legacykeep.legacy.dto.response.RecipientResponse;
import com.legacykeep.legacy.entity.LegacyRecipient;
import com.legacykeep.legacy.repository.LegacyRecipientRepository;
import com.legacykeep.legacy.service.LegacyRecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of LegacyRecipientService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LegacyRecipientServiceImpl implements LegacyRecipientService {

    private final LegacyRecipientRepository recipientRepository;

    @Override
    public Page<RecipientResponse> getRecipientsWithFilters(
            Pageable pageable,
            UUID contentId,
            UUID recipientId,
            LegacyRecipient.RecipientType recipientType,
            LegacyRecipient.RecipientStatus status,
            LegacyRecipient.AccessLevel accessLevel,
            String sortBy,
            String sortDir) {
        
        log.info("Fetching recipients with filters - contentId: {}, recipientId: {}, recipientType: {}, status: {}, accessLevel: {}", 
                contentId, recipientId, recipientType, status, accessLevel);
        
        // For now, return all active recipients with basic pagination
        // TODO: Implement proper filtering logic
        Page<LegacyRecipient> recipientPage = recipientRepository.findAllActive(pageable);
        return recipientPage.map(this::mapToResponse);
    }

    @Override
    public RecipientResponse createRecipient(CreateRecipientRequest request) {
        log.info("Creating new recipient for content: {} and recipient: {}", request.getContentId(), request.getRecipientId());
        
        LegacyRecipient recipient = LegacyRecipient.builder()
                .contentId(request.getContentId())
                .recipientId(request.getRecipientId())
                .recipientType(request.getRecipientType())
                .recipientRelationship(request.getRecipientRelationship())
                .accessLevel(request.getAccessLevel())
                .personalMessage(request.getPersonalMessage())
                .status(request.getStatus() != null ? request.getStatus() : LegacyRecipient.RecipientStatus.PENDING)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        LegacyRecipient savedRecipient = recipientRepository.save(recipient);
        log.info("Created recipient with ID: {}", savedRecipient.getId());

        return mapToResponse(savedRecipient);
    }

    @Override
    @Transactional(readOnly = true)
    public RecipientResponse getRecipientById(UUID id) {
        log.info("Fetching recipient by ID: {}", id);
        LegacyRecipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found with ID: " + id));
        return mapToResponse(recipient);
    }

    @Override
    public RecipientResponse updateRecipient(UUID id, CreateRecipientRequest request) {
        log.info("Updating recipient: {}", id);

        LegacyRecipient existingRecipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found with ID: " + id));

        // Update fields
        existingRecipient.setContentId(request.getContentId());
        existingRecipient.setRecipientId(request.getRecipientId());
        existingRecipient.setRecipientType(request.getRecipientType());
        existingRecipient.setRecipientRelationship(request.getRecipientRelationship());
        existingRecipient.setAccessLevel(request.getAccessLevel());
        existingRecipient.setPersonalMessage(request.getPersonalMessage());
        if (request.getStatus() != null) {
            existingRecipient.setStatus(request.getStatus());
        }
        existingRecipient.setUpdatedAt(ZonedDateTime.now());

        LegacyRecipient updatedRecipient = recipientRepository.save(existingRecipient);
        return mapToResponse(updatedRecipient);
    }

    @Override
    public void deleteRecipient(UUID id) {
        log.info("Soft deleting recipient: {}", id);
        
        LegacyRecipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found with ID: " + id));
        
        // Soft delete - mark as EXPIRED instead of DELETED to avoid unique constraint conflicts
        recipient.setStatus(LegacyRecipient.RecipientStatus.EXPIRED);
        recipientRepository.save(recipient);
        
        log.info("Recipient {} marked as expired (soft delete)", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipientResponse> getRecipientsByContentId(UUID contentId) {
        log.info("Fetching recipients for content: {}", contentId);
        return recipientRepository.findByContentId(contentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecipientResponse> getRecipientsByUserId(UUID userId, Pageable pageable) {
        log.info("Fetching recipients for user: {}", userId);
        // For now, return all active recipients with basic pagination
        // TODO: Implement proper filtering logic
        Page<LegacyRecipient> recipientPage = recipientRepository.findAllActive(pageable);
        return recipientPage.map(this::mapToResponse);
    }

    @Override
    public RecipientResponse updateRecipientStatus(UUID id, LegacyRecipient.RecipientStatus status) {
        log.info("Updating recipient status: {} to {}", id, status);

        LegacyRecipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found with ID: " + id));

        recipient.setStatus(status);
        recipient.setUpdatedAt(ZonedDateTime.now());

        LegacyRecipient updatedRecipient = recipientRepository.save(recipient);
        return mapToResponse(updatedRecipient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipientResponse> getPendingRecipientsForUser(UUID userId) {
        log.info("Fetching pending recipients for user: {}", userId);
        return recipientRepository.findByRecipientIdAndStatus(userId, LegacyRecipient.RecipientStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RecipientResponse> searchRecipients(String keyword, Pageable pageable, UUID contentId, UUID recipientId) {
        log.info("Searching recipients with keyword: '{}', contentId: {}, recipientId: {}", keyword, contentId, recipientId);

        // For now, return all active recipients with basic pagination
        // TODO: Implement proper search logic
        Page<LegacyRecipient> recipientPage = recipientRepository.findAllActive(pageable);
        return recipientPage.map(this::mapToResponse);
    }

    @Override
    public Page<RecipientResponse> getAccessibleRecipients(UUID userId, UUID familyId, Pageable pageable) {
        log.info("Getting accessible recipients for user: {} in family: {}", userId, familyId);

        // For now, return all active recipients with basic pagination
        // TODO: Implement proper access control logic
        Page<LegacyRecipient> recipientPage = recipientRepository.findAllActive(pageable);
        return recipientPage.map(this::mapToResponse);
    }

    private RecipientResponse mapToResponse(LegacyRecipient recipient) {
        return RecipientResponse.builder()
                .id(recipient.getId())
                .contentId(recipient.getContentId())
                .recipientId(recipient.getRecipientId())
                .recipientType(recipient.getRecipientType())
                .recipientRelationship(recipient.getRecipientRelationship())
                .accessLevel(recipient.getAccessLevel())
                .status(recipient.getStatus())
                .personalMessage(recipient.getPersonalMessage())
                .createdAt(recipient.getCreatedAt())
                .updatedAt(recipient.getUpdatedAt())
                // Additional computed fields
                .recipientDisplayName(recipient.getRecipientRelationship()) // TODO: Get from user service
                .statusDescription(recipient.getStatus() != null ? recipient.getStatus().getDescription() : null)
                .accessLevelDescription(recipient.getAccessLevel() != null ? recipient.getAccessLevel().getDescription() : null)
                .canBeDeleted(recipient.canBeDeleted())
                .isAccepted(recipient.isAccepted())
                .isRejected(recipient.isRejected())
                .isPending(recipient.isPending())
                .isExpired(recipient.isExpired())
                .canEdit(recipient.canEdit())
                .canComment(recipient.canComment())
                .canView(recipient.canView())
                .recipientSummary(recipient.getRecipientSummary())
                .build();
    }
}
