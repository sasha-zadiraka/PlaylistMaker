package com.playlistmaker.medialibrary.ui.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.playlistmaker.playlist.domain.Playlist
import com.playlistmaker.playlist.ui.coverFile
import com.playlistmaker.ui.components.CoverImage
import com.playlistmaker.ui.components.PillButton
import com.playlistmaker.ui.components.PlaceholderMessage
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsScreen(
    onPlaylistClick: (playlistId: Long) -> Unit,
    onNewPlaylistClick: () -> Unit,
    viewModel: PlaylistsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PillButton(
            text = stringResource(R.string.screen_media_library_new_playlist_button),
            onClick = onNewPlaylistClick,
            modifier = Modifier.padding(top = 24.dp),
        )

        when (val currentState = state) {
            PlaylistsState.Empty -> PlaceholderMessage(
                imageRes = R.drawable.ic_empty_state_120,
                message = stringResource(R.string.screen_media_library_empty_playlist_response),
                topPadding = 46.dp,
            )

            is PlaylistsState.Content -> PlaylistsGrid(
                playlists = currentState.playlists,
                onPlaylistClick = onPlaylistClick,
            )
        }
    }
}

@Composable
private fun PlaylistsGrid(
    playlists: List<Playlist>,
    onPlaylistClick: (playlistId: Long) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items = playlists, key = { it.id }) { playlist ->
            PlaylistGridItem(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist.id) },
            )
        }
    }
}

@Composable
private fun PlaylistGridItem(
    playlist: Playlist,
    onClick: () -> Unit,
) {
    val colors = PlaylistMakerTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        CoverImage(
            model = playlist.coverFile(),
            placeholderRes = R.drawable.ic_cover_placeholder_103,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
        )

        Text(
            text = playlist.name,
            modifier = Modifier.padding(top = 4.dp),
            style = AppTypography.label,
            color = colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = pluralStringResource(
                R.plurals.screen_playlist_tracks_count,
                playlist.trackCount,
                playlist.trackCount,
            ),
            style = AppTypography.label,
            color = colors.textPrimary,
        )
    }
}
