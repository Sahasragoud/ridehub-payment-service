-- Step 1: Add column allowing NULL temporarily
ALTER TABLE payments
    ADD COLUMN idempotency_key VARCHAR(255);

-- Step 2: Populate existing rows with placeholder values (e.g., using UUIDs)
UPDATE payments
SET idempotency_key = gen_random_uuid()::text
WHERE idempotency_key IS NULL;

-- Step 3: Now enforce NOT NULL and UNIQUE constraints
ALTER TABLE payments
    ALTER COLUMN idempotency_key SET NOT NULL;

ALTER TABLE payments
    ADD CONSTRAINT uk_payments_idempotency_key UNIQUE (idempotency_key);