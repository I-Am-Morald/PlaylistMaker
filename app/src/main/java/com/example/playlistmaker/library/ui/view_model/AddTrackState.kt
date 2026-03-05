package com.example.playlistmaker.library.ui.view_model

sealed interface AddTrackState {
    object Error : AddTrackState
    data class AlreadyExist(val playlistName: String) : AddTrackState
    data class Success(val playlistName: String) : AddTrackState
}
