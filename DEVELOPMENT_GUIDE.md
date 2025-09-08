# 🏛️ Legacy Service - Development Guide

## 📋 **Getting Started**

### **Prerequisites**
- **Java 17**: OpenJDK or Oracle JDK
- **Maven 3.8+**: Build and dependency management
- **PostgreSQL 14+**: Database server
- **Redis 6+**: Caching and session storage
- **Apache Kafka 2.8+**: Message streaming
- **AWS CLI**: For S3 integration (optional for local development)
- **Docker**: For containerized development (optional)

### **Development Environment Setup**

#### **1. Clone Repository**
```bash
git clone https://github.com/lohithsurisetti-dev/legacykeep-legacy-service.git
cd legacykeep-legacy-service
```

#### **2. Database Setup**
```bash
# Create database
createdb -U legacykeep legacy_db

# Run migrations
mvn flyway:migrate
```

#### **3. Redis Setup**
```bash
# Start Redis server
redis-server

# Verify Redis is running
redis-cli ping
```

#### **4. Kafka Setup**
```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka server
bin/kafka-server-start.sh config/server.properties

# Create topics
bin/kafka-topics.sh --create --topic legacy-events --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic content-events --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic inheritance-events --bootstrap-server localhost:9092
```

#### **5. AWS S3 Setup (Optional for Local Development)**
```bash
# Install AWS CLI
pip install awscli

# Configure AWS credentials
aws configure

# Create S3 bucket
aws s3 mb s3://legacykeep-content-dev
```

#### **6. Run Application**
```bash
# Compile and run
mvn clean compile
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🏗️ **Project Structure**

```
legacy-service/
├── src/
│   ├── main/
│   │   ├── java/com/legacykeep/legacy/
│   │   │   ├── LegacyServiceApplication.java
│   │   │   ├── config/                 # Configuration classes
│   │   │   ├── controller/             # REST controllers
│   │   │   ├── service/                # Business logic interfaces
│   │   │   ├── service/impl/           # Business logic implementations
│   │   │   ├── repository/             # Data access layer
│   │   │   ├── entity/                 # JPA entities
│   │   │   ├── dto/                    # Data transfer objects
│   │   │   ├── exception/              # Custom exceptions
│   │   │   ├── security/               # Security configuration
│   │   │   ├── integration/            # External service integration
│   │   │   └── util/                   # Utility classes
│   │   └── resources/
│   │       ├── application.properties  # Application configuration
│   │       ├── db/migration/           # Flyway migrations
│   │       └── static/                 # Static resources
│   └── test/
│       ├── java/com/legacykeep/legacy/
│       │   ├── controller/             # Controller tests
│       │   ├── service/                # Service tests
│       │   ├── repository/             # Repository tests
│       │   └── integration/            # Integration tests
│       └── resources/
│           └── application-test.properties
├── pom.xml                             # Maven configuration
├── README.md                           # Project overview
├── ARCHITECTURE.md                     # Architecture documentation
├── API_DOCUMENTATION.md                # API documentation
└── DEVELOPMENT_GUIDE.md                # This file
```

## 🔧 **Configuration**

### **Application Properties**

#### **Database Configuration**
```properties
# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://localhost:5432/legacy_db
spring.datasource.username=legacykeep
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

#### **Redis Configuration**
```properties
# Redis Connection
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.database=0

# Redis Cache Configuration
spring.cache.type=redis
spring.cache.redis.time-to-live=3600000
```

#### **Kafka Configuration**
```properties
# Kafka Bootstrap Servers
spring.kafka.bootstrap-servers=localhost:9092

# Consumer Configuration
spring.kafka.consumer.group-id=legacy-service-group
spring.kafka.consumer.auto-offset-reset=earliest
```

#### **AWS S3 Configuration**
```properties
# File Storage Settings
legacy.storage.type=s3
legacy.storage.s3.bucket=legacykeep-content-dev
legacy.storage.s3.region=us-east-1
legacy.storage.max-file-size=100MB
```

### **Environment Variables**
```bash
# Database
export DB_USERNAME=legacykeep
export DB_PASSWORD=password
export DATABASE_URL=jdbc:postgresql://localhost:5432/legacy_db

# AWS S3
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
export AWS_S3_BUCKET=legacykeep-content-dev
export AWS_REGION=us-east-1

# JWT
export JWT_SECRET=your_jwt_secret
export JWT_EXPIRATION=900000

# Service URLs
export AUTH_SERVICE_URL=http://localhost:8081/api/v1
export USER_SERVICE_URL=http://localhost:8082/api/v1
export RELATIONSHIP_SERVICE_URL=http://localhost:8084/api/v1
export NOTIFICATION_SERVICE_URL=http://localhost:8083/api/v1
```

## 🧪 **Testing**

### **Running Tests**
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=LegacyContentServiceTest

# Run tests with coverage
mvn test jacoco:report

# Run integration tests
mvn verify
```

### **Test Categories**

#### **Unit Tests**
- **Service Tests**: Test business logic in isolation
- **Repository Tests**: Test data access layer
- **Utility Tests**: Test utility functions

#### **Integration Tests**
- **Controller Tests**: Test REST endpoints
- **Database Tests**: Test database operations
- **External Service Tests**: Test service integrations

#### **End-to-End Tests**
- **User Journey Tests**: Test complete user workflows
- **Performance Tests**: Test system performance
- **Security Tests**: Test security features

### **Test Data Setup**
```java
@TestConfiguration
public class TestDataSetup {
    
    @Bean
    @Primary
    public TestDataBuilder testDataBuilder() {
        return new TestDataBuilder();
    }
    
