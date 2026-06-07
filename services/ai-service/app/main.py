import json
import logging
import os
import re
from typing import List, Optional

import httpx
from fastapi import FastAPI
from pydantic import BaseModel, Field

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("amlgraph-ai")

app = FastAPI(title="AMLGraph AI Investigation Assistant", version="0.3.0")

OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", "http://localhost:11434")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "qwen3:1.7b")
AI_PROVIDER = os.getenv("AI_PROVIDER", "ollama")


class ExplainRequest(BaseModel):
    caseId: str = Field(..., description="Investigation case id")
    alertSeverity: Optional[str] = "HIGH"
    amount: Optional[float] = None
    currency: Optional[str] = "EUR"
    originCountry: Optional[str] = None
    destinationCountry: Optional[str] = None
    customerRiskLevel: Optional[str] = None
    ruleNames: List[str] = Field(default_factory=list)


class ExplainResponse(BaseModel):
    summary: str
    why_alert_fired: List[str]
    risk_indicators: List[str]
    recommended_actions: List[str]
    provider: str = "fallback"


class ChatRequest(BaseModel):
    caseId: str
    message: str


class ChatResponse(BaseModel):
    answer: str
    provider: str = "fallback"


@app.get("/health")
def health() -> dict[str, str]:
    return {
        "status": "UP",
        "provider": AI_PROVIDER,
        "model": OLLAMA_MODEL,
        "ollamaBaseUrl": OLLAMA_BASE_URL,
    }


@app.post("/api/ai/explain", response_model=ExplainResponse)
def explain(request: ExplainRequest) -> ExplainResponse:
    logger.info("Received explain request for caseId=%s", request.caseId)

    if AI_PROVIDER.lower() == "ollama":
        ollama_response = call_ollama_for_explanation(request)
        if ollama_response is not None:
            return ollama_response

    logger.warning("Using fallback explanation for caseId=%s", request.caseId)
    return fallback_explanation(request)


@app.post("/api/ai/chat", response_model=ChatResponse)
def chat(request: ChatRequest) -> ChatResponse:
    logger.info("Received chat request for caseId=%s", request.caseId)

    if AI_PROVIDER.lower() == "ollama":
        ollama_answer = call_ollama_for_chat(request)
        if ollama_answer is not None:
            return ChatResponse(answer=ollama_answer, provider=f"ollama:{OLLAMA_MODEL}")

    logger.warning("Using fallback chat response for caseId=%s", request.caseId)

    return ChatResponse(
        answer=(
            "Based on the current case context, start by reviewing the transaction amount, "
            "destination country, customer risk profile, previous account activity, and the rule that generated the alert. "
            f"Question received: {request.message}"
        ),
        provider="fallback",
    )


def call_ollama_for_explanation(request: ExplainRequest) -> Optional[ExplainResponse]:
    prompt = build_explanation_prompt(request)

    try:
        response = httpx.post(
            f"{OLLAMA_BASE_URL}/api/generate",
            json={
                "model": OLLAMA_MODEL,
                "prompt": prompt,
                "stream": False,
                "format": "json",
                "options": {
                    "temperature": 0.1,
                    "num_predict": 500,
                },
            },
            timeout=120,
        )

        response.raise_for_status()

        raw_text = response.json().get("response", "").strip()
        logger.info("Raw Ollama explanation response: %s", raw_text)

        parsed = extract_json(raw_text)

        if parsed is None:
            logger.warning("Could not parse Ollama JSON response for caseId=%s", request.caseId)
            return None

        return ExplainResponse(
            summary=str(parsed.get("summary") or fallback_explanation(request).summary),
            why_alert_fired=ensure_list(parsed.get("why_alert_fired")),
            risk_indicators=ensure_list(parsed.get("risk_indicators")),
            recommended_actions=ensure_list(parsed.get("recommended_actions")),
            provider=f"ollama:{OLLAMA_MODEL}",
        )

    except Exception as exc:
        logger.exception("Ollama explanation call failed: %s", exc)
        return None


