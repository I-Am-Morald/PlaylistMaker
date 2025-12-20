package com.example.playlistmaker.settings.domain

interface SettingsInteractor {
    fun getThemeValue(): Boolean
    fun setThemeValue(isDarkTheme: Boolean)
    fun applyTheme(isDarkTheme: Boolean)
}