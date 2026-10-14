package com.playlistmaker.playlist.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditPlaylistScreen(
    playlistId: Long,
    onBackClick: () -> Unit,
    viewModel: EditPlaylistViewModel = koinViewModel(),
) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val openCoverPicker = rememberCoverPicker(onCoverSelected = viewModel::onCoverSelected)

    LaunchedEffect(playlistId) {
        viewModel.init(playlistId)
    }

    PlaylistSaveResultEffect(
        state = state,
        onResetState = viewModel::resetState,
        onSaved = { onBackClick() },
    )

    PlaylistEditorContent(
        title = stringResource(R.string.edit_playlist_screen_title),
        saveButtonText = stringResource(R.string.edit_playlist_save_button),
        form = form,
        isSaveButtonEnabled = form.isSaveButtonEnabled && state != CreatePlaylistState.Saving,
        onBackClick = onBackClick,
        onCoverClick = openCoverPicker,
        onNameChange = viewModel::onNameChanged,
        onDescriptionChange = viewModel::onDescriptionChanged,
        onSaveClick = viewModel::createPlaylist,
    )
}
