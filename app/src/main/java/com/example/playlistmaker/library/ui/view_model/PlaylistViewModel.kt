package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.PlaylistInteractor
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.library.ui.fragment.playlist.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val playlists = MutableLiveData<PlaylistsState>()
    fun getPlaylists(): LiveData<PlaylistsState> = playlists

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

}