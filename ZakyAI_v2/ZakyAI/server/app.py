"""
ZakyAI - خادم وسيط (اختياري)
================================
الغرض من هذا الخادم:
  1. إخفاء مفاتيح الـ API الحقيقية عن تطبيق Android/الويب (لا تُشحن أي مفاتيح داخل APK
     أو صفحة HTML عامة أبداً؛ يجب أن تبقى فقط على الخادم كمتغيرات بيئة).
  2. توحيد نقطة اتصال واحدة (Endpoint) للتطبيق بدل استدعاء كل مزود مباشرة من العميل.
  3. تطبيق حدود استخدام (Rate Limiting) وتسجيل الطلبات بشكل مركزي إن أردت لاحقاً.

تشغيل محلي:
    pip install -r requirements.txt
    cp .env.example .env   # ثم ضع مفاتيحك الحقيقية داخل .env
    python app.py
"""

import os
import requests
from flask import Flask, request, jsonify
from flask_cors import CORS
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)
CORS(app)  # في الإنتاج، قيّد المصادر المسموحة بدل السماح للجميع

GROQ_API_KEY = os.getenv("GROQ_API_KEY", "")
GROQ_BASE_URL = os.getenv("GROQ_BASE_URL", "https://api.groq.com/openai/v1/")

CEREBRAS_API_KEY = os.getenv("CEREBRAS_API_KEY", "")
CEREBRAS_BASE_URL = os.getenv("CEREBRAS_BASE_URL", "https://api.cerebras.ai/v1/")

OPENROUTER_API_KEY = os.getenv("OPENROUTER_API_KEY", "")
OPENROUTER_BASE_URL = os.getenv("OPENROUTER_BASE_URL", "https://openrouter.ai/api/v1/")

HF_API_KEY = os.getenv("HUGGINGFACE_API_KEY", "")
HF_BASE_URL = os.getenv("HUGGINGFACE_BASE_URL", "https://api-inference.huggingface.co/models/")

PROVIDERS = {
    "groq": {"base": GROQ_BASE_URL, "key": GROQ_API_KEY, "default_model": "llama-3.3-70b-versatile"},
    "cerebras": {"base": CEREBRAS_BASE_URL, "key": CEREBRAS_API_KEY, "default_model": "llama3.1-70b"},
    "openrouter": {"base": OPENROUTER_BASE_URL, "key": OPENROUTER_API_KEY, "default_model": "openrouter/auto"},
}


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok", "service": "ZakyAI relay server"})


@app.route("/api/chat", methods=["POST"])
def chat():
    """
    نقطة موحّدة للدردشة عبر أي مزود متوافق مع OpenAI Chat Completions.
    body: { "provider": "groq" | "cerebras" | "openrouter", "messages": [...], "temperature": 0.7, "model": "..." }
    """
    data = request.get_json(force=True)
    provider_name = data.get("provider", "groq")
    provider = PROVIDERS.get(provider_name)

    if provider is None:
        return jsonify({"error": f"مزود غير معروف: {provider_name}"}), 400
    if not provider["key"]:
        return jsonify({"error": f"لم يتم ضبط مفتاح API لهذا المزود على الخادم ({provider_name})"}), 500

    payload = {
        "model": data.get("model", provider["default_model"]),
        "messages": data.get("messages", []),
        "temperature": data.get("temperature", 0.7),
    }
    headers = {
        "Authorization": f"Bearer {provider['key']}",
        "Content-Type": "application/json",
    }

    try:
        resp = requests.post(
            provider["base"].rstrip("/") + "/chat/completions",
            json=payload, headers=headers, timeout=60
        )
        resp.raise_for_status()
        return jsonify(resp.json())
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 502


@app.route("/api/huggingface", methods=["POST"])
def huggingface():
    """
    body: { "model_path": "org/model-name", "inputs": "نص الإدخال", "temperature": 0.7 }
    """
    if not HF_API_KEY:
        return jsonify({"error": "لم يتم ضبط مفتاح Hugging Face على الخادم"}), 500

    data = request.get_json(force=True)
    model_path = data.get("model_path")
    if not model_path:
        return jsonify({"error": "model_path مطلوب"}), 400

    headers = {"Authorization": f"Bearer {HF_API_KEY}"}
    payload = {
        "inputs": data.get("inputs", ""),
        "parameters": {"temperature": data.get("temperature", 0.7)}
    }

    try:
        resp = requests.post(HF_BASE_URL.rstrip("/") + "/" + model_path, json=payload, headers=headers, timeout=60)
        resp.raise_for_status()
        return jsonify(resp.json())
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 502


@app.route("/api/image", methods=["POST"])
def generate_image():
    """
    توليد صورة عبر Pollinations.ai (لا يتطلب مفتاح API).
    body: { "prompt": "وصف الصورة" }
    """
    data = request.get_json(force=True)
    prompt = data.get("prompt", "")
    if not prompt:
        return jsonify({"error": "prompt مطلوب"}), 400
    image_url = f"https://image.pollinations.ai/prompt/{requests.utils.quote(prompt)}"
    return jsonify({"image_url": image_url})


if __name__ == "__main__":
    port = int(os.getenv("PORT", 8000))
    # في الإنتاج استخدم gunicorn بدل التشغيل المباشر:
    #   gunicorn -w 4 -b 0.0.0.0:8000 app:app
    app.run(host="0.0.0.0", port=port, debug=False)
