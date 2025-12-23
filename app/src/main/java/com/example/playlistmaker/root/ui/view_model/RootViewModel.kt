package com.example.playlistmaker.root.ui.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor

class RootViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {
    fun setThemeOnStart(){
        settingsInteractor.applyTheme(
            settingsInteractor.getThemeValue()
        )
    }
}