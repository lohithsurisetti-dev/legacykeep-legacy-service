-- =============================================================================
-- LegacyKeep Legacy Service - Enhanced Database Schema
-- Migration: V1__Create_initial_legacy_tables.sql
-- Description: Creates the enhanced database schema for bucket-based legacy content management
-- =============================================================================

-- =============================================================================
-- Legacy Categories (Hierarchical System)
-- =============================================================================
CREATE TABLE legacy_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_category_id UUID, -- For hierarchical structure
    category_level INTEGER NOT NULL DEFAULT 1, -- 1=Root, 2=Sub, 3=Sub-sub
    category_type VARCHAR(50) DEFAULT 'SYSTEM' CHECK (category_type IN ('SYSTEM', 'USER_CREATED')),
    icon VARCHAR(100), -- For UI display
    color VARCHAR(20), -- For UI theming
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_legacy_categories_parent FOREIGN KEY (parent_category_id) REFERENCES legacy_categories(id) ON DELETE CASCADE
);

-- =============================================================================
-- Legacy Buckets (User Collections)
-- =============================================================================
CREATE TABLE legacy_buckets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id UUID NOT NULL, -- Must belong to a category
    creator_id UUID NOT NULL,
    family_id UUID NOT NULL,
    bucket_type VARCHAR(50) DEFAULT 'CUSTOM' CHECK (bucket_type IN ('CUSTOM', 'SYSTEM')),
    privacy_level VARCHAR(50) DEFAULT 'FAMILY' CHECK (privacy_level IN ('PRIVATE', 'FAMILY', 'EXTENDED_FAMILY', 'PUBLIC')),
    is_featured BOOLEAN DEFAULT false, -- For highlighting important buckets
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_legacy_buckets_category FOREIGN KEY (category_id) REFERENCES legacy_categories(id) ON DELETE RESTRICT,
    CONSTRAINT fk_legacy_buckets_creator FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================================================
-- Legacy Content (Enhanced)
-- =============================================================================
CREATE TABLE legacy_content (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    content TEXT, -- For text content
    content_type VARCHAR(50) NOT NULL CHECK (content_type IN ('TEXT', 'AUDIO', 'VIDEO', 'IMAGE', 'DOCUMENT')),
    bucket_id UUID NOT NULL, -- References legacy_buckets
    creator_id UUID NOT NULL,
    family_id UUID NOT NULL,
    generation_level INTEGER,
    privacy_level VARCHAR(50) DEFAULT 'FAMILY' CHECK (privacy_level IN ('PRIVATE', 'FAMILY', 'EXTENDED_FAMILY', 'PUBLIC')),
    status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED', 'DELETED')),
    is_featured BOOLEAN DEFAULT false,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_legacy_content_bucket FOREIGN KEY (bucket_id) REFERENCES legacy_buckets(id) ON DELETE CASCADE,
    CONSTRAINT fk_legacy_content_creator FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================================================
-- Legacy Recipients (Targeted Delivery)
-- =============================================================================
CREATE TABLE legacy_recipients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL,
    recipient_id UUID NOT NULL,
    recipient_type VARCHAR(50) NOT NULL CHECK (recipient_type IN ('SPECIFIC_USER', 'GENERATION', 'RELATIONSHIP')),
    recipient_relationship VARCHAR(100), -- "elder son", "both sons", "grandchildren"
    access_level VARCHAR(50) DEFAULT 'READ' CHECK (access_level IN ('READ', 'COMMENT', 'EDIT')),
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED')),
    personal_message TEXT, -- Personal message from creator
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_legacy_recipients_content FOREIGN KEY (content_id) REFERENCES legacy_content(id) ON DELETE CASCADE,
    CONSTRAINT fk_legacy_recipients_user FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_content_recipient UNIQUE (content_id, recipient_id)
);

-- =============================================================================
-- Legacy Media Files (S3 Integration)
-- =============================================================================
CREATE TABLE legacy_media_files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    s3_url TEXT NOT NULL,
    thumbnail_url TEXT,
    file_type VARCHAR(50) NOT NULL CHECK (file_type IN ('AUDIO', 'VIDEO', 'IMAGE', 'DOCUMENT')),
    processing_status VARCHAR(50) DEFAULT 'PENDING' CHECK (processing_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_legacy_media_files_content FOREIGN KEY (content_id) REFERENCES legacy_content(id) ON DELETE CASCADE
);

