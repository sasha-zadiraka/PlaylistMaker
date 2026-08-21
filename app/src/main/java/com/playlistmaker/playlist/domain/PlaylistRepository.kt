package com.playlistmaker.playlist.domain

import com.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(playlist: Playlist): Long

    suspend fun updatePlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    )

    suspend fun removeTrackFromPlaylist(
        trackId: Long,
        playlist: Playlist
    )

    fun getTracks(
        trackIds: List<Long>
    ): Flow<List<Track>>

    fun getPlaylistById(
        playlistId: Long
    ): Flow<Playlist?>

    suspend fun deletePlaylist(playlistId: Long)
}