    @Bean
    public MockExternalServices mockExternalServices() {
        return new MockExternalServices();
    }
}
```

## 🔄 **Development Workflow**

### **1. Feature Development**
```bash
# Create feature branch
git checkout -b feature/legacy-content-management

# Make changes
# ... implement feature ...

# Run tests
mvn test

# Commit changes
git add .
git commit -m "feat: implement legacy content management"

# Push branch
git push origin feature/legacy-content-management
```

### **2. Database Changes**
```bash
# Create migration file
touch src/main/resources/db/migration/V2__Add_new_feature.sql

# Write migration SQL
# ... SQL changes ...

# Test migration
mvn flyway:migrate

# Commit migration
git add src/main/resources/db/migration/V2__Add_new_feature.sql
git commit -m "db: add new feature migration"
```

### **3. Code Review Process**
1. **Create Pull Request**: Submit PR for review
2. **Code Review**: Peer review of changes
3. **Testing**: Automated and manual testing
4. **Approval**: Get approval from reviewers
5. **Merge**: Merge to main branch

### **4. Deployment**
```bash
# Build application
mvn clean package

# Run application
java -jar target/legacy-service-1.0.0-SNAPSHOT.jar

# Or with Docker
docker build -t legacy-service .
docker run -p 8085:8085 legacy-service
```

## 🐛 **Debugging**

### **Local Debugging**
```bash
# Run with debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Connect debugger to port 5005
```

### **Logging Configuration**
```properties
# Logging Levels
logging.level.com.legacykeep.legacy=DEBUG
logging.level.org.springframework.kafka=WARN
logging.level.org.apache.kafka=WARN
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### **Common Issues**

#### **Database Connection Issues**
```bash
# Check PostgreSQL status
pg_ctl status

# Check database exists
psql -U legacykeep -l

# Check user permissions
psql -U legacykeep -d legacy_db -c "\du"
```

#### **Redis Connection Issues**
```bash
# Check Redis status
redis-cli ping

# Check Redis configuration
redis-cli config get "*"
```

#### **Kafka Connection Issues**
```bash
# Check Kafka status
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Check consumer groups
bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```

## 📊 **Monitoring & Observability**

### **Health Checks**
```bash
# Check application health
curl http://localhost:8085/api/v1/legacy/health

# Check specific services
curl http://localhost:8085/api/v1/legacy/health/database
curl http://localhost:8085/api/v1/legacy/health/redis
curl http://localhost:8085/api/v1/legacy/health/kafka
```

### **Metrics**
```bash
# Application metrics
curl http://localhost:8085/api/v1/legacy/actuator/metrics

# Specific metrics
curl http://localhost:8085/api/v1/legacy/actuator/metrics/jvm.memory.used
curl http://localhost:8085/api/v1/legacy/actuator/metrics/http.server.requests
```

### **Logs**
```bash
# View application logs
tail -f logs/legacy-service.log

# Filter logs by level
grep "ERROR" logs/legacy-service.log

# Filter logs by component
grep "LegacyContentService" logs/legacy-service.log
```

## 🔒 **Security**

### **JWT Token Testing**
```bash
# Get JWT token from Auth Service
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identifier": "test@example.com", "password": "password"}'

# Use token in requests
curl -H "Authorization: Bearer <jwt_token>" \
  http://localhost:8085/api/v1/legacy/content
```

### **Permission Testing**
```bash
# Test with different user roles
# ... test various permission scenarios ...
```

## 🚀 **Performance Optimization**

### **Database Optimization**
```sql
-- Check slow queries
SELECT query, mean_time, calls 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;

-- Check index usage
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;
```

### **Cache Optimization**
```bash
# Check Redis memory usage
redis-cli info memory

# Check cache hit rate
redis-cli info stats | grep keyspace
```

### **Application Optimization**
```bash
# JVM tuning
export JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"

# Run with optimized JVM
java $JAVA_OPTS -jar target/legacy-service-1.0.0-SNAPSHOT.jar
```

## 📚 **Best Practices**

### **Code Quality**
- **Follow SOLID Principles**: Single responsibility, open/closed, etc.
- **Write Clean Code**: Readable, maintainable, and well-documented
- **Use Design Patterns**: Repository, Service, DTO patterns
- **Handle Exceptions**: Proper exception handling and logging

### **Database Best Practices**
- **Use Migrations**: Always use Flyway for schema changes
- **Optimize Queries**: Use proper indexing and query optimization
- **Handle Transactions**: Proper transaction management
- **Data Validation**: Validate data at multiple layers

### **API Design**
- **RESTful Design**: Follow REST conventions
- **Consistent Responses**: Use consistent response formats
- **Proper HTTP Status Codes**: Use appropriate status codes
- **API Versioning**: Version your APIs properly

### **Security Best Practices**
- **Input Validation**: Validate all inputs
- **Authentication**: Proper JWT token validation
- **Authorization**: Check permissions for all operations
- **Data Encryption**: Encrypt sensitive data

## 🔮 **Future Development**

### **Planned Features**
- **AI/ML Integration**: Content analysis and recommendations
- **Advanced Search**: Elasticsearch integration
- **Real-time Features**: WebSocket support
- **Mobile API**: Mobile-optimized endpoints

### **Architecture Evolution**
- **Microservice Decomposition**: Split into smaller services
- **Event Sourcing**: Implement event sourcing pattern
- **CQRS**: Separate read and write models
- **Multi-cloud**: Support multiple cloud providers

---

*This development guide provides comprehensive information for developing and maintaining the Legacy Service.*
