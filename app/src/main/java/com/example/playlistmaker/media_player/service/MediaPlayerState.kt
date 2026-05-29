package com.example.playlistmaker.media_player.service

sealed class MediaPlayerState(
    val isPlayButtonEnabled: Boolean,
    val isButtonPaused: Boolean,
    val progress: String
) {
    class Default : MediaPlayerState(false, false, "00:00")
    class Prepared : MediaPlayerState(true, false, "00:00")
    class Playing(progress: String) : MediaPlayerState(true, true, progress)
    class Paused(progress: String) : MediaPlayerState(true, false, progress)
}