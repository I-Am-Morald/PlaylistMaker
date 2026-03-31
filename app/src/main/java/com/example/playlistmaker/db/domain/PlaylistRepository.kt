package com.example.playlistmaker.db.domain

import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun getCountTracks(playlistId: Long)
    suspend fun updatePlayList(playlist: Playlist)
    suspend fun getPlaylist(playlistId: Long) : Playlist
    fun listPlaylists(): Flow<List<Playlist>>

    fun getPlaylistTracks(trackIds: List<String>): Flow<List<Track>>
    suspend fun getTracksDuration(trackIds: List<String>): String
    suspend fun addPlaylistTrack(playlist: Playlist, track: Track): Boolean
    suspend fun deletePlaylistTrack(playlist: Playlist, track: Track): Boolean

    suspend fun sharePlaylist(playlist: Playlist)
}