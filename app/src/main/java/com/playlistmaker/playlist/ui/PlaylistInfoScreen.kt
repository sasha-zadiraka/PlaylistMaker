package com.playlistmaker.playlist.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.ui.components.BackButton
import com.playlistmaker.ui.components.BottomSheetHandle
import com.playlistmaker.ui.components.ConfirmDialog
import com.playlistmaker.ui.components.CoverImage
import com.playlistmaker.ui.components.FramedIcon
import com.playlistmaker.ui.components.PlaceholderMessage
import com.playlistmaker.ui.components.PlaylistRowItem
import com.playlistmaker.ui.components.TrackItem
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.Black
import com.playlistmaker.ui.theme.PlaylistMakerTheme
import com.playlistmaker.ui.theme.YpLightGrey
import com.playlistmaker.ui.theme.YsDisplay
import com.playlistmaker.util.formatTrackTime
import com.playlistmaker.util.rememberDebouncedClick
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private val SheetTopGap = 24.dp

private val PlaylistTitleStyle = TextStyle(
    fontFamily = YsDisplay,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 28.sp,
)

private val PlaylistInfoStyle = TextStyle(
    fontFamily = YsDisplay,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 21.sp,
)
private val MinSheetPeekHeight = 120.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistInfoScreen(
    playlistId: Long,
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    onEditClick: (playlistId: Long) -> Unit,
    viewModel: PlaylistInfoViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isMenuVisible by rememberSaveable { mutableStateOf(false) }
    var isDeleteDialogVisible by rememberSaveable { mutableStateOf(false) }
    var trackToRemove by rememberSaveable { mutableStateOf<Track?>(null) }

    val onTrackClickDebounced = rememberDebouncedClick(action = onTrackClick)

    LaunchedEffect(playlistId) {
        viewModel.loadPlaylist(playlistId)
    }

    LightStatusBarEffect()

    val content = state as? PlaylistInfoState.Content

    val sharePlaylist: () -> Unit = {
        if (content != null) {
            if (content.playlist.trackCount == 0) {
                Toast.makeText(context, R.string.screen_playlist_share_empty, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.shareText(buildShareText(context.resources, content))
            }
        }
    }

    val hideMenu: (afterHidden: () -> Unit) -> Unit = { afterHidden ->
        scope.launch { menuSheetState.hide() }.invokeOnCompletion {
            if (!menuSheetState.isVisible) {
                isMenuVisible = false
            }
            afterHidden()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YpLightGrey),
    ) {
        if (content != null) {
            PlaylistInfoContent(
                content = content,
                onBackClick = onBackClick,
                onShareClick = sharePlaylist,
                onMenuClick = { isMenuVisible = true },
                onTrackClick = onTrackClickDebounced,
                onTrackLongClick = { trackToRemove = it },
            )
        } else {
            BackButton(
                onClick = onBackClick,
                tint = Black,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 4.dp, top = 4.dp),
            )
        }
    }

    if (content != null) {
        PlaylistMenuBottomSheet(
            visible = isMenuVisible,
            content = content,
            sheetState = menuSheetState,
            onDismissRequest = { isMenuVisible = false },
            onShareClick = { hideMenu(sharePlaylist) },
            onEditClick = { hideMenu { onEditClick(content.playlist.id) } },
            onDeleteClick = { hideMenu { isDeleteDialogVisible = true } },
        )

        ConfirmDialog(
            visible = isDeleteDialogVisible,
            message = stringResource(R.string.screen_playlist_delete_dialog_message, content.playlist.name),
            confirmText = stringResource(R.string.screen_playlist_remove_track_dialog_positive),
            dismissText = stringResource(R.string.screen_playlist_remove_track_dialog_negative),
            onConfirm = {
                isDeleteDialogVisible = false
                viewModel.deletePlaylist(onDeleted = onBackClick)
            },
            onDismiss = { isDeleteDialogVisible = false },
        )
    }

    ConfirmDialog(
        visible = trackToRemove != null,
        message = stringResource(R.string.screen_playlist_remove_track_dialog_message),
        confirmText = stringResource(R.string.screen_playlist_remove_track_dialog_positive),
        dismissText = stringResource(R.string.screen_playlist_remove_track_dialog_negative),
        onConfirm = {
            trackToRemove?.let(viewModel::removeTrackFromPlaylist)
            trackToRemove = null
        },
        onDismiss = { trackToRemove = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistInfoContent(
    content: PlaylistInfoState.Content,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onMenuClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    onTrackLongClick: (Track) -> Unit,
) {
    val density = LocalDensity.current
    var containerHeightPx by remember { mutableIntStateOf(0) }
    var headerHeightPx by remember { mutableIntStateOf(0) }

    // Шторка с треками начинается на 24dp ниже кнопок «Поделиться» и «Меню».
    // Пока экран и шапка не измерены, шторку не показываем (peek = 0),
    // иначе на первом кадре она закрывала бы почти весь экран.
    val sheetPeekHeight = if (containerHeightPx == 0 || headerHeightPx == 0) {
        0.dp
    } else {
        with(density) { (containerHeightPx - headerHeightPx).toDp() - SheetTopGap }
            .coerceAtLeast(MinSheetPeekHeight)
    }

    // modifier самого BottomSheetScaffold применяется к телу (шапке), поэтому
    // высоту экрана меряем на внешнем контейнере. Отступ под статус-бар задаём здесь,
    // чтобы раскрытая шторка останавливалась под ним, а не заходила под системные иконки.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .onSizeChanged { containerHeightPx = it.height },
    ) {
        BottomSheetScaffold(
            scaffoldState = rememberBottomSheetScaffoldState(),
            sheetPeekHeight = sheetPeekHeight,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetContainerColor = PlaylistMakerTheme.colors.bottomSheet,
            sheetShadowElevation = 0.dp,
            sheetTonalElevation = 0.dp,
            sheetDragHandle = { BottomSheetHandle() },
            containerColor = YpLightGrey,
            sheetContent = {
                PlaylistTracks(
                    tracks = content.tracks,
                    onTrackClick = onTrackClick,
                    onTrackLongClick = onTrackLongClick,
                )
            },
        ) {
            PlaylistHeader(
                content = content,
                onBackClick = onBackClick,
                onShareClick = onShareClick,
                onMenuClick = onMenuClick,
                modifier = Modifier.onSizeChanged { headerHeightPx = it.height },
            )
        }
    }
}

@Composable
private fun PlaylistHeader(
    content: PlaylistInfoState.Content,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val playlist = content.playlist

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp),
        ) {
            CoverImage(
                model = playlist.coverFile(),
                placeholderRes = R.drawable.ic_cover_placeholder_233,
                modifier = Modifier.fillMaxSize(),
            )

            BackButton(
                onClick = onBackClick,
                tint = Black,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
            )
        }

        Text(
            text = playlist.name,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 16.dp),
            style = PlaylistTitleStyle,
            color = Black,
        )

        if (playlist.description.isNotBlank()) {
            Text(
                text = playlist.description,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
                style = PlaylistInfoStyle,
                color = Black,
            )
        }

        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = pluralStringResource(
                    R.plurals.screen_playlist_duration_minutes,
                    content.totalDurationMinutes,
                    content.totalDurationMinutes,
                ),
                style = PlaylistInfoStyle,
                color = Black,
            )

            FramedIcon(
                iconRes = R.drawable.ic_dot_13,
                tint = Black,
                frameSize = 13.dp,
            )

            Text(
                text = pluralStringResource(
                    R.plurals.screen_playlist_tracks_count,
                    playlist.trackCount,
                    playlist.trackCount,
                ),
                style = PlaylistInfoStyle,
                color = Black,
            )
        }

        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeaderIconButton(iconRes = R.drawable.ic_share_24, onClick = onShareClick)
            HeaderIconButton(iconRes = R.drawable.ic_more_24, onClick = onMenuClick)
        }
    }
}

