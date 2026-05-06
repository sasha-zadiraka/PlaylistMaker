package com.playlistmaker.search.domain

import com.playlistmaker.search.domain.models.Track

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(query: String, callback: (List<Track>?) -> Unit) {
        repository.searchTracks(query, callback)
    }
}