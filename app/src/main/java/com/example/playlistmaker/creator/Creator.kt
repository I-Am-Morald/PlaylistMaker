package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.settings.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.domain.usecase.AddTrackToSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.settings.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.domain.ExternalNavigator

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    private fun provideSharedPreferencesStorage(
        shared_key: String,
        context: Context
    ): SharedPreferencesStorage {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            shared_key,
            Context.MODE_PRIVATE
        )
        return SharedPreferencesStorage(sharedPreferences)
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferencesStorage("search_history", context))
    }

    fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context, provideSharedPreferencesStorage("theme_pref", context))
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideGetSearchHistoryUseCase(context: Context): GetSearchHistoryUseCase {
        return GetSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideAddTrackToSearchHistoryUseCase(context: Context): AddTrackToSearchHistoryUseCase {
        return AddTrackToSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideClearSearchHistoryUseCase(context: Context): ClearSearchHistoryUseCase {
        return ClearSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun externalNavigator(context: Context): ExternalNavigator = ExternalNavigatorImpl(context)

 }