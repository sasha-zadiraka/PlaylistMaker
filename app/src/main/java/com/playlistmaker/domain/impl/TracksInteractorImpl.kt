package com.playlistmaker.domain.impl

import com.playlistmaker.domain.api.TracksInteractor
import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.repository.TracksRepository

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(query: String, callback: (List<Track>?) -> Unit) {
        repository.searchTracks(query, callback)
    }
}