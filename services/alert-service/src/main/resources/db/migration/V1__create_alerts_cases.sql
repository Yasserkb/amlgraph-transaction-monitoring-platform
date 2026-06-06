CREATE TABLE alerts.alerts (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    rule_id VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    triggered_at TIMESTAMPTZ NOT NULL,
    closed_at TIMESTAMPTZ
);

CREATE TABLE alerts.cases (
    id UUID PRIMARY KEY,
    alert_id UUID NOT NULL,
    assigned_analyst_id UUID,
    status VARCHAR(30) NOT NULL,
    str_required BOOLEAN NOT NULL,
    opened_at TIMESTAMPTZ NOT NULL,
    closed_at TIMESTAMPTZ
);

CREATE TABLE alerts.case_notes (
    id UUID PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES alerts.cases(id),
    author_id UUID NOT NULL,
    content VARCHAR(2000) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_alerts_status ON alerts.alerts(status);
CREATE INDEX idx_alerts_severity ON alerts.alerts(severity);
CREATE INDEX idx_cases_status ON alerts.cases(status);
