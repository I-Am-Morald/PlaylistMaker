package com.example.playlistmaker.settings.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

class SharedPreferencesStorage(val context: Context) {

    private val gson = Gson()
    val sharedPreferences = context.getSharedPreferences(APP_SHARED_KEY, Context.MODE_PRIVATE)

    fun <T> getList(key: String): MutableList<T> {
        val json = sharedPreferences.getString(key, null)
        if (json != null) {
            val array = gson.fromJson(json, Array<Track>::class.java)
            return array.toMutableList() as MutableList<T>
        } else {
            return mutableListOf()
        }
    }

    fun <T> saveList(key: String, list: MutableList<T>) {
        val json = gson.toJson(list)
        sharedPreferences.edit()
            .putString(key, json)
            .apply()
    }

    fun clear(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun getBoolean(key: String): Boolean {
        val value = false
        return sharedPreferences.getBoolean(key, value)
    }

    fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key, value)
            .apply()
    }

    companion object {
        private const val APP_SHARED_KEY = "app_prefs"
    }
}