package com.example.playlistmaker.domain.api

interface SettingsRepository {
    fun getThemeValue(): Boolean
    fun setThemeValue(value: Boolean)
}