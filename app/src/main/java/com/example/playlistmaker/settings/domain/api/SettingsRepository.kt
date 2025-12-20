package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.sharing.domain.EmailData

interface SettingsRepository {
    fun setThemeValue(isDarkTheme: Boolean)
    fun getThemeValue(): Boolean
    fun getShareAppLink(): String
    fun getEmailData(): EmailData
    fun getTermsLink(): String
}