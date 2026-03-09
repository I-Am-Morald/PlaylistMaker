package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.PlaylistInteractor
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.library.ui.fragment.playlistCreate.FragmentState
import kotlinx.coroutines.launch

class PlaylistCreateViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val fragmentState = MutableLiveData<FragmentState>()
    fun getState(): LiveData<FragmentState> = fragmentState

    private lateinit var initPlaylist: Playlist

    fun setFragmentState(playlist: Playlist?) {
        if (playlist != null) {
            initPlaylist = playlist
            fragmentState.value = FragmentState.Edit(playlist)
        } else {
            fragmentState.value = FragmentState.Create
        }
    }

    fun savePlaylist(name: String, description: String?, path: String?) {
        if (fragmentState.value == FragmentState.Create) {
            viewModelScope.launch {
                playlistInteractor.addPlaylist(
                    Playlist(
                        playlistName = name,
                        playlistDescription = description,
                        coverPath = path

                    )
                )
            }
        } else {
            viewModelScope.launch {
                playlistInteractor.updatePlaylist(
                    Playlist(
                        playlistId = initPlaylist.playlistId,
                        playlistName = name,
                        playlistDescription = description,
                        coverPath = if (path.isNullOrEmpty()) initPlaylist.coverPath else path,
                        trackIds = initPlaylist.trackIds,
                        trackCount = initPlaylist.trackCount
                    )
                )
            }
        }
    }
}