package com.example.playlistmaker.db.domain

import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    fun listPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getCountTracks(playlistId: Long)
    suspend fun getPlaylist(playlistId: Long) : Playlist
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean
    suspend fun getTracksDuration(trackIds: List<String>): String
    fun getPlaylistTracks(trackIds: List<String>): Flow<List<Track>>
    suspend fun deletePlaylistTrack(playlist: Playlist, track: Track): Boolean

    suspend fun sharePlaylist(playlist: Playlist)
}