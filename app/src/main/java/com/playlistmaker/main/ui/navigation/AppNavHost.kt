package com.playlistmaker.main.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
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
import com.playlistmaker.search.ui.SearchScreen
import com.playlistmaker.settings.ui.SettingsScreen

private const val SLIDE_DURATION_MILLIS = 300

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MediaLibrary,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable<Screen.Search> { entry ->
            SearchScreen(
                onTrackClick = { track -> entry.navigate(navController, Screen.Player.from(track)) },
            )
        }

        composable<Screen.MediaLibrary> { entry ->
            MediaLibraryScreen(
                onTrackClick = { track -> entry.navigate(navController, Screen.Player.from(track)) },
                onPlaylistClick = { playlistId ->
                    entry.navigate(navController, Screen.PlaylistInfo(playlistId))
                },
                onNewPlaylistClick = { entry.navigate(navController, Screen.CreatePlaylist) },
            )
        }

        composable<Screen.Settings> {
            SettingsScreen()
        }

        composable<Screen.Player>(
            enterTransition = { slideInFromEnd() },
            popExitTransition = { slideOutToEnd() },
        ) { entry ->
            val track = remember(entry) { entry.toRoute<Screen.Player>().toTrack() }

            PlayerScreen(
                track = track,
                onBackClick = { entry.navigateBack(navController) },
                onNewPlaylistClick = { entry.navigate(navController, Screen.CreatePlaylist) },
            )
        }

        composable<Screen.CreatePlaylist> { entry ->
            CreatePlaylistScreen(onBackClick = { entry.navigateBack(navController) })
        }

        composable<Screen.PlaylistInfo> { entry ->
            val route = entry.toRoute<Screen.PlaylistInfo>()

            PlaylistInfoScreen(
                playlistId = route.playlistId,
                onBackClick = { entry.navigateBack(navController) },
                onTrackClick = { track -> entry.navigate(navController, Screen.Player.from(track)) },
                onEditClick = { id -> entry.navigate(navController, Screen.EditPlaylist(id)) },
            )
        }

        composable<Screen.EditPlaylist> { entry ->
            val route = entry.toRoute<Screen.EditPlaylist>()

            EditPlaylistScreen(
                playlistId = route.playlistId,
                onBackClick = { entry.navigateBack(navController) },
            )
        }
    }
}

/**
 * Переходы выполняем, только пока экран, который их инициирует, активен (RESUMED).
 * Во время анимации перехода экран уже не RESUMED, поэтому повторный тап
 * (двойное нажатие «Назад» или по элементу списка) не откроет/не закроет лишний экран.
 */
private fun NavBackStackEntry.navigate(navController: NavHostController, screen: Screen) {
    if (isResumed()) {
        navController.navigate(screen)
    }
}

private fun NavBackStackEntry.navigateBack(navController: NavHostController) {
    if (isResumed()) {
        navController.popBackStack()
    }
}

private fun NavBackStackEntry.isResumed(): Boolean =
    lifecycle.currentState == Lifecycle.State.RESUMED

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
