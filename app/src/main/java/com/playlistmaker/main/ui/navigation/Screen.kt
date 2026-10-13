package com.playlistmaker.main.ui.navigation

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.playlistmaker.R
import com.google.gson.Gson
import com.playlistmaker.search.domain.models.Track

sealed class Screen(val route: String) {

    data object Search : Screen("search")

    data object MediaLibrary : Screen("media_library")

    data object Settings : Screen("settings")

    data object Player : Screen("player/{$ARG_TRACK}") {
        fun createRoute(track: Track): String = "player/${Uri.encode(gson.toJson(track))}"

        fun parseTrack(json: String): Track = gson.fromJson(json, Track::class.java)
    }

    data object CreatePlaylist : Screen("create_playlist")

    data object PlaylistInfo : Screen("playlist/{$ARG_PLAYLIST_ID}") {
        fun createRoute(playlistId: Long): String = "playlist/$playlistId"
    }

    data object EditPlaylist : Screen("edit_playlist/{$ARG_PLAYLIST_ID}") {
        fun createRoute(playlistId: Long): String = "edit_playlist/$playlistId"
    }

    companion object {
        const val ARG_TRACK = "track"
        const val ARG_PLAYLIST_ID = "playlistId"

        private val gson = Gson()
    }
}

enum class TopLevelDestination(
    val screen: Screen,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
) {
    SEARCH(
        screen = Screen.Search,
        titleRes = R.string.screen_main_button_search_title,
        iconRes = R.drawable.ic_search_magnifier_24,
    ),
    MEDIA_LIBRARY(
        screen = Screen.MediaLibrary,
        titleRes = R.string.screen_main_button_media_library,
        iconRes = R.drawable.ic_media_library_24,
    ),
    SETTINGS(
        screen = Screen.Settings,
        titleRes = R.string.screen_main_button_settings,
        iconRes = R.drawable.ic_settings_24,
    ),
}
