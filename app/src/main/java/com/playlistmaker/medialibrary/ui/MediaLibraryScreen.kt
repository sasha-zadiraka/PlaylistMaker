package com.playlistmaker.medialibrary.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.playlistmaker.medialibrary.ui.favorites.FavoriteTracksScreen
import com.playlistmaker.medialibrary.ui.playlists.PlaylistsScreen
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.ui.components.ScreenTitle
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme
import kotlinx.coroutines.launch

private enum class MediaLibraryTab(@StringRes val titleRes: Int) {
    FAVORITE_TRACKS(R.string.screen_media_library_favorite_tracks_tab_title),
    PLAYLISTS(R.string.screen_media_library_playlists_tab_title),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaLibraryScreen(
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (playlistId: Long) -> Unit,
    onNewPlaylistClick: () -> Unit,
) {
    val colors = PlaylistMakerTheme.colors
    val tabs = MediaLibraryTab.entries

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val selectedTabIndex = pagerState.currentPage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding(),
    ) {
        ScreenTitle(text = stringResource(R.string.screen_media_library_title))

        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            containerColor = colors.background,
            contentColor = colors.textPrimary,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        selectedTabIndex = selectedTabIndex,
                        matchContentSize = false,
                    ),
                    height = 2.dp,
                    color = colors.textPrimary,
                )
            },
            divider = {},
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                    selectedContentColor = colors.textPrimary,
                    unselectedContentColor = colors.textPrimary,
                    text = {
                        Text(
                            text = stringResource(tab.titleRes),
                            style = AppTypography.button,
                        )
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (tabs[page]) {
                    MediaLibraryTab.FAVORITE_TRACKS -> FavoriteTracksScreen(
                        onTrackClick = onTrackClick,
                    )

                    MediaLibraryTab.PLAYLISTS -> PlaylistsScreen(
                        onPlaylistClick = onPlaylistClick,
                        onNewPlaylistClick = onNewPlaylistClick,
                    )
                }
            }
        }
    }
}
