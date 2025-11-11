package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val sharedPreferencesStorage: SharedPreferencesStorage) :
    SettingsRepository {
    override fun getThemeValue(): Boolean {
        return sharedPreferencesStorage.getBoolean(DARK_THEME_KEY)
    }

    override fun setThemeValue(value: Boolean) {
        sharedPreferencesStorage.setBoolean(DARK_THEME_KEY, value)
    }

    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
    }
}