@Composable
private fun HeaderIconButton(
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
) {
    FramedIcon(
        iconRes = iconRes,
        tint = Black,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick),
    )
}

@Composable
private fun PlaylistTracks(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onTrackLongClick: (Track) -> Unit,
) {
    if (tracks.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PlaceholderMessage(
                imageRes = R.drawable.ic_empty_state_120,
                message = stringResource(R.string.screen_playlist_tracks_empty),
                topPadding = 24.dp,
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
    ) {
        items(items = tracks, key = { it.trackId }) { track ->
            TrackItem(
                track = track,
                onClick = onTrackClick,
                onLongClick = onTrackLongClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistMenuBottomSheet(
    visible: Boolean,
    content: PlaylistInfoState.Content,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    if (!visible) return

    val colors = PlaylistMakerTheme.colors

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = colors.bottomSheet,
        scrimColor = colors.overlay,
        dragHandle = { BottomSheetHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 16.dp),
        ) {
            PlaylistRowItem(playlist = content.playlist)

            PlaylistMenuItem(
                text = stringResource(R.string.screen_playlist_menu_share),
                onClick = onShareClick,
            )

            PlaylistMenuItem(
                text = stringResource(R.string.screen_playlist_menu_edit),
                onClick = onEditClick,
            )

            PlaylistMenuItem(
                text = stringResource(R.string.screen_playlist_menu_delete),
                onClick = onDeleteClick,
            )
        }
    }
}

@Composable
private fun PlaylistMenuItem(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = AppTypography.primary,
            color = PlaylistMakerTheme.colors.textPrimary,
            textAlign = TextAlign.Start,
        )
    }
}

/**
 * Фон экрана всегда светло-серый, поэтому на время показа экрана
 * иконки статус-бара делаем тёмными (и возвращаем как было при уходе).
 */
@Composable
private fun LightStatusBarEffect() {
    val view = LocalView.current

    DisposableEffect(view) {
        val window = view.context.findActivity()?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        val wasLight = controller?.isAppearanceLightStatusBars

        controller?.isAppearanceLightStatusBars = true

        onDispose {
            if (wasLight != null) {
                controller?.isAppearanceLightStatusBars = wasLight
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun buildShareText(
    resources: Resources,
    content: PlaylistInfoState.Content,
): String {
    val playlist = content.playlist

    return buildString {
        append(playlist.name)

        if (playlist.description.isNotBlank()) {
            append('\n')
            append(playlist.description)
        }

        append('\n')
        append(resources.getString(R.string.screen_playlist_share_tracks_count, playlist.trackCount))

        content.tracks.forEachIndexed { index, track ->
            append('\n')
            append(index + 1)
            append(". ")
            append(track.artistName)
            append(" - ")
            append(track.trackName)
            append(" (")
            append(formatTrackTime(track.trackTimeMillis))
            append(')')
        }
    }
}
