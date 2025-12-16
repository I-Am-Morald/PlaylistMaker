package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.models.Track

class GetThemeSwitcherUseCase(
    private val settingsRepository: SettingsRepository
) {
    fun execute(): Boolean {
        return settingsRepository.getThemeValue()
    }
}