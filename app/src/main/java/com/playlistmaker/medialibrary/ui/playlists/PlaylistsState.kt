package com.playlistmaker.medialibrary.ui.playlists

import com.playlistmaker.playlist.domain.Playlist

sealed interface PlaylistsState {

    data object Empty : PlaylistsState

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsState
}