package com.example.playlistmaker.db.data.converters

import com.example.playlistmaker.db.data.entity.PlaylistEntity
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.google.gson.Gson

class PlaylistsDbConvertor(private val gson: Gson) {

    fun mapToEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.coverPath,
            idToJsonFromList(playlist.trackIds),
            playlist.trackCount
        )
    }

    fun mapFromEntity(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.coverPath,
            idToListFromJson(playlist.trackIdsJson),
            playlist.trackCount
        )
    }

    private fun idToJsonFromList(id: List<String>): String {
        val idsJson: String = gson.toJson(id)
        return idsJson

    }

    private fun idToListFromJson(id: String?): List<String> {
        val idsList: List<String> = try {
            gson.fromJson(id, Array<String>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
        return idsList
    }
}