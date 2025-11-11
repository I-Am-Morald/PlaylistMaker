package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.SettingsRepository

class SetThemeSwitcherUseCase(
    private val settingsRepository: SettingsRepository
) {
    fun execute(value: Boolean) {
        return settingsRepository.setThemeValue(value)
    }
}