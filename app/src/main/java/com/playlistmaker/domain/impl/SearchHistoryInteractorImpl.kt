package com.playlistmaker.domain.impl

import com.playlistmaker.domain.api.SearchHistoryInteractor
import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}