package com.playlistmaker.search.domain

import com.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}