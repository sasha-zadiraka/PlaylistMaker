package com.playlistmaker.playlist.ui

import com.playlistmaker.playlist.domain.Playlist
import com.playlistmaker.search.domain.models.Track

sealed interface PlaylistInfoState {

    data object Loading : PlaylistInfoState

    data class Content(
        val playlist: Playlist,
        val totalDurationMinutes: Int,
        val tracks: List<Track>
    ) : PlaylistInfoState
}