-- Create inheritance_events table
CREATE TABLE inheritance_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inheritance_rule_id UUID NOT NULL REFERENCES inheritance_rules(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL CHECK (event_type IN (
        'RULE_CREATED', 'RULE_UPDATED', 'RULE_DELETED', 'RULE_ACTIVATED', 'RULE_PAUSED',
        'RELATIONSHIP_ADDED', 'RELATIONSHIP_REMOVED', 'RELATIONSHIP_UPDATED',
        'INHERITANCE_TRIGGERED', 'INHERITANCE_COMPLETED', 'INHERITANCE_FAILED',
        'CONTENT_ACCESSED', 'CONTENT_DECLINED', 'CONTENT_SHARED'
    )),
    event_data JSONB NOT NULL,
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID
);

-- Create indexes for inheritance_events
CREATE INDEX idx_inheritance_events_rule_id ON inheritance_events(inheritance_rule_id);
CREATE INDEX idx_inheritance_events_event_type ON inheritance_events(event_type);
CREATE INDEX idx_inheritance_events_created_at ON inheritance_events(created_at);
CREATE INDEX idx_inheritance_events_created_by ON inheritance_events(created_by);

-- Composite indexes for common queries
CREATE INDEX idx_inheritance_events_rule_type ON inheritance_events(inheritance_rule_id, event_type);
CREATE INDEX idx_inheritance_events_type_date ON inheritance_events(event_type, created_at);

-- Add comments
COMMENT ON TABLE inheritance_events IS 'Audit trail for all inheritance-related events';
COMMENT ON COLUMN inheritance_events.inheritance_rule_id IS 'Reference to the inheritance rule that generated this event';
COMMENT ON COLUMN inheritance_events.event_type IS 'Type of event that occurred';
COMMENT ON COLUMN inheritance_events.event_data IS 'Event-specific data in JSON format';
COMMENT ON COLUMN inheritance_events.created_at IS 'Timestamp when the event occurred';
COMMENT ON COLUMN inheritance_events.created_by IS 'User who triggered the event (if applicable)';
