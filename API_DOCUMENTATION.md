# 🏛️ Legacy Service - API Documentation

## 📋 **Overview**

The Legacy Service provides RESTful APIs for managing multi-generational family legacy content. All APIs follow REST conventions and return JSON responses with consistent error handling.

## 🔐 **Authentication**

All API endpoints require JWT authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <jwt_token>
```

## 📊 **Response Format**

### **Success Response**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2025-09-08T12:00:00Z"
}
```

### **Error Response**
```json
{
  "success": false,
  "message": "Error description",
  "error": "ERROR_CODE",
  "details": { ... },
  "timestamp": "2025-09-08T12:00:00Z"
}
```

## 🗂️ **Category Management APIs**

### **Get Categories**
```http
GET /api/v1/legacy/categories?includeSubcategories=true
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "message": "Categories retrieved successfully",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "Personal & Family",
      "description": "Personal and family-related content",
      "categoryLevel": 1,
      "categoryType": "SYSTEM",
      "icon": "family",
      "color": "#4CAF50",
      "subcategories": [
        {
          "id": "550e8400-e29b-41d4-a716-446655440011",
          "name": "Letters & Messages",
          "description": "Personal letters and messages",
          "categoryLevel": 2,
          "bucketCount": 3,
          "contentCount": 15
        }
      ]
    }
  ]
}
```

## 🪣 **Bucket Management APIs**

### **Create Legacy Bucket**
```http
POST /api/v1/legacy/buckets
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "name": "Letters to My Sons",
  "description": "Personal letters and messages for my children",
  "categoryId": "550e8400-e29b-41d4-a716-446655440011",
  "privacyLevel": "FAMILY"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Legacy bucket created successfully",
  "data": {
    "id": "bucket-uuid",
    "name": "Letters to My Sons",
    "description": "Personal letters and messages for my children",
    "categoryId": "550e8400-e29b-41d4-a716-446655440011",
    "categoryName": "Letters & Messages",
    "creatorId": "user-uuid",
    "familyId": "family-uuid",
    "bucketType": "CUSTOM",
    "privacyLevel": "FAMILY",
    "isFeatured": false,
    "createdAt": "2025-09-08T12:00:00Z"
  }
}
```

### **Get Buckets**
```http
GET /api/v1/legacy/buckets?categoryId=550e8400-e29b-41d4-a716-446655440011&search=letters
Authorization: Bearer <jwt_token>
```

## 📝 **Content Management APIs**

### **Create Legacy Content**
```http
POST /api/v1/legacy/content
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "title": "Letter to My Elder Son",
  "content": "Dear son, I want to share with you the wisdom I've gathered over the years...",
  "contentType": "TEXT",
  "bucketId": "bucket-uuid",
  "recipients": [
    {
      "recipientType": "SPECIFIC_USER",
      "recipientId": "son-user-id",
      "personalMessage": "This is for you, my dear son"
    }
  ],
  "mediaFiles": [
    {
      "fileName": "family_photo.jpg",
      "s3Url": "https://legacykeep-content.s3.amazonaws.com/families/789/images/123/original/family_photo.jpg",
      "fileType": "IMAGE"
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Legacy content created successfully",
  "data": {
    "id": 123,
    "title": "Family Recipe: Grandma's Biryani",
    "description": "Traditional family biryani recipe passed down through generations",
    "contentType": "TEXT",
    "category": "Culinary Heritage",
    "tags": ["recipe", "biryani", "family", "traditional"],
    "privacyLevel": "FAMILY",
    "status": "ACTIVE",
    "creatorId": 456,
    "familyId": 789,
    "generationLevel": 2,
    "createdAt": "2025-09-08T12:00:00Z",
    "updatedAt": "2025-09-08T12:00:00Z"
  }
}
```

