package com.playlistmaker.player.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.playlistmaker.playlist.domain.Playlist
import com.playlistmaker.ui.components.BottomSheetHandle
import com.playlistmaker.ui.components.PillButton
import com.playlistmaker.ui.components.PlaylistRowItem
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistBottomSheet(
    visible: Boolean,
    playlists: List<Playlist>,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onNewPlaylistClick: () -> Unit,
) {
    if (!visible) return

    val colors = PlaylistMakerTheme.colors

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = colors.bottomSheet,
        scrimColor = colors.overlay,
        dragHandle = { BottomSheetHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.screen_player_add_to_playlist_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 30.dp, end = 16.dp),
                style = AppTypography.placeholder,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )

            PillButton(
                text = stringResource(R.string.screen_media_library_new_playlist_button),
                onClick = onNewPlaylistClick,
                modifier = Modifier.padding(top = 28.dp),
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
            ) {
                items(items = playlists, key = { it.id }) { playlist ->
                    PlaylistRowItem(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist) },
                    )
                }
            }
        }
    }
}
