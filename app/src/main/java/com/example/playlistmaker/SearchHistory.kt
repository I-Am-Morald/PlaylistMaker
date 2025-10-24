package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class SearchHistory(val sharedPreferences: SharedPreferences) {

    companion object {
        const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    // чтение
    fun getHistory(): MutableList<Track> {
        //   val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return ArrayList<Track>()
        //   return Gson().fromJson(json, ArrayList<Track>()::class.java)
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return mutableListOf()
        try {
            val array = Gson().fromJson(json, Array<Track>::class.java)
            return array.toMutableList()
        } catch (e: JsonSyntaxException) {
            return mutableListOf()
        }
        //Спасибо, Алиса. Я разберусь, что не так позже.
    }

    // запись
    fun addTrackToHistory(tracks: MutableList<Track>, track: Track): MutableList<Track> {
        tracks.removeAll { it.trackId == track.trackId }
        tracks.add(0, track)
        if (tracks.size > MAX_HISTORY_SIZE) {
            tracks.removeAt(tracks.size - 1)
        }
        val json = Gson().toJson(tracks)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
        return tracks
    }

    // очистка
    fun clearHistory() {
        sharedPreferences.edit()
            .remove(HISTORY_KEY)
            .apply()
    }
}