package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.ResponseResult
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(expression: String): Flow<ResponseResult>
}