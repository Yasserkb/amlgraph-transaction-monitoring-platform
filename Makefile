.PHONY: up down logs test backend-test frontend-test ai-test build clean

up:
	docker compose up --build

down:
	docker compose down -v

logs:
	docker compose logs -f --tail=200

test: backend-test frontend-test ai-test

backend-test:
	mvn -f services/pom.xml test

frontend-test:
	cd frontend && pnpm install && pnpm test

ai-test:
	cd services/ai-service && python -m pip install -r requirements.txt && pytest

build:
	docker compose build

clean:
	docker compose down -v --remove-orphans
	find . -name target -type d -prune -exec rm -rf {} +
	find frontend -name node_modules -type d -prune -exec rm -rf {} +
