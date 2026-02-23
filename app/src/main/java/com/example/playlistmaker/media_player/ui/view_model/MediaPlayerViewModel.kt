package com.example.playlistmaker.media_player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_player.ui.fragment.MediaPlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerViewModel(private val mediaPlayer: MediaPlayer) : ViewModel() {

    private var playerState = PlayerState.DEFAULT
    private var previewUrl: String? = ""

    private var timerJob: Job? = null

    private val state = MutableLiveData<MediaPlayerState>()
    fun getState(): LiveData<MediaPlayerState> = state

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerState == PlayerState.PLAYING) {
                delay(UPDATE_INTERVAL)
                state.postValue(MediaPlayerState.Timer(data = getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }

    fun setPreviewUrl(url: String?) {
        previewUrl = url ?: ""
    }
    fun preparePlayer() {
        if (previewUrl.isNullOrEmpty()) {
            return
        }
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
            state.value = MediaPlayerState.Prepared
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerState = PlayerState.PREPARED
            state.value = MediaPlayerState.Complete
            state.value = MediaPlayerState.Timer (data = DEFAULT_DURATION)
        }
    }

    fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }

            else -> Unit
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
        state.value = MediaPlayerState.Playing
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
        state.value = MediaPlayerState.Paused
        timerJob?.cancel()
    }

    fun mediaPlayerOnPaused() {
        pausePlayer()
    }

    fun mediaPlayerOnDestroy() {
        mediaPlayer.release()
    }

    companion object {
        private const val UPDATE_INTERVAL = 300L
        private const val DEFAULT_DURATION = "00:00"
    }

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }
}