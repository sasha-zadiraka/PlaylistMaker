package com.playlistmaker.search.domain

import com.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {

    suspend fun addTrack(track: Track)

    suspend fun getHistory(): List<Track>

    fun clearHistory()
}