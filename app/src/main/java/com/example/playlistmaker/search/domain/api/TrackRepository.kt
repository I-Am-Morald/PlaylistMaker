package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.ResponseResult

interface TrackRepository {
    fun searchTracks(expression: String): ResponseResult
}