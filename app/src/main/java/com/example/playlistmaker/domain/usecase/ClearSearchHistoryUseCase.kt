package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.SearchHistoryRepository

class ClearSearchHistoryUseCase(
    private val searchHistoryRepository: SearchHistoryRepository
) {

    fun execute() {
        searchHistoryRepository.clearSearchHistory()
    }
}