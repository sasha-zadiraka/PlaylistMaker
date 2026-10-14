package com.playlistmaker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun BottomSheetHandle(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(top = 8.dp)
            .size(width = 50.dp, height = 4.dp)
            .background(
                color = PlaylistMakerTheme.colors.bottomSheetHandle,
                shape = RoundedCornerShape(2.dp),
            ),
    )
}
