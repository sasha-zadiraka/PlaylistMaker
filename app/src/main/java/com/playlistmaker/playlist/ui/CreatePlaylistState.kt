package com.playlistmaker.playlist.ui

sealed interface CreatePlaylistState {

    data object Editing : CreatePlaylistState

    data object Saving : CreatePlaylistState

    data class Created(
        val playlistName: String
    ) : CreatePlaylistState

    data class Error(
        val message: String? = null
    ) : CreatePlaylistState
}