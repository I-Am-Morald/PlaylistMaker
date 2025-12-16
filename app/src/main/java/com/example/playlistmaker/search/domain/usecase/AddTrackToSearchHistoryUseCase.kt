package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class AddTrackToSearchHistoryUseCase(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    fun execute(historyList: MutableList<Track>, track: Track): MutableList<Track> {
        return searchHistoryRepository.addTrackToHistory(historyList, track)
    }
}