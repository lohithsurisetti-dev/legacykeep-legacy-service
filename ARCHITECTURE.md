# 🏛️ Legacy Service - Detailed Architecture

## 📋 **System Overview**

The Legacy Service is designed as a microservice within the LegacyKeep ecosystem, responsible for managing multi-generational family legacy preservation. It follows a cloud-native architecture with AWS S3 for media storage and PostgreSQL for metadata management.

## 🏗️ **Architecture Principles**

### **1. Cloud-First Design**
- **AWS S3**: Primary storage for all media files
- **CloudFront CDN**: Global content delivery
- **Auto-scaling**: Dynamic scaling based on demand
- **Multi-region**: Cross-region replication for durability

### **2. Microservices Architecture**
- **Single Responsibility**: Dedicated to legacy content management
- **Loose Coupling**: Minimal dependencies on other services
- **High Cohesion**: Related functionality grouped together
- **Independent Deployment**: Deployable without affecting other services

### **3. Event-Driven Design**
- **Kafka Integration**: Asynchronous communication
- **Event Sourcing**: Track all content changes
- **CQRS Pattern**: Separate read and write models
- **Eventual Consistency**: Acceptable for legacy content

## 🔧 **Technical Architecture**

### **Service Layer Architecture**
```
┌─────────────────────────────────────────────────────────────┐
│                    Legacy Service                           │
├─────────────────────────────────────────────────────────────┤
│  Controller Layer  │  Service Layer  │  Repository Layer   │
│  - REST APIs       │  - Business     │  - Data Access      │
│  - Validation      │    Logic        │  - JPA/Hibernate    │
│  - Security        │  - Integration  │  - Query            │
│  - Documentation   │  - Events       │    Optimization     │
└─────────────────────────────────────────────────────────────┘
```

### **Data Flow Architecture**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │───▶│   Legacy    │───▶│ PostgreSQL  │
│  (Web/Mobile)│    │  Service    │    │ (Metadata)  │
└─────────────┘    └─────────────┘    └─────────────┘
                           │
                           ▼
                   ┌─────────────┐
                   │   AWS S3    │
                   │ (Media Files)│
                   └─────────────┘
```

## 📊 **Database Design**

### **Entity Relationship Diagram**
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  legacy_content │    │legacy_inheritance│    │legacy_categories│
│                 │    │                 │    │                 │
│ id (PK)         │◄───┤ content_id (FK) │    │ id (PK)         │
│ title           │    │ from_user_id    │    │ name            │
│ description     │    │ to_user_id      │    │ description     │
│ content_type    │    │ status          │    │ parent_id (FK)  │
│ s3_url          │    │ created_at      │    │ family_id (FK)  │
│ creator_id (FK) │    └─────────────────┘    └─────────────────┘
│ family_id (FK)  │
│ generation_level│
│ category        │
│ tags            │
│ privacy_level   │
│ status          │
│ created_at      │
│ updated_at      │
└─────────────────┘
```

### **Database Optimization**
- **Indexing Strategy**: Optimized indexes for common queries
- **Partitioning**: Time-based partitioning for analytics
- **Connection Pooling**: HikariCP for database connections
- **Query Optimization**: N+1 query prevention and caching

## 📁 **File Storage Architecture**

### **AWS S3 Bucket Structure**
```
legacykeep-content/
├── families/
│   └── {family_id}/
│       ├── audio/
│       │   └── {content_id}/
│       │       ├── original/
│       │       │   └── {filename}.mp3
│       │       ├── compressed/
│       │       │   └── {filename}_compressed.mp3
│       │       └── thumbnails/
│       │           └── {filename}_thumb.jpg
│       ├── video/
│       │   └── {content_id}/
│       │       ├── original/
│       │       │   └── {filename}.mp4
│       │       ├── compressed/
│       │       │   └── {filename}_compressed.mp4
│       │       ├── thumbnails/
│       │       │   └── {filename}_thumb.jpg
│       │       └── previews/
│       │           └── {filename}_preview.mp4
│       ├── images/
│       │   └── {content_id}/
│       │       ├── original/
│       │       │   └── {filename}.jpg
│       │       ├── resized/
│       │       │   ├── {filename}_1920x1080.jpg
│       │       │   └── {filename}_800x600.jpg
│       │       └── thumbnails/
│       │           └── {filename}_thumb.jpg
│       └── documents/
│           └── {content_id}/
│               ├── original/
│               │   └── {filename}.pdf
│               └── processed/
│                   └── {filename}_text.txt
└── system/
    ├── templates/
    ├── default-content/
    └── backups/
```

### **File Processing Pipeline**
```
Upload Request → S3 Upload → Processing Queue → File Processing → Metadata Update → CDN Distribution
     │              │              │              │              │              │
     ▼              ▼              ▼              ▼              ▼              ▼
┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐
│ Client  │   │   S3    │   │  Kafka  │   │Process  │   │Database │   │CloudFront│
│ Upload  │   │ Storage │   │  Queue  │   │Service  │   │ Update  │   │   CDN   │
└─────────┘   └─────────┘   └─────────┘   └─────────┘   └─────────┘   └─────────┘
```

