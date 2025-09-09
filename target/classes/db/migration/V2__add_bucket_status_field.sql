-- Add status field to legacy_buckets table for soft delete support
ALTER TABLE legacy_buckets ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE';

-- Add index for better query performance
CREATE INDEX idx_legacy_buckets_status ON legacy_buckets(status);

-- Add comment for documentation
COMMENT ON COLUMN legacy_buckets.status IS 'Bucket status: ACTIVE, INACTIVE, ARCHIVED, DELETED';
