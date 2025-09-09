-- Update existing content to have ACTIVE status (in case they have NULL values)
UPDATE legacy_content SET status = 'ACTIVE' WHERE status IS NULL;
