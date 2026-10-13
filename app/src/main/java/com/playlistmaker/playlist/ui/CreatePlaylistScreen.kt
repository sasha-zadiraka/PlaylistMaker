package com.playlistmaker.playlist.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.playlistmaker.ui.components.ConfirmDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreatePlaylistScreen(
    onBackClick: () -> Unit,
    viewModel: CreatePlaylistViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val form by viewModel.form.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var isExitDialogVisible by rememberSaveable { mutableStateOf(false) }

    val coverPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let(viewModel::onCoverSelected)
    }

    val onBack = {
        if (viewModel.hasUnsavedData) {
            isExitDialogVisible = true
        } else {
            onBackClick()
        }
    }

    BackHandler(onBack = onBack)

    LaunchedEffect(state) {
        when (val currentState = state) {
            is CreatePlaylistState.Created -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.new_playlist_created_toast, currentState.playlistName),
                    Toast.LENGTH_SHORT
                ).show()

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
        title = stringResource(R.string.new_playlist_screen_title),
        saveButtonText = stringResource(R.string.new_playlist_create_button),
        form = form,
        isSaveButtonEnabled = form.isSaveButtonEnabled && state != CreatePlaylistState.Saving,
        onBackClick = onBack,
        onCoverClick = {
            coverPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onNameChange = viewModel::onNameChanged,
        onDescriptionChange = viewModel::onDescriptionChanged,
        onSaveClick = viewModel::createPlaylist,
    )

    ConfirmDialog(
        visible = isExitDialogVisible,
        title = stringResource(R.string.new_playlist_exit_dialog_title),
        message = stringResource(R.string.new_playlist_exit_dialog_message),
        confirmText = stringResource(R.string.new_playlist_exit_dialog_finish),
        dismissText = stringResource(R.string.new_playlist_exit_dialog_cancel),
        onConfirm = {
            isExitDialogVisible = false
            onBackClick()
        },
        onDismiss = { isExitDialogVisible = false },
    )
}
