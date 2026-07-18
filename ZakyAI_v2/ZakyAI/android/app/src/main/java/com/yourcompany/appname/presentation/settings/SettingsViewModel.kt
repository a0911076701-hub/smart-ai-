package com.yourcompany.appname.presentation.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.appname.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

data class SettingsUiState(
    val selectedModel: String = "groq",
    val temperature: Float = Constants.DEFAULT_TEMPERATURE,
    val bio: String = "",
    val theme: String = "dark", // dark, light, neon, blue, purple
    val versionTapCount: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _uiState.value = _uiState.value.copy(
                    selectedModel = prefs[stringPreferencesKey(Constants.KEY_SELECTED_MODEL)] ?: "groq",
                    temperature = prefs[floatPreferencesKey(Constants.KEY_TEMPERATURE)] ?: Constants.DEFAULT_TEMPERATURE,
                    bio = prefs[stringPreferencesKey(Constants.KEY_USER_BIO)] ?: "",
                    theme = prefs[stringPreferencesKey(Constants.KEY_THEME)] ?: "dark"
                )
            }
        }
    }

    fun setModel(model: String) = viewModelScope.launch {
        context.dataStore.edit { it[stringPreferencesKey(Constants.KEY_SELECTED_MODEL)] = model }
    }

    fun setTemperature(value: Float) = viewModelScope.launch {
        context.dataStore.edit { it[floatPreferencesKey(Constants.KEY_TEMPERATURE)] = value }
    }

    fun setBio(bio: String) = viewModelScope.launch {
        context.dataStore.edit { it[stringPreferencesKey(Constants.KEY_USER_BIO)] = bio }
    }

    fun setTheme(theme: String) = viewModelScope.launch {
        context.dataStore.edit { it[stringPreferencesKey(Constants.KEY_THEME)] = theme }
    }

    fun resetSettings() = viewModelScope.launch {
        context.dataStore.edit { it.clear() }
    }

    /** يُستدعى عند الضغط على رقم الإصدار؛ بعد 5 ضغطات يمكن فتح لوحة الإدارة */
    fun onVersionTapped(): Boolean {
        val newCount = _uiState.value.versionTapCount + 1
        _uiState.value = _uiState.value.copy(versionTapCount = newCount)
        return newCount >= Constants.ADMIN_PANEL_TRIGGER_TAPS
    }
}
