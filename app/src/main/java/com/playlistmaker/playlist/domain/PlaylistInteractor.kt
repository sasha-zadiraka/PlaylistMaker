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
        playlist: Playlist,
        coverUri: Uri?
    )

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    )

    suspend fun removeTrackFromPlaylist(
        trackId: Long,
        playlist: Playlist
    )

    fun getPlaylistById(
        playlistId: Long
    ): Flow<Playlist?>

    suspend fun deletePlaylist(playlistId: Long)

    fun getTracks(
        trackIds: List<Long>
    ): Flow<List<Track>>
}