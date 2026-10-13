package com.playlistmaker.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Иконка в рамке [frameSize] (по макету — 24×24), вектор рисуется в своём
 * собственном размере по центру. Векторы в drawable не квадратные
 * (например, «Поделиться» 16×18, «Ещё» 4×16), поэтому растягивать их
 * через Modifier.size() нельзя — они искажаются.
 */
@Composable
fun FramedIcon(
    @DrawableRes iconRes: Int,
    tint: Color,
    modifier: Modifier = Modifier,
    frameSize: Dp = 24.dp,
) {
    Box(
        modifier = modifier.size(frameSize),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
        )
    }
}
