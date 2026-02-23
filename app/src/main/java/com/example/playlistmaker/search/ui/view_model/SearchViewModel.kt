package com.example.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.ResponseStatus
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.usecase.AddTrackToSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.search.ui.fragment.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addTrackToSearchHistoryUseCase: AddTrackToSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    private var searchText = ""
    private var searchJob: Job? = null
    private var latestSearchText: String = ""

    private val state = MutableLiveData<SearchState>()
    fun getState(): LiveData<SearchState> = state

    private val _isClickAllowed = MutableLiveData<Boolean>()
    val isClickAllowed: LiveData<Boolean>
        get() = _isClickAllowed

    init {
        loadSearchHistory()
    }

    fun loadSearchHistory() {
        val historyList = getSearchHistoryUseCase.execute()
        state.value = SearchState.SearchHistory(data = historyList)
    }

    fun clearHistory() {
        clearSearchHistoryUseCase.execute()
        loadSearchHistory()
    }

    fun clearSearchQuery() {
        searchJob?.cancel()
    }

    fun addTrackToHistory(track: Track) {
        val historyList = getSearchHistoryUseCase.execute()
        addTrackToSearchHistoryUseCase.execute(historyList, track)
        val historyListUpdated = getSearchHistoryUseCase.execute()
        state.value = SearchState.SearchHistoryUpdated(data = historyListUpdated)
    }

    fun setSearchText(text: String) {
        searchText = text
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

    fun searchDebounce() {
        if (latestSearchText == searchText) {
            return
        }
        latestSearchText = searchText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchQuery(searchText)
        }
    }

    fun searchQuery(query: String) {
        state.value = SearchState.Loading
        viewModelScope.launch {
            trackInteractor
                .searchTracks(query)
                .collect { pair ->
                    when (pair.second) {
                        ResponseStatus.SUCCESS -> {
                            if (pair.first.isNotEmpty()) {
                                val result = SearchState.SearchTrackList(data = pair.first)
                                state.postValue(result)
                            } else {
                                state.postValue(SearchState.NoResult)
                            }
                        }

                        ResponseStatus.ERROR -> {
                            state.postValue(SearchState.Error)
                        }
                    }
                }
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}