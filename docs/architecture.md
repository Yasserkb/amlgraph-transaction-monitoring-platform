# AMLGraph Architecture

AMLGraph follows a cloud-native, event-driven architecture inspired by bank-grade AML and KYC platforms.

```text
React UI → API Gateway → Spring Boot Services → Kafka → AML Rules → Alerts/Cases
                                      ↓
                             PostgreSQL / Neo4j / Redis
                                      ↓
                              Prometheus + Grafana
```

## Services

| Service | Responsibility |
|---|---|
| `gateway` | Single entry point, routing, optional JWT enforcement |
| `transaction-service` | Transaction validation, persistence, Kafka publication |
| `rule-engine-service` | AML detection rules and alert event generation |
| `alert-service` | Alert persistence and case lifecycle management |
| `customer-service` | Customer profiles and risk scores |
| `graph-service` | Graph-ready transaction relationship API |
| `ai-service` | RAG-ready AML assistant API |
| `frontend` | Analyst dashboard deployed locally or on Vercel |

## Event Flow

```text
POST /api/transactions
  ↓
transaction-service saves transaction
  ↓
Kafka topic: transactions.raw
  ↓
rule-engine-service evaluates AML rules
  ↓
Kafka topic: alerts.created
  ↓
alert-service creates alert and investigation case
```

## Kafka Topics

| Topic | Producer | Consumer |
|---|---|---|
| `transactions.raw` | transaction-service | rule-engine-service |
| `alerts.created` | rule-engine-service | alert-service, customer-service |
| `audit.events` | future services | audit-service extension |

## Design Decisions

- Kafka is used for core business events instead of service-to-service synchronous calls.
- PostgreSQL schemas are isolated by service using Flyway migrations.
- The frontend talks only to the Gateway.
- Security is optional locally and designed to be enabled with Keycloak/JWT in production.
- The AI service starts as deterministic and testable, then can be upgraded to real RAG with pgvector.
