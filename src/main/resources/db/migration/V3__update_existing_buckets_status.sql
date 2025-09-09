-- Update existing buckets to have ACTIVE status (in case they have NULL values)
UPDATE legacy_buckets SET status = 'ACTIVE' WHERE status IS NULL;
