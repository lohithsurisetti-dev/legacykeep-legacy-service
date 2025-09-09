package com.legacykeep.legacy.controller;

import com.legacykeep.legacy.dto.request.CreateRecipientRequest;
import com.legacykeep.legacy.dto.response.ApiResponse;
import com.legacykeep.legacy.dto.response.RecipientResponse;
import com.legacykeep.legacy.entity.LegacyRecipient;
import com.legacykeep.legacy.service.LegacyRecipientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing legacy recipients
 */
@RestController
@RequestMapping("/recipients")
@RequiredArgsConstructor
@Slf4j
public class LegacyRecipientController {

    private final LegacyRecipientService recipientService;

    /**
     * Get recipients with filters and pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RecipientResponse>>> getRecipients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) UUID contentId,
            @RequestParam(required = false) UUID recipientId,
            @RequestParam(required = false) LegacyRecipient.RecipientType recipientType,
            @RequestParam(required = false) LegacyRecipient.RecipientStatus status,
            @RequestParam(required = false) LegacyRecipient.AccessLevel accessLevel) {

        log.info("Getting recipients with filters - page: {}, size: {}, contentId: {}, recipientId: {}", 
                page, size, contentId, recipientId);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RecipientResponse> recipients = recipientService.getRecipientsWithFilters(
                pageable, contentId, recipientId, recipientType, status, accessLevel, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success(recipients, "Recipients retrieved successfully"));
    }

    /**
     * Create a new recipient
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RecipientResponse>> createRecipient(
            @Valid @RequestBody CreateRecipientRequest request) {
        
        log.info("Creating recipient for content: {}", request.getContentId());
        
        RecipientResponse recipient = recipientService.createRecipient(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(recipient, "Recipient created successfully"));
    }

    /**
     * Get recipient by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecipientResponse>> getRecipientById(@PathVariable UUID id) {
        log.info("Getting recipient by ID: {}", id);
        
        RecipientResponse recipient = recipientService.getRecipientById(id);
        
        return ResponseEntity.ok(ApiResponse.success(recipient, "Recipient retrieved successfully"));
    }

    /**
     * Update recipient
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RecipientResponse>> updateRecipient(
            @PathVariable UUID id,
            @Valid @RequestBody CreateRecipientRequest request) {
        
        log.info("Updating recipient: {}", id);
        
        RecipientResponse recipient = recipientService.updateRecipient(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(recipient, "Recipient updated successfully"));
    }

    /**
     * Delete recipient (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecipient(@PathVariable UUID id) {
        log.info("Deleting recipient: {}", id);
        
        recipientService.deleteRecipient(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Recipient deleted successfully"));
    }

    /**
     * Get recipients for specific content
     */
    @GetMapping("/content/{contentId}")
    public ResponseEntity<ApiResponse<List<RecipientResponse>>> getRecipientsByContentId(
            @PathVariable UUID contentId) {
        
        log.info("Getting recipients for content: {}", contentId);
        
        List<RecipientResponse> recipients = recipientService.getRecipientsByContentId(contentId);
        
        return ResponseEntity.ok(ApiResponse.success(recipients, "Recipients retrieved successfully"));
    }

    /**
     * Get recipients for specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<RecipientResponse>>> getRecipientsByUserId(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting recipients for user: {}", userId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<RecipientResponse> recipients = recipientService.getRecipientsByUserId(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(recipients, "Recipients retrieved successfully"));
    }

    /**
     * Update recipient status (accept/reject invitation)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RecipientResponse>> updateRecipientStatus(
            @PathVariable UUID id,
            @RequestParam LegacyRecipient.RecipientStatus status) {
        
        log.info("Updating recipient status: {} to {}", id, status);
        
        RecipientResponse recipient = recipientService.updateRecipientStatus(id, status);
        
        return ResponseEntity.ok(ApiResponse.success(recipient, "Recipient status updated successfully"));
    }

    /**
     * Get pending recipients for a user
     */
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<ApiResponse<List<RecipientResponse>>> getPendingRecipientsForUser(
            @PathVariable UUID userId) {
        
        log.info("Getting pending recipients for user: {}", userId);
        
        List<RecipientResponse> recipients = recipientService.getPendingRecipientsForUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success(recipients, "Pending recipients retrieved successfully"));
    }

    /**
     * Search recipients
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<RecipientResponse>>> searchRecipients(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) UUID contentId,
            @RequestParam(required = false) UUID recipientId) {
        
        log.info("Searching recipients with keyword: '{}'", keyword);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<RecipientResponse> recipients = recipientService.searchRecipients(keyword, pageable, contentId, recipientId);
        
        return ResponseEntity.ok(ApiResponse.success(recipients, "Recipients search completed successfully"));
    }

    /**
     * Get accessible recipients for a user in a family
     */
    @GetMapping("/accessible")
    public ResponseEntity<ApiResponse<Page<RecipientResponse>>> getAccessibleRecipients(
            @RequestParam UUID userId,
            @RequestParam UUID familyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting accessible recipients for user: {} in family: {}", userId, familyId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<RecipientResponse> recipients = recipientService.getAccessibleRecipients(userId, familyId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(recipients, "Accessible recipients retrieved successfully"));
    }
}
