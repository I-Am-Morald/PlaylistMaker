package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun getSearchHistory(): MutableList<Track>
    fun addTrackToHistory(tracks: MutableList<Track>, track: Track): MutableList<Track>
    fun clearSearchHistory()
}