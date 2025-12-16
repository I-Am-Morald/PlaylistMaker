package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun getSearchHistory(): MutableList<Track>
    fun addTrackToHistory(tracks: MutableList<Track>, track: Track): MutableList<Track>
    fun clearSearchHistory()
}