### **Get Legacy Content**
```http
GET /api/v1/legacy/content?category=Culinary Heritage&generation=2&tags=recipe&page=0&size=20
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `category`: Filter by category
- `generation`: Filter by generation level
- `tags`: Filter by tags (comma-separated)
- `contentType`: Filter by content type
- `privacyLevel`: Filter by privacy level
- `status`: Filter by status
- `page`: Page number (default: 0)
- `size`: Page size (default: 20)
- `sort`: Sort field (default: createdAt)
- `direction`: Sort direction (ASC/DESC, default: DESC)

**Response:**
```json
{
  "success": true,
  "message": "Legacy content retrieved successfully",
  "data": {
    "content": [
      {
        "id": 123,
        "title": "Family Recipe: Grandma's Biryani",
        "description": "Traditional family biryani recipe...",
        "contentType": "TEXT",
        "category": "Culinary Heritage",
        "tags": ["recipe", "biryani", "family"],
        "privacyLevel": "FAMILY",
        "status": "ACTIVE",
        "creatorId": 456,
        "familyId": 789,
        "generationLevel": 2,
        "createdAt": "2025-09-08T12:00:00Z",
        "updatedAt": "2025-09-08T12:00:00Z"
      }
    ],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1,
      "first": true,
      "last": true
    }
  }
}
```

### **Get Specific Content**
```http
GET /api/v1/legacy/content/123
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "message": "Legacy content retrieved successfully",
  "data": {
    "id": 123,
    "title": "Family Recipe: Grandma's Biryani",
    "description": "Traditional family biryani recipe...",
    "contentType": "TEXT",
    "contentData": "Ingredients: 2 cups basmati rice...",
    "category": "Culinary Heritage",
    "tags": ["recipe", "biryani", "family"],
    "privacyLevel": "FAMILY",
    "status": "ACTIVE",
    "creatorId": 456,
    "familyId": 789,
    "generationLevel": 2,
    "createdAt": "2025-09-08T12:00:00Z",
    "updatedAt": "2025-09-08T12:00:00Z",
    "mediaFiles": [
      {
        "id": 1,
        "fileName": "biryani_photo.jpg",
        "originalFileName": "grandma_biryani.jpg",
        "fileSize": 2048576,
        "mimeType": "image/jpeg",
        "s3Url": "https://legacykeep-content.s3.amazonaws.com/families/789/images/123/original/biryani_photo.jpg",
        "thumbnailUrl": "https://legacykeep-content.s3.amazonaws.com/families/789/images/123/thumbnails/biryani_photo_thumb.jpg",
        "createdAt": "2025-09-08T12:00:00Z"
      }
    ]
  }
}
```

### **Update Content**
```http
PUT /api/v1/legacy/content/123
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "title": "Updated Family Recipe: Grandma's Biryani",
  "description": "Updated description with new ingredients",
  "tags": ["recipe", "biryani", "family", "traditional", "updated"]
}
```

### **Delete Content**
```http
DELETE /api/v1/legacy/content/123
Authorization: Bearer <jwt_token>
```

## 📁 **Media Management APIs**

### **Upload Media File**
```http
POST /api/v1/legacy/media/upload
Content-Type: multipart/form-data
Authorization: Bearer <jwt_token>
```

**Form Data:**
- `file`: Media file (audio, video, image, document)
- `contentId`: Associated content ID
- `title`: File title
- `description`: File description
- `tags`: Comma-separated tags

**Response:**
```json
{
  "success": true,
  "message": "Media file uploaded successfully",
  "data": {
    "id": 1,
    "contentId": 123,
    "fileName": "biryani_photo.jpg",
    "originalFileName": "grandma_biryani.jpg",
    "fileSize": 2048576,
    "mimeType": "image/jpeg",
    "s3Url": "https://legacykeep-content.s3.amazonaws.com/families/789/images/123/original/biryani_photo.jpg",
    "thumbnailUrl": "https://legacykeep-content.s3.amazonaws.com/families/789/images/123/thumbnails/biryani_photo_thumb.jpg",
    "processingStatus": "COMPLETED",
    "createdAt": "2025-09-08T12:00:00Z"
  }
}
```

### **Get Media File**
```http
GET /api/v1/legacy/media/1
Authorization: Bearer <jwt_token>
```

### **Download Media File**
```http
GET /api/v1/legacy/media/1/download
Authorization: Bearer <jwt_token>
```

**Response:** Binary file content with appropriate headers

## 🎯 **Recipient Management APIs**

### **Get Recipient Status**
```http
GET /api/v1/legacy/recipients?status=PENDING&userId=456
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `status`: Filter by status (PENDING, ACCEPTED, REJECTED, EXPIRED)
- `userId`: Filter by user ID
- `contentId`: Filter by content ID
- `recipientType`: Filter by recipient type (SPECIFIC_USER, GENERATION, RELATIONSHIP)
- `page`: Page number
- `size`: Page size

