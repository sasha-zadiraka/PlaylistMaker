package com.playlistmaker.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

/**
 * Обложка трека или плейлиста. Пока картинка грузится, а также если её нет или
 * загрузка не удалась, по центру показывается плейсхолдер [placeholderRes].
 *
 * @param model URL, File или Uri картинки; null — сразу показать плейсхолдер.
 */
@Composable
fun CoverImage(
    model: Any?,
    @DrawableRes placeholderRes: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (model == null) {
            Image(
                painter = painterResource(placeholderRes),
                contentDescription = null,
            )
        } else {
            val placeholder = painterResource(placeholderRes)

            AsyncImage(
                model = model,
                contentDescription = null,
                placeholder = placeholder,
                error = placeholder,
                fallback = placeholder,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}
