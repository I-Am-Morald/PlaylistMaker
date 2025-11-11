package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.ResponseStatus
import com.example.playlistmaker.domain.models.Track

interface TrackInteractor {
    fun searchTracks(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>, status: ResponseStatus)
    }
}