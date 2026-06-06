# AMLGraph — AML Transaction Monitoring Platform

**AMLGraph** is a full-stack portfolio project that I built to simulate a real AML/Fraud monitoring platform used in banking and fintech environments.

The goal of this project is to show how I design and implement a distributed system around real-time transaction monitoring, suspicious activity detection, customer risk scoring, investigation case management, and analyst-facing dashboards.

The project is built around the kind of architecture I work with and want to keep growing into: **Java/Spring Boot microservices, Kafka event-driven flows, PostgreSQL/Flyway, Docker, CI/CD, React/TypeScript, observability, and AI-assisted investigation workflows**.

---

AMLGraph is designed to demonstrate:

- backend engineering with Spring Boot microservices
- event-driven architecture with Kafka
- database ownership per service using PostgreSQL schemas and Flyway
- clean REST APIs behind an API Gateway
- business-rule based alert generation
- case management workflows
- customer risk profiling
- frontend dashboards for analysts
- Dockerized local infrastructure
- CI/CD readiness with GitHub Actions
- a Vercel-ready React frontend
- an extensible foundation for graph analysis and AI investigation support

This is not a toy “hello world” project. It is a realistic engineering foundation that can be extended toward a production-grade AML platform.

---

## Business Flow

The platform follows a typical AML monitoring flow:

```text
Transaction received
        ↓
Transaction stored
        ↓
Kafka event published
        ↓
AML rules evaluated
        ↓
Alert generated
        ↓
Investigation case opened
        ↓
Customer risk updated
        ↓
Analyst reviews the case from the dashboard
```

In practice:

1. A transaction is submitted through the API Gateway.
2. `transaction-service` validates and stores the transaction.
3. The service publishes a `transactions.raw` event to Kafka.
4. `rule-engine-service` consumes the event and applies AML rules.
5. Suspicious transactions generate `alerts.created` events.
6. `alert-service` stores alerts and creates investigation cases for high-risk alerts.
7. `customer-service` exposes customer risk profiles.
8. `graph-service` exposes graph-ready relationship data.
9. `ai-service` provides an investigation assistant API.
10. `frontend` gives analysts a dashboard to explore transactions, alerts, cases, customers, and AI explanations.

---

## Architecture Overview

```text
frontend
  │
  ▼
api-gateway
  ├── transaction-service ──► PostgreSQL
  │          │
  │          ▼
  │     Kafka: transactions.raw
  │          │
  │          ▼
  ├── rule-engine-service
  │          │
  │          ▼
  │     Kafka: alerts.created
  │          │
  │          ▼
  ├── alert-service ────────► PostgreSQL
  │
  ├── customer-service ─────► PostgreSQL
  │
  ├── graph-service ────────► graph-ready API / Neo4j integration point
  │
  └── ai-service ───────────► FastAPI investigation assistant
```

The current implementation focuses on the end-to-end AML workflow and clean service boundaries. Neo4j and the AI layer are prepared as extension points: the graph service exposes graph-ready relationship data, and the AI service provides deterministic investigation explanations that can later be connected to a real RAG pipeline.

---

## Tech Stack

### Backend

- Java 21
- Spring Boot 3
- Spring Cloud Gateway
- Spring Web
- Spring Data JPA
- Spring Kafka
- PostgreSQL 16
- Flyway
- Maven multi-module project
- JUnit 5
- Mockito
- Actuator
- Prometheus metrics

### Event Streaming

- Apache Kafka
- Zookeeper
- JSON event envelopes
- Transaction and alert event topics

### Frontend

- React 18
- TypeScript
- Vite
- MUI
- TanStack Query
- React Router
- Recharts
- React Flow
- Vitest
- Vercel-ready configuration

### AI Service

- Python 3.12
- FastAPI
- Pytest

### Infrastructure and DevOps

