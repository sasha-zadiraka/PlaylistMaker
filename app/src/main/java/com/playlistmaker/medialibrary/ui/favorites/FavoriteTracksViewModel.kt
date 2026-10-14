package com.playlistmaker.medialibrary.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.medialibrary.domain.FavoriteTracksInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val _state = MutableStateFlow<FavoriteTracksState>(
        FavoriteTracksState.Loading
    )

    val state: StateFlow<FavoriteTracksState> =
        _state.asStateFlow()

    init {
        observeFavoriteTracks()
    }

    private fun observeFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks()
                .collect { tracks ->
                    _state.value =
                        if (tracks.isEmpty()) {
                            FavoriteTracksState.Empty
                        } else {
                            FavoriteTracksState.Content(tracks)
                        }
                }
        }
    }
}
