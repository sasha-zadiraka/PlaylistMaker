package com.playlistmaker.medialibrary.ui.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.ui.components.PlaceholderMessage
import com.playlistmaker.ui.components.TrackItem
import com.playlistmaker.util.rememberDebouncedClick
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteTracksScreen(
    onTrackClick: (Track) -> Unit,
    viewModel: FavoriteTracksViewModel = koinViewModel(),
) {
    val state by viewModel.observeState().observeAsState()
    val onTrackClickDebounced = rememberDebouncedClick(action = onTrackClick)

    when (val currentState = state) {
        FavoriteTracksState.Empty -> PlaceholderMessage(
            imageRes = R.drawable.ic_empty_state_120,
            message = stringResource(R.string.screen_media_library_empty_tracks_response),
            topPadding = 106.dp,
        )

        is FavoriteTracksState.Content -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
        ) {
            items(items = currentState.tracks, key = { it.trackId }) { track ->
                TrackItem(track = track, onClick = onTrackClickDebounced)
            }
        }

        null -> Unit
    }
}
