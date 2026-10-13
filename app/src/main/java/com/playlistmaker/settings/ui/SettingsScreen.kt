package com.playlistmaker.settings.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.playlistmaker.ui.components.FramedIcon
import com.playlistmaker.ui.components.ScreenTitle
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme
import org.koin.androidx.compose.koinViewModel

private val SettingsItemHeight = 61.dp

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.observeState().observeAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaylistMakerTheme.colors.background)
            .statusBarsPadding(),
    ) {
        ScreenTitle(text = stringResource(R.string.screen_settings_title))

        Spacer(modifier = Modifier.height(24.dp))

        DarkThemeItem(
            checked = state?.isDarkThemeEnabled ?: false,
            onCheckedChange = viewModel::switchTheme,
        )

        SettingsItem(
            title = stringResource(R.string.screen_settings_share_title),
            iconRes = R.drawable.ic_share_24,
            onClick = viewModel::shareApp,
        )

        SettingsItem(
            title = stringResource(R.string.screen_settings_support_title),
            iconRes = R.drawable.ic_support_24,
            onClick = viewModel::openSupport,
        )

        SettingsItem(
            title = stringResource(R.string.screen_settings_user_agreement_title),
            iconRes = R.drawable.ic_shevron_24,
            onClick = viewModel::openTerms,
        )
    }
}

@Composable
private fun DarkThemeItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = PlaylistMakerTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SettingsItemHeight)
            .clickable { onCheckedChange(!checked) }
            .padding(start = 16.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.screen_settings_dark_theme_title),
            modifier = Modifier.weight(1f),
            style = AppTypography.primary,
            color = colors.textPrimary,
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.switchThumbChecked,
                checkedTrackColor = colors.switchTrackChecked,
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = colors.switchThumbUnchecked,
                uncheckedTrackColor = colors.switchTrackUnchecked,
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
) {
    val colors = PlaylistMakerTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SettingsItemHeight)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = AppTypography.primary,
            color = colors.textPrimary,
        )

        FramedIcon(
            iconRes = iconRes,
            tint = colors.icon,
        )
    }
}
