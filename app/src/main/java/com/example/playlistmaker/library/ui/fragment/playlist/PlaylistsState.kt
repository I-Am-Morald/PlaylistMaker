package com.example.playlistmaker.library.ui.fragment.playlist

import com.example.playlistmaker.library.ui.domain.models.Playlist

sealed interface PlaylistsState {
    object NoData : PlaylistsState
    data class Playlists(val data: List<Playlist>) : PlaylistsState
}