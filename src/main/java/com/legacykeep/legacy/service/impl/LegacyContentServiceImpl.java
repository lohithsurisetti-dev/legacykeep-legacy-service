package com.legacykeep.legacy.service.impl;

import com.legacykeep.legacy.dto.request.CreateContentRequest;
import com.legacykeep.legacy.dto.response.ContentResponse;
import com.legacykeep.legacy.dto.response.MediaFileResponse;
import com.legacykeep.legacy.dto.response.RecipientResponse;
import com.legacykeep.legacy.entity.LegacyContent;
import com.legacykeep.legacy.entity.LegacyMediaFile;
import com.legacykeep.legacy.entity.LegacyRecipient;
import com.legacykeep.legacy.exception.ContentNotFoundException;
import com.legacykeep.legacy.exception.ValidationException;
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
        
        // Return only active content (not deleted) with basic pagination
        // TODO: Implement proper filtering logic
        Page<LegacyContent> contentPage = contentRepository.findAllActive(pageable);
        return contentPage.map(this::convertToResponse);
    }

    @Override
    public ContentResponse createContent(CreateContentRequest request) {
        log.info("Creating new content: {}", request.getTitle());
        
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

        LegacyContent savedContent = contentRepository.save(content);
        log.info("Created content with ID: {}", savedContent.getId());

        // Save media files if provided
        if (request.getMediaFiles() != null && !request.getMediaFiles().isEmpty()) {
            List<LegacyMediaFile> mediaFiles = request.getMediaFiles().stream()
                    .map(mediaRequest -> LegacyMediaFile.builder()
                            .contentId(savedContent.getId())
                            .fileName(mediaRequest.getFileName())
                            .originalFileName(mediaRequest.getOriginalFileName())
                            .fileSize(mediaRequest.getFileSize())
                            .mimeType(mediaRequest.getMimeType())
                            .s3Url(mediaRequest.getS3Url())
                            .thumbnailUrl(mediaRequest.getThumbnailUrl())
                            .fileType(mediaRequest.getFileType())
                            .createdAt(ZonedDateTime.now())
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
        
        // Return only active content (not deleted) with basic pagination
        // TODO: Implement proper search logic
        Page<LegacyContent> contentPage = contentRepository.findAllActive(pageable);
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
        log.info("Soft deleting content: {}", id);
        
        LegacyContent content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found with ID: " + id));
        
        // Soft delete - mark as DELETED instead of removing from database
        content.setStatus(LegacyContent.ContentStatus.DELETED);
        contentRepository.save(content);
        
        log.info("Content {} marked as deleted (soft delete)", id);
    }

    @Override
    public Page<ContentResponse> getAccessibleContent(UUID userId, UUID familyId, Pageable pageable) {
        log.info("Getting accessible content for user: {} in family: {}", userId, familyId);
        
        // Return only active content (not deleted) with basic pagination
        // TODO: Implement proper access control logic
        Page<LegacyContent> contentPage = contentRepository.findAllActive(pageable);
        return contentPage.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ContentResponse getContentById(UUID id) {
        log.debug("Getting content by ID: {}", id);
        LegacyContent content = contentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException(id));
        return convertToResponse(content);
    }

    private ContentResponse convertToResponse(LegacyContent content) {
        List<MediaFileResponse> mediaFiles = mediaFileRepository.findByContentId(content.getId())
                .stream()
                .map(this::convertToMediaResponse)
                .collect(Collectors.toList());

        List<RecipientResponse> recipients = recipientRepository.findByContentId(content.getId())
                .stream()
                .map(this::convertToRecipientResponse)
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

    private MediaFileResponse convertToMediaResponse(LegacyMediaFile mediaFile) {
        return MediaFileResponse.builder()
                .id(mediaFile.getId())
                .contentId(mediaFile.getContentId())
                .fileName(mediaFile.getFileName())
                .originalFileName(mediaFile.getOriginalFileName())
                .fileSize(mediaFile.getFileSize())
                .mimeType(mediaFile.getMimeType())
                .s3Url(mediaFile.getS3Url())
                .thumbnailUrl(mediaFile.getThumbnailUrl())
                .fileType(mediaFile.getFileType())
                .createdAt(mediaFile.getCreatedAt())
                .build();
    }

    private RecipientResponse convertToRecipientResponse(LegacyRecipient recipient) {
        return RecipientResponse.builder()
                .id(recipient.getId())
                .contentId(recipient.getContentId())
                .recipientId(recipient.getRecipientId())
                .recipientType(recipient.getRecipientType())
                .recipientRelationship(recipient.getRecipientRelationship())
                .accessLevel(recipient.getAccessLevel())
                .personalMessage(recipient.getPersonalMessage())
                .status(recipient.getStatus())
                .createdAt(recipient.getCreatedAt())
                .updatedAt(recipient.getUpdatedAt())
                .build();
    }
}