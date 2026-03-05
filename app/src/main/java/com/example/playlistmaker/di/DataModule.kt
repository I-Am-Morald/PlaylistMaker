package com.example.playlistmaker.di

import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.settings.data.storage.SharedPreferencesStorage
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
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

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}