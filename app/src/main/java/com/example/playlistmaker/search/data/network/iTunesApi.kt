package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    @GET("/search?entity=song")
    fun searchTracks(@Query("term") text: String): Call<TrackSearchResponse>

    companion object {
        const val ITUNES_SEARCH_URL = "https://itunes.apple.com"
    }
}