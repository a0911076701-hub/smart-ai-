package com.yourcompany.appname.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yourcompany.appname.data.model.AIModel
import com.yourcompany.appname.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * يدير قائمة نماذج الذكاء الاصطناعي القابلة للإضافة/الحذف/التفعيل من لوحة الإدارة.
 * ملاحظة: apiKeyRef هو مرجع فقط (اسم متغير بيئة)، ولا يُخزَّن أي مفتاح API
 * كنص صريح في Firestore.
 */
@Singleton
class ModelRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection get() = firestore.collection(Constants.COLLECTION_MODELS)

    suspend fun getAllModels(): List<AIModel> =
        collection.orderBy("sortOrder").get().await().toObjects(AIModel::class.java)

    suspend fun getActiveModels(): List<AIModel> =
        getAllModels().filter { it.isActive }

    suspend fun addModel(model: AIModel) {
        val id = model.id.ifBlank { collection.document().id }
        collection.document(id).set(model.copy(id = id)).await()
    }

    suspend fun deleteModel(id: String) {
        collection.document(id).delete().await()
    }

    suspend fun setActive(id: String, active: Boolean) {
        collection.document(id).update("isActive", active).await()
    }

    suspend fun updateSortOrder(id: String, order: Int) {
        collection.document(id).update("sortOrder", order).await()
    }
}
