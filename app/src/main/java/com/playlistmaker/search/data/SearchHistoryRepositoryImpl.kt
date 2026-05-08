package com.playlistmaker.search.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.playlistmaker.util.AppConstants.MAX_HISTORY_SIZE
import com.playlistmaker.util.AppConstants.SEARCH_HISTORY_KEY
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.search.domain.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }

        saveHistory(history)
    }

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson<ArrayList<Track>>(json, type) ?: emptyList()
    }

    override fun clearHistory() {
        sharedPreferences.edit {
            remove(SEARCH_HISTORY_KEY)
        }
    }

    private fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit {
            putString(SEARCH_HISTORY_KEY, json)
        }
    }
}