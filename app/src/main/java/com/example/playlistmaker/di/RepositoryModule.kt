package com.example.playlistmaker.di

import com.example.playlistmaker.db.data.FavoritesRepositoryImpl
import com.example.playlistmaker.db.data.PlaylistRepositoryImpl
import com.example.playlistmaker.db.data.converters.PlaylistTrackDbConvertor
import com.example.playlistmaker.db.data.converters.PlaylistsDbConvertor
import com.example.playlistmaker.db.data.converters.TrackDbConvertor
import com.example.playlistmaker.db.domain.FavoritesRepository
import com.example.playlistmaker.db.domain.PlaylistRepository
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(get(), get())
    }

    factory<TrackRepository> {
        TrackRepositoryImpl(RetrofitNetworkClient(), get())
    }

    factory { TrackDbConvertor() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    factory { PlaylistsDbConvertor(get()) }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get(), get())
    }

    factory { PlaylistTrackDbConvertor() }
}