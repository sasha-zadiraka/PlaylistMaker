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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        navController.navigate(Screen.Player.createRoute(track))
    }
    val navigateBack: () -> Unit = { navController.popBackStack() }

    NavHost(
        navController = navController,
        startDestination = Screen.MediaLibrary.route,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable(Screen.Search.route) {
            SearchScreen(onTrackClick = openPlayer)
        }

        composable(Screen.MediaLibrary.route) {
            MediaLibraryScreen(
                onTrackClick = openPlayer,
                onPlaylistClick = { playlistId ->
                    navController.navigate(Screen.PlaylistInfo.createRoute(playlistId))
                },
                onNewPlaylistClick = {
                    navController.navigate(Screen.CreatePlaylist.route)
                },
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument(Screen.ARG_TRACK) { type = NavType.StringType }),
            enterTransition = { slideInFromEnd() },
            popExitTransition = { slideOutToEnd() },
        ) { backStackEntry ->
            val track = rememberTrackArgument(backStackEntry)

            PlayerScreen(
                track = track,
                onBackClick = navigateBack,
                onNewPlaylistClick = {
                    navController.navigate(Screen.CreatePlaylist.route)
                },
            )
        }

        composable(Screen.CreatePlaylist.route) {
            CreatePlaylistScreen(onBackClick = navigateBack)
        }

        composable(
            route = Screen.PlaylistInfo.route,
            arguments = listOf(navArgument(Screen.ARG_PLAYLIST_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong(Screen.ARG_PLAYLIST_ID) ?: return@composable

            PlaylistInfoScreen(
                playlistId = playlistId,
                onBackClick = navigateBack,
                onTrackClick = openPlayer,
                onEditClick = { id ->
                    navController.navigate(Screen.EditPlaylist.createRoute(id))
                },
            )
        }

        composable(
            route = Screen.EditPlaylist.route,
            arguments = listOf(navArgument(Screen.ARG_PLAYLIST_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong(Screen.ARG_PLAYLIST_ID) ?: return@composable

            EditPlaylistScreen(
                playlistId = playlistId,
                onBackClick = navigateBack,
            )
        }
    }
}

@Composable
private fun rememberTrackArgument(backStackEntry: NavBackStackEntry): Track {
    val json = backStackEntry.arguments?.getString(Screen.ARG_TRACK).orEmpty()
    return remember(json) { Screen.Player.parseTrack(json) }
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
