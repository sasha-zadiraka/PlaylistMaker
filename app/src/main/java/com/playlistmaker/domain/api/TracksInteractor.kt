package com.playlistmaker.domain.api

import com.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(query: String, callback: (List<Track>?) -> Unit)
}