package com.example.playlistmaker.settings.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {

    override fun getThemeValue(): Boolean {
        return settingsRepository.getThemeValue()
    }

    override fun setThemeValue(isDarkTheme: Boolean) {
        return settingsRepository.setThemeValue(isDarkTheme)
    }

    override fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}