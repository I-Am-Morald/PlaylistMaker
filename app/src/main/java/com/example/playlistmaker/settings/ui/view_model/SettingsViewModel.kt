package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    fun getThemeValue(): Boolean {
        return settingsInteractor.getThemeValue()
    }

    fun switchTheme(darkThemeValue: Boolean) {
        settingsInteractor.setThemeValue(darkThemeValue)
        applyTheme(darkThemeValue)
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