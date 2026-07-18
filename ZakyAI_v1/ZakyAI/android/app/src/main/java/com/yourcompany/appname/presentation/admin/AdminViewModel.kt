package com.yourcompany.appname.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.appname.data.model.AIModel
import com.yourcompany.appname.data.model.AdminStats
import com.yourcompany.appname.data.model.User
import com.yourcompany.appname.data.repository.AdminRepository
import com.yourcompany.appname.data.repository.ModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val isUnlocked: Boolean = false,
    val isFirstTimeSetup: Boolean = false,
    val passwordError: String? = null,
    val stats: AdminStats = AdminStats(),
    val users: List<User> = emptyList(),
    val models: List<AIModel> = emptyList(),
    val exportedJson: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val modelRepository: ModelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val isFirstTime = adminRepository.isFirstTimeSetup()
            _uiState.update { it.copy(isFirstTimeSetup = isFirstTime) }
        }
    }

    fun submitPassword(password: String) {
        viewModelScope.launch {
            if (_uiState.value.isFirstTimeSetup) {
                adminRepository.setPassword(password)
                unlockAndLoad()
                return@launch
            }
            val ok = adminRepository.verifyPassword(password)
            if (ok) unlockAndLoad()
            else _uiState.update { it.copy(passwordError = "كلمة المرور غير صحيحة") }
        }
    }

    fun changePassword(newPassword: String) {
        viewModelScope.launch { adminRepository.setPassword(newPassword) }
    }

    private fun unlockAndLoad() {
        _uiState.update { it.copy(isUnlocked = true, passwordError = null) }
        refreshStats()
        refreshUsers()
        refreshModels()
    }

    fun refreshStats() = viewModelScope.launch {
        _uiState.update { it.copy(stats = adminRepository.getStats()) }
    }

    fun refreshUsers() = viewModelScope.launch {
        _uiState.update { it.copy(users = adminRepository.getAllUsers()) }
    }

    fun refreshModels() = viewModelScope.launch {
        _uiState.update { it.copy(models = modelRepository.getAllModels()) }
    }

    fun addModel(name: String, apiUrl: String, apiKeyRef: String, type: String) = viewModelScope.launch {
        modelRepository.addModel(
            AIModel(name = name, apiUrl = apiUrl, apiKeyRef = apiKeyRef, type = type, sortOrder = _uiState.value.models.size)
        )
        refreshModels()
    }

    fun deleteModel(id: String) = viewModelScope.launch {
        modelRepository.deleteModel(id)
        refreshModels()
    }

    fun toggleModelActive(id: String, active: Boolean) = viewModelScope.launch {
        modelRepository.setActive(id, active)
        refreshModels()
    }

    fun reorderModel(id: String, newOrder: Int) = viewModelScope.launch {
        modelRepository.updateSortOrder(id, newOrder)
        refreshModels()
    }

    fun exportData() = viewModelScope.launch {
        _uiState.update { it.copy(exportedJson = adminRepository.exportAllDataAsJson()) }
    }

    fun wipeAllData() = viewModelScope.launch {
        adminRepository.wipeAllData()
        refreshStats()
    }
}
