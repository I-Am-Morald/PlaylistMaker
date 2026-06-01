package com.example.playlistmaker.media_player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.db.domain.FavoritesInteractor
import com.example.playlistmaker.db.domain.PlaylistInteractor
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.library.ui.view_model.AddTrackState
import com.example.playlistmaker.media_player.domain.MediaPlayerControl
import com.example.playlistmaker.media_player.service.MediaPlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class MediaPlayerViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val mediaState = MutableLiveData<MediaPlayerState>()
    fun getMediaState(): LiveData<MediaPlayerState> = mediaState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    private val _playlistsList = MutableLiveData<List<Playlist>>()
    val playlistsList: LiveData<List<Playlist>>
        get() = _playlistsList

    private var mediaPlayerControl: MediaPlayerControl? = null

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.listPlaylists().collect { playlists ->
                _playlistsList.postValue(playlists)
            }
        }
    }

    fun setMediaPlayerControl(mediaPlayerControl: MediaPlayerControl) {
        this.mediaPlayerControl = mediaPlayerControl

        viewModelScope.launch {
            mediaPlayerControl.getPlayerState().collect {
                mediaState.postValue(it)
            }
        }
    }

    fun removeMediaPlayerControl() {
        mediaPlayerControl = null
    }

    fun startForegroundNotification() {
        mediaPlayerControl?.startForegroundNotification()
    }

    fun stopForegroundNotification() {
        mediaPlayerControl?.stopForegroundNotification()
    }

    fun playbackControl() {
        mediaPlayerControl?.playbackControl()
    }

    fun setIsFavorite(id: String) {
        viewModelScope.launch {
            val isFavorite = id in appDatabase.trackDao().getTracksIdList()
            _isFavorite.postValue(isFavorite)
        }
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

    private var _addTrackStatus = MutableLiveData<AddTrackState>()
    val addTrackStatus: LiveData<AddTrackState>
        get() = _addTrackStatus

    fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        if (track.trackId in playlist.trackIds) {
            _addTrackStatus.value = AddTrackState.AlreadyExist(playlist.playlistName)
            return
        }
        viewModelScope.launch {
            val trackAdded = playlistInteractor.addTrackToPlaylist(playlist, track)
            val result = if (trackAdded) {
                AddTrackState.Success(playlist.playlistName)
            } else {
                AddTrackState.Error
            }
            _addTrackStatus.postValue(result)
            getPlaylists()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerControl = null
    }
}