CREATE TABLE customers.customers (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    nationality VARCHAR(2) NOT NULL,
    country_of_residence VARCHAR(2) NOT NULL,
    risk_score INT NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    kyc_status VARCHAR(20) NOT NULL,
    pep BOOLEAN NOT NULL,
    sanctioned BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

INSERT INTO customers.customers (
    id, full_name, nationality, country_of_residence, risk_score, risk_level, kyc_status, pep, sanctioned, created_at, updated_at
) VALUES
('11111111-1111-1111-1111-111111111111', 'Sofia Martin', 'FR', 'FR', 35, 'MEDIUM', 'VERIFIED', false, false, now(), now()),
('22222222-2222-2222-2222-222222222222', 'Karim El Mansouri', 'MA', 'MA', 62, 'HIGH', 'VERIFIED', true, false, now(), now()),
('33333333-3333-3333-3333-333333333333', 'Global Import SARL', 'MA', 'AE', 80, 'CRITICAL', 'VERIFIED', false, false, now(), now())
ON CONFLICT (id) DO NOTHING;
