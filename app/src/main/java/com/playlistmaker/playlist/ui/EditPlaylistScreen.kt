package com.playlistmaker.playlist.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val form by viewModel.form.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val coverPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let(viewModel::onCoverSelected)
    }

    LaunchedEffect(playlistId) {
        viewModel.init(playlistId)
    }

    LaunchedEffect(state) {
        when (state) {
            is CreatePlaylistState.Created -> {
                viewModel.resetState()
                onBackClick()
            }

            is CreatePlaylistState.Error -> {
                Toast.makeText(
                    context,
                    R.string.new_playlist_creation_error,
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.resetState()
            }

            CreatePlaylistState.Editing,
            CreatePlaylistState.Saving -> Unit
        }
    }

    PlaylistEditorContent(
        title = stringResource(R.string.edit_playlist_screen_title),
        saveButtonText = stringResource(R.string.edit_playlist_save_button),
        form = form,
        isSaveButtonEnabled = form.isSaveButtonEnabled && state != CreatePlaylistState.Saving,
        onBackClick = onBackClick,
        onCoverClick = {
            coverPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onNameChange = viewModel::onNameChanged,
        onDescriptionChange = viewModel::onDescriptionChanged,
        onSaveClick = viewModel::createPlaylist,
    )
}
