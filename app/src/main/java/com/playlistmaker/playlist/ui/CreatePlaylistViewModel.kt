package com.playlistmaker.playlist.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    protected val interactor: PlaylistInteractor
) : ViewModel() {

    protected val _state =
        MutableStateFlow<CreatePlaylistState>(
            CreatePlaylistState.Editing
        )

    val state: StateFlow<CreatePlaylistState> =
        _state.asStateFlow()

    protected val _form = MutableStateFlow(PlaylistFormState())

    val form: StateFlow<PlaylistFormState> =
        _form.asStateFlow()

    val playlistName: String
        get() = _form.value.name

    val playlistDescription: String
        get() = _form.value.description

    val selectedCoverUri: Uri?
        get() = _form.value.coverUri

    open val hasUnsavedData: Boolean
        get() = playlistName.isNotBlank() ||
                playlistDescription.isNotBlank() ||
                selectedCoverUri != null

    fun onNameChanged(name: String) {
        _form.update { it.copy(name = name) }
    }

    fun onDescriptionChanged(description: String) {
        _form.update { it.copy(description = description) }
    }

    fun onCoverSelected(uri: Uri) {
        _form.update { it.copy(coverUri = uri) }
    }

    open fun createPlaylist() {
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