package com.example.playlistmaker.di

import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(), get())
    }

    single<TrackRepository>{
        TrackRepositoryImpl(RetrofitNetworkClient())
    }

}