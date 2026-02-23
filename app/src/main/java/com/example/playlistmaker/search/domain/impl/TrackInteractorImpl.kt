package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.ResponseStatus
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>, ResponseStatus>> {
        return trackRepository.searchTracks(expression).map { result ->
            Pair(result.data, result.status)
        }
    }
}