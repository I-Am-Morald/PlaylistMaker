package com.example.playlistmaker.library.ui.fragment.playlistCreate

import com.example.playlistmaker.library.ui.domain.models.Playlist

sealed interface FragmentState {
    object Create : FragmentState
    data class Edit(val data: Playlist) : FragmentState
}