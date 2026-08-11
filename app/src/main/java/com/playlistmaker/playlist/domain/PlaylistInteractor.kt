package com.playlistmaker.playlist.domain

import android.net.Uri
import com.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(
        name: String,
        description: String,
        coverUri: Uri?
    ): Long

    suspend fun updatePlaylist(
        playlist: Playlist
    )

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    )
}