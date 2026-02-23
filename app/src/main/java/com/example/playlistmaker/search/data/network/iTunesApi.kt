package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    @GET("/search?entity=song")
    suspend fun searchTracks(@Query("term") text: String): TrackSearchResponse

    companion object {
        const val ITUNES_SEARCH_URL = "https://itunes.apple.com"
    }
}