package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.settings.data.storage.SharedPreferencesStorage
import com.google.gson.Gson
import org.koin.dsl.module


val dataModule = module {

    single {
        Gson()
    }

    single {
        SharedPreferencesStorage(get(), get())
    }

    factory {
        MediaPlayer()
    }
}