- Docker
- Docker Compose
- PostgreSQL
- Kafka
- Redis
- Neo4j integration point
- Prometheus
- Grafana
- GitHub Actions
- Vercel deployment configuration

---

## Services

| Service | Port | Responsibility |
|---|---:|---|
| `gateway` | `8080` | Single entry point for frontend and external clients |
| `transaction-service` | `8081` | Stores transactions and publishes transaction events |
| `rule-engine-service` | `8082` | Consumes transactions and evaluates AML rules |
| `alert-service` | `8083` | Stores alerts and manages investigation cases |
| `customer-service` | `8084` | Exposes customer profiles and risk data |
| `graph-service` | `8085` | Exposes graph-ready relationship data |
| `ai-service` | `8087` | Provides investigation assistant APIs |
| `frontend` | `3000` | Analyst dashboard |
| `postgres` | `5432` | Relational storage |
| `kafka` | `29092` | Local Kafka access |
| `prometheus` | `9090` | Metrics scraping |
| `grafana` | `3001` | Dashboards |

---

## Repository Structure

```text
amlgraph/
├── .github/
│   └── workflows/
│       └── ci.yml
├── docs/
│   ├── screenshots
│   ├── architecture.md
│   ├── api-contracts.md
│   └── diagrams/
├── frontend/
│   ├── src/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── vercel.json
├── infra/
│   └── prometheus/
│       └── prometheus.yml
├── scripts/
│   └── seed-demo-transaction.sh
├── services/
│   ├── pom.xml
│   ├── common/
│   ├── gateway/
│   ├── transaction-service/
│   ├── rule-engine-service/
│   ├── alert-service/
│   ├── customer-service/
│   ├── graph-service/
│   └── ai-service/
├── docker-compose.yml
├── Makefile
├── .env.example
└── README.md
```

---

## Quick Start

### Prerequisites

You need:

- Docker Desktop
- Docker Compose
- Git

For local development outside Docker, you also need:

- Java 21
- Maven
- Node.js 20+
- pnpm
- Python 3.12

---

## Run the Full Platform

Clone the repository:

```bash
git clone  amlgraph
cd amlgraph
```

Create the environment file:

```bash
cp .env.example .env
```

Start everything:

```bash
docker compose up --build
```

Or run it in detached mode:

```bash
docker compose up -d --build
```

Check the containers:

```bash
docker compose ps -a
```

The important services should be `Up`:

```text
amlgraph-gateway
amlgraph-frontend
amlgraph-transaction-service
amlgraph-rule-engine-service
amlgraph-alert-service
amlgraph-customer-service
amlgraph-ai-service
amlgraph-postgres
amlgraph-kafka
```

---

## Local URLs

| App | URL |
|---|---|
| Frontend | http://localhost:3000 |
| API Gateway | http://localhost:8080 |
| Transaction Service Swagger | http://localhost:8081/swagger-ui/index.html |
| Alert Service Swagger | http://localhost:8083/swagger-ui/index.html |
| Customer Service Swagger | http://localhost:8084/swagger-ui/index.html |
| AI Service Docs | http://localhost:8087/docs |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3001 |

Grafana default credentials:

```text
admin / admin
```

---

## Health Checks

After startup, test the platform:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8087/health
```

You can also test the gateway routes:

```bash
curl http://localhost:8080/api/transactions
curl http://localhost:8080/api/alerts
curl http://localhost:8080/api/cases
curl http://localhost:8080/api/customers
```

---

## Demo: Create a Suspicious Transaction

Create a transaction that should trigger AML rules:

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "11111111-1111-1111-1111-111111111111",
    "sourceAccountId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
    "destinationAccountId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
    "amount": 15000.00,
    "currency": "EUR",
    "transactionType": "TRANSFER",
    "channel": "SWIFT",
    "originCountry": "FR",
    "destinationCountry": "AE",
    "reference": "Large cross-border transfer",
    "executedAt": "2026-06-05T12:00:00Z"
  }'
```

