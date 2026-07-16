CREATE TABLE payments (

                          id BIGSERIAL PRIMARY KEY,

                          ride_id BIGINT NOT NULL,

                          payer_id BIGINT NOT NULL,

                          amount DECIMAL(10,2) NOT NULL,

                          currency VARCHAR(20) NOT NULL,

                          payment_method VARCHAR(30) NOT NULL,

                          status VARCHAR(30) NOT NULL,

                          transaction_id VARCHAR(255) UNIQUE,

                          gateway VARCHAR(100),

                          gateway_order_id VARCHAR(255),

                          gateway_payment_id VARCHAR(255),

                          failure_reason TEXT,

                          created_at TIMESTAMP NOT NULL,

                          updated_at TIMESTAMP

);