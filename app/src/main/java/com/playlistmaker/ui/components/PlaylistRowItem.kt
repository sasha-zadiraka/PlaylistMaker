package com.playlistmaker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.playlistmaker.playlist.domain.Playlist
import com.playlistmaker.playlist.ui.coverFile
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme

/**
 * Строка «обложка 45dp + название + количество треков».
 * Используется в шторке «Добавить в плейлист» на экране плеера и в меню плейлиста.
 */
@Composable
fun PlaylistRowItem(
    playlist: Playlist,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val colors = PlaylistMakerTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(61.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 13.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverImage(
            model = playlist.coverFile(),
            placeholderRes = R.drawable.ic_cover_placeholder_45,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
        ) {
            Text(
                text = playlist.name,
                style = AppTypography.primary,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = pluralStringResource(
                    R.plurals.screen_playlist_tracks_count,
                    playlist.trackCount,
                    playlist.trackCount,
                ),
                modifier = Modifier.padding(top = 1.dp),
                style = AppTypography.small,
                color = colors.textSecondary,
                maxLines = 1,
            )
        }
    }
}
