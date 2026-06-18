package com.example.playlistmaker.settings.ui.view_model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        getThemeValue()
    }

    private fun getThemeValue() {
        _isDarkTheme.value = settingsInteractor.getThemeValue()
    }

    fun switchTheme(darkThemeValue: Boolean) {
        _isDarkTheme.value = darkThemeValue
        applyTheme(darkThemeValue)
        settingsInteractor.setThemeValue(darkThemeValue)
    }

    private fun applyTheme(darkThemeValue: Boolean) {
        settingsInteractor.applyTheme(darkThemeValue)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun supportApp() {
        sharingInteractor.supportApp()
    }

    fun termsApp() {
        sharingInteractor.termsApp()
    }
}