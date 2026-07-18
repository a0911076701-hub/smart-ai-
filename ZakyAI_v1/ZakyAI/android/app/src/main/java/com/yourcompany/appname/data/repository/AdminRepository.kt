package com.yourcompany.appname.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yourcompany.appname.data.model.AdminStats
import com.yourcompany.appname.data.model.User
import com.yourcompany.appname.utils.Constants
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * منطق لوحة الإدارة: إحصائيات، قائمة المستخدمين، وكلمة مرور المدير.
 *
 * ⚠️ ملاحظة أمنية: كلمة مرور المدير لا تُخزَّن أبدًا كنص صريح، بل كـ SHA-256 hash
 * داخل مستند admin_config/security في Firestore. هذا لا يزال أضعف من نظام
 * مصادقة حقيقي (مثل ربط دور admin بحساب Firebase Auth عبر Custom Claims)،
 * لذا يُنصح باعتباره طبقة حماية إضافية فقط للوصول السريع من داخل التطبيق،
 * وليس آلية الأمان الوحيدة للوحة الإدارة في مشروع إنتاجي حقيقي.
 */
@Singleton
class AdminRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val configDoc get() = firestore.collection(Constants.COLLECTION_ADMIN_CONFIG).document("security")

    suspend fun verifyPassword(input: String): Boolean {
        val snapshot = configDoc.get().await()
        val storedHash = snapshot.getString("passwordHash")
        if (storedHash == null) {
            // أول تشغيل: لا يوجد هاش محفوظ بعد، لذلك نطلب من المدير تعيينه أولًا
            return false
        }
        return hash(input) == storedHash
    }

    suspend fun isFirstTimeSetup(): Boolean = !configDoc.get().await().contains("passwordHash")

    suspend fun setPassword(newPassword: String) {
        configDoc.set(mapOf("passwordHash" to hash(newPassword)))
    }

    suspend fun getStats(): AdminStats {
        val usersCount = firestore.collection(Constants.COLLECTION_USERS).get().await().size()
        val convCount = firestore.collection(Constants.COLLECTION_CONVERSATIONS).get().await().size()
        val todayStart = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault())
            .toInstant().toEpochMilli()
        val requestsToday = firestore.collection(Constants.COLLECTION_CONVERSATIONS)
            .whereGreaterThanOrEqualTo("updatedAt", todayStart)
            .get().await().size()
        return AdminStats(usersCount, convCount, requestsToday)
    }

    suspend fun getAllUsers(): List<User> =
        firestore.collection(Constants.COLLECTION_USERS).get().await().toObjects(User::class.java)

    suspend fun exportAllDataAsJson(): String {
        val users = getAllUsers()
        val convSnapshot = firestore.collection(Constants.COLLECTION_CONVERSATIONS).get().await()
        val convJson = convSnapshot.documents.joinToString(",", prefix = "[", postfix = "]") { it.data.toString() }
        val usersJson = users.joinToString(",", prefix = "[", postfix = "]") { it.toString() }
        return """{"users":$usersJson,"conversations":$convJson}"""
    }

    suspend fun wipeAllData() {
        val batch = firestore.batch()
        firestore.collection(Constants.COLLECTION_CONVERSATIONS).get().await().documents.forEach {
            batch.delete(it.reference)
        }
        batch.commit().await()
        // ملاحظة: عمدًا لا نحذف مجموعة "users" هنا لتفادي فقدان الحسابات بالخطأ.
    }

    private fun hash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
