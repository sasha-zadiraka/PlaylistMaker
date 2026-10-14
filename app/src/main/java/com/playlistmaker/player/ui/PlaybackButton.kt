package com.playlistmaker.player.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R

/**
 * Кнопка Play/Pause. Пока плеер не подготовлен ([enabled] == false),
 * кнопка не реагирует на нажатия.
 */
@Composable
fun PlaybackButton(
    isPlaying: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconRes = if (isPlaying) R.drawable.ic_pause_83 else R.drawable.ic_play_83

    Image(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = modifier
            .size(83.dp)
            .clip(CircleShape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick,
            ),
    )
}
