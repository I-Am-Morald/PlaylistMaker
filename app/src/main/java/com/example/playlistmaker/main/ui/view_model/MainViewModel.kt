package com.example.playlistmaker.main.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class MainViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel()  {

    val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme
    init {
        _isDarkTheme.value = settingsRepository.getThemeValue()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(settingsRepository = Creator.provideSettingsRepository(context)) as T
        }
    }
}