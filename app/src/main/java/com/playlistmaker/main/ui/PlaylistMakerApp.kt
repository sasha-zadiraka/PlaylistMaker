package com.playlistmaker.main.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.playlistmaker.main.ui.navigation.AppBottomBar
import com.playlistmaker.main.ui.navigation.AppNavHost
import com.playlistmaker.main.ui.navigation.TopLevelDestination
import com.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun PlaylistMakerApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val selectedTab = remember(currentDestination) {
        currentDestination?.let(::findTopLevelDestination)
    }

    // Высота клавиатуры меняется на каждом кадре анимации, а нам важен только факт
    // «видна/не видна» — derivedStateOf перезапускает композицию лишь при смене флага.
    // WindowInsets.isImeVisible не подходит: после пересоздания активити (смена темы)
    // он может «залипнуть» в true.
    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime
    val isKeyboardVisible by remember(density, imeInsets) {
        derivedStateOf { imeInsets.getBottom(density) > 0 }
    }

    Scaffold(
        containerColor = PlaylistMakerTheme.colors.background,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (selectedTab != null && !isKeyboardVisible) {
                AppBottomBar(
                    selectedTab = selectedTab,
                    onDestinationClick = { destination ->
                        navController.navigate(destination.screen) {
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

private fun findTopLevelDestination(destination: NavDestination): TopLevelDestination? {
    return TopLevelDestination.entries.firstOrNull { tab ->
        destination.hasRoute(tab.screen::class)
    }
}
