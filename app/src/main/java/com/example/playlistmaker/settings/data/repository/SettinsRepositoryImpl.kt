package com.example.playlistmaker.settings.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.domain.EmailData

class SettingsRepositoryImpl(private val context: Context, private val sharedPreferencesStorage: SharedPreferencesStorage) :
    SettingsRepository {

    override fun getThemeValue(): Boolean {
        return sharedPreferencesStorage.getBoolean(DARK_THEME_KEY)
    }

    override fun setThemeValue(isDarkTheme: Boolean) {
        sharedPreferencesStorage.setBoolean(DARK_THEME_KEY, isDarkTheme)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun getShareAppLink(): String {
        return context.getString(R.string.course_url)
    }

    override fun getEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.email_subject),
            body = context.getString(R.string.email_text)
        )
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.terms_url)
    }

    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
    }
}