ALTER TABLE payments
    ADD COLUMN receipt_number VARCHAR(100);

ALTER TABLE payments
    ADD COLUMN gateway_response_code VARCHAR(20);

ALTER TABLE payments
    ADD COLUMN gateway_message VARCHAR(255);

ALTER TABLE payments
    ADD COLUMN processed_at TIMESTAMP;