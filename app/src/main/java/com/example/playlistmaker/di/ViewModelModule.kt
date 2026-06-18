package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.library.ui.view_model.PlaylistCreateViewModel
import com.example.playlistmaker.library.ui.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.root.ui.view_model.RootViewModel
import com.example.playlistmaker.media_player.ui.view_model.MediaPlayerViewModel
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        RootViewModel(get())
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
        MediaPlayerViewModel(get(), get(), get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistCreateViewModel(get())
    }

    viewModel { PlaylistInfoViewModel(get()) }
}