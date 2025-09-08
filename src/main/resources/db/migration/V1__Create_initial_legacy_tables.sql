-- =============================================================================
-- LegacyKeep Legacy Service - Initial Database Schema
-- Migration: V1__Create_initial_legacy_tables.sql
-- Description: Creates the initial database schema for legacy content management
-- =============================================================================

-- =============================================================================
-- Legacy Content Table
-- =============================================================================
CREATE TABLE legacy_content (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    content_type VARCHAR(50) NOT NULL CHECK (content_type IN ('TEXT', 'AUDIO', 'VIDEO', 'IMAGE', 'DOCUMENT')),
    content_data TEXT, -- For text content
    file_path VARCHAR(500), -- For media files
    file_size BIGINT,
    mime_type VARCHAR(100),
    creator_id BIGINT NOT NULL,
    family_id BIGINT NOT NULL,
    generation_level INTEGER NOT NULL CHECK (generation_level > 0),
    category VARCHAR(100),
    tags TEXT[], -- Array of tags
    privacy_level VARCHAR(50) DEFAULT 'FAMILY' CHECK (privacy_level IN ('PRIVATE', 'FAMILY', 'EXTENDED_FAMILY', 'PUBLIC')),
    status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED', 'DELETED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_legacy_content_creator FOREIGN KEY (creator_id) REFERENCES users(id),
    CONSTRAINT fk_legacy_content_family FOREIGN KEY (family_id) REFERENCES families(id)
);

-- =============================================================================
-- Legacy Inheritance Table
-- =============================================================================
CREATE TABLE legacy_inheritance (
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    inheritance_type VARCHAR(50) NOT NULL CHECK (inheritance_type IN ('DIRECT', 'INDIRECT')),
    inheritance_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED')),
    notes TEXT,
    expires_at TIMESTAMP,
    
    CONSTRAINT fk_legacy_inheritance_content FOREIGN KEY (content_id) REFERENCES legacy_content(id) ON DELETE CASCADE,
    CONSTRAINT fk_legacy_inheritance_from_user FOREIGN KEY (from_user_id) REFERENCES users(id),
    CONSTRAINT fk_legacy_inheritance_to_user FOREIGN KEY (to_user_id) REFERENCES users(id),
    CONSTRAINT unique_inheritance UNIQUE (content_id, to_user_id)
);

-- =============================================================================
-- Legacy Categories Table
-- =============================================================================
CREATE TABLE legacy_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_category_id BIGINT,
    family_id BIGINT,
    is_system_category BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_legacy_categories_parent FOREIGN KEY (parent_category_id) REFERENCES legacy_categories(id),
    CONSTRAINT fk_legacy_categories_family FOREIGN KEY (family_id) REFERENCES families(id),
    CONSTRAINT unique_category_name_per_family UNIQUE (name, family_id)
);

-- =============================================================================
-- Legacy Permissions Table
-- =============================================================================
CREATE TABLE legacy_permissions (
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    permission_type VARCHAR(50) NOT NULL CHECK (permission_type IN ('VIEW', 'ADD', 'EDIT', 'DELETE', 'ADMIN')),
    granted_by BIGINT NOT NULL,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    
    CONSTRAINT fk_legacy_permissions_content FOREIGN KEY (content_id) REFERENCES legacy_content(id) ON DELETE CASCADE,
    CONSTRAINT fk_legacy_permissions_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_legacy_permissions_granted_by FOREIGN KEY (granted_by) REFERENCES users(id),
    CONSTRAINT unique_user_content_permission UNIQUE (content_id, user_id, permission_type)
);

-- =============================================================================
-- Legacy Analytics Table
-- =============================================================================
CREATE TABLE legacy_analytics (
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL CHECK (action_type IN ('VIEW', 'DOWNLOAD', 'SHARE', 'COMMENT', 'LIKE', 'INHERIT')),
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB,
    
    CONSTRAINT fk_legacy_analytics_content FOREIGN KEY (content_id) REFERENCES legacy_content(id) ON DELETE CASCADE,
    CONSTRAINT fk_legacy_analytics_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =============================================================================
-- Legacy Comments Table
-- =============================================================================
CREATE TABLE legacy_comments (
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    comment_text TEXT NOT NULL,
    parent_comment_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'DELETED', 'HIDDEN')),
    
    CONSTRAINT fk_legacy_comments_content FOREIGN KEY (content_id) REFERENCES legacy_content(id) ON DELETE CASCADE,
    CONSTRAINT fk_legacy_comments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_legacy_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES legacy_comments(id)
);

-- =============================================================================
-- Legacy Media Files Table
-- =============================================================================
CREATE TABLE legacy_media_files (
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_hash VARCHAR(64), -- SHA-256 hash for deduplication
    thumbnail_path VARCHAR(500),
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_legacy_media_files_content FOREIGN KEY (content_id) REFERENCES legacy_content(id) ON DELETE CASCADE
);

-- =============================================================================
-- Legacy Content Versions Table
-- =============================================================================
CREATE TABLE legacy_content_versions (
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL,
    version_number INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    content_data TEXT,
    file_path VARCHAR(500),
    changed_by BIGINT NOT NULL,
    change_reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_legacy_content_versions_content FOREIGN KEY (content_id) REFERENCES legacy_content(id) ON DELETE CASCADE,
    CONSTRAINT fk_legacy_content_versions_changed_by FOREIGN KEY (changed_by) REFERENCES users(id),
    CONSTRAINT unique_content_version UNIQUE (content_id, version_number)
);

