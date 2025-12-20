package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.NetworkResponse
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.repository.NetworkClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    private val client: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(iTunesApi.ITUNES_SEARCH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: iTunesApi by lazy {
        client.create(iTunesApi::class.java)
    }

    override fun doRequest(dto: Any): NetworkResponse {
        try {
            if (dto is TrackSearchRequest) {
                val resp = api.searchTracks(dto.expression).execute()

                val body = resp.body() ?: NetworkResponse()

                return body.apply { resultCode = resp.code() }
            } else {
                return NetworkResponse().apply { resultCode = 400 }
            }
        } catch (exception: HttpException) {
            return NetworkResponse().apply { resultCode = exception.code() }
        } catch (e: Exception) {
            return NetworkResponse().apply { resultCode = 400 }

        }
    }
}