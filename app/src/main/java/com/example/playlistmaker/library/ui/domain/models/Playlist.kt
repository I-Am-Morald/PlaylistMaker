package com.example.playlistmaker.library.ui.domain.models

class Playlist(
    val playlistId: Long = 0,
    val playlistName: String,
    val playlistDescription: String?,
    val coverPath: String?,
    val trackIds: List<String> = emptyList(),
    val trackCount: Int = 0
)