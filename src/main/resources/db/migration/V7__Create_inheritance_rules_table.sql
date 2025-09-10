-- Create inheritance_rules table
CREATE TABLE inheritance_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL REFERENCES legacy_content(id) ON DELETE CASCADE,
    creator_id UUID NOT NULL,
    
    -- Targeting Configuration
    target_type VARCHAR(50) NOT NULL CHECK (target_type IN ('RELATIONSHIP_TYPE', 'GENERATION', 'CONTEXT', 'CUSTOM')),
    target_value VARCHAR(255) NOT NULL,
    target_metadata JSONB,
    
    -- Inheritance Configuration
    inheritance_trigger VARCHAR(50) NOT NULL CHECK (inheritance_trigger IN ('IMMEDIATE', 'EVENT_BASED', 'TIME_BASED', 'MANUAL')),
    trigger_metadata JSONB,
    
    -- Status and Lifecycle
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'PAUSED', 'COMPLETED', 'CANCELLED')),
    priority INTEGER DEFAULT 0,
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_by UUID NOT NULL
);

-- Create indexes for inheritance_rules
CREATE INDEX idx_inheritance_rules_content_id ON inheritance_rules(content_id);
CREATE INDEX idx_inheritance_rules_creator_id ON inheritance_rules(creator_id);
CREATE INDEX idx_inheritance_rules_target_type ON inheritance_rules(target_type);
CREATE INDEX idx_inheritance_rules_target_value ON inheritance_rules(target_value);
CREATE INDEX idx_inheritance_rules_trigger ON inheritance_rules(inheritance_trigger);
CREATE INDEX idx_inheritance_rules_status ON inheritance_rules(status);
CREATE INDEX idx_inheritance_rules_priority ON inheritance_rules(priority);
CREATE INDEX idx_inheritance_rules_created_at ON inheritance_rules(created_at);

-- Composite indexes for common queries
CREATE INDEX idx_inheritance_rules_content_status ON inheritance_rules(content_id, status);
CREATE INDEX idx_inheritance_rules_creator_status ON inheritance_rules(creator_id, status);
CREATE INDEX idx_inheritance_rules_target_type_value ON inheritance_rules(target_type, target_value);

-- Add comments
COMMENT ON TABLE inheritance_rules IS 'Defines inheritance rules for legacy content distribution';
COMMENT ON COLUMN inheritance_rules.content_id IS 'Reference to the legacy content being inherited';
COMMENT ON COLUMN inheritance_rules.creator_id IS 'User who created the inheritance rule';
COMMENT ON COLUMN inheritance_rules.target_type IS 'Type of targeting (RELATIONSHIP_TYPE, GENERATION, CONTEXT, CUSTOM)';
COMMENT ON COLUMN inheritance_rules.target_value IS 'Specific target value (e.g., "Son", "CHILDREN", "FAMILY")';
COMMENT ON COLUMN inheritance_rules.target_metadata IS 'Additional targeting criteria in JSON format';
COMMENT ON COLUMN inheritance_rules.inheritance_trigger IS 'When inheritance should occur (IMMEDIATE, EVENT_BASED, TIME_BASED, MANUAL)';
COMMENT ON COLUMN inheritance_rules.trigger_metadata IS 'Trigger-specific configuration in JSON format';
COMMENT ON COLUMN inheritance_rules.status IS 'Current status of the inheritance rule';
COMMENT ON COLUMN inheritance_rules.priority IS 'Priority level for rule execution (higher = more priority)';
