package com.example.playlistmaker.di
import com.example.playlistmaker.main.ui.view_model.MainViewModel
import com.example.playlistmaker.media_player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainViewModel(get())
    }

    viewModel {
        SettingsViewModel(
            sharingInteractor = get(),
            settingsInteractor = get()
        )
    }

    viewModel {
        SearchViewModel(
            trackInteractor = get(),
            getSearchHistoryUseCase = get(),
            addTrackToSearchHistoryUseCase = get(),
            clearSearchHistoryUseCase = get()
        )
    }

    viewModel {
        MediaPlayerViewModel()
    }


}