package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.NetworkResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.repository.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun doRequest(dto: Any): NetworkResponse {
        return withContext(Dispatchers.IO) {
            try {
                if (dto is TrackSearchRequest) {
                    val resp = api.searchTracks(dto.expression)

                    resp.apply { resultCode = 200 }
                } else {
                    NetworkResponse().apply { resultCode = 400 }
                }
            } catch (exception: HttpException) {
                NetworkResponse().apply { resultCode = exception.code() }
            } catch (e: Exception) {
                NetworkResponse().apply { resultCode = 400 }

            }
        }
    }
}