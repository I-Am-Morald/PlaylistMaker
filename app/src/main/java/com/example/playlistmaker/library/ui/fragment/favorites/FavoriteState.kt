package com.example.playlistmaker.library.ui.fragment.favorites

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoriteState {
    object NoData : FavoriteState
    data class FavoriteTracksList(val data: List<Track>) : FavoriteState
}