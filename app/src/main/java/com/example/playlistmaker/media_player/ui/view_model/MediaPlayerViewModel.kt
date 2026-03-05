package com.example.playlistmaker.media_player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.db.domain.FavoritesInteractor
import com.example.playlistmaker.media_player.ui.fragment.MediaPlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerViewModel(
    private val mediaPlayer: MediaPlayer,
    private val favoritesInteractor: FavoritesInteractor,
    private val appDatabase: AppDatabase
) : ViewModel() {

    private var playerState = PlayerState.DEFAULT
    private var previewUrl: String? = ""

    private var timerJob: Job? = null

    private val mediaState = MutableLiveData<MediaPlayerState>()
    fun getMediaState(): LiveData<MediaPlayerState> = mediaState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    fun setIsFavorite(id: String) {
        viewModelScope.launch {
        val isFavorite = id in appDatabase.trackDao().getTracksIdList()
        _isFavorite.postValue(isFavorite)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerState == PlayerState.PLAYING) {
                delay(UPDATE_INTERVAL)
                mediaState.postValue(MediaPlayerState.Timer(data = getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            ?: "00:00"
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
            mediaState.value = MediaPlayerState.Prepared
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerState = PlayerState.PREPARED
            mediaState.value = MediaPlayerState.Complete
            mediaState.value = MediaPlayerState.Timer(data = DEFAULT_DURATION)
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
        mediaState.value = MediaPlayerState.Playing
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
        mediaState.value = MediaPlayerState.Paused
        timerJob?.cancel()
    }

    fun mediaPlayerOnPaused() {
        if (mediaPlayer.isPlaying) {
            pausePlayer()
        }
    }

    fun releasePlayer() {
        mediaPlayer.release()
    }

    companion object {
        private const val UPDATE_INTERVAL = 300L
        private const val DEFAULT_DURATION = "00:00"
    }

    fun onFavoriteClicked(track: Track) {
        val currentStatus = _isFavorite.value ?: false
        val newFavoriteStatus = !currentStatus
        when (currentStatus) {
            true -> viewModelScope.launch {
                favoritesInteractor.deleteFavoriteTrack(track)
            }

            false -> viewModelScope.launch {
                favoritesInteractor.addFavoriteTrack(track)
            }
        }
        _isFavorite.postValue(newFavoriteStatus)
    }

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }
}