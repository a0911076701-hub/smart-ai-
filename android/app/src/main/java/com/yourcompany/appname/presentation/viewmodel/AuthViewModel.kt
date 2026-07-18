package com.yourcompany.appname.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.appname.data.model.User
import com.yourcompany.appname.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithGoogle(idToken: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.signInWithGoogle(idToken)
                .onSuccess { _uiState.value = AuthUiState.Success(it) }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "فشل تسجيل الدخول") }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.signInWithEmail(email, password)
                .onSuccess { _uiState.value = AuthUiState.Success(it) }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "بيانات الدخول غير صحيحة") }
        }
    }

    fun register(name: String, email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.registerWithEmail(name, email, password)
                .onSuccess { _uiState.value = AuthUiState.Success(it) }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "فشل إنشاء الحساب") }
        }
    }

    fun continueAsGuest() {
        _uiState.value = AuthUiState.Success(
            User(uid = "guest_${System.currentTimeMillis()}", isGuest = true, name = "زائر")
        )
    }
}
