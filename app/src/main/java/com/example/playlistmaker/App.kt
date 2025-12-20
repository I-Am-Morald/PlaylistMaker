package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.creator.Creator

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        val interactor = Creator.provideSettingsInteractor(this)
        interactor.applyTheme(
            interactor.getThemeValue()
        )
    }
}