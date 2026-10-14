package com.playlistmaker.player.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.playlistmaker.R
import com.playlistmaker.player.service.PlaybackService
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.search.domain.models.getCoverArtwork
import com.playlistmaker.ui.components.BackTopBar
import com.playlistmaker.ui.components.CoverImage
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme
import com.playlistmaker.ui.theme.White
import com.playlistmaker.util.formatTrackTime
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    track: Track,
    onBackClick: () -> Unit,
    onNewPlaylistClick: () -> Unit,
    viewModel: PlayerViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    // Прогресс обновляется каждые 300 мс. Чтобы не перерисовывать весь экран,
    // наверху читаем только флаги, а текст прогресса — внутри PlaybackProgress.
    val playerState = viewModel.observeState().observeAsState(PlayerState())
    val isPlaying by remember { derivedStateOf { playerState.value.isPlaying } }
    val isPrepared by remember { derivedStateOf { playerState.value.isPrepared } }
    val isFavorite by remember { derivedStateOf { playerState.value.isFavorite } }
    val playlists by viewModel.observePlaylists().observeAsState(emptyList())

    val scope = rememberCoroutineScope()
    // Шторка открывается на фиксированную высоту (частично раскрытое состояние)
    // и раскрывается на весь экран только свайпом вверх. Если плейлистов мало,
    // шторка просто подстраивается под содержимое.
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var isPlaylistsSheetVisible by rememberSaveable { mutableStateOf(false) }

    val hidePlaylistsSheet: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                isPlaylistsSheetVisible = false
            }
        }
    }

    LaunchedEffect(track) {
        viewModel.setTrack(track)
    }

    PlaybackServiceBinding(track = track, viewModel = viewModel)
    NotificationPermissionRequest()
    AppForegroundTracking(viewModel = viewModel)

    LaunchedEffect(Unit) {
        viewModel.observeAddTrackResult().collect { result ->
            if (result is AddTrackToPlaylistResult.Added) {
                hidePlaylistsSheet()
            }
            showAddTrackResultToast(context, result)
        }
    }

    PlayerContent(
        track = track,
        isPlaying = isPlaying,
        isPrepared = isPrepared,
        isFavorite = isFavorite,
        progress = { playerState.value.progress },
        onBackClick = onBackClick,
        onPlayClick = viewModel::playbackControl,
        onFavoriteClick = viewModel::onFavoriteClicked,
        onAddToPlaylistClick = {
            viewModel.loadPlaylists()
            isPlaylistsSheetVisible = true
        },
    )

    AddToPlaylistBottomSheet(
        visible = isPlaylistsSheetVisible,
        playlists = playlists,
        sheetState = sheetState,
        onDismissRequest = { isPlaylistsSheetVisible = false },
        onPlaylistClick = viewModel::addTrackToPlaylist,
        onNewPlaylistClick = {
            isPlaylistsSheetVisible = false
            onNewPlaylistClick()
        },
    )
}

/**
 * Привязка к сервису воспроизведения на время, пока экран плеера находится в композиции.
 */
@Composable
private fun PlaybackServiceBinding(
    track: Track,
    viewModel: PlayerViewModel,
) {
    val context = LocalContext.current
    val serviceConnection = remember(viewModel) { PlaybackServiceConnection(viewModel) }

    DisposableEffect(track) {
        val intent = Intent(context, PlaybackService::class.java).apply {
            putExtra(PlaybackService.EXTRA_PREVIEW_URL, track.previewUrl)
            putExtra(PlaybackService.EXTRA_ARTIST_NAME, track.artistName)
            putExtra(PlaybackService.EXTRA_TRACK_NAME, track.trackName)
        }

        val isBound = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        onDispose {
            if (isBound) {
                context.unbindService(serviceConnection)
            }
            viewModel.detachService()
        }
    }
}

@Composable
private fun NotificationPermissionRequest() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (!hasNotificationPermission(context)) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

/**
 * Уведомление о воспроизведении показывается, когда приложение уходит в фон (ON_STOP),
 * и скрывается при возвращении (ON_START). Подписываемся на жизненный цикл активити,
 * а не NavBackStackEntry: переход на другой экран внутри приложения — это не уход в фон.
 */
