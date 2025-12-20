package com.example.playlistmaker.search.ui.activity

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    object Loading : SearchState
    object NoResult : SearchState
    object Error : SearchState
    data class SearchHistory(val data: List<Track>) : SearchState
    data class SearchHistoryUpdated(val data: List<Track>) : SearchState
    data class SearchTrackList(val data: List<Track>) : SearchState
}