**Response:**
```json
{
  "success": true,
  "message": "Recipient status retrieved successfully",
  "data": {
    "recipients": [
      {
        "id": "recipient-uuid",
        "contentId": "content-uuid",
        "contentTitle": "Letter to My Elder Son",
        "bucketName": "Letters to My Sons",
        "creatorId": "creator-uuid",
        "creatorName": "John Doe",
        "recipientId": "recipient-uuid",
        "recipientName": "Jane Doe",
        "recipientType": "SPECIFIC_USER",
        "recipientRelationship": "elder son",
        "accessLevel": "READ",
        "status": "PENDING",
        "personalMessage": "This is for you, my dear son",
        "createdAt": "2025-09-08T12:00:00Z"
      }
    ],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

### **Accept Content**
```http
POST /api/v1/legacy/recipients/{recipientId}/accept
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "message": "Thank you for sharing this with me"
}
```

### **Reject Content**
```http
POST /api/v1/legacy/recipients/{recipientId}/reject
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "reason": "Not ready to receive this content yet"
}
```

## 🔍 **Search & Discovery APIs**

### **Search Legacy Content**
```http
GET /api/v1/legacy/search?q=letter&bucketId=bucket-uuid&categoryId=550e8400-e29b-41d4-a716-446655440011
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `q`: Search query
- `bucketId`: Filter by specific bucket
- `categoryId`: Filter by category
- `contentType`: Content type filter (TEXT, AUDIO, VIDEO, IMAGE, DOCUMENT)
- `recipientType`: Filter by recipient type
- `status`: Filter by content status
- `dateFrom`: Start date filter
- `dateTo`: End date filter
- `page`: Page number
- `size`: Page size

**Response:**
```json
{
  "success": true,
  "message": "Search completed successfully",
  "data": {
    "results": [
      {
        "id": 123,
        "title": "Family Recipe: Grandma's Biryani",
        "description": "Traditional family biryani recipe...",
        "contentType": "TEXT",
        "category": "Culinary Heritage",
        "tags": ["recipe", "biryani", "family"],
        "relevanceScore": 0.95,
        "createdAt": "2025-09-08T12:00:00Z"
      }
    ],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    },
    "facets": {
      "categories": [
        {"name": "Culinary Heritage", "count": 1}
      ],
      "generations": [
        {"level": 2, "count": 1}
      ],
      "contentTypes": [
        {"type": "TEXT", "count": 1}
      ]
    }
  }
}
```

### **Search Buckets**
```http
GET /api/v1/legacy/buckets/search?q=letters&categoryId=550e8400-e29b-41d4-a716-446655440011
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `q`: Search query for bucket names
- `categoryId`: Filter by category
- `creatorId`: Filter by bucket creator
- `isFeatured`: Filter featured buckets
- `page`: Page number
- `size`: Page size

### **Get Content Recommendations**
```http
GET /api/v1/legacy/recommendations?userId=456&limit=10
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `userId`: User ID for recommendations
- `limit`: Number of recommendations (default: 10)
- `type`: Recommendation type (SIMILAR, TRENDING, FAMILY, BUCKET)

### **Get Legacy Timeline**
```http
GET /api/v1/legacy/timeline?familyId=789&generation=2&year=2025
Authorization: Bearer <jwt_token>
```

## 📊 **Analytics APIs**

### **Get Legacy Analytics**
```http
GET /api/v1/legacy/analytics?period=30d&type=content_creation
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `period`: Time period (7d, 30d, 90d, 1y)
- `type`: Analytics type (content_creation, inheritance, engagement)
- `familyId`: Filter by family ID
- `generation`: Filter by generation level

**Response:**
```json
{
  "success": true,
  "message": "Analytics retrieved successfully",
  "data": {
    "period": "30d",
    "type": "content_creation",
    "metrics": {
      "totalContent": 150,
      "contentByType": {
        "TEXT": 80,
        "IMAGE": 45,
        "AUDIO": 15,
        "VIDEO": 10
      },
      "contentByGeneration": {
        "1": 20,
        "2": 60,
        "3": 70
      },
      "contentByCategory": {
        "Culinary Heritage": 30,
        "Family Traditions": 25,
        "Personal Memories": 40,
        "Cultural Heritage": 35,
        "Skills & Knowledge": 20
      },
      "trends": {
        "daily": [
          {"date": "2025-09-01", "count": 5},
          {"date": "2025-09-02", "count": 8},
          {"date": "2025-09-03", "count": 3}
        ]
      }
    }
  }
}
```

### **Get Content Analytics**
```http
GET /api/v1/legacy/analytics/content/123
Authorization: Bearer <jwt_token>
```

### **Get Inheritance Analytics**
```http
GET /api/v1/legacy/analytics/inheritance?period=30d
Authorization: Bearer <jwt_token>
```

## 🏷️ **Category Management APIs**

### **Get Categories**
```http
GET /api/v1/legacy/categories?familyId=789&includeSystem=true
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "message": "Categories retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Culinary Heritage",
      "description": "Family recipes, cooking techniques, and food traditions",
      "parentCategoryId": null,
      "familyId": null,
      "isSystemCategory": true,
      "createdAt": "2025-09-08T12:00:00Z"
    },
    {
      "id": 2,
      "name": "Family Recipes",
      "description": "Our family's special recipes",
      "parentCategoryId": 1,
      "familyId": 789,
      "isSystemCategory": false,
      "createdAt": "2025-09-08T12:00:00Z"
    }
  ]
}
```

### **Create Category**
```http
POST /api/v1/legacy/categories
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "name": "Family Recipes",
  "description": "Our family's special recipes",
  "parentCategoryId": 1,
  "familyId": 789
}
```

## 💬 **Comments APIs**

### **Get Content Comments**
```http
GET /api/v1/legacy/content/123/comments?page=0&size=20
Authorization: Bearer <jwt_token>
```

### **Add Comment**
```http
POST /api/v1/legacy/content/123/comments
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "commentText": "This recipe brings back so many memories!",
  "parentCommentId": null
}
```

### **Update Comment**
```http
PUT /api/v1/legacy/comments/1
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

