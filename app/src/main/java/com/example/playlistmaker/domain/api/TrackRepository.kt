package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.ResponseResult

interface TrackRepository {
    fun searchTracks(expression: String): ResponseResult
}