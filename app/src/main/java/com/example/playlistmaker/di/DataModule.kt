package com.example.playlistmaker.di

import com.example.playlistmaker.settings.data.storage.SharedPreferencesStorage
import org.koin.dsl.module


val dataModule = module {

    single {
        SharedPreferencesStorage(get())
    }
}