package com.yourcompany.appname.presentation.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.appname.data.model.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private object Keys {
    val MODEL = stringPreferencesKey("selected_model")
    val TEMPERATURE = floatPreferencesKey("temperature")
    val BIO = stringPreferencesKey("user_bio")
    val THEME = stringPreferencesKey("theme")
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    /** عداد الضغط على رقم الإصدار لفتح لوحة الإدارة (5 ضغطات) */
    private var versionTapCount = 0
    private val _adminUnlocked = MutableStateFlow(false)
    val adminUnlocked: StateFlow<Boolean> = _adminUnlocked.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.data.map { prefs ->
                UserSettings(
                    selectedModel = prefs[Keys.MODEL] ?: "groq",
                    temperature = prefs[Keys.TEMPERATURE] ?: 0.7f,
                    userBio = prefs[Keys.BIO] ?: "",
                    theme = prefs[Keys.THEME] ?: "dark"
                )
            }.collect { _settings.value = it }
        }
    }

    fun updateModel(model: String) = save { it[Keys.MODEL] = model }
    fun updateTemperature(value: Float) = save { it[Keys.TEMPERATURE] = value }
    fun updateBio(bio: String) = save { it[Keys.BIO] = bio }
    fun updateTheme(theme: String) = save { it[Keys.THEME] = theme }

    fun resetSettings() = save {
        it[Keys.MODEL] = "groq"
        it[Keys.TEMPERATURE] = 0.7f
        it[Keys.BIO] = ""
        it[Keys.THEME] = "dark"
    }

    fun onVersionTapped() {
        versionTapCount++
        if (versionTapCount >= 5) {
            _adminUnlocked.value = true
            versionTapCount = 0
        }
    }

    private fun save(block: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        viewModelScope.launch { dataStore.edit(block) }
    }
}
