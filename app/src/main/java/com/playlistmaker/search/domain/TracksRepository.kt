package com.playlistmaker.search.domain

import com.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(query: String, callback: (List<Track>?) -> Unit)
}