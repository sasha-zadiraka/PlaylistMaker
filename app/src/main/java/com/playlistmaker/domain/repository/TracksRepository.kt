package com.playlistmaker.domain.repository

import com.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(query: String, callback: (List<Track>?) -> Unit)
}