Then check generated alerts and cases:

```bash
curl http://localhost:8080/api/alerts
curl http://localhost:8080/api/cases
```

You should see an alert created by the rule engine and an investigation case created by the alert service.

---

## Demo Script

A helper script is available:

```bash
./scripts/seed-demo-transaction.sh
```

On Windows PowerShell, you can run the equivalent curl command manually or use Git Bash.

---

## AML Rules Implemented

The rule engine currently includes deterministic AML rules.

Examples:

| Rule | Description |
|---|---|
| Large transaction | Flags transactions above configured thresholds |
| High-risk country flow | Flags transactions involving high-risk destinations |
| Unusual hour activity | Flags suspicious transfers executed during unusual hours |

The rule engine is intentionally simple and readable. The goal is to make the business logic easy to explain in interviews and easy to replace later with Drools, decision tables, or a rules DSL.

---

## API Examples

### Get Transactions

```bash
curl http://localhost:8080/api/transactions
```

### Get Alerts

```bash
curl http://localhost:8080/api/alerts
```

### Get Cases

```bash
curl http://localhost:8080/api/cases
```

### Get Customers

```bash
curl http://localhost:8080/api/customers
```

### Ask the AI Investigation Assistant

```bash
curl -X POST http://localhost:8080/api/ai/explain \
  -H "Content-Type: application/json" \
  -d '{
    "caseId": "demo-case-001"
  }'
```

---

## Frontend

The frontend is an analyst dashboard built with React and TypeScript.

It includes pages for:

- Dashboard overview
- Transactions
- Alerts
- Investigation cases
- Customers
- AI investigation assistant

The frontend talks to the backend through the API Gateway.

Local URL:

```text
http://localhost:3000
```

The API base URL is configured using:

```text
VITE_API_BASE_URL
```

For local Docker usage, it points to:

```text
http://localhost:8080/api
```

---

## Running Tests

### Backend Tests

From the root directory:

```bash
mvn -f services/pom.xml test
```

### Frontend Tests

```bash
cd frontend
pnpm install
pnpm test
pnpm build
```

### AI Service Tests

```bash
cd services/ai-service
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
pytest
```

On Windows PowerShell:

```powershell
cd services/ai-service
python -m venv .venv
.\.venv\Scripts\activate
pip install -r requirements.txt
pytest
```

---

## CI/CD

The repository includes GitHub Actions for:

- backend tests
- frontend tests
- AI service tests
- Docker image builds
- optional Vercel deployment for the frontend

Workflow file:

```text
.github/workflows/ci.yml
```

The goal is to keep the repository close to a professional delivery setup, not just local code.

---

## Vercel Deployment

The frontend is prepared for Vercel deployment.

Recommended settings:

| Setting | Value |
|---|---|
| Framework Preset | Vite |
| Root Directory | `frontend` |
| Build Command | `pnpm build` |
| Output Directory | `dist` |
| Environment Variable | `VITE_API_BASE_URL=https://your-gateway-domain/api` |

Required GitHub secrets for automated deployment:

```text
VERCEL_TOKEN
VERCEL_ORG_ID
VERCEL_PROJECT_ID
```

---

## Observability

Each Spring Boot service exposes Actuator endpoints and Prometheus metrics.

Prometheus runs locally at:

```text
http://localhost:9090
```

Grafana runs locally at:

```text
http://localhost:3001
```

This makes it possible to extend the project with dashboards for:

- request count
- response times
- JVM metrics
- Kafka consumers
- service health
- business metrics such as alerts generated per severity

---

## Database Migrations

Each service owns its database schema through Flyway migrations.

Examples:

```text
transaction-service -> transactions schema
alert-service       -> alerts schema
customer-service    -> customers schema
```

This keeps database ownership clear and avoids putting all application data into one shared schema.

---

## Design Decisions

### Why Microservices?

AML systems naturally involve multiple bounded contexts:

