package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.PlaylistInteractor
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.library.ui.fragment.playlist.PlaylistsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val playlists = MutableLiveData<PlaylistsState>()
    fun getPlaylists(): LiveData<PlaylistsState> = playlists

    private val _isClickAllowed = MutableLiveData<Boolean>()
    val isClickAllowed: LiveData<Boolean>
        get() = _isClickAllowed

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.listPlaylists().collect { playlistsList ->
                updatePlaylists(playlistsList)
            }
        }
    }

    private fun updatePlaylists(playlistsList: List<Playlist>) {
        if (playlistsList.isEmpty()) {
            playlists.value = PlaylistsState.NoData
        } else {
            playlists.value = PlaylistsState.Playlists(playlistsList)
        }
    }

    fun refreshPlaylists() {
        loadPlaylists()
    }

    fun clickDebounce() {
        _isClickAllowed.value?.let { isTrue ->
            if (isTrue) {
                _isClickAllowed.postValue(false)
                viewModelScope.launch {
                    delay(CLICK_DEBOUNCE_DELAY)
                    _isClickAllowed.postValue(true)
                }
            }
        }
    }


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}