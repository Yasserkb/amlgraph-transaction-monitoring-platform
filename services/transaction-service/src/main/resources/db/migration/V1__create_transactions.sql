CREATE TABLE transactions.transactions (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    source_account_id UUID NOT NULL,
    destination_account_id UUID NOT NULL,
    amount NUMERIC(18, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    channel VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    origin_country VARCHAR(2),
    destination_country VARCHAR(2),
    reference VARCHAR(255),
    executed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_transactions_customer ON transactions.transactions(customer_id);
CREATE INDEX idx_transactions_executed_at ON transactions.transactions(executed_at);
CREATE INDEX idx_transactions_status ON transactions.transactions(status);
