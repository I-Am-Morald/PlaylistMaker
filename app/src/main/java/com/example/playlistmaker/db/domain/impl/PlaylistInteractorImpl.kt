package com.example.playlistmaker.db.domain.impl

import com.example.playlistmaker.db.domain.PlaylistInteractor
import com.example.playlistmaker.db.domain.PlaylistRepository
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlin.String

class PlaylistInteractorImpl(
    private val playlistsRepository: PlaylistRepository
) : PlaylistInteractor {
    override fun listPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.listPlaylists()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistsRepository.addPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistsRepository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistsRepository.updatePlayList(playlist)
    }

    override suspend fun getPlaylist(playlistId: Long): Playlist {
        return playlistsRepository.getPlaylist(playlistId)
    }

    override suspend fun getCountTracks(playlistId: Long) {
        playlistsRepository.getCountTracks(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean {
        return playlistsRepository.addPlaylistTrack(playlist, track)
    }

    override suspend fun getTracksDuration(trackIds: List<String>): String {
        return playlistsRepository.getTracksDuration(trackIds)
    }

    override fun getPlaylistTracks(trackIds: List<String>): Flow<List<Track>> {
        return playlistsRepository.getPlaylistTracks(trackIds)
    }

    override suspend fun deletePlaylistTrack(playlist: Playlist, track: Track): Boolean {
        return playlistsRepository.deletePlaylistTrack(playlist, track)
    }

    override suspend fun sharePlaylist(playlist: Playlist) {
        playlistsRepository.sharePlaylist(playlist)
    }
}