-- =============================================================================
-- Indexes for Performance
-- =============================================================================

-- Legacy Content Indexes
CREATE INDEX idx_legacy_content_creator ON legacy_content(creator_id);
CREATE INDEX idx_legacy_content_family ON legacy_content(family_id);
CREATE INDEX idx_legacy_content_type ON legacy_content(content_type);
CREATE INDEX idx_legacy_content_category ON legacy_content(category);
CREATE INDEX idx_legacy_content_status ON legacy_content(status);
CREATE INDEX idx_legacy_content_created_at ON legacy_content(created_at);
CREATE INDEX idx_legacy_content_generation ON legacy_content(generation_level);

-- Legacy Inheritance Indexes
CREATE INDEX idx_legacy_inheritance_content ON legacy_inheritance(content_id);
CREATE INDEX idx_legacy_inheritance_from_user ON legacy_inheritance(from_user_id);
CREATE INDEX idx_legacy_inheritance_to_user ON legacy_inheritance(to_user_id);
CREATE INDEX idx_legacy_inheritance_status ON legacy_inheritance(status);
CREATE INDEX idx_legacy_inheritance_date ON legacy_inheritance(inheritance_date);

-- Legacy Permissions Indexes
CREATE INDEX idx_legacy_permissions_content ON legacy_permissions(content_id);
CREATE INDEX idx_legacy_permissions_user ON legacy_permissions(user_id);
CREATE INDEX idx_legacy_permissions_type ON legacy_permissions(permission_type);

-- Legacy Analytics Indexes
CREATE INDEX idx_legacy_analytics_content ON legacy_analytics(content_id);
CREATE INDEX idx_legacy_analytics_user ON legacy_analytics(user_id);
CREATE INDEX idx_legacy_analytics_action ON legacy_analytics(action_type);
CREATE INDEX idx_legacy_analytics_date ON legacy_analytics(action_date);

-- Legacy Comments Indexes
CREATE INDEX idx_legacy_comments_content ON legacy_comments(content_id);
CREATE INDEX idx_legacy_comments_user ON legacy_comments(user_id);
CREATE INDEX idx_legacy_comments_parent ON legacy_comments(parent_comment_id);
CREATE INDEX idx_legacy_comments_created_at ON legacy_comments(created_at);

-- Legacy Media Files Indexes
CREATE INDEX idx_legacy_media_files_content ON legacy_media_files(content_id);
CREATE INDEX idx_legacy_media_files_hash ON legacy_media_files(file_hash);

-- =============================================================================
-- System Categories (Default Categories)
-- =============================================================================
INSERT INTO legacy_categories (name, description, is_system_category) VALUES
('Family Traditions & Rituals', 'Customs, ceremonies, and traditional practices', true),
('Life Lessons & Advice', 'Wisdom, guidance, and life experiences', true),
('Cultural Heritage (Sanatana)', 'Religious practices, spiritual teachings, and cultural values', true),
('Personal Memories', 'Individual stories, experiences, and personal history', true),
('Family Events & Celebrations', 'Festivals, birthdays, anniversaries, and special occasions', true),
('Skills & Knowledge', 'Professional skills, hobbies, and specialized knowledge', true),
('Family History & Genealogy', 'Ancestral information, family tree, and historical records', true),
('Traditional Arts & Crafts', 'Handicrafts, art forms, and creative expressions', true),
('Culinary Heritage', 'Family recipes, cooking techniques, and food traditions', true),
('Spiritual & Religious Content', 'Prayers, mantras, religious practices, and spiritual guidance', true);

-- =============================================================================
-- Comments and Documentation
-- =============================================================================
COMMENT ON TABLE legacy_content IS 'Main table for storing legacy content across generations';
COMMENT ON TABLE legacy_inheritance IS 'Tracks content inheritance from one generation to another';
COMMENT ON TABLE legacy_categories IS 'Categories for organizing legacy content';
COMMENT ON TABLE legacy_permissions IS 'User permissions for legacy content access and modification';
COMMENT ON TABLE legacy_analytics IS 'Analytics and usage tracking for legacy content';
COMMENT ON TABLE legacy_comments IS 'Comments and discussions on legacy content';
COMMENT ON TABLE legacy_media_files IS 'Metadata for media files associated with legacy content';
COMMENT ON TABLE legacy_content_versions IS 'Version history for legacy content changes';

COMMENT ON COLUMN legacy_content.content_type IS 'Type of content: TEXT, AUDIO, VIDEO, IMAGE, DOCUMENT';
COMMENT ON COLUMN legacy_content.generation_level IS 'Generation level: 1=Grandparents, 2=Parents, 3=Children, 4=Grandchildren';
COMMENT ON COLUMN legacy_content.privacy_level IS 'Privacy level: PRIVATE, FAMILY, EXTENDED_FAMILY, PUBLIC';
COMMENT ON COLUMN legacy_inheritance.inheritance_type IS 'Type of inheritance: DIRECT (parent-child), INDIRECT (grandparent-grandchild)';
COMMENT ON COLUMN legacy_permissions.permission_type IS 'Permission level: VIEW, ADD, EDIT, DELETE, ADMIN';
