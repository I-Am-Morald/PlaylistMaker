package com.example.playlistmaker.db.domain.impl

import com.example.playlistmaker.db.domain.PlaylistInteractor
import com.example.playlistmaker.db.domain.PlaylistRepository
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistsRepository: PlaylistRepository
) : PlaylistInteractor {
    override fun listPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.listPlaylists()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistsRepository.addPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistsRepository.deletePlaylist(playlist)
    }

    override suspend fun getCountTracks(playlistId: Long) {
        playlistsRepository.getCountTracks(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean {
        return playlistsRepository.addPlaylistTrack(playlist, track)
    }
}