-- =============================================================================
-- Legacy Comments Table
-- =============================================================================
CREATE TABLE legacy_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL,
    user_id UUID NOT NULL,
    comment_text TEXT NOT NULL,
    parent_comment_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'DELETED', 'HIDDEN')),
    
    CONSTRAINT fk_legacy_comments_content FOREIGN KEY (content_id) REFERENCES legacy_content(id) ON DELETE CASCADE,
    CONSTRAINT fk_legacy_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_legacy_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES legacy_comments(id) ON DELETE CASCADE
);

-- =============================================================================
-- Legacy Analytics Table
-- =============================================================================
CREATE TABLE legacy_analytics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL,
    user_id UUID NOT NULL,
    action_type VARCHAR(50) NOT NULL CHECK (action_type IN ('VIEW', 'DOWNLOAD', 'SHARE', 'COMMENT', 'LIKE', 'INHERIT', 'ACCEPT', 'REJECT')),
    action_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB,
    
    CONSTRAINT fk_legacy_analytics_content FOREIGN KEY (content_id) REFERENCES legacy_content(id) ON DELETE CASCADE,
    CONSTRAINT fk_legacy_analytics_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================================================
-- Indexes for Performance
-- =============================================================================

-- Legacy Categories Indexes
CREATE INDEX idx_legacy_categories_parent ON legacy_categories(parent_category_id);
CREATE INDEX idx_legacy_categories_type ON legacy_categories(category_type);
CREATE INDEX idx_legacy_categories_active ON legacy_categories(is_active);

-- Legacy Buckets Indexes
CREATE INDEX idx_legacy_buckets_category ON legacy_buckets(category_id);
CREATE INDEX idx_legacy_buckets_creator ON legacy_buckets(creator_id);
CREATE INDEX idx_legacy_buckets_family ON legacy_buckets(family_id);
CREATE INDEX idx_legacy_buckets_featured ON legacy_buckets(is_featured);

-- Legacy Content Indexes
CREATE INDEX idx_legacy_content_bucket ON legacy_content(bucket_id);
CREATE INDEX idx_legacy_content_creator ON legacy_content(creator_id);
CREATE INDEX idx_legacy_content_family ON legacy_content(family_id);
CREATE INDEX idx_legacy_content_type ON legacy_content(content_type);
CREATE INDEX idx_legacy_content_status ON legacy_content(status);
CREATE INDEX idx_legacy_content_featured ON legacy_content(is_featured);
CREATE INDEX idx_legacy_content_created_at ON legacy_content(created_at);

-- Legacy Recipients Indexes
CREATE INDEX idx_legacy_recipients_content ON legacy_recipients(content_id);
CREATE INDEX idx_legacy_recipients_user ON legacy_recipients(recipient_id);
CREATE INDEX idx_legacy_recipients_status ON legacy_recipients(status);
CREATE INDEX idx_legacy_recipients_type ON legacy_recipients(recipient_type);

-- Legacy Media Files Indexes
CREATE INDEX idx_legacy_media_files_content ON legacy_media_files(content_id);
CREATE INDEX idx_legacy_media_files_type ON legacy_media_files(file_type);
CREATE INDEX idx_legacy_media_files_status ON legacy_media_files(processing_status);

-- Legacy Comments Indexes
CREATE INDEX idx_legacy_comments_content ON legacy_comments(content_id);
CREATE INDEX idx_legacy_comments_user ON legacy_comments(user_id);
CREATE INDEX idx_legacy_comments_parent ON legacy_comments(parent_comment_id);
CREATE INDEX idx_legacy_comments_created_at ON legacy_comments(created_at);

-- Legacy Analytics Indexes
CREATE INDEX idx_legacy_analytics_content ON legacy_analytics(content_id);
CREATE INDEX idx_legacy_analytics_user ON legacy_analytics(user_id);
CREATE INDEX idx_legacy_analytics_action ON legacy_analytics(action_type);
CREATE INDEX idx_legacy_analytics_date ON legacy_analytics(action_date);