### **Delete Comment**
```http
DELETE /api/v1/legacy/comments/1
Authorization: Bearer <jwt_token>
```

## 🔒 **Permission Management APIs**

### **Get Content Permissions**
```http
GET /api/v1/legacy/content/123/permissions
Authorization: Bearer <jwt_token>
```

### **Grant Permission**
```http
POST /api/v1/legacy/content/123/permissions
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "userId": 789,
  "permissionType": "EDIT",
  "expiresAt": "2025-12-31T23:59:59Z"
}
```

### **Revoke Permission**
```http
DELETE /api/v1/legacy/content/123/permissions/1
Authorization: Bearer <jwt_token>
```

## 🏥 **Health & Monitoring APIs**

### **Health Check**
```http
GET /api/v1/legacy/health
```

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2025-09-08T12:00:00Z",
  "services": {
    "database": "UP",
    "redis": "UP",
    "kafka": "UP",
    "s3": "UP"
  }
}
```

### **Service Info**
```http
GET /api/v1/legacy/info
```

## 📋 **Error Codes**

| Code | Description |
|------|-------------|
| `LEGACY_CONTENT_NOT_FOUND` | Content not found |
| `LEGACY_INHERITANCE_NOT_FOUND` | Inheritance not found |
| `LEGACY_PERMISSION_DENIED` | Insufficient permissions |
| `LEGACY_INVALID_CONTENT_TYPE` | Invalid content type |
| `LEGACY_FILE_UPLOAD_FAILED` | File upload failed |
| `LEGACY_INHERITANCE_EXPIRED` | Inheritance has expired |
| `LEGACY_CATEGORY_NOT_FOUND` | Category not found |
| `LEGACY_COMMENT_NOT_FOUND` | Comment not found |
| `LEGACY_VALIDATION_ERROR` | Validation error |
| `LEGACY_SERVICE_UNAVAILABLE` | Service temporarily unavailable |

## 🔧 **Rate Limiting**

- **General APIs**: 1000 requests per hour per user
- **File Upload**: 100 requests per hour per user
- **Search APIs**: 500 requests per hour per user
- **Analytics APIs**: 100 requests per hour per user

## 📚 **SDK Examples**

### **JavaScript/Node.js**
```javascript
const axios = require('axios');

const legacyService = axios.create({
  baseURL: 'http://localhost:8085/api/v1/legacy',
  headers: {
    'Authorization': `Bearer ${jwtToken}`,
    'Content-Type': 'application/json'
  }
});

// Create content
const createContent = async (contentData) => {
  try {
    const response = await legacyService.post('/content', contentData);
    return response.data;
  } catch (error) {
    console.error('Error creating content:', error.response.data);
    throw error;
  }
};

// Search content
const searchContent = async (query, filters = {}) => {
  try {
    const params = { q: query, ...filters };
    const response = await legacyService.get('/search', { params });
    return response.data;
  } catch (error) {
    console.error('Error searching content:', error.response.data);
    throw error;
  }
};
```

### **Python**
```python
import requests

class LegacyServiceClient:
    def __init__(self, base_url, jwt_token):
        self.base_url = base_url
        self.headers = {
            'Authorization': f'Bearer {jwt_token}',
            'Content-Type': 'application/json'
        }
    
    def create_content(self, content_data):
        response = requests.post(
            f'{self.base_url}/content',
            json=content_data,
            headers=self.headers
        )
        response.raise_for_status()
        return response.json()
    
    def search_content(self, query, **filters):
        params = {'q': query, **filters}
        response = requests.get(
            f'{self.base_url}/search',
            params=params,
            headers=self.headers
        )
        response.raise_for_status()
        return response.json()
```

---

*This API documentation provides comprehensive coverage of all Legacy Service endpoints and usage examples.*
