package com.playlistmaker.main.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.playlistmaker.main.ui.navigation.AppBottomBar
import com.playlistmaker.main.ui.navigation.AppNavHost
import com.playlistmaker.main.ui.navigation.TopLevelDestination
import com.playlistmaker.ui.theme.PlaylistMakerTheme

private val topLevelRoutes = TopLevelDestination.entries.map { it.screen.route }.toSet()

@Composable
fun PlaylistMakerApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // WindowInsets.isImeVisible после пересоздания активити (смена темы) может залипнуть в true,
    // поэтому смотрим на фактическую высоту клавиатуры.
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val isBottomBarVisible = currentRoute in topLevelRoutes && !isKeyboardVisible

    Scaffold(
        containerColor = PlaylistMakerTheme.colors.background,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (isBottomBarVisible) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onDestinationClick = { destination ->
                        navController.navigate(destination.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        )
    }
}
