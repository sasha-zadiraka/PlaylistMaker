package com.playlistmaker.search.domain

import com.playlistmaker.search.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}