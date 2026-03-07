package com.example.playlistmaker.library.ui.view_model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.PlaylistInteractor
import com.example.playlistmaker.library.ui.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistCreateViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    fun addPlaylist(name: String, description: String?, path: String?) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(
                Playlist(
                    playlistName = name,
                    playlistDescription = description,
                    coverPath = path

                )
            )
        }
    }
}