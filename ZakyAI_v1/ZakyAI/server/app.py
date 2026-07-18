"""
ZakyAI - خادم وسيط اختياري (FastAPI)
=====================================
الهدف من هذا الخادم: توحيد الاتصال بمزودي الذكاء الاصطناعي المختلفين
(Groq, Cerebras, OpenRouter, Hugging Face) خلف واجهة واحدة، بحيث لا يحتاج
تطبيق Android لحمل مفاتيح API الحساسة داخل الـ APK.

هذا اختياري: يمكن للتطبيق الاتصال مباشرة بمزودي الذكاء الاصطناعي دون هذا
الخادم، لكن استخدام خادم وسيط أكثر أمانًا لأنه يُبقي المفاتيح على الخادم
فقط وليس داخل تطبيق يمكن فك حزمه (reverse-engineered).

تشغيل محلي:
    pip install -r requirements.txt --break-system-packages
    cp .env.example .env   # ثم املأ مفاتيحك الحقيقية في .env
    uvicorn app:app --reload --port 8000
"""

import os
from typing import Literal

import httpx
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

load_dotenv()

app = FastAPI(title="ZakyAI Proxy Server")

# اسمح بالاتصال من تطبيق Android / لوحة الويب أثناء التطوير.
# في الإنتاج، حدد النطاقات المسموحة بدقة بدل "*"
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

PROVIDERS = {
    "groq": {
        "base_url": "https://api.groq.com/openai/v1/chat/completions",
        "key_env": "GROQ_API_KEY",
    },
    "cerebras": {
        "base_url": "https://api.cerebras.ai/v1/chat/completions",
        "key_env": "CEREBRAS_API_KEY",
    },
    "openrouter": {
        "base_url": "https://openrouter.ai/api/v1/chat/completions",
        "key_env": "OPENROUTER_API_KEY",
    },
}


class ChatMessage(BaseModel):
    role: str
    content: str


class ChatRequest(BaseModel):
    provider: Literal["groq", "cerebras", "openrouter", "huggingface"]
    model: str
    messages: list[ChatMessage]
    temperature: float = 0.7
    max_tokens: int = 2048


@app.get("/health")
async def health():
    return {"status": "ok"}


@app.post("/chat")
async def chat(req: ChatRequest):
    """نقطة نهاية موحدة للدردشة عبر أي مزود مدعوم."""
    if req.provider == "huggingface":
        return await _chat_huggingface(req)
    return await _chat_openai_compatible(req)


async def _chat_openai_compatible(req: ChatRequest):
    provider_cfg = PROVIDERS[req.provider]
    api_key = os.getenv(provider_cfg["key_env"], "")
    if not api_key:
        raise HTTPException(500, f"مفتاح {provider_cfg['key_env']} غير مُعرَّف في .env")

    payload = {
        "model": req.model,
        "messages": [m.model_dump() for m in req.messages],
        "temperature": req.temperature,
        "max_tokens": req.max_tokens,
    }
    headers = {"Authorization": f"Bearer {api_key}"}

    async with httpx.AsyncClient(timeout=60) as client:
        resp = await client.post(provider_cfg["base_url"], json=payload, headers=headers)

    if resp.status_code != 200:
        raise HTTPException(resp.status_code, resp.text)

    data = resp.json()
    reply = data["choices"][0]["message"]["content"]
    return {"reply": reply}


async def _chat_huggingface(req: ChatRequest):
    api_key = os.getenv("HUGGINGFACE_API_KEY", "")
    if not api_key:
        raise HTTPException(500, "مفتاح HUGGINGFACE_API_KEY غير مُعرَّف في .env")

    url = f"https://api-inference.huggingface.co/models/{req.model}"
    prompt = req.messages[-1].content if req.messages else ""
    payload = {
        "inputs": prompt,
        "parameters": {"temperature": req.temperature, "max_new_tokens": req.max_tokens},
    }
    headers = {"Authorization": f"Bearer {api_key}"}

    async with httpx.AsyncClient(timeout=60) as client:
        resp = await client.post(url, json=payload, headers=headers)

    if resp.status_code != 200:
        raise HTTPException(resp.status_code, resp.text)

    data = resp.json()
    reply = data[0]["generated_text"] if isinstance(data, list) else str(data)
    return {"reply": reply}


@app.post("/image")
async def generate_image(prompt: str):
    """توليد صورة عبر Pollinations.ai - لا يحتاج مفتاح API."""
    import urllib.parse
    encoded = urllib.parse.quote(prompt)
    return {"image_url": f"https://image.pollinations.ai/prompt/{encoded}"}
