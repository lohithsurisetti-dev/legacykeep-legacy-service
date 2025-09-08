package com.legacykeep.legacy.service.impl;

import com.legacykeep.legacy.dto.request.CreateContentRequest;
import com.legacykeep.legacy.dto.response.ContentResponse;
import com.legacykeep.legacy.dto.response.MediaFileResponse;
import com.legacykeep.legacy.dto.response.RecipientResponse;
import com.legacykeep.legacy.entity.LegacyContent;
import com.legacykeep.legacy.entity.LegacyMediaFile;
import com.legacykeep.legacy.entity.LegacyRecipient;
import com.legacykeep.legacy.repository.LegacyContentRepository;
import com.legacykeep.legacy.repository.LegacyMediaFileRepository;
import com.legacykeep.legacy.repository.LegacyRecipientRepository;
import com.legacykeep.legacy.service.LegacyContentService;
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
 * Implementation of LegacyContentService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LegacyContentServiceImpl implements LegacyContentService {

    private final LegacyContentRepository contentRepository;
    private final LegacyMediaFileRepository mediaFileRepository;
    private final LegacyRecipientRepository recipientRepository;

    @Override
    public Page<ContentResponse> getContentWithFilters(
            Pageable pageable,
            UUID bucketId,
            UUID creatorId,
            UUID familyId,
            LegacyContent.ContentType contentType,
            Boolean featured,
            String sortBy,
            String sortDir) {
        
        log.info("Fetching content with filters - bucketId: {}, creatorId: {}, familyId: {}, contentType: {}, featured: {}", 
                bucketId, creatorId, familyId, contentType, featured);
        
        // For now, return all content with basic pagination
        // TODO: Implement proper filtering logic
        Page<LegacyContent> contentPage = contentRepository.findAll(pageable);
        return contentPage.map(this::convertToResponse);
    }

    @Override
    public ContentResponse createContent(CreateContentRequest request) {
        log.info("Creating new content: {}", request.getTitle());
        
        // Create content entity
        LegacyContent content = LegacyContent.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .contentType(request.getContentType())
                .bucketId(request.getBucketId())
                .creatorId(request.getCreatorId())
                .familyId(request.getFamilyId())
                .generationLevel(request.getGenerationLevel())
                .privacyLevel(request.getPrivacyLevel())
                .isFeatured(request.getIsFeatured())
                .sortOrder(request.getSortOrder())
                .build();

        final LegacyContent savedContent = contentRepository.save(content);
        log.info("Content created with ID: {}", savedContent.getId());

        // Save media files if provided
        if (request.getMediaFiles() != null && !request.getMediaFiles().isEmpty()) {
            List<LegacyMediaFile> mediaFiles = request.getMediaFiles().stream()
                    .map(mediaRequest -> LegacyMediaFile.builder()
                            .contentId(savedContent.getId())
                            .fileName(mediaRequest.getFileName())
                            .originalFileName(mediaRequest.getOriginalFileName())
                            .s3Url(mediaRequest.getS3Url())
                            .thumbnailUrl(mediaRequest.getThumbnailUrl())
                            .fileType(mediaRequest.getFileType())
                            .fileSize(mediaRequest.getFileSize())
                            .mimeType(mediaRequest.getMimeType())
                            .build())
                    .collect(Collectors.toList());
            
            mediaFileRepository.saveAll(mediaFiles);
            log.info("Saved {} media files for content: {}", mediaFiles.size(), savedContent.getId());
        }

        // Save recipients if provided
        if (request.getRecipients() != null && !request.getRecipients().isEmpty()) {
            List<LegacyRecipient> recipients = request.getRecipients().stream()
                    .map(recipientRequest -> LegacyRecipient.builder()
                            .contentId(savedContent.getId())
                            .recipientId(recipientRequest.getRecipientId())
                            .recipientType(recipientRequest.getRecipientType())
                            .recipientRelationship(recipientRequest.getRecipientRelationship())
                            .accessLevel(recipientRequest.getAccessLevel())
                            .personalMessage(recipientRequest.getPersonalMessage())
                            .build())
                    .collect(Collectors.toList());
            
            recipientRepository.saveAll(recipients);
            log.info("Saved {} recipients for content: {}", recipients.size(), savedContent.getId());
        }

        return convertToResponse(savedContent);
    }

    @Override
    public Page<ContentResponse> searchContent(String keyword, Pageable pageable, UUID bucketId, UUID creatorId) {
        log.info("Searching content with keyword: '{}', bucketId: {}, creatorId: {}", keyword, bucketId, creatorId);
        
        // For now, return all content with basic pagination
        // TODO: Implement proper search logic
        Page<LegacyContent> contentPage = contentRepository.findAll(pageable);
        return contentPage.map(this::convertToResponse);
    }

    @Override
    public ContentResponse updateContent(UUID id, CreateContentRequest request) {
        log.info("Updating content: {}", id);
        
        LegacyContent existingContent = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found with ID: " + id));
        
        // Update fields
        existingContent.setTitle(request.getTitle());
        existingContent.setContent(request.getContent());
        existingContent.setContentType(request.getContentType());
        existingContent.setBucketId(request.getBucketId());
        existingContent.setGenerationLevel(request.getGenerationLevel());
        existingContent.setPrivacyLevel(request.getPrivacyLevel());
        existingContent.setIsFeatured(request.getIsFeatured());
        existingContent.setSortOrder(request.getSortOrder());
        
        LegacyContent updatedContent = contentRepository.save(existingContent);
        return convertToResponse(updatedContent);
    }

    @Override
    public void deleteContent(UUID id) {
        log.info("Deleting content: {}", id);
        
        LegacyContent content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found with ID: " + id));
        
        // Delete associated media files and recipients first
        List<LegacyMediaFile> mediaFiles = mediaFileRepository.findByContentId(id);
        if (!mediaFiles.isEmpty()) {
            mediaFileRepository.deleteAll(mediaFiles);
        }
        
        List<LegacyRecipient> recipients = recipientRepository.findByContentId(id);
        if (!recipients.isEmpty()) {
            recipientRepository.deleteAll(recipients);
        }
        
        contentRepository.delete(content);
    }

    @Override
    public Page<ContentResponse> getAccessibleContent(UUID userId, UUID familyId, Pageable pageable) {
        log.info("Getting accessible content for user: {} in family: {}", userId, familyId);
        
        // For now, return all content with basic pagination
        // TODO: Implement proper access control logic
        Page<LegacyContent> contentPage = contentRepository.findAll(pageable);
        return contentPage.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ContentResponse getContentById(UUID id) {
        log.debug("Getting content by ID: {}", id);
        LegacyContent content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with ID: " + id));
        return convertToResponse(content);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getContentByBucket(UUID bucketId) {
        log.debug("Getting content by bucket: {}", bucketId);
        List<LegacyContent> contents = contentRepository.findByBucketId(bucketId);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> getContentByBucket(UUID bucketId, Pageable pageable) {
        log.debug("Getting content by bucket with pagination: {}", bucketId);
        Page<LegacyContent> contents = contentRepository.findByBucketId(bucketId, pageable);
        return contents.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getContentByCreator(UUID creatorId) {
        log.debug("Getting content by creator: {}", creatorId);
        List<LegacyContent> contents = contentRepository.findByCreatorId(creatorId);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> getContentByCreator(UUID creatorId, Pageable pageable) {
        log.debug("Getting content by creator with pagination: {}", creatorId);
        Page<LegacyContent> contents = contentRepository.findByCreatorId(creatorId, pageable);
        return contents.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getContentByFamily(UUID familyId) {
        log.debug("Getting content by family: {}", familyId);
        List<LegacyContent> contents = contentRepository.findByFamilyId(familyId);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getFeaturedContent() {
        log.debug("Getting featured content");
        List<LegacyContent> contents = contentRepository.findFeaturedContent();
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getFeaturedContentByCreator(UUID creatorId) {
        log.debug("Getting featured content by creator: {}", creatorId);
        List<LegacyContent> contents = contentRepository.findFeaturedContentByCreator(creatorId);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getAccessibleContent(UUID userId, UUID familyId) {
        log.debug("Getting accessible content for user: {} in family: {}", userId, familyId);
        List<LegacyContent> contents = contentRepository.findAccessibleContent(userId, familyId);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> getAccessibleContent(UUID userId, UUID familyId, Pageable pageable) {
        log.debug("Getting accessible content for user with pagination: {} in family: {}", userId, familyId);
        Page<LegacyContent> contents = contentRepository.findAccessibleContent(userId, familyId, pageable);
        return contents.map(this::convertToResponse);
    }

    @Override
    public ContentResponse updateContent(UUID id, CreateContentRequest request) {
        log.info("Updating content: {}", id);
        
        LegacyContent content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with ID: " + id));

        // Update content fields
        content.setTitle(request.getTitle());
        content.setContent(request.getContent());
        content.setContentType(request.getContentType());
        content.setBucketId(request.getBucketId());
        content.setGenerationLevel(request.getGenerationLevel());
        content.setPrivacyLevel(request.getPrivacyLevel());
        content.setIsFeatured(request.getIsFeatured());
        content.setSortOrder(request.getSortOrder());

        content = contentRepository.save(content);
        log.info("Content updated: {}", content.getId());

        return convertToResponse(content);
    }

    @Override
    public void deleteContent(UUID id) {
        log.info("Deleting content: {}", id);
        
        // Delete associated media files
        List<LegacyMediaFile> mediaFiles = mediaFileRepository.findByContentId(id);
        if (!mediaFiles.isEmpty()) {
            mediaFileRepository.deleteAll(mediaFiles);
        }
        
        // Delete associated recipients
        List<LegacyRecipient> recipients = recipientRepository.findByContentId(id);
        if (!recipients.isEmpty()) {
            recipientRepository.deleteAll(recipients);
        }
        
        // Delete content
        contentRepository.deleteById(id);
        
        log.info("Content deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> searchContent(String title) {
        log.debug("Searching content by title: {}", title);
        List<LegacyContent> contents = contentRepository.findByTitleContainingIgnoreCase(title);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> searchContentByCreator(UUID creatorId, String title) {
        log.debug("Searching content by creator: {} and title: {}", creatorId, title);
        List<LegacyContent> contents = contentRepository.findByCreatorIdAndTitleContainingIgnoreCase(creatorId, title);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> searchContentByBucket(UUID bucketId, String title) {
        log.debug("Searching content by bucket: {} and title: {}", bucketId, title);
        List<LegacyContent> contents = contentRepository.findByBucketIdAndTitleContainingIgnoreCase(bucketId, title);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getContentByType(LegacyContent.ContentType contentType) {
        log.debug("Getting content by type: {}", contentType);
        List<LegacyContent> contents = contentRepository.findByContentType(contentType);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getContentByCreatorAndType(UUID creatorId, LegacyContent.ContentType contentType) {
        log.debug("Getting content by creator: {} and type: {}", creatorId, contentType);
        List<LegacyContent> contents = contentRepository.findByCreatorIdAndContentType(creatorId, contentType);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getContentByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        log.debug("Getting content by date range: {} to {}", startDate, endDate);
        List<LegacyContent> contents = contentRepository.findByCreatedAtBetween(startDate, endDate);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getContentByCreatorAndDateRange(UUID creatorId, ZonedDateTime startDate, ZonedDateTime endDate) {
        log.debug("Getting content by creator: {} and date range: {} to {}", creatorId, startDate, endDate);
        List<LegacyContent> contents = contentRepository.findByCreatorIdAndCreatedAtBetween(creatorId, startDate, endDate);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentResponse> getRecentContentByCreator(UUID creatorId) {
        log.debug("Getting recent content by creator: {}", creatorId);
        List<LegacyContent> contents = contentRepository.findRecentContentByCreator(creatorId);
        return contents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert LegacyContent entity to ContentResponse DTO
     */
    private ContentResponse convertToResponse(LegacyContent content) {
        // Get media files for this content
        List<MediaFileResponse> mediaFiles = mediaFileRepository.findByContentId(content.getId())
                .stream()
                .map(media -> MediaFileResponse.builder()
                        .id(media.getId())
                        .contentId(media.getContentId())
                        .fileName(media.getFileName())
                        .originalFileName(media.getOriginalFileName())
                        .s3Url(media.getS3Url())
                        .thumbnailUrl(media.getThumbnailUrl())
                        .fileType(media.getFileType())
                        .fileSize(media.getFileSize())
                        .mimeType(media.getMimeType())
                        .processingStatus(media.getProcessingStatus())
                        .createdAt(media.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        // Get recipients for this content
        List<RecipientResponse> recipients = recipientRepository.findByContentId(content.getId())
                .stream()
                .map(recipient -> RecipientResponse.builder()
                        .id(recipient.getId())
                        .recipientId(recipient.getRecipientId())
                        .recipientType(recipient.getRecipientType())
                        .recipientRelationship(recipient.getRecipientRelationship())
                        .accessLevel(recipient.getAccessLevel())
                        .status(recipient.getStatus())
                        .personalMessage(recipient.getPersonalMessage())
                        .createdAt(recipient.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ContentResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .content(content.getContent())
                .contentType(content.getContentType())
                .bucketId(content.getBucketId())
                .creatorId(content.getCreatorId())
                .familyId(content.getFamilyId())
                .generationLevel(content.getGenerationLevel())
                .privacyLevel(content.getPrivacyLevel())
                .isFeatured(content.getIsFeatured())
                .sortOrder(content.getSortOrder())
                .mediaFiles(mediaFiles)
                .recipients(recipients)
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt())
                .build();
    }
}
