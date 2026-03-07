package com.example.playlistmaker.db.data

import com.example.playlistmaker.db.data.converters.PlaylistTrackDbConvertor
import com.example.playlistmaker.db.data.converters.PlaylistsDbConvertor
import com.example.playlistmaker.db.data.entity.PlaylistEntity
import com.example.playlistmaker.db.domain.PlaylistRepository
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.Long

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistsDbConvertor: PlaylistsDbConvertor,
    private val playlistTrackDbConvertor: PlaylistTrackDbConvertor,
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistsDbConvertor.mapToEntity(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylistById(playlist.playlistId)
    }

    override suspend fun getCountTracks(playlistId: Long) {
        appDatabase.playlistDao().getTrackCountById(playlistId)
    }

    override fun listPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(getConvertedPlaylistEntity(playlists))
    }

    private fun getConvertedPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistsDbConvertor.mapFromEntity(playlist) }
    }

    override suspend fun updatePlayList(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlistsDbConvertor.mapToEntity(playlist))
    }

    override suspend fun addPlaylistTrack(playlist: Playlist, track: Track): Boolean {
        try {
            appDatabase.playlistTrackDao().addTrack(playlistTrackDbConvertor.mapToEntity(track))

            val thisTrackIds = playlist.trackIds.toMutableList()
            thisTrackIds.add(track.trackId)
            val updatedPlaylist = Playlist(
                playlistId = playlist.playlistId,
                playlistName = playlist.playlistName,
                playlistDescription = playlist.playlistDescription,
                coverPath = playlist.coverPath,
                trackIds = thisTrackIds.toList(),
                trackCount = thisTrackIds.size
            )
            appDatabase.playlistDao()
                .updatePlaylist(playlistsDbConvertor.mapToEntity(updatedPlaylist))
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun getPlaylistTrack(trackId: String) {
        //Nothing to do...
    }

    override suspend fun deletePlaylistTrack(trackId: String) {
        //Nothing to do...
    }

}