package com.example.playlistmaker.db.domain

import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun getCountTracks(playlistId: Long)
    suspend fun updatePlayList(playlist: Playlist)
    fun listPlaylists(): Flow<List<Playlist>>

    suspend fun addPlaylistTrack(playlist: Playlist, track: Track): Boolean
    suspend fun getPlaylistTrack(trackId: String)
    suspend fun deletePlaylistTrack(trackId: String)
}