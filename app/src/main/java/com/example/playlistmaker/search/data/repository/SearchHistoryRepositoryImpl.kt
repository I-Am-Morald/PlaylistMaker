package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.settings.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val sharedPreferencesStorage: SharedPreferencesStorage,
    private val appDatabase: AppDatabase
) :
    SearchHistoryRepository {

    override suspend fun getSearchHistory(): MutableList<Track> {
        val favoriteTracksId = appDatabase.trackDao().getTracksIdList()
        val historyList: MutableList<Track> = sharedPreferencesStorage.getList(SEARCH_HISTORY_KEY)
        historyList.replaceAll { track ->
            track.copy(isFavorite = track.trackId in favoriteTracksId)
        }
        return historyList
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