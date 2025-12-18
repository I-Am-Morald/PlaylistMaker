package com.example.playlistmaker.media_player.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media_player.activity.MediaPlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerViewModel : ViewModel() {

    private val progressHandler = Handler(Looper.getMainLooper())
    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.DEFAULT
    private var previewUrl: String? = ""

    private val state = MutableLiveData<MediaPlayerState>()
    fun getState(): LiveData<MediaPlayerState> = state

    private fun progressTask() {
        if (playerState == PlayerState.PLAYING) {
            val currentDuration =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            val timerState = MediaPlayerState.Timer(data = currentDuration)
            state.postValue(timerState)
        }
    }

    fun startProgressTask() {
        progressHandler.post(object : Runnable {
            override fun run() {
                progressTask()
                progressHandler.postDelayed(this, UPDATE_INTERVAL)
            }
        })
    }

    fun stopProgressTask() {
        progressHandler.removeCallbacksAndMessages(null)
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
            playerState = PlayerState.PREPARED
            state.value = MediaPlayerState.Complete
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
        startProgressTask()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
        state.value = MediaPlayerState.Paused
        stopProgressTask()
    }

    fun mediaPlayerOnPaused() {
        pausePlayer()
    }

    fun mediaPlayerOnDestroy() {
        mediaPlayer.release()
        stopProgressTask()
    }

    companion object {
        private const val UPDATE_INTERVAL = 500L
    }

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }
}