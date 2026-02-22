package com.example.playlistmaker.media_player.ui.fragment

sealed interface MediaPlayerState {
    object Playing : MediaPlayerState
    object Paused : MediaPlayerState
    object Prepared : MediaPlayerState
    object Complete : MediaPlayerState
    data class Timer(val data: String) : MediaPlayerState
}