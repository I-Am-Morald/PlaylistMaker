package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.ResponseResult
import com.example.playlistmaker.domain.models.ResponseStatus
import com.example.playlistmaker.domain.models.Track
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(val networkClient: NetworkClient) : TrackRepository {
    override fun searchTracks(expression: String): ResponseResult {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            return ResponseResult(
                status = ResponseStatus.SUCCESS,
                data = (response as TrackSearchResponse).results.map {
                    Track(
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        trackId = it.trackId,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl
                    )
                }
            )
        } else {
            return ResponseResult(
                status = ResponseStatus.ERROR
            )
        }

    }

}