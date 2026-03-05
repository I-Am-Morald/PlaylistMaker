package com.example.playlistmaker.db.domain.impl

import com.example.playlistmaker.db.domain.FavoritesInteractor
import com.example.playlistmaker.db.domain.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {
    override fun favoriteTrackList(): Flow<List<Track>> {
        return favoritesRepository.favoriteTracksList()
    }

    override suspend fun addFavoriteTrack(track: Track) {
       favoritesRepository.addFavoriteTrack(track)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        favoritesRepository.deleteFavoriteTrack(track)
    }
}