-- =============================================================================
-- System Categories (Hierarchical Default Categories)
-- =============================================================================

-- Root Categories
INSERT INTO legacy_categories (id, name, description, category_level, category_type, icon, color, sort_order) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Personal & Family', 'Personal and family-related content', 1, 'SYSTEM', 'family', '#4CAF50', 1),
('550e8400-e29b-41d4-a716-446655440002', 'Cultural & Religious', 'Cultural heritage and religious content', 1, 'SYSTEM', 'temple', '#FF9800', 2),
('550e8400-e29b-41d4-a716-446655440003', 'Knowledge & Skills', 'Educational and skill-based content', 1, 'SYSTEM', 'book', '#2196F3', 3),
('550e8400-e29b-41d4-a716-446655440004', 'Media & Memories', 'Photos, videos, and digital memories', 1, 'SYSTEM', 'camera', '#9C27B0', 4),
('550e8400-e29b-41d4-a716-446655440005', 'Future & Legacy', 'Future plans and legacy guidance', 1, 'SYSTEM', 'future', '#607D8B', 5);

-- Personal & Family Subcategories
INSERT INTO legacy_categories (id, name, description, parent_category_id, category_level, category_type, icon, color, sort_order) VALUES
('550e8400-e29b-41d4-a716-446655440011', 'Letters & Messages', 'Personal letters and messages', '550e8400-e29b-41d4-a716-446655440001', 2, 'SYSTEM', 'mail', '#4CAF50', 1),
('550e8400-e29b-41d4-a716-446655440012', 'Family Stories', 'Family narratives and stories', '550e8400-e29b-41d4-a716-446655440001', 2, 'SYSTEM', 'story', '#4CAF50', 2),
('550e8400-e29b-41d4-a716-446655440013', 'Personal Memories', 'Individual memories and experiences', '550e8400-e29b-41d4-a716-446655440001', 2, 'SYSTEM', 'memory', '#4CAF50', 3),
('550e8400-e29b-41d4-a716-446655440014', 'Life Lessons', 'Wisdom and life experiences', '550e8400-e29b-41d4-a716-446655440001', 2, 'SYSTEM', 'wisdom', '#4CAF50', 4);

-- Cultural & Religious Subcategories
INSERT INTO legacy_categories (id, name, description, parent_category_id, category_level, category_type, icon, color, sort_order) VALUES
('550e8400-e29b-41d4-a716-446655440021', 'Traditions & Rituals', 'Family traditions and rituals', '550e8400-e29b-41d4-a716-446655440002', 2, 'SYSTEM', 'ritual', '#FF9800', 1),
('550e8400-e29b-41d4-a716-446655440022', 'Festival Celebrations', 'Festival and celebration content', '550e8400-e29b-41d4-a716-446655440002', 2, 'SYSTEM', 'festival', '#FF9800', 2),
('550e8400-e29b-41d4-a716-446655440023', 'Religious Teachings', 'Religious and spiritual teachings', '550e8400-e29b-41d4-a716-446655440002', 2, 'SYSTEM', 'teaching', '#FF9800', 3),
('550e8400-e29b-41d4-a716-446655440024', 'Spiritual Guidance', 'Spiritual guidance and practices', '550e8400-e29b-41d4-a716-446655440002', 2, 'SYSTEM', 'spiritual', '#FF9800', 4);

-- Knowledge & Skills Subcategories
INSERT INTO legacy_categories (id, name, description, parent_category_id, category_level, category_type, icon, color, sort_order) VALUES
('550e8400-e29b-41d4-a716-446655440031', 'Recipes & Cooking', 'Family recipes and cooking techniques', '550e8400-e29b-41d4-a716-446655440003', 2, 'SYSTEM', 'cooking', '#2196F3', 1),
('550e8400-e29b-41d4-a716-446655440032', 'Crafts & Hobbies', 'Traditional crafts and hobbies', '550e8400-e29b-41d4-a716-446655440003', 2, 'SYSTEM', 'craft', '#2196F3', 2),
('550e8400-e29b-41d4-a716-446655440033', 'Professional Skills', 'Professional knowledge and skills', '550e8400-e29b-41d4-a716-446655440003', 2, 'SYSTEM', 'professional', '#2196F3', 3),
('550e8400-e29b-41d4-a716-446655440034', 'Life Skills', 'Essential life skills and knowledge', '550e8400-e29b-41d4-a716-446655440003', 2, 'SYSTEM', 'life-skills', '#2196F3', 4);

