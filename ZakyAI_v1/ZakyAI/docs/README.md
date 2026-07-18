# ZakyAI — تعليمات التشغيل

## ⚠️ تنبيه أمني عاجل — اقرأ هذا أولًا

الرسالة الأصلية التي طلبت هذا المشروع تضمّنت مفاتيح API حقيقية لعدة خدمات
(Groq، Hugging Face، Cerebras، OpenRouter) وكلمة مرور المدير، مكتوبة كنص
صريح. **لم أضع أيًا من هذه المفاتيح داخل الأكواد المُولَّدة**، لأن أي مفتاح
يُكتب مباشرة في كود Android يمكن استخراجه بسهولة من ملف APK حتى بدون فك
تشفير حقيقي، وأي مفتاح يظهر في محادثة نصية يجب افتراض أنه مكشوف.

قبل المتابعة، يُرجى:
1. **إبطال (Revoke/Rotate) كل مفتاح API** الذي كان في الرسالة الأصلية من
   لوحة تحكم كل خدمة (Groq, Hugging Face, Cerebras, OpenRouter)، وتوليد
   مفاتيح جديدة.
2. **تغيير كلمة مرور المدير** من القيمة الافتراضية فور أول تشغيل للوحة
   الإدارة (التطبيق ولوحة الويب كلاهما يطلبان ذلك تلقائيًا في أول دخول).
3. عدم مشاركة ملف `google-services.json` أو مفاتيح `.env` علنًا (مثلاً
   في مستودع Git عام)، رغم أن مفتاح Firebase نفسه ليس سرًا حساسًا كباقي
   المفاتيح.

---

## 📁 هيكل المشروع

```
ZakyAI/
├── android/          تطبيق Android (Kotlin + Jetpack Compose)
├── web/dashboard/     لوحة تحكم ويب (HTML/CSS/JS + Firebase)
├── server/            خادم Python وسيط اختياري (FastAPI)
└── docs/README.md     هذا الملف
```

---

## 1) تشغيل تطبيق Android

1. افتح مجلد `android/` في Android Studio (Hedgehog أو أحدث).
2. حمّل ملف `google-services.json` الحقيقي لمشروعك من Firebase Console
   (Project Settings → Your apps) وضعه في `android/app/google-services.json`
   (استخدم `google-services.json.example` كمرجع للشكل فقط).
3. انسخ `android/local.properties.example` إلى `android/local.properties`
   واملأ مفاتيح API **الجديدة** (بعد إبطال القديمة) فيه:
   ```
   GROQ_API_KEY=...
   HUGGINGFACE_API_KEY=...
   CEREBRAS_API_KEY=...
   OPENROUTER_API_KEY=...
   ```
4. فعّل Google Sign-In في Firebase Authentication، وأضف الـ SHA-1 الخاص
   بمفتاح التوقيع في إعدادات مشروع Firebase.
5. اضغط Run. عند أول فتح للوحة الإدارة (بالضغط 5 مرات على رقم الإصدار في
   الإعدادات) سيُطلب منك تعيين كلمة مرور جديدة لأنه لا يوجد هاش محفوظ بعد.

## 2) تشغيل لوحة تحكم الويب

لوحة الويب (`web/dashboard/index.html`) تتصل مباشرة بـ Firestore من
المتصفح، فلا تحتاج بناءً أو خادمًا:

1. افتح الملف مباشرة في المتصفح، أو قدّمه عبر أي خادم ثابت بسيط:
   ```bash
   cd web/dashboard
   python3 -m http.server 8080
   ```
2. تأكد من ضبط **Firestore Security Rules** في Firebase Console بحيث:
   - القراءة/الكتابة على `admin_config` مسموحة فقط عبر منطق مصادقة حقيقي
     (وليس فقط تحقق كلمة مرور من طرف العميل كما هو مطبّق هنا للتبسيط).
   - المستخدمون العاديون لا يمكنهم قراءة محادثات غيرهم.
3. عند أول دخول ستُطلب منك تعيين كلمة مرور المدير (تُخزَّن كـ SHA-256 hash
   فقط في `admin_config/security`، وليس كنص صريح).

## 3) تشغيل الخادم الوسيط Python (اختياري)

استخدمه فقط إن أردت إبقاء مفاتيح API بعيدة تمامًا عن تطبيق Android
(موصى به للإنتاج):

```bash
cd server
pip install -r requirements.txt --break-system-packages
cp .env.example .env   # ثم املأ المفاتيح الجديدة
uvicorn app:app --reload --port 8000
```

بعدها عدّل `AiChatService.kt` في تطبيق Android ليستدعي `http://<server>:8000/chat`
بدلاً من الاتصال المباشر بكل مزود.

---

## 🧩 ملاحظات حول الاكتمال

هذا المشروع أساس عملي متكامل (بنية، مصادقة، دردشة متعددة النماذج، لوحة
إدارة، لوحة ويب) لكنه **ليس تطبيقًا جاهزًا للنشر على المتجر بدون مراجعة**.
أشياء يجب إكمالها قبل الإنتاج:
- ربط Google Sign-In الفعلي داخل `MainActivity.kt` (المكان محدد بتعليق TODO).
- كتابة Firestore Security Rules كاملة (حاليًا التحقق من صلاحيات المدير
  يجري من طرف العميل فقط، وهذا غير كافٍ وحده لبيئة إنتاج).
- إضافة معالجة أفضل للأخطاء وحالات فقدان الاتصال.
- اختبارات وحدة (Unit Tests) للـ Repositories والـ ViewModels.
- أيقونة تطبيق فعلية بدل placeholder افتراضي.
