package com.yourcompany.appname.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yourcompany.appname.data.model.AIModel
import com.yourcompany.appname.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * إدارة النماذج المضافة من لوحة الإدارة.
 * تُخزَّن بيانات النموذج (الاسم، الرابط، النوع، الحالة) في Firestore.
 * مفاتيح الـ API الفعلية لهذه النماذج المخصصة لا يجب تخزينها في مستند يقرأه العميل مباشرة؛
 * الأفضل تمريرها عبر الخادم الوسيط (server/app.py) الذي يحمل المفتاح في متغيرات بيئة آمنة.
 */
@Singleton
class ModelRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection get() = firestore.collection(Constants.COLLECTION_MODELS)

    suspend fun getModels(): List<AIModel> =
        collection.orderBy("order").get().await().toObjects(AIModel::class.java)

    suspend fun addModel(model: AIModel) {
        val id = collection.document().id
        collection.document(id).set(model.copy(id = id)).await()
    }

    suspend fun deleteModel(id: String) {
        collection.document(id).delete().await()
    }

    suspend fun toggleActive(id: String, isActive: Boolean) {
        collection.document(id).update("isActive", isActive).await()
    }

    suspend fun setDefault(id: String) {
        val all = getModels()
        all.forEach { collection.document(it.id).update("isDefault", it.id == id).await() }
    }
}