### **File Processing Features**
- **Automatic Compression**: Reduce file sizes for storage efficiency
- **Format Conversion**: Convert to web-optimized formats
- **Thumbnail Generation**: Create preview images for media files
- **Metadata Extraction**: Extract EXIF data and file properties
- **Virus Scanning**: Security scanning for uploaded files
- **Duplicate Detection**: SHA-256 hash-based deduplication

## 🔍 **Search Architecture**

### **Search Implementation**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ PostgreSQL  │    │Elasticsearch│    │   Redis     │
│ (Metadata)  │    │ (Full-text) │    │  (Cache)    │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       └───────────────────┼───────────────────┘
                           │
                   ┌─────────────┐
                   │Search API   │
                   │ (Aggregated)│
                   └─────────────┘
```

### **Search Features**
- **Full-text Search**: Search across all content types
- **Faceted Search**: Filter by category, generation, type
- **Semantic Search**: AI-powered content understanding
- **Media Search**: Search within audio/video content
- **Auto-complete**: Suggest search terms
- **Search Analytics**: Track search patterns

## 🔒 **Security Architecture**

### **Authentication & Authorization**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │───▶│   Legacy    │───▶│   Auth      │
│  Request    │    │  Service    │    │  Service    │
└─────────────┘    └─────────────┘    └─────────────┘
                           │
                           ▼
                   ┌─────────────┐
                   │Permission   │
                   │  Check      │
                   └─────────────┘
```

### **Security Layers**
1. **API Gateway**: Rate limiting and request validation
2. **JWT Validation**: Token-based authentication
3. **Service Authentication**: Inter-service communication security
4. **Database Security**: Encrypted connections and data
5. **S3 Security**: Bucket policies and IAM roles
6. **Network Security**: VPC and security groups

### **Data Protection**
- **Encryption at Rest**: All data encrypted in database and S3
- **Encryption in Transit**: TLS/SSL for all communications
- **Access Logging**: Comprehensive audit trails
- **Data Anonymization**: Analytics data anonymization
- **GDPR Compliance**: Data deletion and portability

## 📈 **Performance Architecture**

### **Caching Strategy**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │───▶│   Redis     │───▶│ PostgreSQL  │
│  Request    │    │  (Cache)    │    │ (Database)  │
└─────────────┘    └─────────────┘    └─────────────┘
                           │
                           ▼
                   ┌─────────────┐
                   │CloudFront   │
                   │   (CDN)     │
                   └─────────────┘
```

### **Performance Optimizations**
- **Redis Caching**: Frequently accessed data caching
- **CDN Distribution**: Global content delivery
- **Database Indexing**: Optimized query performance
- **Connection Pooling**: Efficient database connections
- **Async Processing**: Non-blocking file processing
- **Load Balancing**: Distribute traffic across instances

## 🔄 **Event-Driven Architecture**

### **Kafka Integration**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Legacy    │───▶│   Kafka     │───▶│Notification │
│  Service    │    │  Events     │    │  Service    │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       ▼                   ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│Content      │    │Inheritance  │    │Analytics    │
│Events       │    │Events       │    │Events       │
└─────────────┘    └─────────────┘    └─────────────┘
```

### **Event Types**
- **Content Events**: Content creation, update, deletion
- **Inheritance Events**: Inheritance requests, acceptances, rejections
- **User Events**: User registration, profile updates
- **Analytics Events**: Content views, downloads, interactions

## 🚀 **Deployment Architecture**

### **Container Architecture**
```
┌─────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                       │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Legacy    │  │   Legacy    │  │   Legacy    │         │
│  │  Service    │  │  Service    │  │  Service    │         │
│  │ (Instance 1)│  │ (Instance 2)│  │ (Instance 3)│         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Redis     │  │ PostgreSQL  │  │   Kafka     │         │
│  │  (Cache)    │  │ (Database)  │  │ (Message)   │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### **Deployment Strategy**
- **Blue-Green Deployment**: Zero-downtime deployments
- **Rolling Updates**: Gradual service updates
- **Health Checks**: Automatic health monitoring
- **Auto-scaling**: Dynamic scaling based on load
- **Load Balancing**: Traffic distribution

## 📊 **Monitoring & Observability**

### **Monitoring Stack**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Prometheus  │    │   Grafana   │    │    ELK      │
│ (Metrics)   │    │(Dashboards) │    │  (Logs)     │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       └───────────────────┼───────────────────┘
                           │
                   ┌─────────────┐
                   │   Alert     │
                   │  Manager    │
                   └─────────────┘
```

### **Key Metrics**
- **Application Metrics**: Response times, error rates, throughput
- **Infrastructure Metrics**: CPU, memory, disk, network
- **Business Metrics**: Content creation, inheritance, user engagement
- **Security Metrics**: Failed authentications, suspicious activities

## 🔮 **Future Architecture Considerations**

### **Scalability**
- **Horizontal Scaling**: Add more service instances
- **Database Sharding**: Partition data across multiple databases
- **Microservice Decomposition**: Split into smaller services
- **Event Sourcing**: Store events for better scalability

### **Advanced Features**
- **AI/ML Integration**: Content analysis and recommendations
- **Blockchain**: Immutable content verification
- **Edge Computing**: Process content closer to users
- **Multi-cloud**: Distribute across multiple cloud providers

---

*This architecture document provides a comprehensive overview of the Legacy Service design and implementation approach.*
