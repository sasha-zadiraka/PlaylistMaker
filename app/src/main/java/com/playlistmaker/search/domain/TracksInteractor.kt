package com.playlistmaker.search.domain

import com.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(query: String, callback: (List<Track>?) -> Unit)
}