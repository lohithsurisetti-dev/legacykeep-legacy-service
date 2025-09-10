-- Create inheritance_status table
CREATE TABLE inheritance_status (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL REFERENCES legacy_content(id) ON DELETE CASCADE,
    recipient_id UUID NOT NULL,
    inheritance_rule_id UUID NOT NULL REFERENCES inheritance_rules(id) ON DELETE CASCADE,
    
    -- Status Tracking
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'INHERITED', 'ACCESSED', 'DECLINED')),
    inherited_at TIMESTAMP,
    accessed_at TIMESTAMP,
    declined_at TIMESTAMP,
    
    -- Relationship Context
    relationship_type_id BIGINT,
    relationship_context JSONB,
    
    -- Metadata
    metadata JSONB,
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    UNIQUE(content_id, recipient_id, inheritance_rule_id)
);

-- Create indexes for inheritance_status
CREATE INDEX idx_inheritance_status_content_id ON inheritance_status(content_id);
CREATE INDEX idx_inheritance_status_recipient_id ON inheritance_status(recipient_id);
CREATE INDEX idx_inheritance_status_rule_id ON inheritance_status(inheritance_rule_id);
CREATE INDEX idx_inheritance_status_status ON inheritance_status(status);
CREATE INDEX idx_inheritance_status_inherited_at ON inheritance_status(inherited_at);
CREATE INDEX idx_inheritance_status_accessed_at ON inheritance_status(accessed_at);
CREATE INDEX idx_inheritance_status_relationship_type_id ON inheritance_status(relationship_type_id);
CREATE INDEX idx_inheritance_status_created_at ON inheritance_status(created_at);

-- Composite indexes for common queries
CREATE INDEX idx_inheritance_status_recipient_status ON inheritance_status(recipient_id, status);
CREATE INDEX idx_inheritance_status_content_status ON inheritance_status(content_id, status);
CREATE INDEX idx_inheritance_status_rule_status ON inheritance_status(inheritance_rule_id, status);

-- Add comments
COMMENT ON TABLE inheritance_status IS 'Tracks inheritance status for each recipient of legacy content';
COMMENT ON COLUMN inheritance_status.content_id IS 'Reference to the legacy content being inherited';
COMMENT ON COLUMN inheritance_status.recipient_id IS 'User who is receiving the inherited content';
COMMENT ON COLUMN inheritance_status.inheritance_rule_id IS 'Reference to the inheritance rule that triggered this status';
COMMENT ON COLUMN inheritance_status.status IS 'Current status of inheritance (PENDING, INHERITED, ACCESSED, DECLINED)';
COMMENT ON COLUMN inheritance_status.inherited_at IS 'Timestamp when content was inherited';
COMMENT ON COLUMN inheritance_status.accessed_at IS 'Timestamp when recipient first accessed the content';
COMMENT ON COLUMN inheritance_status.declined_at IS 'Timestamp when recipient declined the inheritance';
COMMENT ON COLUMN inheritance_status.relationship_type_id IS 'Reference to relationship type (if applicable)';
COMMENT ON COLUMN inheritance_status.relationship_context IS 'Additional relationship context in JSON format';
COMMENT ON COLUMN inheritance_status.metadata IS 'Additional status information in JSON format';
