package com.playlistmaker.playlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.playlist.domain.PlaylistInteractor
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.sharing.domain.SharingInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _state = MutableStateFlow<PlaylistInfoState>(
        PlaylistInfoState.Loading
    )

    val state: StateFlow<PlaylistInfoState> =
        _state.asStateFlow()

    private var loadPlaylistJob: Job? = null

    fun loadPlaylist(playlistId: Long) {
        loadPlaylistJob?.cancel()

        loadPlaylistJob = viewModelScope.launch {
            val playlist = playlistInteractor
                .getPlaylistById(playlistId)
                .first()
                ?: return@launch

            val tracks = playlistInteractor
                .getTracks(playlist.trackIds)
                .first()

            val durationSum = tracks.sumOf {
                it.trackTimeMillis
            }

            val totalDurationMinutes =
                (durationSum / 60000L).toInt()

            _state.value = PlaylistInfoState.Content(
                playlist = playlist,
                totalDurationMinutes = totalDurationMinutes,
                tracks = tracks
            )
        }
    }

    fun removeTrackFromPlaylist(track: Track) {
        val currentState = _state.value

        if (currentState !is PlaylistInfoState.Content) {
            return
        }

        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(
                trackId = track.trackId,
                playlist = currentState.playlist
            )

            loadPlaylist(currentState.playlist.id)
        }
    }

    fun shareText(text: String) {
        sharingInteractor.shareText(text)
    }

    fun deletePlaylist(onDeleted: () -> Unit) {
        val currentState = _state.value

        if (currentState !is PlaylistInfoState.Content) {
            return
        }

        viewModelScope.launch {
            playlistInteractor.deletePlaylist(currentState.playlist)
            onDeleted()
        }
    }
}