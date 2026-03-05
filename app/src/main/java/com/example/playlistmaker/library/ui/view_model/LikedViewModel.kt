package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.FavoritesInteractor
import com.example.playlistmaker.library.ui.fragment.favorites.FavoriteState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LikedViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val favorite = MutableLiveData<FavoriteState>()
    fun getFavorite(): LiveData<FavoriteState> = favorite

    private val _isClickAllowed = MutableLiveData<Boolean>()
    val isClickAllowed: LiveData<Boolean>
        get() = _isClickAllowed

    fun loadFavorites() {
        viewModelScope.launch {
            favoritesInteractor.favoriteTrackList().collect { favoriteList ->
                updateFavoriteState(favoriteList)
            }
        }
    }

    private fun updateFavoriteState(favoriteList: List<Track>) {
        if (favoriteList.isEmpty()) {
            favorite.value = FavoriteState.NoData
        } else {
            favorite.value = FavoriteState.FavoriteTracksList(data = favoriteList)
        }
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