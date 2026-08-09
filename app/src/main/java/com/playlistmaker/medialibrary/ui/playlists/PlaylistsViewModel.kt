package com.playlistmaker.medialibrary.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableStateFlow<PlaylistsState>(
        PlaylistsState.Empty
    )

    val state: StateFlow<PlaylistsState> =
        _state.asStateFlow()

    private var playlistsJob: Job? = null

    init {
        observePlaylists()
    }

    private fun observePlaylists() {
        playlistsJob?.cancel()

        playlistsJob = viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect { playlists ->
                    _state.value = if (playlists.isEmpty()) {
                        PlaylistsState.Empty
                    } else {
                        PlaylistsState.Content(playlists)
                    }
                }
        }
    }
}