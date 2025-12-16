package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class GetSearchHistoryUseCase(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    fun execute(): MutableList<Track> {
        return searchHistoryRepository.getSearchHistory()
    }
}