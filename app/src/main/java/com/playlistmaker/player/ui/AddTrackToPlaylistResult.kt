package com.playlistmaker.player.ui

sealed interface AddTrackToPlaylistResult {

    data class Added(
        val playlistName: String
    ) : AddTrackToPlaylistResult

    data class AlreadyAdded(
        val playlistName: String
    ) : AddTrackToPlaylistResult

    data object Error : AddTrackToPlaylistResult
}