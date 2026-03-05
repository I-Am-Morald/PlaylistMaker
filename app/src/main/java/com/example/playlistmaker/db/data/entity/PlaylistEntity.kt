package com.example.playlistmaker.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String?,
    val coverPath: String?,
    val trackIdsJson: String = "[]",
    val trackCount: Int
)