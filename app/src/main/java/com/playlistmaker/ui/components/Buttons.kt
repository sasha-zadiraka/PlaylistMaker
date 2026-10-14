package com.playlistmaker.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme
import com.playlistmaker.ui.theme.White

/**
 * Небольшая «таблетка» в инвертированных цветах темы:
 * «Обновить», «Очистить историю», «Новый плейлист».
 */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = PlaylistMakerTheme.colors

    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(54.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.inverseButton,
            contentColor = colors.inverseButtonText,
        ),
        contentPadding = PaddingValues(horizontal = 14.dp),
        elevation = null,
    ) {
        Text(
            text = text,
            style = AppTypography.button,
        )
    }
}

/**
 * Широкая синяя кнопка внизу экрана создания/редактирования плейлиста.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = PlaylistMakerTheme.colors

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.accent,
            contentColor = White,
            disabledContainerColor = colors.inactive,
            disabledContentColor = White,
        ),
        elevation = null,
    ) {
        Text(
            text = text,
            style = AppTypography.button.copy(fontSize = 16.sp),
        )
    }
}
