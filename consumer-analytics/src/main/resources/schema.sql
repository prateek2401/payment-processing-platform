CREATE TABLE IF NOT EXISTS analytics (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    currency VARCHAR(10) NOT NULL,
    total_transactions INT NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    UNIQUE (date, currency)
);
