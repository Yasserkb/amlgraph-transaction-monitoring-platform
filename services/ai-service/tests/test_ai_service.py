from pathlib import Path
import sys

ROOT_DIR = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT_DIR))

from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "UP"


def test_explain_case():
    response = client.post("/api/ai/explain", json={"caseId": "case-1"})
    assert response.status_code == 200
    body = response.json()
    assert "summary" in body
    assert len(body["recommended_actions"]) >= 1
