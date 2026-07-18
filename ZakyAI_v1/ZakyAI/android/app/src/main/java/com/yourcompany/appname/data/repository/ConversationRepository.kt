package com.yourcompany.appname.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yourcompany.appname.data.model.Conversation
import com.yourcompany.appname.data.model.Message
import com.yourcompany.appname.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * يدير حفظ/قراءة/حذف المحادثات في Firestore.
 */
@Singleton
class ConversationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection get() = firestore.collection(Constants.COLLECTION_CONVERSATIONS)

    suspend fun getConversationsForUser(userId: String): List<Conversation> =
        collection.whereEqualTo("userId", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .get().await()
            .toObjects(Conversation::class.java)

    suspend fun getConversation(id: String): Conversation? =
        collection.document(id).get().await().toObject(Conversation::class.java)

    /** ينشئ أو يحدّث محادثة، ويحتفظ فقط بآخر MAX_CONTEXT_MESSAGES رسالة كسياق فعلي */
    suspend fun saveConversation(conversation: Conversation) {
        val trimmed = conversation.copy(
            messages = conversation.messages.takeLast(Constants.MAX_CONTEXT_MESSAGES * 2),
            updatedAt = System.currentTimeMillis()
        )
        val id = trimmed.id.ifBlank { collection.document().id }
        collection.document(id).set(trimmed.copy(id = id)).await()
    }

    suspend fun addMessage(conversationId: String, message: Message) {
        val conv = getConversation(conversationId) ?: return
        saveConversation(conv.copy(messages = conv.messages + message))
    }

    suspend fun renameConversation(id: String, newTitle: String) {
        collection.document(id).update("title", newTitle).await()
    }

    suspend fun deleteConversation(id: String) {
        collection.document(id).delete().await()
    }
}
