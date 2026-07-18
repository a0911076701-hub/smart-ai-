# ZakyAI — دليل الإعداد والتشغيل

## ⚠️ أول خطوة: أمّن مفاتيحك
شاركت مفاتيح API حقيقية (Groq, Hugging Face, Cerebras, OpenRouter) وكلمة مرور إدارية في طلب الإنشاء.
تم **استبعادها** من كل الملفات المُولَّدة عمداً واستبدالها بمتغيرات تُقرأ من ملفات محلية غير مرفوعة لـ Git.
**افعل الآن:**
1. اذهب إلى لوحات تحكم Groq / Hugging Face / Cerebras / OpenRouter وأعد توليد (Regenerate) كل مفتاح شاركته سابقاً.
2. غيّر كلمة مرور المدير أول ما تشغّل التطبيق (لوحة الإدارة تخزّن فقط بصمة SHA-256 لها في Firestore، وليس نصاً صريحاً).

---

## 1) تشغيل تطبيق Android

1. افتح مجلد `android/` في Android Studio (Koala أو أحدث).
2. ضع ملف `google-services.json` الخاص بمشروع Firebase الحقيقي داخل `android/app/`.
   (احصل عليه من: Firebase Console → إعدادات المشروع → تطبيقاتك → تنزيل google-services.json)
3. انسخ `local.properties.example` إلى `local.properties` واملأ مفاتيحك الحقيقية فيه.
   هذا الملف مُستثنى من Git تلقائياً عبر `.gitignore`.
4. من Firebase Console فعّل:
   - Authentication → Email/Password و Google Sign-In.
   - Firestore Database (وضع الإنتاج) وطبّق قواعد `firestore.rules` المرفقة في جذر المشروع.
5. اضغط Run ▶ لتشغيل التطبيق على المحاكي أو جهاز حقيقي.

### هيكلة الكود
يتبع المشروع نمط Clean Architecture مبسّط:
- `data/` نماذج البيانات، واجهات Retrofit، والـ Repositories.
- `presentation/` الشاشات (Compose) والـ ViewModels لكل من: Auth, Chat, Settings, Admin.
- `di/` وحدات Hilt لحقن الاعتماديات (Firebase, Retrofit).
- `utils/Constants.kt` جميع الثوابت (تُقرأ المفاتيح من BuildConfig وليس كنص صريح).

### فتح لوحة الإدارة
من داخل التطبيق: الإعدادات ← اضغط على رقم الإصدار 5 مرات ← أدخل كلمة المرور.
أول مرة تُدخل فيها كلمة مرور، تُحفظ بصمتها كإعداد أولي في Firestore.

---

## 2) تشغيل لوحة تحكم الويب

1. افتح `web/dashboard/index.html` وعدّل قسم `firebaseConfig` في أسفل الملف بمعلومات
   مشروع Firebase الحقيقي (Project Settings → Web app → SDK config).
2. افتحه مباشرة في المتصفح، أو قدّمه عبر أي خادم استاتيكي:
   ```bash
   cd web/dashboard
   python3 -m http.server 5500
   ```
3. تأكد أن قواعد Firestore (`firestore.rules`) تسمح فقط للمدير (custom claim `admin: true`)
   بالكتابة على `ai_models` و `admin_config`، وإلا فأي زائر للصفحة سيقدر يعدّل النماذج.

---

## 3) تشغيل الخادم الوسيط Python (اختياري لكن موصى به)

يُستخدم لإخفاء مفاتيح Groq/Cerebras/OpenRouter/HF عن العميل تماماً بدل تضمينها في APK.

```bash
cd server
python3 -m venv venv
source venv/bin/activate   # على Windows: venv\Scripts\activate
pip install -r requirements.txt
cp .env.example .env
# ثم افتح .env وضع مفاتيحك الحقيقية
python app.py
```

سيعمل على `http://localhost:8000`. نقاط النهاية المتاحة:
- `POST /api/chat` — دردشة موحّدة (groq/cerebras/openrouter).
- `POST /api/huggingface` — استدعاء نموذج Hugging Face.
- `POST /api/image` — توليد صورة عبر Pollinations.ai.

إن استخدمت هذا الخادم، غيّر `Constants.kt` في تطبيق Android بحيث يستدعي
`http://YOUR_SERVER/api/chat` بدل استدعاء Groq/Cerebras مباشرة من الجهاز — هذا يمنع
تسريب مفاتيحك حتى لو تم فك حزم APK.

---

## 4) قائمة التحقق الأمنية قبل النشر
- [ ] لم يعد أي مفتاح API حقيقي مكتوباً داخل أي ملف مصدري تم رفعه لـ Git.
- [ ] تم تفعيل Firebase App Check لحماية Firestore من الاستخدام من خارج تطبيقك.
- [ ] تم ضبط custom claim `admin` عبر Firebase Admin SDK لحساب واحد موثوق فقط.
- [ ] `local.properties`, `.env`, `google-services.json` جميعها داخل `.gitignore`.
- [ ] تم تفعيل Minify/ProGuard في بناء الإصدار (release) لتطبيق Android.
