package com.example.playlistmaker

import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Serializable {
    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

    fun getFormattedDate(): String {
        if (releaseDate == null) return ""

        val dfIn = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val dfOut = SimpleDateFormat("yyyy", Locale.getDefault())
        return try {
            dfOut.format(dfIn.parse(releaseDate))
        } catch (e: ParseException) {
            ""
        }
    }
}