-- Media & Memories Subcategories
INSERT INTO legacy_categories (id, name, description, parent_category_id, category_level, category_type, icon, color, sort_order) VALUES
('550e8400-e29b-41d4-a716-446655440041', 'Photos & Videos', 'Family photos and videos', '550e8400-e29b-41d4-a716-446655440004', 2, 'SYSTEM', 'photo', '#9C27B0', 1),
('550e8400-e29b-41d4-a716-446655440042', 'Audio Recordings', 'Voice messages and recordings', '550e8400-e29b-41d4-a716-446655440004', 2, 'SYSTEM', 'audio', '#9C27B0', 2),
('550e8400-e29b-41d4-a716-446655440043', 'Documents & Certificates', 'Important documents and certificates', '550e8400-e29b-41d4-a716-446655440004', 2, 'SYSTEM', 'document', '#9C27B0', 3),
('550e8400-e29b-41d4-a716-446655440044', 'Digital Artifacts', 'Digital memories and artifacts', '550e8400-e29b-41d4-a716-446655440004', 2, 'SYSTEM', 'digital', '#9C27B0', 4);

-- Future & Legacy Subcategories
INSERT INTO legacy_categories (id, name, description, parent_category_id, category_level, category_type, icon, color, sort_order) VALUES
('550e8400-e29b-41d4-a716-446655440051', 'Wishes & Dreams', 'Future wishes and dreams', '550e8400-e29b-41d4-a716-446655440005', 2, 'SYSTEM', 'wish', '#607D8B', 1),
('550e8400-e29b-41d4-a716-446655440052', 'Advice & Guidance', 'Future advice and guidance', '550e8400-e29b-41d4-a716-446655440005', 2, 'SYSTEM', 'advice', '#607D8B', 2),
('550e8400-e29b-41d4-a716-446655440053', 'Family Values', 'Core family values and principles', '550e8400-e29b-41d4-a716-446655440005', 2, 'SYSTEM', 'values', '#607D8B', 3),
('550e8400-e29b-41d4-a716-446655440054', 'Future Plans', 'Future plans and aspirations', '550e8400-e29b-41d4-a716-446655440005', 2, 'SYSTEM', 'plans', '#607D8B', 4);

-- =============================================================================
-- Comments and Documentation
-- =============================================================================
COMMENT ON TABLE legacy_categories IS 'Hierarchical categories for organizing legacy content';
COMMENT ON TABLE legacy_buckets IS 'User-created collections/buckets for organizing legacy content';
COMMENT ON TABLE legacy_content IS 'Main table for storing legacy content within buckets';
COMMENT ON TABLE legacy_recipients IS 'Targeted delivery system for legacy content to specific recipients';
COMMENT ON TABLE legacy_media_files IS 'Metadata for media files stored in AWS S3';
COMMENT ON TABLE legacy_comments IS 'Comments and discussions on legacy content';
COMMENT ON TABLE legacy_analytics IS 'Analytics and usage tracking for legacy content';

COMMENT ON COLUMN legacy_categories.category_level IS 'Hierarchy level: 1=Root, 2=Sub, 3=Sub-sub';
COMMENT ON COLUMN legacy_buckets.category_id IS 'Must belong to a category for organization';
COMMENT ON COLUMN legacy_content.bucket_id IS 'Content must belong to a bucket';
COMMENT ON COLUMN legacy_recipients.recipient_type IS 'Type: SPECIFIC_USER, GENERATION, RELATIONSHIP';
COMMENT ON COLUMN legacy_recipients.recipient_relationship IS 'Relationship description: "elder son", "both sons", "grandchildren"';
COMMENT ON COLUMN legacy_media_files.s3_url IS 'AWS S3 URL for the media file';
COMMENT ON COLUMN legacy_media_files.processing_status IS 'File processing status: PENDING, PROCESSING, COMPLETED, FAILED';
