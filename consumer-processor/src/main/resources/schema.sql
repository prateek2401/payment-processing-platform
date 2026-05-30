CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    payment_id VARCHAR(255) NOT NULL UNIQUE,
    user_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    partition INT NOT NULL,
    offset BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP NOT NULL
);
