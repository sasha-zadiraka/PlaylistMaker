package com.playlistmaker.playlist.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _state =
        MutableStateFlow<CreatePlaylistState>(
            CreatePlaylistState.Editing
        )

    val state: StateFlow<CreatePlaylistState> =
        _state.asStateFlow()

    var playlistName: String = ""
        private set

    var playlistDescription: String = ""
        private set

    var selectedCoverUri: Uri? = null
        private set

    val isCreateButtonEnabled: Boolean
        get() = playlistName.isNotBlank()

    val hasUnsavedData: Boolean
        get() = playlistName.isNotBlank() ||
                playlistDescription.isNotBlank() ||
                selectedCoverUri != null

    fun onNameChanged(name: String) {
        playlistName = name
    }

    fun onDescriptionChanged(description: String) {
        playlistDescription = description
    }

    fun onCoverSelected(uri: Uri) {
        selectedCoverUri = uri
    }

    fun createPlaylist() {
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
                interactor.createPlaylist(
                    name = name,
                    description = playlistDescription,
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

    fun resetState() {
        _state.value = CreatePlaylistState.Editing
    }
}