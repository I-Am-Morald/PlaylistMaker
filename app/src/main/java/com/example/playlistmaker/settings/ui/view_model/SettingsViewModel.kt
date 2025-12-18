package com.example.playlistmaker.settings.ui.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.data.SharingInteractorImpl
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

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val settingsRepository = Creator.provideSettingsRepository(context)
            val sharingInteractor = SharingInteractorImpl(
                externalNavigator = Creator.externalNavigator(context),
                settingsRepository = settingsRepository
            )
            return SettingsViewModel(
                sharingInteractor = sharingInteractor,
                settingsInteractor = SettingsInteractorImpl(settingsRepository)
            ) as T
        }
    }
}