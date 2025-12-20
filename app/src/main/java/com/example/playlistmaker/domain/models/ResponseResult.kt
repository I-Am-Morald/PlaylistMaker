package com.example.playlistmaker.domain.models

data class ResponseResult(
    val data: List<Track> = emptyList(),
    val status: ResponseStatus,
)

enum class ResponseStatus {
    SUCCESS,
    ERROR
}