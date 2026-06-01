package com.example.playlistmaker.media_player.domain

import com.example.playlistmaker.media_player.service.MediaPlayerState
import kotlinx.coroutines.flow.StateFlow

interface MediaPlayerControl {
    fun getPlayerState(): StateFlow<MediaPlayerState>
    fun startPlayer()
    fun pausePlayer()
    fun playbackControl()
    fun startForegroundNotification()
    fun stopForegroundNotification()
}