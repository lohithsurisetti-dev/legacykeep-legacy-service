# Inheritance System Documentation

## Overview

The Inheritance System is a core feature of the LegacyKeep platform that enables automatic content inheritance based on family relationships and custom rules. It provides a flexible framework for multi-generational legacy preservation with dynamic targeting and triggering mechanisms.

## Architecture

### Core Components

1. **Inheritance Rules** - Define who inherits what content and when
2. **Inheritance Status** - Track the status of inheritance for each recipient
3. **Inheritance Events** - Log all inheritance-related activities
4. **Relationship Service Integration** - Dynamic targeting based on family relationships

### Database Schema

#### Inheritance Rules Table (`inheritance_rules`)
- `id` - Primary key (UUID)
- `content_id` - Reference to legacy content (UUID)
- `creator_id` - User who created the rule (UUID)
- `target_type` - How to target recipients (RELATIONSHIP_TYPE, RELATIONSHIP_CATEGORY, GENERATION, CONTEXT, CUSTOM)
- `target_value` - Specific target value (e.g., "Son", "FAMILY", "1")
- `target_metadata` - Additional targeting information (JSONB)
- `inheritance_trigger` - When to trigger inheritance (IMMEDIATE, TIME_BASED, EVENT_BASED, MANUAL)
- `trigger_metadata` - Trigger-specific configuration (JSONB)
- `status` - Rule status (ACTIVE, PAUSED, INACTIVE)
- `priority` - Rule priority (integer)
- `created_at`, `updated_at` - Timestamps
- `created_by`, `updated_by` - Audit fields

#### Inheritance Status Table (`inheritance_status`)
- `id` - Primary key (UUID)
- `inheritance_rule_id` - Reference to inheritance rule (UUID)
- `recipient_id` - User who should inherit content (UUID)
- `content_id` - Content being inherited (UUID)
- `status` - Inheritance status (PENDING, GRANTED, ACCESSED, DECLINED, EXPIRED)
- `relationship_context` - Relationship information (JSONB)
- `metadata` - Additional status information (JSONB)
- `created_at`, `updated_at` - Timestamps
- `created_by`, `updated_by` - Audit fields

#### Inheritance Events Table (`inheritance_events`)
- `id` - Primary key (UUID)
- `inheritance_rule_id` - Reference to inheritance rule (UUID)
- `event_type` - Type of event (RULE_CREATED, RULE_UPDATED, RULE_DELETED, INHERITANCE_GRANTED, INHERITANCE_ACCESSED, INHERITANCE_DECLINED)
- `event_data` - Event-specific data (JSONB)
- `created_at` - Timestamp
- `created_by` - Audit field

## API Endpoints

### Inheritance Rule Management

#### Create Inheritance Rule
```http
POST /api/v1/inheritance/rules
Content-Type: application/json

{
  "contentId": "469173d8-0802-4f06-a206-fc9ac253242a",
  "targetType": "RELATIONSHIP_TYPE",
  "targetValue": "Son",
  "inheritanceTrigger": "IMMEDIATE",
  "priority": 1
}
```

#### Update Inheritance Rule
```http
PUT /api/v1/inheritance/rules/{ruleId}
Content-Type: application/json

{
  "targetValue": "Daughter",
  "priority": 2
}
```

#### Get Inheritance Rule by ID
```http
GET /api/v1/inheritance/rules/{ruleId}
```

#### Get Inheritance Rules by Status
```http
GET /api/v1/inheritance/rules/status/{status}
```

#### Delete Inheritance Rule
```http
DELETE /api/v1/inheritance/rules/{ruleId}
```

#### Activate/Pause Inheritance Rule
```http
POST /api/v1/inheritance/rules/{ruleId}/activate
POST /api/v1/inheritance/rules/{ruleId}/pause
```

### Relationship-Based Inheritance

#### Create Relationship-Based Rule
```http
POST /api/v1/inheritance/rules/relationship-type
Content-Type: application/json

{
  "contentId": "469173d8-0802-4f06-a206-fc9ac253242a",
  "relationshipTypeName": "Son",
  "inheritanceTrigger": "IMMEDIATE",
  "triggerMetadata": {"test": "data"},
  "priority": 3
}
```

#### Create Category-Based Rule
```http
POST /api/v1/inheritance/rules/relationship-category
Content-Type: application/json

{
  "contentId": "469173d8-0802-4f06-a206-fc9ac253242a",
  "relationshipCategory": "FAMILY",
  "inheritanceTrigger": "IMMEDIATE",
  "triggerMetadata": {},
  "priority": 1
}
```

### Inheritance Status Management

