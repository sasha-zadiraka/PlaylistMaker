package com.playlistmaker.search.domain

import com.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(query: String): Flow<List<Track>?> {
        return repository.searchTracks(query)
    }
}