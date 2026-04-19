package com.playlistmaker.domain.api

import com.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}