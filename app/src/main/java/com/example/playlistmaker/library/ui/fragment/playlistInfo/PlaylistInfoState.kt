package com.example.playlistmaker.library.ui.fragment.playlistInfo

import com.example.playlistmaker.search.domain.models.Track

sealed interface PlaylistInfoState {
    object NoData : PlaylistInfoState
    data class PlaylistTrackList(val data: List<Track>) : PlaylistInfoState
}