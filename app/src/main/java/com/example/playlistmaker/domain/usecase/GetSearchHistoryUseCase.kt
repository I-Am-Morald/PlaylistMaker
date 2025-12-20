package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class GetSearchHistoryUseCase(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    fun execute(): MutableList<Track> {
        return searchHistoryRepository.getSearchHistory()
    }
}