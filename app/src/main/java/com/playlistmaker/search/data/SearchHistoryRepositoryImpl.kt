package com.playlistmaker.search.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.playlistmaker.data.db.AppDatabase
import com.playlistmaker.search.domain.SearchHistoryRepository
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.util.AppConstants.MAX_HISTORY_SIZE
import com.playlistmaker.util.AppConstants.SEARCH_HISTORY_KEY

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val database: AppDatabase
) : SearchHistoryRepository {

    override suspend fun addTrack(track: Track) {
        val history = getSavedHistory().toMutableList()

        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }

        saveHistory(history)
    }

    override suspend fun getHistory(): List<Track> {
        val favoriteTrackIds = database
            .trackDao()
            .getFavoriteTrackIds()

        return getSavedHistory().map { track ->
            track.apply {
                isFavorite = trackId in favoriteTrackIds
            }
        }
    }

    override fun clearHistory() {
        sharedPreferences.edit {
            remove(SEARCH_HISTORY_KEY)
        }
    }

    private fun getSavedHistory(): List<Track> {
        val json = sharedPreferences
            .getString(SEARCH_HISTORY_KEY, null)
            ?: return emptyList()

        val type = object : TypeToken<ArrayList<Track>>() {}.type

        return gson.fromJson<ArrayList<Track>>(json, type)
            ?: emptyList()
    }

    private fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)

        sharedPreferences.edit {
            putString(SEARCH_HISTORY_KEY, json)
        }
    }
}