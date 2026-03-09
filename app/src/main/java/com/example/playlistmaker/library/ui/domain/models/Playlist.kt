package com.example.playlistmaker.library.ui.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Playlist(
    val playlistId: Long = 0,
    val playlistName: String,
    val playlistDescription: String?,
    val coverPath: String?,
    val trackIds: List<String> = emptyList(),
    val trackCount: Int = 0
) : Parcelable