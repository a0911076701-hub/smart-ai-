"""
ZakyAI Server - خادم وسيط لمعالجة طلبات APIs
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import requests
import os
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)
CORS(app)

GROQ_API_KEY = os.getenv("GROQ_API_KEY", "")
HUGGINGFACE_API_KEY = os.getenv("HUGGINGFACE_API_KEY", "")
CEREBRAS_API_KEY = os.getenv("CEREBRAS_API_KEY", "")
OPENROUTER_API_KEY = os.getenv("OPENROUTER_API_KEY", "")

GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"
HUGGINGFACE_URL = "https://api-inference.huggingface.co/models/"
CEREBRAS_URL = "https://api.cerebras.ai/v1/chat/completions"
OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions"

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "ok", "service": "ZakyAI Server", "version": "1.0.0"})

@app.route('/api/groq', methods=['POST'])
def groq_proxy():
    try:
        data = request.json
        headers = {"Authorization": f"Bearer {GROQ_API_KEY}", "Content-Type": "application/json"}
        response = requests.post(GROQ_URL, json=data, headers=headers, timeout=60)
        return jsonify(response.json()), response.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/huggingface/<model>', methods=['POST'])
def huggingface_proxy(model):
    try:
        data = request.json
        headers = {"Authorization": f"Bearer {HUGGINGFACE_API_KEY}", "Content-Type": "application/json"}
        url = f"{HUGGINGFACE_URL}{model}"
        response = requests.post(url, json=data, headers=headers, timeout=120)
        return jsonify(response.json()), response.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/cerebras', methods=['POST'])
def cerebras_proxy():
    try:
        data = request.json
        headers = {"Authorization": f"Bearer {CEREBRAS_API_KEY}", "Content-Type": "application/json"}
        response = requests.post(CEREBRAS_URL, json=data, headers=headers, timeout=60)
        return jsonify(response.json()), response.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/openrouter', methods=['POST'])
def openrouter_proxy():
    try:
        data = request.json
        headers = {
            "Authorization": f"Bearer {OPENROUTER_API_KEY}",
            "Content-Type": "application/json",
            "HTTP-Referer": "https://zakyai.app",
            "X-Title": "ZakyAI"
        }
        response = requests.post(OPENROUTER_URL, json=data, headers=headers, timeout=60)
        return jsonify(response.json()), response.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/image', methods=['POST'])
def generate_image():
    try:
        data = request.json
        prompt = data.get("prompt", "")
        import urllib.parse
        encoded_prompt = urllib.parse.quote(prompt)
        image_url = f"https://image.pollinations.ai/prompt/{encoded_prompt}?width=1024&height=1024&nologo=true"
        return jsonify({"success": True, "url": image_url})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    print("🚀 ZakyAI Server يعمل على المنفذ 5000")
    app.run(host='0.0.0.0', port=5000, debug=True)
