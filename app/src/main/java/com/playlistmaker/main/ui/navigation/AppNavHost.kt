package com.playlistmaker.main.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.playlistmaker.medialibrary.ui.MediaLibraryScreen
import com.playlistmaker.player.ui.PlayerScreen
import com.playlistmaker.playlist.ui.CreatePlaylistScreen
import com.playlistmaker.playlist.ui.EditPlaylistScreen
import com.playlistmaker.playlist.ui.PlaylistInfoScreen
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.search.ui.SearchScreen
import com.playlistmaker.settings.ui.SettingsScreen

private const val SLIDE_DURATION_MILLIS = 300

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val openPlayer: (Track) -> Unit = { track ->
        navController.navigate(Screen.Player.from(track))
    }
    val navigateBack: () -> Unit = { navController.popBackStack() }

    NavHost(
        navController = navController,
        startDestination = Screen.MediaLibrary,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable<Screen.Search> {
            SearchScreen(onTrackClick = openPlayer)
        }

        composable<Screen.MediaLibrary> {
            MediaLibraryScreen(
                onTrackClick = openPlayer,
                onPlaylistClick = { playlistId ->
                    navController.navigate(Screen.PlaylistInfo(playlistId))
                },
                onNewPlaylistClick = {
                    navController.navigate(Screen.CreatePlaylist)
                },
            )
        }

        composable<Screen.Settings> {
            SettingsScreen()
        }

        composable<Screen.Player>(
            enterTransition = { slideInFromEnd() },
            popExitTransition = { slideOutToEnd() },
        ) { backStackEntry ->
            val track = remember(backStackEntry) {
                backStackEntry.toRoute<Screen.Player>().toTrack()
            }

            PlayerScreen(
                track = track,
                onBackClick = navigateBack,
                onNewPlaylistClick = {
                    navController.navigate(Screen.CreatePlaylist)
                },
            )
        }

        composable<Screen.CreatePlaylist> {
            CreatePlaylistScreen(onBackClick = navigateBack)
        }

        composable<Screen.PlaylistInfo> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.PlaylistInfo>()

            PlaylistInfoScreen(
                playlistId = route.playlistId,
                onBackClick = navigateBack,
                onTrackClick = openPlayer,
                onEditClick = { id ->
                    navController.navigate(Screen.EditPlaylist(id))
                },
            )
        }

        composable<Screen.EditPlaylist> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.EditPlaylist>()

            EditPlaylistScreen(
                playlistId = route.playlistId,
                onBackClick = navigateBack,
            )
        }
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInFromEnd(): EnterTransition =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(SLIDE_DURATION_MILLIS),
    )

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutToEnd(): ExitTransition =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(SLIDE_DURATION_MILLIS),
    )
