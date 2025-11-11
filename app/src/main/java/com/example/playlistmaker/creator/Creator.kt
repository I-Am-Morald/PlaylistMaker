package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.usecase.AddTrackToSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.GetThemeSwitcherUseCase
import com.example.playlistmaker.domain.usecase.SetThemeSwitcherUseCase

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

    private fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(provideSharedPreferencesStorage("theme_pref", context))
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

    fun provideGetThemeSwitcherUseCase(context: Context): GetThemeSwitcherUseCase {
        return GetThemeSwitcherUseCase(provideSettingsRepository(context))
    }

    fun provideSetThemeSwitcherUseCase(context: Context): SetThemeSwitcherUseCase {
        return SetThemeSwitcherUseCase(provideSettingsRepository(context))
    }
}