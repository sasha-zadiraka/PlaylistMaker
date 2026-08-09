package com.playlistmaker.playlist.data

import com.playlistmaker.data.db.PlaylistTrackDao
import com.playlistmaker.data.db.PlaylistTrackEntity
import com.playlistmaker.playlist.data.db.PlaylistDao
import com.playlistmaker.playlist.data.db.PlaylistDbConverter
import com.playlistmaker.playlist.domain.Playlist
import com.playlistmaker.playlist.domain.PlaylistRepository
import com.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val converter: PlaylistDbConverter
) : PlaylistRepository {

    override suspend fun createPlaylist(
        playlist: Playlist
    ): Long {
        return playlistDao.insertPlaylist(
            converter.map(playlist)
        )
    }

    override suspend fun updatePlaylist(
        playlist: Playlist
    ) {
        playlistDao.updatePlaylist(
            converter.map(playlist)
        )
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists()
            .distinctUntilChanged()
            .map { playlistEntities ->
                playlistEntities.map { entity ->
                    converter.map(entity)
                }
            }
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        val playlistTrackEntity = PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            addedAt = System.currentTimeMillis()
        )

        playlistTrackDao.insertTrack(
            playlistTrackEntity
        )

        val updatedPlaylist = playlist.copy(
            trackIds = playlist.trackIds + track.trackId,
            trackCount = playlist.trackCount + 1
        )

        playlistDao.updatePlaylist(
            converter.map(updatedPlaylist)
        )
    }
}