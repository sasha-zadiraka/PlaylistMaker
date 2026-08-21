package com.playlistmaker.playlist.ui

import androidx.lifecycle.viewModelScope
import com.playlistmaker.playlist.domain.Playlist
import com.playlistmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    interactor: PlaylistInteractor
) : CreatePlaylistViewModel(interactor) {

    private val _editingPlaylist =
        MutableStateFlow<Playlist?>(null)

    val editingPlaylist: StateFlow<Playlist?> =
        _editingPlaylist.asStateFlow()

    override val hasUnsavedData: Boolean
        get() = false

    fun init(playlistId: Long) {
        if (_editingPlaylist.value != null) {
            return
        }

        viewModelScope.launch {
            val playlist = interactor
                .getPlaylistById(playlistId)
                .first()
                ?: return@launch

            _editingPlaylist.value = playlist
        }
    }

    override fun createPlaylist() {
        val playlist = _editingPlaylist.value ?: return
        val name = playlistName.trim()

        if (name.isEmpty()) {
            return
        }

        if (_state.value == CreatePlaylistState.Saving) {
            return
        }

        _state.value = CreatePlaylistState.Saving

        viewModelScope.launch {
            runCatching {
                interactor.updatePlaylist(
                    playlist = playlist.copy(
                        name = name,
                        description = playlistDescription
                    ),
                    coverUri = selectedCoverUri
                )
            }
                .onSuccess {
                    _state.value = CreatePlaylistState.Created(
                        playlistName = name
                    )
                }
                .onFailure { throwable ->
                    _state.value = CreatePlaylistState.Error(
                        message = throwable.message
                    )
                }
        }
    }
}
