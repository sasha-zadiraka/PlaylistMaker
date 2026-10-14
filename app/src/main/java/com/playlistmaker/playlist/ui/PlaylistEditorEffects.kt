package com.playlistmaker.playlist.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.example.playlistmaker.R

/**
 * Возвращает действие «открыть системный выбор фото» для обложки плейлиста.
 */
@Composable
fun rememberCoverPicker(onCoverSelected: (Uri) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let(onCoverSelected)
    }

    return remember(launcher) {
        {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}

/**
 * Реакция на результат сохранения плейлиста, общая для создания и редактирования:
 * при успехе — [onSaved], при ошибке — тост. После обработки состояние сбрасывается.
 */
@Composable
fun PlaylistSaveResultEffect(
    state: CreatePlaylistState,
    onResetState: () -> Unit,
    onSaved: (playlistName: String) -> Unit,
) {
    val context = LocalContext.current
    val currentOnResetState by rememberUpdatedState(onResetState)
    val currentOnSaved by rememberUpdatedState(onSaved)

    LaunchedEffect(state) {
        when (state) {
            is CreatePlaylistState.Created -> {
                currentOnResetState()
                currentOnSaved(state.playlistName)
            }

            is CreatePlaylistState.Error -> {
                Toast.makeText(
                    context,
                    R.string.new_playlist_creation_error,
                    Toast.LENGTH_SHORT
                ).show()

                currentOnResetState()
            }

            CreatePlaylistState.Editing,
            CreatePlaylistState.Saving -> Unit
        }
    }
}
