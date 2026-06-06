from fastapi import FastAPI
from pydantic import BaseModel, Field
from typing import List

app = FastAPI(title="AMLGraph AI Investigation Assistant", version="0.1.0")


class ExplainRequest(BaseModel):
    caseId: str = Field(..., description="Investigation case id")


class ExplainResponse(BaseModel):
    summary: str
    why_alert_fired: List[str]
    risk_indicators: List[str]
    recommended_actions: List[str]


class ChatRequest(BaseModel):
    caseId: str
    message: str


class ChatResponse(BaseModel):
    answer: str


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "UP"}


@app.post("/api/ai/explain", response_model=ExplainResponse)
def explain(request: ExplainRequest) -> ExplainResponse:
    return ExplainResponse(
        summary=f"Case {request.caseId} contains indicators that require AML analyst review.",
        why_alert_fired=[
            "The transaction matched one or more configured AML detection rules.",
            "The event should be reviewed against customer risk profile, transaction purpose, and jurisdiction risk.",
        ],
        risk_indicators=[
            "Large amount compared with reporting thresholds.",
            "Possible high-risk jurisdiction exposure.",
            "Potential need for enhanced due diligence documentation.",
        ],
        recommended_actions=[
            "Verify source of funds and transaction purpose.",
            "Review customer KYC profile and recent activity.",
            "Escalate to compliance officer if the explanation is not satisfactory.",
        ],
    )


@app.post("/api/ai/chat", response_model=ChatResponse)
def chat(request: ChatRequest) -> ChatResponse:
    return ChatResponse(
        answer=(
            "Based on the current case context, focus first on transaction amount, destination country, "
            "customer risk score, and whether similar activity appeared in recent history. "
            f"Question received: {request.message}"
        )
    )
