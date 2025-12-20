package com.example.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.ResponseStatus
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.usecase.AddTrackToSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.search.ui.activity.SearchState

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addTrackToSearchHistoryUseCase: AddTrackToSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    private val state = MutableLiveData<SearchState>()
    fun getState(): LiveData<SearchState> = state

    init {
        loadSearchHistory()
    }

    fun loadSearchHistory() {
        val historyList = getSearchHistoryUseCase.execute()
        state.value = SearchState.SearchHistory(data = historyList)
    }

    fun clearHistory() {
        clearSearchHistoryUseCase.execute()
    }

    fun addTrackToHistory(track: Track) {
        val historyList = getSearchHistoryUseCase.execute()
        addTrackToSearchHistoryUseCase.execute(historyList, track)
        val historyListUpdated = getSearchHistoryUseCase.execute()
        state.value = SearchState.SearchHistoryUpdated(data = historyListUpdated)
    }

    fun searchQuery(query: String) {
        state.value = SearchState.Loading
        trackInteractor.searchTracks(
            query,
            object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>, status: ResponseStatus) {
                    when (status) {
                        ResponseStatus.SUCCESS -> {
                            if (foundTracks.isNotEmpty()) {
                                val result = SearchState.SearchTrackList(data = foundTracks)
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


            })
    }

}