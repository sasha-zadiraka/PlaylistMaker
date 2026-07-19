package com.playlistmaker.medialibrary.ui.favorites

import com.playlistmaker.search.domain.models.Track

sealed interface FavoriteTracksState {

    data object Empty : FavoriteTracksState

    data class Content(
        val tracks: List<Track>
    ) : FavoriteTracksState
}