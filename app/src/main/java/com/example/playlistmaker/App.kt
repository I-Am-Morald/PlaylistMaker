package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        const val THEME_PREFERENCES = "theme_pref"
        const val THEME_KEY = "dark_theme"
    }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs =getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        switchTheme(sharedPrefs.getBoolean(THEME_KEY, darkTheme))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        val sharedPrefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(THEME_KEY, darkTheme)
            .apply()
    }
}