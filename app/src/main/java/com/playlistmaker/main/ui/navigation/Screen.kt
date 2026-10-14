package com.playlistmaker.main.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.playlistmaker.R
import com.playlistmaker.search.domain.models.Track
import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object Search : Screen

    @Serializable
    data object MediaLibrary : Screen

    @Serializable
    data object Settings : Screen

    /**
     * Трек передаётся примитивными полями: так маршрут сериализуется
     * без изменений в domain-модели [Track].
     */
    @Serializable
    data class Player(
        val trackId: Long,
        val trackName: String,
        val artistName: String,
        val trackTimeMillis: Long,
        val artworkUrl100: String,
        val collectionName: String,
        val releaseDate: String,
        val primaryGenreName: String,
        val country: String,
        val previewUrl: String,
        val isFavorite: Boolean,
    ) : Screen {

        fun toTrack(): Track = Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl,
            isFavorite = isFavorite,
        )

        companion object {
            fun from(track: Track) = Player(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                isFavorite = track.isFavorite,
            )
        }
    }

    @Serializable
    data object CreatePlaylist : Screen

    @Serializable
    data class PlaylistInfo(val playlistId: Long) : Screen

    @Serializable
    data class EditPlaylist(val playlistId: Long) : Screen
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
