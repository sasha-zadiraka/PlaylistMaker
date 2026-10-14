package com.playlistmaker.main.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun AppBottomBar(
    selectedTab: TopLevelDestination,
    onDestinationClick: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = PlaylistMakerTheme.colors

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = colors.bottomSheetHandle,
        )

        BottomAppBar(
            containerColor = colors.background,
            contentColor = colors.navigationUnselected,
            tonalElevation = 0.dp,
            contentPadding = PaddingValues(0.dp),
        ) {
            TopLevelDestination.entries.forEach { destination ->
                NavigationBarItem(
                    selected = destination == selectedTab,
                    onClick = { onDestinationClick(destination) },
                    icon = {
                        Icon(
                            painter = painterResource(destination.iconRes),
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(destination.titleRes),
                            style = AppTypography.label,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.navigationSelected,
                        selectedTextColor = colors.navigationSelected,
                        unselectedIconColor = colors.navigationUnselected,
                        unselectedTextColor = colors.navigationUnselected,
                        indicatorColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}
