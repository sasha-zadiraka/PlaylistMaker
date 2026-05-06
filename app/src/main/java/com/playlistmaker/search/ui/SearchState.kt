package com.playlistmaker.search.ui

import com.playlistmaker.search.domain.models.Track

sealed interface SearchState {

    object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    object Empty : SearchState

    object Error : SearchState

    data class History(
        val tracks: List<Track>
    ) : SearchState

    object NothingFound : SearchState
}