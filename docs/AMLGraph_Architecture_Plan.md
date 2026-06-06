# AMLGraph — Full Architectural & Technical Plan
### Transaction Monitoring & Fraud Detection Platform

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Goals & Scope](#2-goals--scope)
3. [System Architecture](#3-system-architecture)
4. [Domain Model](#4-domain-model)
5. [Microservices Breakdown](#5-microservices-breakdown)
6. [Data Architecture](#6-data-architecture)
7. [Event-Driven Architecture (Kafka)](#7-event-driven-architecture-kafka)
8. [Security Architecture](#8-security-architecture)
9. [API Design](#9-api-design)
10. [Frontend Architecture](#10-frontend-architecture)
11. [Observability & Monitoring](#11-observability--monitoring)
12. [Infrastructure & DevOps](#12-infrastructure--devops)
13. [AI / RAG Layer](#13-ai--rag-layer)
14. [Project Structure](#14-project-structure)
15. [Tech Stack Summary](#15-tech-stack-summary)
16. [Recruiter Positioning](#16-recruiter-positioning)

---

## 1. Project Overview

**AMLGraph** is a cloud-native, event-driven Anti-Money Laundering (AML) and fraud detection platform. It simulates the transaction monitoring systems used by real banks and fintech companies to detect suspicious financial activity, generate alerts, and support analyst investigation workflows.

The platform is not a toy project. It mirrors the architecture of production AML systems used in banking — with real regulatory framing (FATF, 6AMLD, ACPR), real patterns (structuring, smurfing, layering), and real engineering (Kafka, graph relationships, rule engine, audit trail, RBAC).

**One-liner for your README and LinkedIn:**

> *AMLGraph is a cloud-native AML transaction monitoring platform built on Spring Boot microservices, Kafka event streaming, PostgreSQL, and Neo4j graph analysis — with an integrated AI investigation assistant powered by RAG.*

---

## 2. Goals & Scope

### What it does

- Ingests synthetic bank transactions in real time via Kafka
- Runs a configurable rule engine to detect suspicious patterns
- Scores each customer by risk level (Low / Medium / High / Critical)
- Generates AML alerts and opens investigation cases
- Supports analyst review workflows (assign, comment, escalate, close)
- Produces audit logs for every action (immutable, timestamped)
- Exposes a dashboard for compliance officers and analysts
- Provides an AI assistant to explain suspicious behavior in plain language

### What it does NOT do

- It does not connect to real banking systems
- It does not process real money or real customer data
- It does not replace a certified AML compliance solution

### Regulatory framing (for README and interviews)

| Reference | What it influences in the project |
|---|---|
| FATF Recommendations | Risk-based approach, customer risk scoring |
| EU 6th AML Directive (6AMLD) | Criminal offense definitions, 22 predicate offenses |
| ACPR (France) | Alert escalation, STR (Suspicious Transaction Report) logic |
| GDPR | Data minimization in audit logs, anonymization of synthetic data |

---

## 3. System Architecture

### High-Level Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        AMLGraph Platform                            │
│                                                                     │
│  ┌──────────────┐    ┌──────────────────────────────────────────┐  │
│  │  React UI    │───▶│           API Gateway (Spring Cloud)     │  │
│  │  (Analyst    │    │       /auth  /transactions  /alerts      │  │
│  │   Dashboard) │    │       /cases  /customers  /reports       │  │
│  └──────────────┘    └──────────┬───────────────────────────────┘  │
│                                 │                                   │
│          ┌──────────────────────┼──────────────────────────┐       │
│          │                      │                          │       │
│          ▼                      ▼                          ▼       │
│  ┌──────────────┐  ┌──────────────────────┐  ┌─────────────────┐  │
│  │  Transaction │  │   Alert & Case       │  │  Customer Risk  │  │
│  │  Ingestion   │  │   Management         │  │  Service        │  │
│  │  Service     │  │   Service            │  │                 │  │
│  └──────┬───────┘  └──────────────────────┘  └─────────────────┘  │
│         │                                                           │
│         ▼                                                           │
│  ┌─────────────────────────────────────────┐                       │
│  │              Apache Kafka               │                       │
│  │  transactions.raw  |  alerts.created    │                       │
│  │  cases.updated     |  audit.events      │                       │
│  └──────┬──────────────────────────────────┘                       │
│         │                                                           │
│         ▼                                                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐ │
│  │  Rule Engine │  │  Graph       │  │  AI Investigation        │ │
│  │  Service     │  │  Analysis    │  │  Assistant (RAG)         │ │
│  │  (Drools)    │  │  Service     │  │  (Python FastAPI)        │ │
│  └──────────────┘  │  (Neo4j)     │  └──────────────────────────┘ │
│                    └──────────────┘                                 │
│                                                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐ │
│  │  PostgreSQL  │  │    Neo4j     │  │  Redis                   │ │
│  │  (main DB)   │  │  (graph DB)  │  │  (cache + sessions)      │ │
│  └──────────────┘  └──────────────┘  └──────────────────────────┘ │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Observability: Prometheus + Grafana + OpenTelemetry         │  │
│  └──────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
```

### Architecture Style

- **Microservices** — each service has its own codebase, database schema, and Kafka topics
- **Event-driven** — services communicate asynchronously through Kafka; no direct service-to-service REST calls for core flows
- **CQRS-lite** — write path goes through Kafka; read path queries PostgreSQL directly via service APIs
- **API Gateway** — single entry point for the frontend; handles routing, authentication, and rate limiting

---

## 4. Domain Model

### Core Entities

```
Customer
├── id (UUID)
├── fullName
├── dateOfBirth
├── nationality
├── countryOfResidence
├── riskScore (0-100)
├── riskLevel (LOW / MEDIUM / HIGH / CRITICAL)
├── kycStatus (PENDING / VERIFIED / REJECTED / EXPIRED)
├── pep (boolean) — Politically Exposed Person
├── sanctioned (boolean)
├── createdAt
└── updatedAt

Account
├── id (UUID)
├── customerId (FK)
├── iban
├── currency
├── accountType (CURRENT / SAVINGS / BUSINESS)
├── status (ACTIVE / FROZEN / CLOSED)
├── openedAt
└── balance

Transaction
├── id (UUID)
├── sourceAccountId (FK)
├── destinationAccountId (FK)
├── amount
├── currency
├── transactionType (TRANSFER / WITHDRAWAL / DEPOSIT / PAYMENT)
├── channel (ONLINE / BRANCH / ATM / SWIFT)
├── status (PENDING / COMPLETED / REJECTED / FLAGGED)
├── originCountry
├── destinationCountry
├── reference
├── executedAt
└── metadata (JSONB)

Alert
├── id (UUID)
├── transactionId (FK, nullable)
├── customerId (FK)
├── ruleId (FK)
├── severity (LOW / MEDIUM / HIGH / CRITICAL)
├── status (OPEN / UNDER_REVIEW / ESCALATED / CLOSED / FALSE_POSITIVE)
├── description
├── triggeredAt
└── closedAt

Case
├── id (UUID)
├── alertId (FK)
├── assignedAnalystId (FK)
├── status (NEW / IN_PROGRESS / ESCALATED / SUBMITTED / CLOSED)
├── notes (List)
├── strRequired (boolean) — Suspicious Transaction Report
├── openedAt
└── closedAt

AuditEvent
├── id (UUID)
├── entityType
├── entityId
├── action
├── performedBy
├── previousState (JSONB)
├── newState (JSONB)
└── timestamp

Rule
├── id (UUID)
├── name
├── description
├── category (STRUCTURING / SMURFING / LAYERING / RAPID_MOVEMENT / HIGH_RISK_COUNTRY / ...)
├── enabled (boolean)
├── severity (LOW / MEDIUM / HIGH / CRITICAL)
├── parameters (JSONB)
└── version
```

### Graph Model (Neo4j)

```
Nodes:
  (:Customer {id, name, riskLevel})
  (:Account {id, iban, currency})
  (:Country {code, name, riskLevel})

Relationships:
  (:Customer)-[:OWNS]->(:Account)
  (:Account)-[:SENT {amount, currency, at}]->(:Account)
  (:Account)-[:LOCATED_IN]->(:Country)
  (:Customer)-[:LINKED_TO]->(:Customer)   // shared phone, address, device
```

Graph queries enable:
- Detecting money mule networks (circular fund flows)
- Identifying account clusters sharing the same customer attributes
- Mapping fund layering across multiple hops

---

## 5. Microservices Breakdown

### 5.1 API Gateway (`amlgraph-gateway`)

**Tech:** Spring Cloud Gateway

**Responsibilities:**
- Route requests to downstream services
- JWT validation (delegated to Keycloak)
- Rate limiting per user/IP (Redis-backed)
- Request/response logging for audit
- Swagger aggregation (Springdoc)

**Routes:**
```
/api/auth/**         → auth-service
/api/transactions/** → transaction-service
/api/alerts/**       → alert-service
/api/cases/**        → case-service
/api/customers/**    → customer-service
/api/reports/**      → reporting-service
/api/ai/**           → ai-service (Python FastAPI)
```

---

### 5.2 Transaction Ingestion Service (`transaction-service`)

**Tech:** Spring Boot 3, Java 21, PostgreSQL, Kafka Producer

**Responsibilities:**
- Receive transaction events (REST or Kafka consumer for simulator)
- Validate transaction schema
- Persist to PostgreSQL
- Publish `transactions.raw` Kafka topic
- Expose REST endpoints for transaction history, search, and detail

**Key endpoints:**
```
POST   /api/transactions                  Submit new transaction
GET    /api/transactions/{id}             Get transaction detail
GET    /api/transactions?customerId=&from=&to=&status=   Search
GET    /api/transactions/{id}/graph       Get related graph data
```

**Kafka events published:**
```
Topic: transactions.raw
Key: transactionId
Payload: TransactionCreatedEvent { id, sourceAccountId, destinationAccountId,
          amount, currency, originCountry, destinationCountry, executedAt, metadata }
```

---

### 5.3 Rule Engine Service (`rule-engine-service`)

**Tech:** Spring Boot 3, Drools 8 (or custom rule engine), Kafka Consumer + Producer

**Responsibilities:**
- Consume `transactions.raw`
- Apply AML detection rules
- Score each transaction
- Publish `alerts.created` when a rule fires

**Detection rules implemented:**

| Rule ID | Name | Description |
|---|---|---|
| R001 | Large Cash Transaction | Single transaction above €10,000 |
| R002 | Structuring / Smurfing | Multiple transactions just below reporting threshold within 24h |
| R003 | Rapid Fund Movement | Funds in and out of same account within 2 hours |
| R004 | High-Risk Country | Transaction originating from FATF high-risk country |
| R005 | Dormant Account Activation | Inactive account suddenly transacting high volume |
| R006 | Round-Trip Transaction | Funds return to origin account via 2+ hops |
| R007 | PEP Transaction | Transaction involving a politically exposed person |
| R008 | Sanctioned Entity | Transaction involving sanctioned customer or country |
| R009 | Velocity Spike | Transaction volume 5x above 30-day average |
| R010 | Unusual Hour | High-value transaction at 2-5 AM |

**Rule parameters are configurable** (stored in PostgreSQL, loaded at startup, hot-reloadable via Actuator).

**Kafka events consumed:**
```
Topic: transactions.raw
```

**Kafka events published:**
```
Topic: alerts.created
Payload: AlertCreatedEvent { alertId, transactionId, customerId, ruleId,
          severity, description, triggeredAt }
```

---

### 5.4 Alert & Case Management Service (`alert-service`)

**Tech:** Spring Boot 3, PostgreSQL, Kafka Consumer + Producer

**Responsibilities:**
- Consume `alerts.created`
- Persist alerts
- Auto-create investigation cases for HIGH and CRITICAL alerts
- Manage case lifecycle (assign, escalate, close)
- Send notifications (email stub / in-app)
- Expose REST endpoints for alert and case management

**Key endpoints:**
```
GET    /api/alerts                         List alerts (with filters)
GET    /api/alerts/{id}                    Get alert detail
PUT    /api/alerts/{id}/status             Update alert status
GET    /api/cases                          List cases
GET    /api/cases/{id}                     Get case detail
POST   /api/cases/{id}/notes               Add analyst note
PUT    /api/cases/{id}/assign              Assign to analyst
PUT    /api/cases/{id}/escalate            Escalate case
PUT    /api/cases/{id}/close               Close case
POST   /api/cases/{id}/str                 Generate STR report
```

**Case state machine:**
```
NEW → IN_PROGRESS → ESCALATED → SUBMITTED (STR) → CLOSED
NEW → IN_PROGRESS → CLOSED (false positive)
```

---

### 5.5 Customer Risk Service (`customer-service`)

**Tech:** Spring Boot 3, PostgreSQL, Kafka Consumer

**Responsibilities:**
- Manage customer profiles and accounts
- Maintain risk scores (recalculated on each new alert)
- Expose customer search and detail endpoints
- Consume alert events to update risk scores

**Risk scoring formula (simplified):**
```
riskScore = base_country_risk
          + pep_flag * 30
          + sanctioned_flag * 50
          + open_alerts_count * 5
          + (HIGH alerts * 10)
          + (CRITICAL alerts * 20)
          - closed_false_positive * 3

riskLevel:
  0-25   → LOW
  26-50  → MEDIUM
  51-75  → HIGH
  76-100 → CRITICAL
```

---

### 5.6 Graph Analysis Service (`graph-service`)

**Tech:** Spring Boot 3, Neo4j Driver, Kafka Consumer

**Responsibilities:**
- Consume `transactions.raw` and sync to Neo4j
- Execute graph queries for fund flow analysis
- Expose endpoints for relationship visualization
- Detect circular transactions and linked account networks

**Key Cypher queries:**

```cypher
-- Find accounts that received funds from a given account (1 hop)
MATCH (a:Account {id: $accountId})-[t:SENT]->(b:Account)
RETURN b, t

-- Find 2-hop fund flow (layering detection)
MATCH path = (a:Account {id: $accountId})-[:SENT*2]->(c:Account)
RETURN path

-- Find round-trip transactions
MATCH path = (a:Account)-[:SENT*2..5]->(a)
RETURN path, length(path) as hops
ORDER BY hops ASC

-- Find accounts linked to same customer attributes
MATCH (c1:Customer)-[:OWNS]->(a1:Account)
MATCH (c2:Customer)-[:OWNS]->(a2:Account)
WHERE c1 <> c2 AND (c1.phone = c2.phone OR c1.address = c2.address)
RETURN c1, c2
```

---

### 5.7 Reporting Service (`reporting-service`)

**Tech:** Spring Boot 3, PostgreSQL (read-only replica), iText or JasperReports

**Responsibilities:**
- Generate PDF reports for cases and STR submissions
- Provide dashboard aggregation data (stats, KPIs)
- Export alerts and cases to CSV

**Dashboard KPIs:**
```
- Total alerts today / this week / this month
- Alert breakdown by severity
- Alert breakdown by rule
- Average case resolution time
- Open cases by analyst
- Customer risk distribution
- Top 10 highest-risk customers
- Transaction volume heatmap by hour
```

---

### 5.8 AI Investigation Assistant (`ai-service`)

**Tech:** Python 3.12, FastAPI, LangChain, pgvector (PostgreSQL extension)

**Responsibilities:**
- Accept a case or alert ID
- Retrieve relevant context (transaction data, rule metadata, customer profile, past similar cases)
- Generate a plain-language explanation of why the alert was triggered
- Suggest investigation actions
- Answer analyst questions in natural language

**RAG pipeline:**
```
1. Analyst asks: "Why was this alert triggered and is it suspicious?"
2. ai-service fetches: transaction data, customer profile, rule definition, past similar cases
3. Chunks are embedded and stored in pgvector
4. Semantic search finds most relevant context
5. LLM (Claude claude-sonnet-4-20250514 via API) generates explanation
6. Response returned to analyst dashboard
```

**Key endpoints:**
```
POST /api/ai/explain        { caseId } → plain-language explanation
POST /api/ai/chat           { caseId, message } → analyst Q&A
POST /api/ai/suggest        { caseId } → suggested next investigation steps
```

---

### 5.9 Data Simulator (`simulator-service`)

**Tech:** Spring Boot 3 or Python script

**Purpose:** Generate realistic synthetic transaction data for demo and testing.

**Generates:**
- Normal transaction patterns (salary, rent, utilities, shopping)
- Suspicious patterns (structuring, rapid movement, high-risk country flows)
- Customer profiles (normal, PEP, high-risk)
- Configurable injection rate (transactions per second)

**Modes:**
```
NORMAL     — realistic everyday transactions
ATTACK     — inject suspicious patterns (structuring, mule network, etc.)
MIXED      — 95% normal + 5% suspicious (realistic ratio)
REPLAY     — replay a pre-defined scenario file
```

---

## 6. Data Architecture

### PostgreSQL Schema (per service, separate schema)

```sql
-- Schema: transactions
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_account_id UUID NOT NULL,
    destination_account_id UUID NOT NULL,
    amount NUMERIC(18, 2) NOT NULL,
    currency CHAR(3) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    channel VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    origin_country CHAR(2),
    destination_country CHAR(2),
    reference VARCHAR(255),
    executed_at TIMESTAMPTZ NOT NULL,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transactions_source ON transactions(source_account_id);
CREATE INDEX idx_transactions_destination ON transactions(destination_account_id);
CREATE INDEX idx_transactions_executed_at ON transactions(executed_at);
CREATE INDEX idx_transactions_status ON transactions(status);

-- Schema: alerts
CREATE TABLE alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id UUID,
    customer_id UUID NOT NULL,
    rule_id UUID NOT NULL,
    severity VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    description TEXT,
    triggered_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    closed_at TIMESTAMPTZ
);

-- Schema: cases
CREATE TABLE cases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    alert_id UUID NOT NULL REFERENCES alerts(id),
    assigned_analyst_id UUID,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    str_required BOOLEAN DEFAULT FALSE,
    opened_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    closed_at TIMESTAMPTZ
);

CREATE TABLE case_notes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id UUID NOT NULL REFERENCES cases(id),
    author_id UUID NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Schema: audit
CREATE TABLE audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    performed_by UUID NOT NULL,
    previous_state JSONB,
    new_state JSONB,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Audit table is append-only (no UPDATE, no DELETE — enforced at application level)
```

### Data Flow

```
Simulator → Kafka (transactions.raw)
         → transaction-service (persist + index)
         → rule-engine-service (detect + score)
         → Kafka (alerts.created)
         → alert-service (persist alert + create case)
         → Kafka (cases.updated)
         → customer-service (update risk score)
         → graph-service (update Neo4j)
         → reporting-service (update dashboard stats)
```

---

## 7. Event-Driven Architecture (Kafka)

### Topics

| Topic | Producer | Consumers | Description |
|---|---|---|---|
| `transactions.raw` | transaction-service | rule-engine-service, graph-service | New transaction ingested |
| `alerts.created` | rule-engine-service | alert-service, customer-service | Rule fired, alert created |
| `cases.updated` | alert-service | notification-service, reporting-service | Case state changed |
| `customers.risk_updated` | customer-service | alert-service, reporting-service | Risk score changed |
| `audit.events` | all services | audit-service | Every state-changing action |

### Kafka Configuration

```yaml
# Per topic
transactions.raw:
  partitions: 6
  replication-factor: 1   # 1 for dev, 3 for prod
  retention.ms: 604800000  # 7 days
  cleanup.policy: delete

audit.events:
  partitions: 3
  replication-factor: 1
  retention.ms: 31536000000  # 1 year (audit retention)
  cleanup.policy: delete
```

### Event Schema (Avro-style, serialized as JSON for simplicity)

```json
// TransactionCreatedEvent
{
  "eventType": "TRANSACTION_CREATED",
  "eventId": "uuid",
  "occurredAt": "ISO-8601",
  "payload": {
    "transactionId": "uuid",
    "sourceAccountId": "uuid",
    "destinationAccountId": "uuid",
    "amount": 15000.00,
    "currency": "EUR",
    "originCountry": "MA",
    "destinationCountry": "AE",
    "executedAt": "ISO-8601"
  }
}

// AlertCreatedEvent
{
  "eventType": "ALERT_CREATED",
  "eventId": "uuid",
  "occurredAt": "ISO-8601",
  "payload": {
    "alertId": "uuid",
    "transactionId": "uuid",
    "customerId": "uuid",
    "ruleId": "uuid",
    "severity": "HIGH",
    "description": "Transaction of €15,000 to UAE from dormant account"
  }
}
```

---

## 8. Security Architecture

### Authentication & Authorization

**Identity Provider:** Keycloak

**Realms:**
```
amlgraph-realm
  └── Clients: amlgraph-frontend, amlgraph-gateway
  └── Roles:
       COMPLIANCE_OFFICER  → read everything, manage rules
       ANALYST             → read alerts, manage own cases
       ADMIN               → full access
       READONLY            → dashboard and reporting only
```

**JWT Flow:**
```
User → Keycloak (login) → JWT token
     → Frontend stores token
     → All API requests include Authorization: Bearer <token>
     → Gateway validates token with Keycloak JWKS
     → Downstream services receive user claims in header
```

### Security Controls

| Control | Implementation |
|---|---|
| Authentication | Keycloak OAuth2 / OIDC |
| Authorization | Role-based (Spring Security + @PreAuthorize) |
| Transport | HTTPS (TLS 1.3) — self-signed in dev, Let's Encrypt in prod |
| API rate limiting | Spring Cloud Gateway + Redis |
| Audit trail | Every write action produces an AuditEvent |
| Data masking | IBAN and customer PII masked in logs |
| Secret management | Docker secrets in dev, AWS Secrets Manager stub in prod |

### Audit Trail Design

Every service that mutates state publishes an `AuditEvent` to Kafka before returning the response:

```java
// Example in case-service
public Case updateCaseStatus(UUID caseId, CaseStatus newStatus, UUID analystId) {
    Case existing = caseRepository.findById(caseId).orElseThrow();
    Case updated = caseRepository.save(existing.withStatus(newStatus));
    
    auditPublisher.publish(AuditEvent.builder()
        .entityType("CASE")
        .entityId(caseId)
        .action("STATUS_UPDATED")
        .performedBy(analystId)
        .previousState(toJson(existing))
        .newState(toJson(updated))
        .build());
    
    return updated;
}
```

The audit table is **append-only** — no service is allowed to UPDATE or DELETE from it.

---

## 9. API Design

### Design Principles

- REST with OpenAPI 3.0 spec (Springdoc auto-generation)
- Resource-based URLs (nouns, not verbs)
- Consistent error responses
- Pagination on all list endpoints
- ISO 8601 dates everywhere
- UUID as all resource identifiers

### Standard Response Envelope

```json
// Success (list)
{
  "data": [...],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 142,
    "totalPages": 8
  }
}

// Success (single)
{
  "data": { ... }
}

// Error
{
  "error": {
    "code": "ALERT_NOT_FOUND",
    "message": "Alert with id '...' not found",
    "timestamp": "2025-01-15T10:30:00Z",
    "traceId": "abc123"
  }
}
```

### Key API Contracts

```
Transaction Service
  GET  /api/transactions?page=0&size=20&customerId=&status=&from=&to=
  GET  /api/transactions/{id}
  POST /api/transactions
  GET  /api/transactions/{id}/related   → related transactions (graph)

Alert Service
  GET  /api/alerts?severity=&status=&from=&to=&page=
  GET  /api/alerts/{id}
  PUT  /api/alerts/{id}                 { status, comment }

Case Service
  GET  /api/cases?status=&assignedTo=&page=
  GET  /api/cases/{id}
  POST /api/cases/{id}/notes            { content }
  PUT  /api/cases/{id}/assign           { analystId }
  PUT  /api/cases/{id}/status           { status }
  POST /api/cases/{id}/str              → generates PDF STR report

Customer Service
  GET  /api/customers?riskLevel=&page=
  GET  /api/customers/{id}
  GET  /api/customers/{id}/accounts
  GET  /api/customers/{id}/transactions
  GET  /api/customers/{id}/alerts
  GET  /api/customers/{id}/risk-history

Dashboard Service (Reporting)
  GET  /api/dashboard/stats             → KPI summary
  GET  /api/dashboard/alerts/by-rule    → alert count per rule
  GET  /api/dashboard/alerts/by-hour    → heatmap data
  GET  /api/reports/cases/{id}/pdf      → PDF case report
```

---

## 10. Frontend Architecture

### Stack

```
React 18 + TypeScript
Vite (build tool)
React Router v6 (navigation)
TanStack Query (server state, caching, pagination)
Zustand (client state — auth, filters)
MUI v5 (component library)
Recharts (charts and graphs)
React Flow (graph/network visualization for Neo4j data)
Axios (HTTP client with interceptors)
```

### Pages & Screens

```
/login                      Keycloak-backed login page

/dashboard                  KPI cards, alert volume chart, risk distribution
/transactions               Transaction list with filters + search
/transactions/:id           Transaction detail + related graph view
/alerts                     Alert list (filterable by severity, status, rule)
/alerts/:id                 Alert detail + linked case
/cases                      Case queue (assigned to me / all open / escalated)
/cases/:id                  Case workspace (notes, timeline, analyst actions)
/customers                  Customer list with risk level filter
/customers/:id              Customer profile (accounts, transactions, risk history)
/rules                      AML rule configuration (admin only)
/reports                    Report generation and export
/ai-assistant               AI investigation assistant chat interface
```

### Component Architecture

```
src/
├── api/                    Axios instances + typed API functions per service
├── components/
│   ├── alerts/             AlertCard, AlertBadge, AlertFilters
│   ├── cases/              CaseTimeline, NoteEditor, CaseStatus
│   ├── charts/             AlertHeatmap, RiskDonut, VolumeChart
│   ├── graph/              TransactionGraph (React Flow)
│   ├── layout/             AppShell, Sidebar, TopBar
│   └── shared/             DataTable, StatusChip, ConfirmDialog
├── hooks/                  useAlerts, useCases, useCustomer, useAI
├── pages/                  One file per route
├── store/                  Zustand slices (auth, ui)
└── types/                  TypeScript interfaces matching API contracts
```

### Graph Visualization

The transaction relationship graph (React Flow) shows:

```
Nodes: Customer (circle), Account (rectangle), Country (diamond)
Edges: SENT (with amount label), OWNS, LOCATED_IN
Colors:
  GREEN    LOW risk
  YELLOW   MEDIUM risk
  ORANGE   HIGH risk
  RED      CRITICAL risk / sanctioned
```

Clicking a node opens a detail panel. Suspicious paths are highlighted in red.

---

## 11. Observability & Monitoring

### Stack

```
Prometheus      → metrics scraping (all Spring Boot services expose /actuator/prometheus)
Grafana         → dashboards and alerting
OpenTelemetry   → distributed tracing (traces across services via Kafka headers)
Loki            → log aggregation (optional, can use ELK instead)
```

### Key Metrics

```
amlgraph_transactions_ingested_total          Counter
amlgraph_alerts_created_total{severity}       Counter
amlgraph_rule_executions_total{ruleId}        Counter
amlgraph_case_resolution_seconds              Histogram
amlgraph_kafka_consumer_lag{topic}            Gauge
amlgraph_ai_request_duration_seconds          Histogram
```

### Grafana Dashboards

1. **Platform Overview** — transactions/sec, alert rate, error rate, service health
2. **Rule Engine** — rules fired per minute, top triggered rules, false positive rate
3. **Case Management** — open cases, avg resolution time, cases by analyst
4. **Infrastructure** — Kafka lag, DB connections, JVM heap, CPU/memory per service

### Distributed Tracing

Each Kafka message carries a `traceId` header injected by the producer. Consumers extract and propagate it. This allows tracing a single transaction from ingestion → rule engine → alert → case creation across all services in Grafana Tempo or Jaeger.

---

## 12. Infrastructure & DevOps

### Local Development (Docker Compose)

```yaml
# docker-compose.yml services:
services:
  postgres:       image: postgres:16-alpine, port 5432
  neo4j:          image: neo4j:5, ports 7474 (browser), 7687 (bolt)
  redis:          image: redis:7-alpine, port 6379
  kafka:          image: confluentinc/cp-kafka:7.6, port 9092
  zookeeper:      image: confluentinc/cp-zookeeper:7.6
  keycloak:       image: quay.io/keycloak/keycloak:24, port 8080
  prometheus:     image: prom/prometheus, port 9090
  grafana:        image: grafana/grafana, port 3000

  # Services
  gateway:              port 8000
  transaction-service:  port 8001
  rule-engine-service:  port 8002
  alert-service:        port 8003
  customer-service:     port 8004
  graph-service:        port 8005
  reporting-service:    port 8006
  ai-service:           port 8007
  simulator-service:    port 8008

  # Frontend
  frontend:             port 3001
```

### Kubernetes Manifests (production-ready structure)

```
k8s/
├── namespace.yaml
├── configmaps/
│   └── services-config.yaml
├── secrets/                    (placeholders — real secrets via Secrets Manager)
├── deployments/
│   ├── gateway-deployment.yaml
│   ├── transaction-service-deployment.yaml
│   └── ... (one per service)
├── services/
│   └── ... (ClusterIP for internal, LoadBalancer for gateway)
├── ingress/
│   └── ingress.yaml            (NGINX ingress controller)
└── hpa/
    └── ... (HorizontalPodAutoscaler per service)
```

### CI/CD Pipeline (GitHub Actions)

```yaml
# .github/workflows/ci.yml
Trigger: push to main, PR to main

Jobs:
  test:
    - Unit tests (JUnit 5 + Mockito)
    - Integration tests (Testcontainers — Postgres, Kafka, Redis)
    - Code coverage (JaCoCo > 70%)

  build:
    - docker build per service
    - docker push to GitHub Container Registry

  security:
    - Trivy container vulnerability scan
    - OWASP Dependency Check

  deploy-dev:
    - helm upgrade / kubectl apply to dev namespace
    - Smoke test (health check endpoints)
```

### Service Health Endpoints

Every Spring Boot service exposes:
```
GET /actuator/health        → UP / DOWN + component health
GET /actuator/info          → version, git commit, build time
GET /actuator/prometheus    → metrics
GET /actuator/env           → (restricted to admin)
```

---

## 13. AI / RAG Layer

### Architecture

```
Analyst (frontend)
    │
    ▼
POST /api/ai/explain { caseId }
    │
    ▼
ai-service (Python FastAPI)
    │
    ├── 1. Fetch context from APIs:
    │       - Case detail (case-service)
    │       - Alert detail (alert-service)
    │       - Transaction detail (transaction-service)
    │       - Customer profile (customer-service)
    │       - Rule definition (rule-engine-service)
    │
    ├── 2. Embed context chunks → pgvector
    │
    ├── 3. Semantic search: find most relevant past cases
    │
    ├── 4. Build prompt:
    │       System: "You are an AML compliance expert assistant..."
    │       Context: [retrieved chunks]
    │       Question: "Explain why this alert was triggered..."
    │
    ├── 5. Call LLM API (Claude claude-sonnet-4-20250514)
    │
    └── 6. Return structured response to frontend
```

### Prompt Design

```
System prompt:
"You are an AML compliance expert assistant for a bank's compliance team.
Your role is to explain why AML alerts were triggered and help analysts
investigate suspicious transactions. Be precise, factual, and reference
the specific data provided. Do not speculate beyond the data given.
Format your response in clear sections: Summary, Why This Alert Fired,
Risk Indicators, Recommended Actions."

User prompt:
"Analyze this AML alert:

ALERT: [severity] - [rule name]
TRANSACTION: €[amount] from [country] to [country], account [masked IBAN]
CUSTOMER: Risk level [X], PEP: [yes/no], KYC status: [status]
RULE: [rule description and parameters]
SIMILAR PAST CASES: [top 3 semantically similar cases from pgvector]

Explain why this alert fired and what the analyst should investigate next."
```

### pgvector Setup

```sql
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE case_embeddings (
    id UUID PRIMARY KEY,
    case_id UUID NOT NULL,
    content TEXT NOT NULL,
    embedding vector(1536),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX ON case_embeddings USING ivfflat (embedding vector_cosine_ops);
```

---

## 14. Project Structure

### Monorepo Layout

```
amlgraph/
├── README.md
├── docker-compose.yml
├── docker-compose.override.yml   (local dev secrets)
├── k8s/
├── docs/
│   ├── architecture.md           (this document)
│   ├── api-contracts.md
│   ├── aml-rules.md
│   └── diagrams/
│       ├── system-architecture.png
│       ├── domain-model.png
│       ├── kafka-flows.png
│       └── graph-model.png
│
├── services/
│   ├── amlgraph-gateway/
│   │   ├── src/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── transaction-service/
│   │   ├── src/
│   │   │   ├── main/java/com/amlgraph/transaction/
│   │   │   │   ├── api/           Controllers
│   │   │   │   ├── domain/        Entities, value objects
│   │   │   │   ├── service/       Business logic
│   │   │   │   ├── repository/    JPA repositories
│   │   │   │   ├── kafka/         Producers and consumers
│   │   │   │   └── config/        Spring config beans
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/  Flyway SQL scripts
│   │   ├── src/test/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── rule-engine-service/
│   ├── alert-service/
│   ├── customer-service/
│   ├── graph-service/
│   ├── reporting-service/
│   ├── simulator-service/
│   └── ai-service/              (Python)
│       ├── app/
│       │   ├── main.py
│       │   ├── routers/
│       │   ├── services/
│       │   └── rag/
│       ├── requirements.txt
│       └── Dockerfile
│
└── frontend/
    ├── src/
    ├── public/
    ├── package.json
    └── Dockerfile
```

### Spring Boot Service Template

Each Java service follows this package structure:

```
com.amlgraph.<service>/
├── api/
│   ├── controller/       @RestController classes
│   ├── dto/              Request / Response DTOs
│   └── mapper/           MapStruct mappers (DTO ↔ domain)
├── domain/
│   ├── entity/           JPA @Entity classes
│   ├── enums/            Status enums, severity levels
│   └── event/            Kafka event POJOs
├── service/
│   ├── <Service>.java    Business logic interface
│   └── impl/             Implementation
├── repository/           Spring Data JPA repositories
├── kafka/
│   ├── producer/         KafkaTemplate usage
│   └── consumer/         @KafkaListener handlers
├── config/               SecurityConfig, KafkaConfig, etc.
└── exception/            Custom exceptions + @ControllerAdvice
```

---

## 15. Tech Stack Summary

| Layer | Technology | Version |
|---|---|---|
| Language (backend) | Java | 21 LTS |
| Framework | Spring Boot | 3.3 |
| API Gateway | Spring Cloud Gateway | 2023.x |
| Rule Engine | Drools | 8.x |
| Language (AI) | Python | 3.12 |
| AI Framework | FastAPI + LangChain | latest |
| Message Broker | Apache Kafka | 3.7 |
| Primary DB | PostgreSQL | 16 |
| Graph DB | Neo4j | 5.x |
| Cache | Redis | 7 |
| Vector Search | pgvector | 0.7 |
| Identity | Keycloak | 24 |
| Frontend | React + TypeScript | 18 + 5 |
| UI Library | MUI | 5.x |
| Graph Viz | React Flow | 11.x |
| Build | Vite | 5.x |
| ORM | Spring Data JPA / Hibernate | 6.x |
| DB Migrations | Flyway | 10.x |
| Containerization | Docker + Docker Compose | 26 |
| Orchestration | Kubernetes | 1.30 |
| CI/CD | GitHub Actions | - |
| Metrics | Prometheus + Grafana | latest |
| Tracing | OpenTelemetry | 1.x |
| Testing | JUnit 5, Mockito, Testcontainers | - |
| Code Quality | SonarQube (Docker) | Community |
| Docs | Springdoc OpenAPI | 2.x |

---

## 16. Recruiter Positioning

### What to say in interviews and your README

> *"AMLGraph is a transaction monitoring platform that simulates how banks detect financial crime. It ingests real-time transactions via Kafka, runs a configurable AML rule engine (structuring, smurfing, PEP flags, high-risk countries), scores customer risk, creates investigation cases, and provides an AI assistant to help compliance analysts understand suspicious patterns. The backend is a Spring Boot 3 microservices architecture with Kafka, PostgreSQL, and Neo4j for graph-based money flow analysis. The frontend is React + TypeScript with React Flow for interactive transaction network visualization."*

### Skills this project demonstrates

| Skill | Where it shows |
|---|---|
| Spring Boot microservices | All 7 Java services |
| Event-driven with Kafka | 5 topics, producer/consumer in every service |
| PostgreSQL + JPA | All services, Flyway migrations |
| Graph databases (Neo4j) | graph-service |
| Security (Keycloak, JWT, RBAC) | Gateway + all services |
| Docker + Kubernetes | docker-compose + k8s/ manifests |
| CI/CD (GitHub Actions) | .github/workflows/ |
| Observability | Prometheus + Grafana dashboards |
| React + TypeScript | Frontend with React Flow graph viz |
| AI / RAG | ai-service with LangChain + pgvector |
| Regulatory awareness | FATF, 6AMLD, ACPR framing |
| Banking domain (AML/KYC) | Entire project |
| Testing | JUnit + Testcontainers integration tests |

### Conversation hooks for French banking recruiters

- "I implemented FATF risk-based approach in the customer risk scoring"
- "The rule engine covers structuring and smurfing detection as defined under 6AMLD"
- "The audit trail is append-only — every state change is immutable, which mirrors ACPR requirements"
- "I used Neo4j to model account relationships because graph traversal for layering detection is fundamentally better than SQL joins across 3+ hops"

---

*Document version: 1.0 — Architecture only. Implementation not included.*
