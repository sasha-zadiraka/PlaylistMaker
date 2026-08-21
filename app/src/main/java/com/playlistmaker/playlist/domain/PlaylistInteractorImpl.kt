package com.playlistmaker.playlist.domain

import android.net.Uri
import com.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository,
    private val imageRepository: PlaylistImageRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(
        name: String,
        description: String,
        coverUri: Uri?
    ): Long {
        val coverPath = coverUri
            ?.let { uri ->
                imageRepository.saveImage(uri)
            }
            .orEmpty()

        val playlist = Playlist(
            name = name.trim(),
            description = description.trim(),
            coverPath = coverPath,
            trackIds = emptyList(),
            trackCount = 0
        )

        return repository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(
        playlist: Playlist,
        coverUri: Uri?
    ) {
        val coverPath = coverUri
            ?.let { uri -> imageRepository.saveImage(uri) }
            ?: playlist.coverPath

        repository.updatePlaylist(
            playlist.copy(
                name = playlist.name.trim(),
                description = playlist.description.trim(),
                coverPath = coverPath
            )
        )
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        repository.addTrackToPlaylist(
            track = track,
            playlist = playlist
        )
    }

    override suspend fun removeTrackFromPlaylist(
        trackId: Long,
        playlist: Playlist
    ) {
        repository.removeTrackFromPlaylist(
            trackId = trackId,
            playlist = playlist
        )
    }

    override fun getPlaylistById(
        playlistId: Long
    ): Flow<Playlist?> {
        return repository.getPlaylistById(playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override fun getTracks(
        trackIds: List<Long>
    ): Flow<List<Track>> {
        return repository.getTracks(trackIds)
    }
}