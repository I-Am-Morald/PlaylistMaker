package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    suspend fun getSearchHistory(): MutableList<Track>
    fun addTrackToHistory(tracks: MutableList<Track>, track: Track): MutableList<Track>
    fun clearSearchHistory()
}