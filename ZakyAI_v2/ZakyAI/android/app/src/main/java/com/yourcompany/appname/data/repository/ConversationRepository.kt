package com.yourcompany.appname.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yourcompany.appname.data.model.Conversation
import com.yourcompany.appname.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection get() = firestore.collection(Constants.COLLECTION_CONVERSATIONS)

    suspend fun getConversations(userId: String): List<Conversation> {
        val snapshot = collection
            .whereEqualTo("userId", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.toObjects(Conversation::class.java)
    }

    suspend fun saveConversation(conversation: Conversation) {
        val id = conversation.id.ifEmpty { collection.document().id }
        collection.document(id).set(conversation.copy(id = id)).await()
    }

    suspend fun deleteConversation(id: String) {
        collection.document(id).delete().await()
    }

    suspend fun renameConversation(id: String, newTitle: String) {
        collection.document(id).update("title", newTitle).await()
    }
}
