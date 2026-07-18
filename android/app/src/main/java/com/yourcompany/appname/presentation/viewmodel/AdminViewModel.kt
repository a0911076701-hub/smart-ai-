package com.yourcompany.appname.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.yourcompany.appname.data.model.AIModel
import com.yourcompany.appname.data.model.User
import com.yourcompany.appname.data.repository.ModelRepository
import com.yourcompany.appname.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import javax.inject.Inject

data class AdminUiState(
    val isUnlocked: Boolean = false,
    val error: String? = null,
    val userCount: Int = 0,
    val conversationCount: Int = 0,
    val dailyRequestCount: Int = 0,
    val users: List<User> = emptyList(),
    val models: List<AIModel> = emptyList()
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val modelRepository: ModelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private fun sha256(input: String): String =
        MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun tryUnlock(passwordInput: String) {
        viewModelScope.launch {
            try {
                val doc = firestore.collection(Constants.COLLECTION_ADMIN).document("main").get().await()
                val storedHash = doc.getString("passwordHash")
                if (storedHash == null) {
                    firestore.collection(Constants.COLLECTION_ADMIN).document("main")
                        .set(mapOf("passwordHash" to sha256(passwordInput))).await()
                    _uiState.value = _uiState.value.copy(isUnlocked = true, error = null)
                    loadStats()
                } else if (storedHash == sha256(passwordInput)) {
                    _uiState.value = _uiState.value.copy(isUnlocked = true, error = null)
                    loadStats()
                } else {
                    _uiState.value = _uiState.value.copy(isUnlocked = false, error = "كلمة المرور غير صحيحة")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "خطأ في التحقق")
            }
        }
    }

    fun changePassword(newPassword: String) = viewModelScope.launch {
        firestore.collection(Constants.COLLECTION_ADMIN).document("main")
            .set(mapOf("passwordHash" to sha256(newPassword))).await()
    }

    private fun loadStats() = viewModelScope.launch {
        val users = firestore.collection(Constants.COLLECTION_USERS).get().await().toObjects(User::class.java)
        val conversations = firestore.collection(Constants.COLLECTION_CONVERSATIONS).get().await()
        val models = modelRepository.getModels()
        _uiState.value = _uiState.value.copy(
            users = users,
            userCount = users.size,
            conversationCount = conversations.size(),
            models = models
        )
    }

    fun addModel(name: String, apiUrl: String, apiKeyRef: String, type: String) = viewModelScope.launch {
        modelRepository.addModel(AIModel(name = name, apiUrl = apiUrl, apiKeyRef = apiKeyRef, type = type))
        loadStats()
    }

    fun deleteModel(id: String) = viewModelScope.launch {
        modelRepository.deleteModel(id)
        loadStats()
    }

    fun toggleModel(id: String, active: Boolean) = viewModelScope.launch {
        modelRepository.toggleActive(id, active)
        loadStats()
    }

    fun setDefaultModel(id: String) = viewModelScope.launch {
        modelRepository.setDefault(id)
        loadStats()
    }

    fun clearAllData() = viewModelScope.launch {
        val conversations = firestore.collection(Constants.COLLECTION_CONVERSATIONS).get().await()
        conversations.documents.forEach { it.reference.delete() }
        loadStats()
    }
}
