package com.example.playlistmaker.db.data

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.playlistmaker.db.data.converters.PlaylistTrackDbConvertor
import com.example.playlistmaker.db.data.converters.PlaylistsDbConvertor
import com.example.playlistmaker.db.data.entity.PlaylistEntity
import com.example.playlistmaker.db.domain.PlaylistRepository
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.Long

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistsDbConvertor: PlaylistsDbConvertor,
    private val playlistTrackDbConvertor: PlaylistTrackDbConvertor,
    private val context: Context
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistsDbConvertor.mapToEntity(playlist))
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        appDatabase.playlistDao().deletePlaylistById(playlistId)
        val deletedTracklist = getPlaylist(playlistId)
        checkDeletedTrackInPlaylists(deletedTracklist.trackIds)
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

    override suspend fun getPlaylist(playlistId: Long): Playlist {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)
        return playlistsDbConvertor.mapFromEntity(playlist)
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

    override fun getPlaylistTracks(trackIds: List<String>): Flow<List<Track>> = flow {
        val playlistTracks = appDatabase.playlistTrackDao().getTracksListById(trackIds)
        emit(playlistTracks.map { track ->
            playlistTrackDbConvertor.mapFromEntity(track)
        }.reversed())
    }

    override suspend fun getTracksDuration(trackIds: List<String>): String {
        var totalDuration: Long = 0
        appDatabase.playlistTrackDao().getTracksDuration(trackIds).forEach { TimeInMillis ->
            totalDuration += TimeInMillis
        }
        val totalMinutes = (totalDuration / 1000 / 60).toInt()
        return formatMinutes(totalMinutes)
    }

    private fun formatMinutes(minutes: Int): String {
        return when {
            minutes == 0 -> "0 минут"
            minutes % 100 in 11..14 -> "$minutes минут"
            minutes % 10 == 1 -> "$minutes минута"
            minutes % 10 in 2..4 -> "$minutes минуты"
            else -> "$minutes минут"
        }
    }

    override suspend fun deletePlaylistTrack(playlist: Playlist, track: Track): Boolean {
        try {
            if (possibleToDelete(track.trackId)) {
                appDatabase.playlistTrackDao().deleteTrackById(track.trackId)
            }

            val thisTrackIds = playlist.trackIds.toMutableList()
            thisTrackIds.remove(track.trackId)
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

    private suspend fun possibleToDelete(trackId: String): Boolean {
        var possible = true
        listPlaylists().collect { playlists ->
            for (playlist in playlists) {
                if (playlist.trackIds.contains(trackId)) {
                    possible = false
                    return@collect
                }
            }
        }
        return possible
    }

    private suspend fun checkDeletedTrackInPlaylists(trackIds: List<String>) {
        trackIds.forEach { trackId ->
            if (possibleToDelete(trackId)) {
                appDatabase.playlistTrackDao().deleteTrackById(trackId)
            }
        }
    }

    override suspend fun sharePlaylist(playlist: Playlist) {
        val textBuilder = StringBuilder().apply {
            appendLine(playlist.playlistName)
            if (!playlist.playlistDescription.isNullOrBlank()) {
                appendLine(playlist.playlistDescription)
            }
            appendLine(formatCount(playlist.trackCount))
        }
        getPlaylistTracks(playlist.trackIds).collect { trackList ->
            trackList.withIndex().forEach { (index, track) ->
                textBuilder.appendLine(
                    "${index + 1}. ${track.artistName} - ${track.trackName} (${
                        SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(track.trackTimeMillis)
                    })"
                )
            }
        }

        createIntent(textBuilder.toString())
    }

    private fun createIntent(text: String) {
        Log.d("IntentText", text)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun formatCount(count: Int): String = when {
        count % 100 in 11..19 -> "$count треков"
        count % 10 == 1 -> "$count трек"
        count % 10 in 2..4 -> "$count трека"
        else -> "$count треков"
    }

}