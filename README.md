# 🏛️ LegacyKeep Legacy Service

## 📖 **Overview**

The Legacy Service is the core component of LegacyKeep's multi-generational family legacy preservation system. It manages the creation, storage, inheritance, and preservation of family legacy content across generations, ensuring that cultural heritage, traditions, and personal memories are never lost.

## 🎯 **Core Mission**

To preserve and pass down family legacies, traditions, and cultural heritage across generations through a comprehensive digital platform that supports:

- **Multi-Generational Content Flow**: Grandparents → Parents → Children → Grandchildren
- **Cultural Preservation**: Sanatana traditions, family rituals, and customs
- **Multi-Media Support**: Text, audio, video, images, and documents
- **Legacy Inheritance**: Automatic content inheritance to next generation
- **Family Collaboration**: Multi-user content creation and editing
- **Eternal Preservation**: Content that lasts for centuries

## 🏗️ **Architecture**

### **Service Components**
- **Content Management**: CRUD operations for legacy content
- **Media Storage**: Cloud-based file storage with metadata management
- **Inheritance System**: Automatic content inheritance across generations
- **Search & Discovery**: Advanced search and content recommendation
- **Analytics**: Usage tracking and legacy preservation metrics
- **Integration**: Seamless integration with other LegacyKeep services

### **Technology Stack**
- **Framework**: Spring Boot 3.2.0
- **Database**: PostgreSQL with Flyway migrations
- **Cache**: Redis for performance optimization
- **Message Queue**: Apache Kafka for event-driven architecture
- **File Storage**: AWS S3 for media files
- **Search**: Elasticsearch for content discovery
- **Security**: JWT authentication and authorization

## 📊 **Database Schema**

### **Core Tables**

#### **legacy_content**
Main table for storing legacy content metadata
- Content information (title, description, type)
- Creator and family associations
- Generation level and categorization
- Privacy and status controls
- S3 URLs for media files

#### **legacy_inheritance**
Tracks content inheritance across generations
- Inheritance relationships
- Status tracking (pending, accepted, rejected)
- Expiration and notification management

#### **legacy_categories**
Content organization and categorization
- System and family-specific categories
- Hierarchical category structure
- Cultural and religious content categories

#### **legacy_permissions**
Granular access control for content
- User-specific permissions
- Generation-based access levels
- Content modification rights

#### **legacy_analytics**
Usage tracking and engagement metrics
- Content interaction analytics
- User engagement patterns
- Legacy preservation metrics

## 🔗 **Service Integration**

### **Auth Service Integration**
- JWT token validation for all requests
- User authentication and authorization
- Permission-based content access

### **User Service Integration**
- User profile information
- Family relationship data
- User preferences and settings

### **Relationship Service Integration**
- Family tree and relationship mapping
- Generation level determination
- Family member validation

### **Notification Service Integration**
- Inheritance notifications
- Content update alerts
- Legacy preservation reminders

## 📁 **File Storage Strategy**

### **AWS S3 Architecture**
```
legacy-content/
├── families/
│   └── {family_id}/
│       ├── audio/
│       │   ├── {content_id}/
│       │   │   ├── original/
│       │   │   ├── compressed/
│       │   │   └── thumbnails/
│       ├── video/
│       │   ├── {content_id}/
│       │   │   ├── original/
│       │   │   ├── compressed/
│       │   │   ├── thumbnails/
│       │   │   └── previews/
│       ├── images/
│       │   ├── {content_id}/
│       │   │   ├── original/
│       │   │   ├── resized/
│       │   │   └── thumbnails/
│       └── documents/
│           └── {content_id}/
│               ├── original/
│               └── processed/
└── system/
    ├── templates/
    ├── default-content/
    └── backups/
```

### **File Processing Pipeline**
1. **Upload**: Files uploaded to S3 with unique content IDs
2. **Processing**: Automatic compression, resizing, and format optimization
3. **Metadata Extraction**: EXIF data, duration, file properties
4. **Thumbnail Generation**: Preview images for media files
5. **CDN Distribution**: CloudFront for fast global access
6. **Backup**: Cross-region replication for durability

### **Database Storage**
- **File URLs**: S3 object URLs for all media files
- **Metadata**: File size, type, dimensions, duration
- **Processing Status**: Upload, processing, and availability status
- **Access Control**: S3 bucket policies and signed URLs

## 🔍 **Search & Discovery**

### **Search Capabilities**
- **Full-text Search**: Search across all content types
- **Faceted Search**: Filter by category, generation, type, date
- **Semantic Search**: AI-powered content understanding
- **Media Search**: Search within audio/video content
- **Tag-based Search**: Find content by tags and categories

### **Content Discovery**
- **Recommendation Engine**: Suggest relevant content
- **Legacy Timeline**: Chronological view of family legacy
- **Generation-based Discovery**: Content organized by generation
- **Cultural Categories**: Specialized cultural and religious content

## 🔒 **Security & Privacy**

### **Access Control**
- **Family-only Access**: Content restricted to family members
- **Generation-based Permissions**: Different access levels for different generations
- **Content-level Privacy**: Granular privacy controls
- **Audit Logging**: Track all access and modifications

