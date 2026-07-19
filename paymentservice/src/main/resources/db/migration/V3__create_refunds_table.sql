CREATE TABLE refunds
(
    id BIGSERIAL PRIMARY KEY,

    payment_id BIGINT NOT NULL,

    ride_id BIGINT NOT NULL,

    amount NUMERIC(10,2) NOT NULL,

    reason VARCHAR(255),

    status VARCHAR(30) NOT NULL,

    refund_transaction_id VARCHAR(100) UNIQUE,

    processed_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP
);