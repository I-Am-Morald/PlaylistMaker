package com.example.playlistmaker.settings.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _darkTheme = MutableLiveData<Boolean>()
    val darkTheme: LiveData<Boolean> = _darkTheme

    init {
        _darkTheme.value = settingsRepository.getThemeValue()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        settingsRepository.setThemeValue(darkThemeEnabled)
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
                settingsRepository = settingsRepository
            ) as T
        }
    }
}