def call_ollama_for_chat(request: ChatRequest) -> Optional[str]:
    prompt = f"""
You are an AML investigation assistant helping a financial crime analyst.

Case ID: {request.caseId}

Analyst question:
{request.message}

Answer clearly and professionally.
Focus on AML reasoning, investigation steps, and risk indicators.
Do not invent facts that are not provided.
"""

    try:
        response = httpx.post(
            f"{OLLAMA_BASE_URL}/api/generate",
            json={
                "model": OLLAMA_MODEL,
                "prompt": prompt,
                "stream": False,
                "options": {
                    "temperature": 0.2,
                    "num_predict": 500,
                },
            },
            timeout=120,
        )

        response.raise_for_status()

        answer = response.json().get("response", "").strip()
        logger.info("Raw Ollama chat response: %s", answer)

        return answer if answer else None

    except Exception as exc:
        logger.exception("Ollama chat call failed: %s", exc)
        return None


def build_explanation_prompt(request: ExplainRequest) -> str:
    return f"""
You are an AML investigation assistant for a banking transaction monitoring platform.

Generate an analyst-ready explanation for this AML case.

Case context:
- Case ID: {request.caseId}
- Alert severity: {request.alertSeverity}
- Amount: {request.amount}
- Currency: {request.currency}
- Origin country: {request.originCountry}
- Destination country: {request.destinationCountry}
- Customer risk level: {request.customerRiskLevel}
- Triggered rules: {", ".join(request.ruleNames) if request.ruleNames else "not provided"}

Return valid JSON only.

The JSON must use exactly this structure:

{{
  "summary": "short analyst summary",
  "why_alert_fired": ["reason 1", "reason 2"],
  "risk_indicators": ["indicator 1", "indicator 2"],
  "recommended_actions": ["action 1", "action 2"]
}}

Rules:
- No markdown.
- No text outside JSON.
- No customer names.
- No invented facts.
- Professional compliance tone.
"""


def extract_json(text: str) -> Optional[dict]:
    if not text:
        return None

    cleaned = text.strip()

    cleaned = cleaned.replace("```json", "")
    cleaned = cleaned.replace("```", "")
    cleaned = re.sub(r"<think>.*?</think>", "", cleaned, flags=re.DOTALL).strip()

    try:
        return json.loads(cleaned)
    except json.JSONDecodeError:
        pass

    match = re.search(r"\{[\s\S]*\}", cleaned)

    if not match:
        return None

    try:
        return json.loads(match.group(0))
    except json.JSONDecodeError:
        return None


def ensure_list(value) -> List[str]:
    if isinstance(value, list):
        cleaned = [str(item).strip() for item in value if str(item).strip()]
        return cleaned if cleaned else ["No specific information was provided by the model."]

    if isinstance(value, str) and value.strip():
        return [value.strip()]

    return ["No specific information was provided by the model."]


def fallback_explanation(request: ExplainRequest) -> ExplainResponse:
    risk_indicators = []

    if request.amount and request.amount >= 10000:
        risk_indicators.append("Large transaction amount compared with common AML review thresholds.")

    if request.destinationCountry in {"AE", "IR", "KP", "MM", "RU"}:
        risk_indicators.append("Possible exposure to a high-risk or sensitive jurisdiction.")

    if request.customerRiskLevel in {"HIGH", "CRITICAL"}:
        risk_indicators.append("Customer already has an elevated risk profile.")

    if not risk_indicators:
        risk_indicators.append("The transaction matched one or more configured AML monitoring rules.")

    return ExplainResponse(
        summary=f"Case {request.caseId} contains indicators that require AML analyst review.",
        why_alert_fired=[
            "The transaction matched one or more AML detection rules.",
            "The case should be reviewed against customer profile, transaction purpose, and jurisdiction risk.",
        ],
        risk_indicators=risk_indicators,
        recommended_actions=[
            "Verify source of funds and transaction purpose.",
            "Review the customer KYC profile and recent transaction history.",
            "Request supporting documentation if the activity is not consistent with the customer profile.",
            "Escalate to a compliance officer if the explanation is not satisfactory.",
        ],
        provider="fallback",
    )