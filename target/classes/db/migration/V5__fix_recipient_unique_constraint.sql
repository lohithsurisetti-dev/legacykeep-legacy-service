-- Fix unique constraint for legacy_recipients to support soft delete
-- The current constraint only considers content_id and recipient_id,
-- but we need to allow multiple records with same content_id and recipient_id
-- as long as only one is active (not DELETED)

-- Drop the existing unique constraint
ALTER TABLE legacy_recipients DROP CONSTRAINT IF EXISTS uk_legacy_recipients_content_recipient;

-- Create a partial unique index that only applies to non-deleted records
-- This allows multiple deleted records but only one active record per content-recipient pair
CREATE UNIQUE INDEX uk_legacy_recipients_content_recipient_active 
ON legacy_recipients (content_id, recipient_id) 
WHERE status != 'DELETED';

-- Add comment for documentation
COMMENT ON INDEX uk_legacy_recipients_content_recipient_active IS 'Ensures only one active recipient per content-recipient pair, allows multiple deleted records';