### **Data Protection**
- **Encryption**: All content encrypted at rest and in transit
- **S3 Security**: Bucket policies and IAM roles
- **Data Anonymization**: Analytics data anonymization
- **GDPR Compliance**: Data deletion and portability support

## 📈 **Analytics & Monitoring**

### **Content Analytics**
- **Creation Metrics**: Content creation rates by generation
- **Engagement Metrics**: User interaction with content
- **Inheritance Tracking**: Legacy flow across generations
- **Cultural Preservation**: Cultural content preservation rates

### **System Monitoring**
- **Performance Metrics**: Response times and throughput
- **Storage Metrics**: S3 usage and costs
- **Error Tracking**: System errors and failures
- **User Experience**: User satisfaction and engagement

## 🚀 **API Endpoints**

### **Content Management**
- `POST /api/v1/legacy/content` - Create legacy content
- `GET /api/v1/legacy/content` - Get content with filtering
- `GET /api/v1/legacy/content/{id}` - Get specific content
- `PUT /api/v1/legacy/content/{id}` - Update content
- `DELETE /api/v1/legacy/content/{id}` - Delete content

### **Media Management**
- `POST /api/v1/legacy/media/upload` - Upload media files
- `GET /api/v1/legacy/media/{id}` - Get media file
- `GET /api/v1/legacy/media/{id}/download` - Download media file

### **Inheritance Management**
- `GET /api/v1/legacy/inheritance` - Get inheritance status
- `POST /api/v1/legacy/inheritance/{id}/accept` - Accept inheritance
- `POST /api/v1/legacy/inheritance/{id}/reject` - Reject inheritance

### **Search & Discovery**
- `GET /api/v1/legacy/search` - Search legacy content
- `GET /api/v1/legacy/recommendations` - Get content recommendations
- `GET /api/v1/legacy/timeline` - Get legacy timeline

### **Analytics**
- `GET /api/v1/legacy/analytics` - Get legacy analytics
- `GET /api/v1/legacy/analytics/content` - Get content analytics
- `GET /api/v1/legacy/analytics/inheritance` - Get inheritance analytics

## 🔧 **Configuration**

### **Environment Variables**
```bash
# Database
DB_USERNAME=legacykeep
DB_PASSWORD=password
DATABASE_URL=jdbc:postgresql://localhost:5432/legacy_db

# AWS S3
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
AWS_S3_BUCKET=legacykeep-content
AWS_REGION=us-east-1

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=900000

# Service URLs
AUTH_SERVICE_URL=http://localhost:8081/api/v1
USER_SERVICE_URL=http://localhost:8082/api/v1
RELATIONSHIP_SERVICE_URL=http://localhost:8084/api/v1
NOTIFICATION_SERVICE_URL=http://localhost:8083/api/v1
```

## 🧪 **Testing**

### **Test Categories**
- **Unit Tests**: Individual component testing
- **Integration Tests**: Service integration testing
- **End-to-End Tests**: Complete user journey testing
- **Performance Tests**: Load and stress testing
- **Security Tests**: Security vulnerability testing

### **Test Data**
- **Mock Data**: Synthetic family data for testing
- **Test Media**: Sample audio, video, and image files
- **Test Scenarios**: Multi-generational inheritance scenarios

## 📚 **Development Guide**

### **Getting Started**
1. **Prerequisites**: Java 17, Maven, PostgreSQL, Redis, Kafka
2. **Database Setup**: Run Flyway migrations
3. **AWS Setup**: Configure S3 bucket and IAM roles
4. **Service Dependencies**: Start Auth, User, Relationship, Notification services
5. **Run Application**: `mvn spring-boot:run`

### **Development Workflow**
1. **Feature Development**: Create feature branches
2. **Database Changes**: Add Flyway migrations
3. **Testing**: Write comprehensive tests
4. **Code Review**: Peer review process
5. **Deployment**: Automated deployment pipeline

## 🔮 **Future Enhancements**

### **AI/ML Features**
- **Content Generation**: AI-assisted content creation
- **Content Organization**: Automatic content categorization
- **Legacy Recommendations**: AI-powered content suggestions
- **Content Quality**: AI-based content quality assessment

### **Advanced Features**
- **Virtual Reality**: VR experiences for legacy content
- **Augmented Reality**: AR overlays for family photos
- **Voice Commands**: Voice-controlled legacy management
- **Blockchain**: Immutable legacy content verification

## 📞 **Support & Contact**

- **Documentation**: [Legacy Service Architecture](./docs/LEGACY_SERVICE_ARCHITECTURE.md)
- **API Documentation**: [Swagger UI](http://localhost:8085/api/v1/swagger-ui.html)
- **Health Check**: [Health Endpoint](http://localhost:8085/api/v1/legacy/health)
- **Issues**: [GitHub Issues](https://github.com/lohithsurisetti-dev/legacykeep-legacy-service/issues)

---

*This service is part of the LegacyKeep platform, dedicated to preserving family legacies across generations.*
