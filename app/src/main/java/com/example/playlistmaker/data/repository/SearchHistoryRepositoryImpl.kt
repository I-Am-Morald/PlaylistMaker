package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(private val sharedPreferencesStorage: SharedPreferencesStorage) :
    SearchHistoryRepository {

    override fun getSearchHistory(): MutableList<Track> {
        return sharedPreferencesStorage.getList(SEARCH_HISTORY_KEY)
    }

    override fun addTrackToHistory(tracks: MutableList<Track>, track: Track): MutableList<Track> {
        tracks.removeAll { it.trackId == track.trackId }
        tracks.add(0, track)
        if (tracks.size > MAX_HISTORY_SIZE) {
            tracks.removeAt(tracks.size - 1)
        }
        sharedPreferencesStorage.saveList(SEARCH_HISTORY_KEY, tracks)
        return tracks
    }

    override fun clearSearchHistory() {
        sharedPreferencesStorage.clear(SEARCH_HISTORY_KEY)
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }
}