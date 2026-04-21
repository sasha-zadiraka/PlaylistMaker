package com.playlistmaker.domain.repository

import com.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}