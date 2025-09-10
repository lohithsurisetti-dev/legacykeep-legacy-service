package com.legacykeep.legacy.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacykeep.legacy.service.RelationshipServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of RelationshipServiceClient.
 * Communicates with the Relationship Service via REST API.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RelationshipServiceClientImpl implements RelationshipServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${relationship.service.base-url:http://localhost:8083/relationship}")
    private String relationshipServiceBaseUrl;

    @Value("${relationship.service.jwt-secret:legacykeep-jwt-secret-key-change-in-production-512-bits-minimum-required-for-hs512-algorithm}")
    private String jwtSecret;

    @Override
    public List<RelationshipInfo> getActiveUserRelationships(UUID userId) {
        try {
            log.debug("Fetching active relationships for user: {}", userId);
            
            String url = relationshipServiceBaseUrl + "/api/v1/relationships/user/" + userId + "/active";
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseRelationshipList(response.getBody());
            }
            
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching active relationships for user {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<RelationshipInfo> getRelationshipsByType(UUID userId, String relationshipTypeName) {
        try {
            log.debug("Fetching relationships by type '{}' for user: {}", relationshipTypeName, userId);
            
            // First get all active relationships
            List<RelationshipInfo> allRelationships = getActiveUserRelationships(userId);
            
            // Filter by relationship type name
            return allRelationships.stream()
                    .filter(rel -> relationshipTypeName.equalsIgnoreCase(rel.getRelationshipTypeName()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching relationships by type '{}' for user {}: {}", 
                     relationshipTypeName, userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<RelationshipInfo> getRelationshipsByCategory(UUID userId, String category) {
        try {
            log.debug("Fetching relationships by category '{}' for user: {}", category, userId);
            
            // First get all active relationships
            List<RelationshipInfo> allRelationships = getActiveUserRelationships(userId);
            
            // Filter by category
            return allRelationships.stream()
                    .filter(rel -> category.equalsIgnoreCase(rel.getRelationshipCategory()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching relationships by category '{}' for user {}: {}", 
                     category, userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<UUID> getUsersByRelationshipType(UUID userId, String relationshipTypeName) {
        List<RelationshipInfo> relationships = getRelationshipsByType(userId, relationshipTypeName);
        return relationships.stream()
                .map(rel -> rel.getOtherUserId(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<UUID> getUsersByRelationshipCategory(UUID userId, String category) {
        List<RelationshipInfo> relationships = getRelationshipsByCategory(userId, category);
        return relationships.stream()
                .map(rel -> rel.getOtherUserId(userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean relationshipExists(UUID user1Id, UUID user2Id) {
        try {
            log.debug("Checking if relationship exists between users: {} and {}", user1Id, user2Id);
            
            String url = relationshipServiceBaseUrl + "/api/v1/relationships/check/" + user1Id + "/" + user2Id;
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                return Boolean.TRUE.equals(body.get("data"));
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error checking relationship existence between users {} and {}: {}", 
                     user1Id, user2Id, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean activeRelationshipExists(UUID user1Id, UUID user2Id) {
        try {
            log.debug("Checking if active relationship exists between users: {} and {}", user1Id, user2Id);
            
            String url = relationshipServiceBaseUrl + "/api/v1/relationships/check-active/" + user1Id + "/" + user2Id;
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                return Boolean.TRUE.equals(body.get("data"));
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error checking active relationship existence between users {} and {}: {}", 
                     user1Id, user2Id, e.getMessage());
            return false;
        }
    }

    @Override
    public RelationshipTypeInfo getRelationshipTypeByName(String relationshipTypeName) {
        try {
            log.debug("Fetching relationship type by name: {}", relationshipTypeName);
            
            String url = relationshipServiceBaseUrl + "/api/v1/relationship-types/name/" + relationshipTypeName;
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseRelationshipType(response.getBody());
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error fetching relationship type by name '{}': {}", relationshipTypeName, e.getMessage());
            return null;
        }
    }

    @Override
    public List<RelationshipTypeInfo> getRelationshipTypesByCategory(String category) {
        try {
            log.debug("Fetching relationship types by category: {}", category);
            
            String url = relationshipServiceBaseUrl + "/api/v1/relationship-types/category/" + category;
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseRelationshipTypeList(response.getBody());
            }
            
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching relationship types by category '{}': {}", category, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> getFamilyMembers(UUID familyId) {
        try {
            String url = relationshipServiceBaseUrl + "/api/v1/families/" + familyId + "/members";
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody != null && responseBody.containsKey("data")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> members = (List<Map<String, Object>>) responseBody.get("data");
                return members != null ? members : Collections.emptyList();
            }
            
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching family members for family '{}': {}", familyId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getUserInfo(UUID userId) {
        try {
            String url = relationshipServiceBaseUrl + "/api/v1/users/" + userId;
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody != null && responseBody.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> userInfo = (Map<String, Object>) responseBody.get("data");
                return userInfo != null ? userInfo : Collections.emptyMap();
            }
            
            return Collections.emptyMap();
        } catch (Exception e) {
            log.error("Error fetching user info for user '{}': {}", userId, e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Create HTTP headers with JWT authentication.
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        // TODO: Add JWT token generation/validation
        // For now, we'll assume the relationship service is accessible without authentication
        // In production, you would generate a JWT token here
        return headers;
    }

    /**
     * Parse relationship list from API response.
     */
    @SuppressWarnings("unchecked")
    private List<RelationshipInfo> parseRelationshipList(Map<String, Object> response) {
        try {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data == null) {
                return Collections.emptyList();
            }

            List<Map<String, Object>> relationships = (List<Map<String, Object>>) data.get("content");
            if (relationships == null) {
                return Collections.emptyList();
            }

            return relationships.stream()
                    .map(this::parseRelationship)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error parsing relationship list: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Parse single relationship from API response.
     */
    @SuppressWarnings("unchecked")
    private RelationshipInfo parseRelationship(Map<String, Object> relationshipData) {
        try {
            UUID relationshipId = UUID.fromString(relationshipData.get("id").toString());
            UUID user1Id = UUID.fromString(relationshipData.get("user1Id").toString());
            UUID user2Id = UUID.fromString(relationshipData.get("user2Id").toString());
            String status = relationshipData.get("status").toString();
            String metadata = (String) relationshipData.get("metadata");

            Map<String, Object> relationshipTypeData = (Map<String, Object>) relationshipData.get("relationshipType");
            String relationshipTypeName = relationshipTypeData.get("name").toString();
            String relationshipCategory = relationshipTypeData.get("category").toString();

            return new RelationshipInfo(relationshipId, user1Id, user2Id, 
                                      relationshipTypeName, relationshipCategory, 
                                      status, metadata);
        } catch (Exception e) {
            log.error("Error parsing relationship: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Parse relationship type from API response.
     */
    @SuppressWarnings("unchecked")
    private RelationshipTypeInfo parseRelationshipType(Map<String, Object> response) {
        try {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data == null) {
                return null;
            }

            Long id = Long.valueOf(data.get("id").toString());
            String name = data.get("name").toString();
            String category = data.get("category").toString();
            Boolean bidirectional = Boolean.valueOf(data.get("bidirectional").toString());
            String metadata = (String) data.get("metadata");

            return new RelationshipTypeInfo(id, name, category, bidirectional, metadata);
        } catch (Exception e) {
            log.error("Error parsing relationship type: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Parse relationship type list from API response.
     */
    @SuppressWarnings("unchecked")
    private List<RelationshipTypeInfo> parseRelationshipTypeList(Map<String, Object> response) {
        try {
            List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
            if (data == null) {
                return Collections.emptyList();
            }

            return data.stream()
                    .map(this::parseRelationshipTypeFromList)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error parsing relationship type list: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Parse relationship type from list item.
     */
    private RelationshipTypeInfo parseRelationshipTypeFromList(Map<String, Object> data) {
        try {
            Long id = Long.valueOf(data.get("id").toString());
            String name = data.get("name").toString();
            String category = data.get("category").toString();
            Boolean bidirectional = Boolean.valueOf(data.get("bidirectional").toString());
            String metadata = (String) data.get("metadata");

            return new RelationshipTypeInfo(id, name, category, bidirectional, metadata);
        } catch (Exception e) {
            log.error("Error parsing relationship type from list: {}", e.getMessage());
            return null;
        }
    }
}
