package com.playlistmaker.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.ui.components.LoadingIndicator
import com.playlistmaker.ui.components.PillButton
import com.playlistmaker.ui.components.PlaceholderMessage
import com.playlistmaker.ui.components.ScreenTitle
import com.playlistmaker.ui.components.TrackItem
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme
import com.playlistmaker.ui.theme.YpBlue
import com.playlistmaker.util.rememberDebouncedClick
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    onTrackClick: (Track) -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.observeState().observeAsState()
    // observeAsState() без аргумента стартует с текущего значения LiveData,
    // поэтому при возврате на экран текст запроса доступен уже в первой композиции.
    val searchText by viewModel.observeSearchText().observeAsState()

    LaunchedEffect(Unit) {
        if (searchText.isNullOrEmpty()) {
            viewModel.showHistoryIfNeeded()
        }
    }

    val onTrackClickDebounced = rememberDebouncedClick<Track> { track ->
        viewModel.saveTrackToHistory(track)
        onTrackClick(track)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaylistMakerTheme.colors.background)
            .statusBarsPadding(),
    ) {
        ScreenTitle(text = stringResource(R.string.screen_search_title))

        SearchField(
            text = searchText.orEmpty(),
            onTextChange = viewModel::onSearchTextChanged,
            onSearch = { viewModel.searchImmediately(searchText.orEmpty()) },
            onFocused = viewModel::onSearchFocused,
            onFocusLost = viewModel::onSearchFocusLost,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
        )

        SearchContent(
            state = state,
            onTrackClick = onTrackClickDebounced,
            onRetryClick = { viewModel.searchImmediately(searchText.orEmpty()) },
            onClearHistoryClick = viewModel::clearHistory,
        )
    }
}

@Composable
private fun SearchField(
    text: String,
    onTextChange: (String) -> Unit,
    onSearch: () -> Unit,
    onFocused: () -> Unit,
    onFocusLost: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = PlaylistMakerTheme.colors
    val keyboardController = LocalSoftwareKeyboardController.current

    // onFocusChanged срабатывает и при первой композиции (поле ещё без фокуса).
    // Потерей фокуса считаем только переход «был фокус → нет фокуса».
    // Значение не участвует в отрисовке, поэтому это обычный объект, а не State.
    val focusTracker = remember { FocusTracker() }

    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .onFocusChanged { focusState ->
                when {
                    focusState.isFocused -> {
                        focusTracker.hadFocus = true
                        onFocused()
                    }

                    focusTracker.hadFocus -> {
                        focusTracker.hadFocus = false
                        onFocusLost()
                    }
                }
            },
        singleLine = true,
        textStyle = AppTypography.primary.copy(color = colors.searchFieldText),
        cursorBrush = SolidColor(YpBlue),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                onSearch()
                keyboardController?.hide()
            },
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.searchField, RoundedCornerShape(8.dp))
                    .padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search_magnifier_14),
                    contentDescription = null,
                    tint = colors.searchFieldIcon,
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.screen_search_input_hint),
                            style = AppTypography.primary,
                            color = colors.searchFieldHint,
                        )
                    }
                    innerTextField()
                }

                ClearTextButton(
                    visible = text.isNotEmpty(),
                    onClick = { onTextChange("") },
                )
            }
        },
    )
}

@Composable
private fun ClearTextButton(
    visible: Boolean,
    onClick: () -> Unit,
) {
    if (!visible) return

    Box(
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_cross_button_16),
            contentDescription = null,
            tint = PlaylistMakerTheme.colors.searchFieldIcon,
        )
    }
}

@Composable
private fun SearchContent(
    state: SearchState?,
    onTrackClick: (Track) -> Unit,
    onRetryClick: () -> Unit,
    onClearHistoryClick: () -> Unit,
) {
    when (state) {
        SearchState.Loading -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 140.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            LoadingIndicator()
        }

        is SearchState.Content -> TrackList(
            tracks = state.tracks,
            onTrackClick = onTrackClick,
        )

        SearchState.Empty -> PlaceholderMessage(
            imageRes = R.drawable.ic_empty_state_120,
            message = stringResource(R.string.screen_search_error_empty_response),
            topPadding = 102.dp,
        )

        SearchState.Error -> PlaceholderMessage(
            imageRes = R.drawable.ic_no_internet_120,
            message = stringResource(R.string.screen_search_error_no_internet),
            topPadding = 102.dp,
            action = {
                PillButton(
                    text = stringResource(R.string.screen_search_button_reload),
                    onClick = onRetryClick,
                )
            },
        )

        is SearchState.History -> SearchHistory(
            tracks = state.tracks,
            onTrackClick = onTrackClick,
            onClearHistoryClick = onClearHistoryClick,
        )

        SearchState.NothingFound, null -> Unit
    }
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
    ) {
        items(items = tracks) { track ->
            TrackItem(track = track, onClick = onTrackClick)
        }
    }
}

@Composable
private fun SearchHistory(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistoryClick: () -> Unit,
) {
    if (tracks.isEmpty()) return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                text = stringResource(R.string.screen_search_history_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 12.dp),
                style = AppTypography.placeholder,
                color = PlaylistMakerTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
        }

        items(items = tracks) { track ->
            TrackItem(track = track, onClick = onTrackClick)
        }

        item {
            PillButton(
                text = stringResource(R.string.screen_search_button_clean_history),
                onClick = onClearHistoryClick,
                modifier = Modifier.padding(top = 24.dp),
            )
        }
    }
}

private class FocusTracker {
    var hadFocus = false
}
