package com.example.playlistmaker.db.domain

import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    fun listPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun getCountTracks(playlistId: Long)
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) : Boolean
}