#### Get Inheritance Status by Recipient
```http
GET /api/v1/inheritance/recipient/{recipientId}/status
```

#### Get Inheritance Status by Content
```http
GET /api/v1/inheritance/content/{contentId}/status
```

#### Mark Content as Accessed
```http
POST /api/v1/inheritance/content/{contentId}/access/{recipientId}?ruleId={ruleId}
```

#### Decline Inheritance
```http
POST /api/v1/inheritance/content/{contentId}/decline/{recipientId}?ruleId={ruleId}
```

### Utility Endpoints

#### Check Inheritance Access
```http
GET /api/v1/inheritance/access/{userId}/{contentId}
```

#### Get Eligible Recipients
```http
GET /api/v1/inheritance/content/{contentId}/eligible-recipients
```

#### Get Available Relationship Types
```http
GET /api/v1/inheritance/relationship-types
```

#### Get Available Relationship Categories
```http
GET /api/v1/inheritance/relationship-categories
```

## Target Types

### RELATIONSHIP_TYPE
Targets users based on specific relationship types (e.g., "Son", "Daughter", "Father", "Mother")

### RELATIONSHIP_CATEGORY
Targets users based on relationship categories:
- `FAMILY` - Family relationships
- `SOCIAL` - Social relationships
- `PROFESSIONAL` - Professional relationships
- `CUSTOM` - Custom relationship types

### GENERATION
Targets users based on generation level (e.g., "1" for children, "2" for grandchildren)

### CONTEXT
Targets users based on relationship context (e.g., "Elder Son", "Younger Daughter")

### CUSTOM
Allows for custom targeting logic with user-defined parameters

## Inheritance Triggers

### IMMEDIATE
Inheritance is processed immediately when the rule is created

### TIME_BASED
Inheritance is processed at a specific time or after a certain duration

### EVENT_BASED
Inheritance is processed when specific events occur (e.g., user registration, content update)

### MANUAL
Inheritance is processed manually by authorized users

## Inheritance Statuses

### PENDING
Inheritance has been granted but not yet accessed by the recipient

### GRANTED
Inheritance has been granted and is available to the recipient

### ACCESSED
The recipient has accessed the inherited content

### DECLINED
The recipient has declined the inheritance

### EXPIRED
The inheritance has expired and is no longer available

## Integration with Relationship Service

The Inheritance System integrates with the Relationship Service to provide dynamic targeting based on family relationships. This integration allows for:

1. **Dynamic Recipient Discovery** - Automatically find users based on relationship types
2. **Real-time Relationship Updates** - Inheritance rules adapt to relationship changes
3. **Flexible Targeting** - Support for various relationship categories and types
4. **Context-Aware Inheritance** - Consider relationship context and metadata

## Error Handling

The system includes comprehensive error handling with custom exceptions:

- `InheritanceRuleNotFoundException` - When inheritance rule is not found
- `InheritanceStatusNotFoundException` - When inheritance status is not found
- `RelationshipNotFoundException` - When relationship is not found
- `RelationshipTypeNotFoundException` - When relationship type is not found
- `ContentNotFoundException` - When content is not found

All errors are wrapped in the standard `ApiResponse` format with appropriate HTTP status codes.

## Security

- All endpoints require proper authentication (JWT tokens)
- Authorization checks ensure users can only access their own inheritance data
- Audit trails track all inheritance-related activities
- Input validation prevents malicious data injection

## Performance Considerations

- Database indexes on frequently queried fields
- Pagination support for large result sets
- Lazy loading for related entities
- Caching for relationship data
- Asynchronous processing for inheritance events

## Testing

The system has been thoroughly tested with:

- ✅ Basic inheritance rule creation and management
- ✅ Relationship-based inheritance targeting
- ✅ Status tracking and updates
- ✅ Error handling and validation
- ✅ Authentication and authorization
- ✅ Database integration and JSONB serialization

## Future Enhancements

1. **Advanced Triggering** - More sophisticated trigger conditions
2. **Bulk Operations** - Batch processing for multiple inheritance rules
3. **Notification Integration** - Real-time notifications for inheritance events
4. **Analytics** - Inheritance usage and effectiveness metrics
5. **Templates** - Predefined inheritance rule templates
6. **Conditional Logic** - Complex conditional inheritance rules

## Migration History

- **V7** - Create inheritance_rules table
- **V8** - Create inheritance_status table  
- **V9** - Create inheritance_events table

## Dependencies

- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL with JSONB support
- Jackson for JSON serialization
- Lombok for boilerplate reduction
- Spring Security for authentication
- Flyway for database migrations

---

*Last Updated: September 9, 2025*
*Version: 1.0.0*