- transactions
- rules
- alerts
- cases
- customers
- graph relationships
- investigation assistance

Splitting the system this way makes the responsibilities clearer and allows each service to evolve independently.

### Why Kafka?

Transaction monitoring is event-driven by nature. Kafka allows the platform to process transactions asynchronously and keeps the ingestion flow decoupled from the rule engine and alert generation.

### Why PostgreSQL and Flyway?

PostgreSQL is reliable for structured financial data, and Flyway gives a clean, repeatable migration strategy.

### Why React?

The analyst dashboard needs a fast and flexible UI with tables, charts, filters, and investigation views. React with TypeScript is a good fit for this type of internal platform.

### Why FastAPI for the AI service?

The AI assistant is isolated from the Java backend so it can evolve independently. This makes it easier to later add RAG, vector search, prompt templates, and LLM providers without coupling that logic to the core banking services.

---

## What Is Implemented

Current implementation includes:

- API Gateway routing
- transaction ingestion API
- transaction persistence
- Kafka transaction event publishing
- Kafka transaction consumption
- AML rule evaluation
- alert event publishing
- alert persistence
- automatic case creation
- customer profile API
- customer risk model foundation
- graph-ready relationship API
- AI investigation assistant API
- React analyst dashboard
- Docker Compose local platform
- Prometheus and Grafana setup
- unit tests for core business logic
- GitHub Actions workflow
- Vercel-ready frontend configuration

---

## What Is Intentionally Simplified

This is a portfolio project, so some parts are intentionally simplified but prepared for extension:

- authentication is prepared at the gateway level but not fully enforced by default
- graph analysis currently exposes graph-ready data instead of a complete graph investigation engine
- the AI assistant is deterministic and RAG-ready, but not connected to a real LLM provider by default
- AML rules are implemented in Java code instead of Drools or a full rule management UI
- local Docker Compose is used instead of Kubernetes for the first version

These choices keep the project runnable and understandable while leaving clear space for future upgrades.

---

## Troubleshooting

### Gateway returns 500 for `/api/transactions` or `/api/customers`

Check if the target service is running:

```bash
docker compose ps -a
```

If a service is exited, inspect its logs:

```bash
docker compose logs --tail=200 transaction-service
docker compose logs --tail=200 customer-service
docker compose logs --tail=200 alert-service
```

A gateway `UnknownHostException` usually means the backend container is not running.

### Reset the platform completely

```bash
docker compose down -v
docker compose up -d --build
```

This removes volumes and recreates the databases from Flyway migrations.

### Check Docker DNS from the gateway

```bash
docker exec -it amlgraph-gateway sh -c "getent hosts transaction-service customer-service alert-service ai-service"
```

---

## Roadmap

The next improvements I plan to add are:

- Keycloak authentication and role-based access control
- analyst/admin role separation
- Testcontainers integration tests for Kafka and PostgreSQL
- real Neo4j persistence for customer-account-transaction networks
- graph path detection for mule accounts and circular transfers
- Drools or decision-table based AML rules
- bulk transaction import
- advanced filtering in the dashboard
- case assignment workflow
- audit trail for analyst actions
- RAG-based AI assistant with pgvector
- Kubernetes manifests and Helm charts
- cloud deployment for the backend

---

## Portfolio Summary

AMLGraph demonstrates how I approach backend and full-stack engineering for financial platforms.

It combines:

- Java/Spring Boot backend engineering
- event-driven architecture
- Kafka-based processing
- database migrations
- API Gateway design
- Dockerized infrastructure
- React/TypeScript frontend development
- observability
- CI/CD preparation
- AI and graph-analysis extension points

The project reflects the type of systems I want to build professionally: clean, explainable, scalable, and close to real banking and fintech use cases.

---

## Author

**Yasser Koubachi**  
Software Engineer — Java / Spring Boot / React / Cloud / Data & AI Engineering

GitHub: `Yasserkb`