@Composable
private fun AppForegroundTracking(viewModel: PlayerViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalActivity.current as? LifecycleOwner ?: return

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (!hasNotificationPermission(context)) return@LifecycleEventObserver

            when (event) {
                Lifecycle.Event.ON_START -> viewModel.onAppForegrounded()
                Lifecycle.Event.ON_STOP -> viewModel.onAppBackgrounded()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun PlayerContent(
    track: Track,
    isPlaying: Boolean,
    isPrepared: Boolean,
    isFavorite: Boolean,
    progress: () -> String,
    onBackClick: () -> Unit,
    onPlayClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
) {
    val colors = PlaylistMakerTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        BackTopBar(onBackClick = onBackClick)

        CoverImage(
            model = track.getCoverArtwork(),
            placeholderRes = R.drawable.ic_cover_placeholder_233,
            modifier = Modifier
                .padding(start = 24.dp, top = 26.dp, end = 24.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
        )

        Text(
            text = track.trackName,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp),
            style = AppTypography.screenTitle,
            color = colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = track.artistName,
            modifier = Modifier.padding(start = 24.dp, top = 12.dp, end = 24.dp),
            style = AppTypography.button,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        PlayerControls(
            isPlaying = isPlaying,
            isPrepared = isPrepared,
            isFavorite = isFavorite,
            onPlayClick = onPlayClick,
            onFavoriteClick = onFavoriteClick,
            onAddToPlaylistClick = onAddToPlaylistClick,
            modifier = Modifier.padding(start = 24.dp, top = 30.dp, end = 24.dp),
        )

        PlaybackProgress(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        )

        TrackDetails(
            track = track,
            modifier = Modifier.padding(top = 30.dp),
        )
    }
}

@Composable
private fun PlaybackProgress(
    progress: () -> String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = progress(),
        modifier = modifier,
        style = AppTypography.button,
        color = PlaylistMakerTheme.colors.textPrimary,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun PlayerControls(
    isPlaying: Boolean,
    isPrepared: Boolean,
    isFavorite: Boolean,
    onPlayClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerSecondaryButton(
            iconRes = R.drawable.ic_add_to_playlist_23,
            onClick = onAddToPlaylistClick,
        )

        PlaybackButton(
            isPlaying = isPlaying,
            enabled = isPrepared,
            onClick = onPlayClick,
        )

        PlayerSecondaryButton(
            iconRes = if (isFavorite) {
                R.drawable.ic_favorite_active_23
            } else {
                R.drawable.ic_add_to_favorite_23
            },
            onClick = onFavoriteClick,
            tint = Color.Unspecified,
        )
    }
}

@Composable
private fun PlayerSecondaryButton(
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    tint: Color = White,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(51.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = PlaylistMakerTheme.colors.playerSecondaryButton,
        ),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
        )
    }
}

@Composable
private fun TrackDetails(
    track: Track,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TrackDetailsRow(
            label = stringResource(R.string.screen_player_track_length),
            value = formatTrackTime(track.trackTimeMillis),
        )

        if (track.collectionName.isNotEmpty()) {
            TrackDetailsRow(
                label = stringResource(R.string.screen_player_track_album),
                value = track.collectionName,
            )
        }

        if (track.releaseDate.isNotEmpty()) {
            TrackDetailsRow(
                label = stringResource(R.string.screen_player_track_year),
                value = track.releaseDate,
            )
        }

        TrackDetailsRow(
            label = stringResource(R.string.screen_player_track_genre),
            value = track.primaryGenreName,
        )

        TrackDetailsRow(
            label = stringResource(R.string.screen_player_track_country),
            value = track.country,
        )
    }
}

@Composable
private fun TrackDetailsRow(
    label: String,
    value: String,
) {
    val colors = PlaylistMakerTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 32.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = AppTypography.caption,
            color = colors.inactive,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Text(
                text = value,
                style = AppTypography.caption,
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
            )
        }
    }
}

private fun hasNotificationPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return true
    }

    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

private fun showAddTrackResultToast(
    context: Context,
    result: AddTrackToPlaylistResult,
) {
    val message = when (result) {
        is AddTrackToPlaylistResult.Added -> context.getString(
            R.string.screen_player_track_added_to_playlist,
            result.playlistName
        )

        is AddTrackToPlaylistResult.AlreadyAdded -> context.getString(
            R.string.screen_player_track_already_added_to_playlist,
            result.playlistName
        )

        AddTrackToPlaylistResult.Error -> context.getString(
            R.string.screen_player_track_add_error
        )
    }

    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
