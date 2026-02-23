package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.dto.NetworkResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): NetworkResponse
}