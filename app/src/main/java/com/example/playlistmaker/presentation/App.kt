package com.example.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.usecase.SetThemeSwitcherUseCase

class App : Application() {

    private lateinit var setThemeSwitcherUseCase: SetThemeSwitcherUseCase

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val getThemeSwitcherUseCase = Creator.provideGetThemeSwitcherUseCase(this)
        setThemeSwitcherUseCase = Creator.provideSetThemeSwitcherUseCase(this)
        darkTheme = getThemeSwitcherUseCase.execute()
        switchTheme(darkTheme)
    }

    fun isDarkThemeEnabled(): Boolean {
        return darkTheme
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
        setThemeSwitcherUseCase.execute(darkTheme)
    }
}