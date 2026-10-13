package com.playlistmaker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme
import com.playlistmaker.util.formatTrackTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackItem(
    track: Track,
    onClick: (Track) -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: ((Track) -> Unit)? = null,
) {
    val colors = PlaylistMakerTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 61.dp)
            .combinedClickable(
                onClick = { onClick(track) },
                onLongClick = onLongClick?.let { { it(track) } },
            )
            .padding(start = 13.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverImage(
            model = track.artworkUrl100,
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
                text = track.trackName,
                style = AppTypography.primary,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.padding(top = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = track.artistName,
                    modifier = Modifier.weight(1f, fill = false),
                    style = AppTypography.small,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                FramedIcon(
                    iconRes = R.drawable.ic_dot_13,
                    tint = colors.textSecondary,
                    frameSize = 13.dp,
                )

                Text(
                    text = formatTrackTime(track.trackTimeMillis),
                    style = AppTypography.small,
                    color = colors.textSecondary,
                    maxLines = 1,
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        FramedIcon(
            iconRes = R.drawable.ic_shevron_24,
            tint = colors.textSecondary,
        )
    }
}
