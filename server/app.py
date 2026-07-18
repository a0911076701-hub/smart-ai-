import os, requests
from flask import Flask, request, jsonify
from flask_cors import CORS
from dotenv import load_dotenv

load_dotenv()
app = Flask(__name__)
CORS(app)

GROQ_API_KEY = os.getenv("GROQ_API_KEY", "")
CEREBRAS_API_KEY = os.getenv("CEREBRAS_API_KEY", "")
OPENROUTER_API_KEY = os.getenv("OPENROUTER_API_KEY", "")
HF_API_KEY = os.getenv("HUGGINGFACE_API_KEY", "")

PROVIDERS = {
    "groq": {"base": "https://api.groq.com/openai/v1/", "key": GROQ_API_KEY},
    "cerebras": {"base": "https://api.cerebras.ai/v1/", "key": CEREBRAS_API_KEY},
    "openrouter": {"base": "https://openrouter.ai/api/v1/", "key": OPENROUTER_API_KEY},
}

@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok", "service": "ZakyAI Server"})

@app.route("/api/chat", methods=["POST"])
def chat():
    data = request.get_json(force=True)
    provider = PROVIDERS.get(data.get("provider", "groq"))
    if not provider or not provider["key"]:
        return jsonify({"error": "Invalid provider or missing API key"}), 400
    headers = {"Authorization": f"Bearer {provider['key']}", "Content-Type": "application/json"}
    payload = {"model": data.get("model", "llama-3.3-70b-versatile"), "messages": data.get("messages", []), "temperature": data.get("temperature", 0.7)}
    try:
        resp = requests.post(provider["base"] + "chat/completions", json=payload, headers=headers, timeout=60)
        return jsonify(resp.json()), resp.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 502

@app.route("/api/image", methods=["POST"])
def image():
    prompt = request.get_json(force=True).get("prompt", "")
    return jsonify({"image_url": f"https://image.pollinations.ai/prompt/{requests.utils.quote(prompt)}"})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=False)
