package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.PlaylistInteractor
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.library.ui.fragment.playlistInfo.PlaylistInfoState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private lateinit var gettedPlaylist: Playlist

    private val _currentPlaylist = MutableLiveData<Playlist>()
    val currentPlaylist: LiveData<Playlist>
        get() = _currentPlaylist

    private val tracksList = MutableLiveData<PlaylistInfoState>()
    fun getTracksLists(): LiveData<PlaylistInfoState> = tracksList

    private val _duration = MutableLiveData<String>()
    val duration: LiveData<String>
        get() = _duration

    fun getPlaylistInfo(trackIds: List<String>) {
        loadTracks(trackIds)
        getDuration(trackIds)
    }

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            gettedPlaylist = playlistInteractor.getPlaylist(playlistId)
            getPlaylistInfo(gettedPlaylist.trackIds)
            _currentPlaylist.postValue(gettedPlaylist)
        }
    }

    private fun getDuration(trackIds: List<String>) {
        viewModelScope.launch {
            val trackDuration = playlistInteractor.getTracksDuration(trackIds)
            _duration.postValue(trackDuration)
        }
    }

    private fun loadTracks(trackIds: List<String>) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistTracks(trackIds).collect { playlistsList ->
                updateTracksList(playlistsList)
            }
        }
    }

    private fun updateTracksList(trackList: List<Track>) {
        if (trackList.isNullOrEmpty()) {
            tracksList.postValue(PlaylistInfoState.NoData)
        } else {
            tracksList.postValue(PlaylistInfoState.PlaylistTrackList(trackList))
        }
    }

    fun deleteTrack(playlist: Playlist,  track: Track) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylistTrack(playlist, track)
            loadPlaylist(playlist.playlistId)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId)
        }
    }

    fun sharePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.sharePlaylist(playlist)
        }

    }

}