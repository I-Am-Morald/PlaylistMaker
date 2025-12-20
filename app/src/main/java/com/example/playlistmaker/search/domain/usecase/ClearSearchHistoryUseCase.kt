package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.api.SearchHistoryRepository

class ClearSearchHistoryUseCase(
    private val searchHistoryRepository: SearchHistoryRepository
) {

    fun execute() {
        searchHistoryRepository.clearSearchHistory()
    }
}