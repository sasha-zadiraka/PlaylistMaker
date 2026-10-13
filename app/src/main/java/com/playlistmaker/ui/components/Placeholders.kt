package com.playlistmaker.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme
import com.playlistmaker.ui.theme.YpBlue

/**
 * Заглушка «картинка + сообщение» для пустых состояний и ошибок.
 * [action] — необязательный элемент под сообщением (например, кнопка «Обновить»).
 */
@Composable
fun PlaceholderMessage(
    @DrawableRes imageRes: Int,
    message: String,
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = topPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )

        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 16.dp, end = 24.dp),
            style = AppTypography.placeholder,
            color = PlaylistMakerTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )

        if (action != null) {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                action()
            }
        }
    }
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier.size(44.dp),
        color = YpBlue,
    )
}
